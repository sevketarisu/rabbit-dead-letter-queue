package com.example.consumer;

import com.example.consumer.api.Constants;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author Zoltan Altfatter
 */
@Configuration
@EnableRetry
public class AmqpConfig {

  @Bean
  Queue incomingQueue() {
    return QueueBuilder.durable(Constants.INCOMING_QUEUE_NAME)
        .withArgument("x-dead-letter-exchange", "")
        .withArgument("x-dead-letter-routing-key", Constants.DEAD_LETTER_QUEUE_NAME)
        .build();
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
    exponentialBackOffPolicy.setInitialInterval(500);
    exponentialBackOffPolicy.setMultiplier(2);
    exponentialBackOffPolicy.setMaxInterval(3000);

    retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(2);
    retryTemplate.setRetryPolicy(retryPolicy);

    return retryTemplate;
  }

}
