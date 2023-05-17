package fr.sii.ogham.sample.springboot.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

@SpringBootApplication
@PropertySource("application-sms-template.properties")  // just needed to be able to run the sample
public class TemplateSample {

  public static void main(String[] args) {
    SpringApplication.run(TemplateSample.class, args);
  }
  
  @RestController
  public static class SmsController {
    // Messaging service is automatically created using Spring Boot features
    // The configuration can be set into application-sms-template.properties
    // The configuration files are stored into src/main/resources
    @Autowired
    MessagingService messagingService;                                         // <1>
    
    @PostMapping(value="api/sms/template")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendSms(@RequestParam("to") String to, @RequestParam("name") String name, @RequestParam("value") int value) throws MessagingException {
      // send the SMS using fluent API
      messagingService.send(new Sms()                                          // <2>
           .message().template("register",                                     // <3>
                               new SimpleBean(name, value))                    // <4>
           .to(to));                                                           // <5>
    }
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
