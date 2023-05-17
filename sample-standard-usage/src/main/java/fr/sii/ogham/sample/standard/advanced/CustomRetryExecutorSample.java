package fr.sii.ogham.sample.standard.advanced;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.async.Awaiter;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.async.WaitException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.exception.retry.RetryException;
import fr.sii.ogham.core.exception.retry.RetryExecutionInterruptedException;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.retry.RetryStrategy;
import fr.sii.ogham.core.retry.RetryStrategyProvider;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;

public class CustomRetryExecutorSample {
  private static final Logger LOG = LoggerFactory.getLogger(CustomRetryExecutorSample.class);
  
  private static final int INVALID_PORT = 65000;

  public static void main(String[] args) throws MessagingException {
    // Instantiate the messaging service using default behavior and
    // provided properties
    MessagingService service = MessagingBuilder.standard()
        .environment()
          .properties()
            .set("mail.smtp.host", "localhost")
            .set("mail.smtp.port", INVALID_PORT)                                                                  // <1>
            .set("ogham.email.send-retry.max-attempts", 5)                                                        // <2>
            .set("ogham.email.send-retry.delay-between-attempts", 500)                                            // <3>
            .set("ogham.sms.smpp.host", "localhost")
            .set("ogham.sms.smpp.port", INVALID_PORT)                                                             // <4>
            .set("ogham.sms.send-retry.max-attempts", 5)                                                          // <5>
            .set("ogham.sms.send-retry.exponential-initial-delay", 500)                                           // <6>
            .and()
          .and()
        .email()
          .autoRetry()
            .executor(CustomRetryExecutor::new)                                                                   // <7>
            .and()
          .and()
        .sms()
          .autoRetry()
            .executor(CustomRetryExecutor::new)                                                                   // <8>
            .and()
          .and()
        .build();
    try {
      // send an email using fluent API
      service.send(new Email()
          .subject("BasicSample")
          .body().string("email content")
          .from("sender@yopmail.com")
          .to("ogham-test@yopmail.com"));
    } catch(MessagingException e) {
      LOG.error("Failed to send email", e);
    }
    try {
      // send a sms using fluent API
      service.send(new Sms()
          .message().string("SMS content")
          .from("000000000")
          .to("111111111"));
    } catch(MessagingException e) {
      LOG.error("Failed to send SMS", e);
    }
  }

  /**
   * Custom strategy implementation. For the sample, the executor reproduces
   * the base behavior of SimpleRetryExecutor.
   * 
   * @author Aurélien Baudet
   *
   */
  public static class CustomRetryExecutor implements RetryExecutor {
    private final RetryStrategyProvider retryStrategyProvider;
    private final Awaiter awaiter;

    public CustomRetryExecutor(RetryStrategyProvider retryStrategyProvider, Awaiter awaiter) {
      super();
      this.retryStrategyProvider = retryStrategyProvider;
      this.awaiter = awaiter;
    }

    @Override
    public <V> V execute(Callable<V> actionToRetry) throws RetryException {
      try {
        RetryStrategy retry = retryStrategyProvider.provide();                                                    // <9>
        List<Exception> failures = new ArrayList<>();
        do {
          Instant executionStart = Instant.now();                                                                 // <10>
          try {
            return actionToRetry.call();                                                                          // <11>
          } catch (Exception e) {
            failures.add(e);
            Instant next = retry.nextDate(executionStart, Instant.now());                                         // <12>
            LOG.warn("Failed to execute action. Cause: {}.\nRetrying at {}", e.getMessage(), next, e);
            awaiter.waitUntil(next);                                                                              // <13>
          }
        } while (!retry.terminated());                                                                            // <14>
        throw new MaximumAttemptsReachedException("Maximum attempts to execute action is reached", failures);     // <15>
      } catch (WaitException e) {
        throw new RetryExecutionInterruptedException(e);                                                          // <16>
      }

    }

  }
}
