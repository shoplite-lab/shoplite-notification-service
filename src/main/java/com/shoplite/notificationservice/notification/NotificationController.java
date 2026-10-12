package com.shoplite.notificationservice.notification;

import java.time.Instant;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationRepository repository;

	public NotificationController(NotificationRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<NotificationView> mine(@AuthenticationPrincipal Jwt jwt) {
		return repository.findTop50ByUserIdOrderByCreatedAtDesc(jwt.getSubject())
			.stream()
			.map(n -> new NotificationView(n.getId(), n.getChannel(), n.getMessage(), n.getCreatedAt()))
			.toList();
	}

	public record NotificationView(Long id, String channel, String message, Instant createdAt) {
	}

}
