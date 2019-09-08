package com.example.producer;

import com.example.producer.api.Constants;
import com.example.producer.api.PaymentOrder;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.iban4j.Iban;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Zoltan Altfatter
 */
@Component
public class Producer {

  private static final Logger logger = LoggerFactory.getLogger(Producer.class);
  private static final Random random = new Random();
  private static final AtomicLong counter = new AtomicLong();
  private AmqpTemplate amqpTemplate;

  public Producer(AmqpTemplate amqpTemplate) {
    this.amqpTemplate = amqpTemplate;
  }

  @Scheduled(fixedDelay = 1L)
  public void send() {

    PaymentOrder paymentOrder = new PaymentOrder(
        Iban.random().toFormattedString(),
        Iban.random().toFormattedString(),
        new BigDecimal(counter.incrementAndGet()));

    logger.info("Sending payload \'{}\'", paymentOrder);

    amqpTemplate.convertAndSend(Constants.EXCHANGE_NAME, Constants.ROUTING_KEY_NAME, paymentOrder);
  }
}
