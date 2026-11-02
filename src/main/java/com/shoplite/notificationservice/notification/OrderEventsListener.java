package com.shoplite.notificationservice.notification;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrderEventsListener {

	private static final Logger log = LoggerFactory.getLogger(OrderEventsListener.class);

	private static final int MESSAGE_LIMIT = 500;

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
		String text = "Hi %s, your order #%d (%s, total $%s) is confirmed.".formatted(p.username(), p.orderId(),
				itemSummary(p), p.totalAmount());
		if (text.length() > MESSAGE_LIMIT) {
			text = text.substring(0, MESSAGE_LIMIT - 3) + "...";
		}
		repository.save(new Notification(event.eventId(), p.userId(), "EMAIL", text));
		// A real system would call an email/SMS provider here.
		log.info("Notification sent to {} for order {}", p.username(), p.orderId());
	}

	/** Old single-item events have no {@code items}; keep the message readable for them too. */
	private static String itemSummary(OrderCreatedEvent.Payload payload) {
		if (payload.items() == null || payload.items().isEmpty()) {
			return "your items";
		}
		return payload.items()
			.stream()
			.map(item -> item.quantity() + " x " + item.productName())
			.collect(Collectors.joining(", "));
	}

}
