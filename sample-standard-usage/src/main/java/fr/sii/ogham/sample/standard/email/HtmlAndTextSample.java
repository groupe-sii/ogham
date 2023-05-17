package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlAndTextSample {
  private static String html = "<!DOCTYPE html>"
                + "<html>"
                +   "<head><meta charset=\"utf-8\" /></head>"
                +   "<body>"
                +     "<h1 class=\"title\">Hello World</h1>"
                +     "<p class=\"text\">Foo bar</p>"
                +   "</body>"
                + "</html>";
  private static String text = "Hello World !\r\n"
                + "Foo bar";

  public static void main(String[] args) throws MessagingException {
    // configure properties (could be stored in a properties file or defined
    // in System properties)
    Properties properties = new Properties();
    properties.put("mail.smtp.host", "<your server host>");
    properties.put("mail.smtp.port", "<your server port>");
    properties.put("ogham.email.from.default-value", "<email address to display for the sender user>");
    // Instantiate the messaging service using default behavior and
    // provided properties
    MessagingService service = MessagingBuilder.standard()
        .environment()
          .properties(properties)
          .and()
        .build();
    // send the email using the fluent API
    service.send(new Email()
        .subject("HtmlAndTextSample")
        .text().string(text)              // <1>
        .html().string(html)              // <2>
        .to("ogham-test@yopmail.com"));
  }
}
