package com.shoplite.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlingConfig {

	/**
	 * Retry a failing message 3 times (1s apart), then park it on {@code <topic>-dlt} so one
	 * poison message cannot block the whole partition.
	 */
	@Bean
	DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> template) {
		return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 3));
	}

}
