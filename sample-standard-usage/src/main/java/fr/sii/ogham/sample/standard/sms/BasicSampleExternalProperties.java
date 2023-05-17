package fr.sii.ogham.sample.standard.sms;

import java.io.IOException;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class BasicSampleExternalProperties {

  public static void main(String[] args) throws MessagingException, IOException {
    // Instantiate the messaging service using default behavior and
    // provided properties
    // Load properties from file that is in classpath (src/main/resources/sms.properties)
    MessagingService service = MessagingBuilder.standard()
        .environment()
          .properties("/sms.properties")                 // <1>
          .and()
        .build();
    // send the sms using fluent API
    service.send(new Sms()
        .message().string("sms content")
        .to("+33752962193"));
  }

}
