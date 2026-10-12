package com.shoplite.notificationservice.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrderEventsListener {

	private static final Logger log = LoggerFactory.getLogger(OrderEventsListener.class);

	private final NotificationRepository repository;

	private final ObjectMapper json;

	public OrderEventsListener(NotificationRepository repository, ObjectMapper json) {
		this.repository = repository;
		this.json = json;
	}

	@KafkaListener(topics = "${shoplite.kafka.order-created-topic}")
	@Transactional
	public void onOrderCreated(String message) {
		OrderCreatedEvent event = json.readValue(message, OrderCreatedEvent.class);
		if (repository.existsByEventId(event.eventId())) {
			log.info("Duplicate event {} ignored (idempotent consumer)", event.eventId());
			return;
		}
		var p = event.payload();
		String text = "Hi %s, your order #%d (%d x %s, total $%s) is confirmed.".formatted(p.username(), p.orderId(),
				p.quantity(), p.productName(), p.totalAmount());
		repository.save(new Notification(event.eventId(), p.userId(), "EMAIL", text));
		// A real system would call an email/SMS provider here.
		log.info("Notification sent to {} for order {}", p.username(), p.orderId());
	}

}
