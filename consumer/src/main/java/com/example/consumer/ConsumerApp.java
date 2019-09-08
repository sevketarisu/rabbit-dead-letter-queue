package com.example.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * @author Zoltan Altfatter
 */
@SpringBootApplication
@EnableRetry
public class ConsumerApp {

  public static void main(String[] args){
    SpringApplication.run(ConsumerApp.class, args);
  }
}
