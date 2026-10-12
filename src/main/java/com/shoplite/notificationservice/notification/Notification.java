package com.shoplite.notificationservice.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String eventId;

	private String userId;

	private String channel;

	private String message;

	private Instant createdAt = Instant.now();

	protected Notification() {
	}

	public Notification(String eventId, String userId, String channel, String message) {
		this.eventId = eventId;
		this.userId = userId;
		this.channel = channel;
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public String getChannel() {
		return channel;
	}

	public String getMessage() {
		return message;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

}
