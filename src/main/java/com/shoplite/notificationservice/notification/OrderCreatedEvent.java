package com.shoplite.notificationservice.notification;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Consumer-side copy of the {@code order.created} contract owned by order-service.
 * Polyrepo trade-off: no shared library, so the contract is documented and duplicated.
 */
public record OrderCreatedEvent(String eventId, String eventType, Instant occurredAt, Payload payload) {

	public record Payload(long orderId, String userId, String username, long productId, String productName,
			int quantity, BigDecimal totalAmount) {
	}

}
