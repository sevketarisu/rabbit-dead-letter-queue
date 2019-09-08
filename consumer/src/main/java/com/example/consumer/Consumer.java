package com.example.consumer;

import com.example.consumer.api.Constants;
import com.example.consumer.api.PaymentOrder;
import java.util.Date;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Zoltan Altfatter
 */
@Component
public class Consumer {

  private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

  private RetryTemplate retryTemplate;

  @Autowired
  public Consumer(RetryTemplate retryTemplate){
    this.retryTemplate=retryTemplate;
  }

  @RabbitListener(queues = Constants.INCOMING_QUEUE_NAME)
  @Retryable
  public void process(@Payload PaymentOrder paymentOrder)//// throws InsufficientFundsException////
  {
    retryTemplate.execute((RetryCallback<Void, RuntimeException>) retryContext -> {
      processPaymentOrder(paymentOrder);
      return null;
    });
  }

  private void processPaymentOrder(PaymentOrder paymentOrder) {

    logger.info("Processing at \'{}\' payload \'{}\'", new Date(), paymentOrder);

    if (paymentOrder.getAmount().intValue() % 5 == 0 && new Random().nextBoolean()
    ) {
      logger.warn("Exception for payment:" + paymentOrder.toString());
      throw new RuntimeException("This is should be retried");
      // throw new InsufficientFundsException("insufficient funds on account " + paymentOrder.getFrom());
    }
    logger.info("Processed at \'{}\' payload \'{}\'", new Date(), paymentOrder);
  }

  @Recover
  public void recover(Throwable throwable, PaymentOrder paymentOrder) {
    logger.info("SEND EMAIL for:{}", paymentOrder);
    throw new AmqpRejectAndDontRequeueException(throwable);
  }
}