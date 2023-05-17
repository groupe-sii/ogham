package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlTemplateSample {
  public static void main(String[] args) throws MessagingException {
    // configure properties (could be stored in a properties file or defined
    // in System properties)
    Properties properties = new Properties();
    properties.setProperty("mail.smtp.host", "<your server host>");
    properties.setProperty("mail.smtp.port", "<your server port>");
    properties.setProperty("ogham.email.from.default-value", "<email address to display for the sender user>");
    // Instantiate the messaging service using default behavior and
    // provided properties
    MessagingService service = MessagingBuilder.standard()               // <1>
        .environment()
          .properties(properties)                                        // <2>
          .and()
        .build();                                                        // <3>
    // send the email using fluent API
    service.send(new Email()                                             // <4>
        .subject("HtmlTemplateSample")
        .body().template("classpath:/template/thymeleaf/simple.html",    // <5>
                      new SimpleBean("foo", 42))                         // <6>
        .to("ogham-test@yopmail.com"));
  }

  public static class SimpleBean {
    private String name;
    private int value;
    public SimpleBean(String name, int value) {
      super();
      this.name = name;
      this.value = value;
    }
    public String getName() {
      return name;
    }
    public int getValue() {
      return value;
    }
  }
}
