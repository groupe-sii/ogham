package fr.sii.ogham.sample.test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServer;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

@JsmppServer                                                                               // <1>
public class SmsTestSample {
  private MessagingService oghamService;

  @BeforeEach
  public void setUp(SmppServerSimulator<SubmitSm> smppServer) throws IOException {         // <2>
    oghamService = MessagingBuilder.standard()
        .environment()
          .properties()
            .set("ogham.sms.from.default-value", "+33603040506")
            .set("ogham.sms.smpp.host", "localhost")                                       // <3>
            .set("ogham.sms.smpp.port", smppServer.getPort())                              // <4>
            .and()
          .and()
        .build();
  }
  
  @Test
  public void simple(SmppServerSimulator<SubmitSm> smppServer) throws MessagingException { // <5>
    // @formatter:off
    oghamService.send(new Sms()
        .message().string("sms content")
        .to("0601020304"));
    assertThat(smppServer).receivedMessages()                                              // <6>
      .count(is(1))                                                                        // <7>
      .message(0)                                                                          // <8>
        .content(is("sms content"))                                                        // <9>
        .from()
          .number(is("+33603040506"))                                                      // <10>
          .and()
        .to()
          .number(is("0601020304"));                                                       // <11>
    // @formatter:on
  }
}
