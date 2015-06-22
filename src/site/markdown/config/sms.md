## SMS configuration

### Globally configure sender phone number

Ogham lets you set the sender phone number directly into properties. This phone number is automatically used for all sent SMS: 

```java
package fr.sii.ogham.sample.standard.sms;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		// ...
		properties.setProperty("ogham.sms.from", "060504030201");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the sms
		service.send(new Sms("sms content", "<recipient phone number>"));
		// => sender phone number is "060504030201"
	}

}
```

If you specify the sender phone number, this value is used instead of the global one:


```java
package fr.sii.ogham.sample.standard.sms;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;

public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		// ...
		properties.setProperty("ogham.sms.from", "060504030201");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the sms
		service.send(new Sms("sms content", new Sender("010203040506"), "<recipient phone number>"));
		// the sender phone number is "010203040506"
	}

}

```

### Configure SMPP

[SMPP](http://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer) is the standard protocol to send SMS. SMPP is quite complex and has many configurable properties:

| Property                                             | Description                                                                    | Required | Default value |
|------------------------------------------------------|--------------------------------------------------------------------------------|:--------:|:-------------:|
| ogham.sms.smpp.host                                  | The host of the SMPP or SMSC server                                            |   Yes    |               |
| ogham.sms.smpp.port                                  | The port of the SMPP or SMSC server                                            |   Yes    |               |
| ogham.sms.smpp.systemId                              | The system identifier used for authenticating                                  |   Yes    |               |
| ogham.sms.smpp.password                              | The password for the system identifier                                         |   Yes    |               |
| ogham.sms.smpp.window.size                           | Message exchange may be synchronous, where each peer waits for a response for<br/>each PDU being sent, or asynchronous, where multiple requests can be issued 7<br/>without waiting and acknowledged in a skew order by the other peer; the number<br/>of unacknowledged requests is called a window                                                                                                                         |   No     |       1       |
| ogham.sms.smpp.window.monitor.interval               | Time to wait between two window checks                                         |   No     |   disabled    |
| ogham.sms.smpp.timeout.connection                    | The timeout TCP/IP connection for SMPP session                                 |   No     |  10 seconds   |
| ogham.sms.smpp.timeout.bind                          | The maximum time to wait for the bind response                                 |   No     |   5 seconds   |
| ogham.sms.smpp.timeout.unbind                        | The maximum time to wait for unbind response                                   |   No     |   5 seconds   |
| ogham.sms.smpp.timeout.request                       | The maximum time to wait for an endpoint to respond to a request               |   No     |   disabled    |
| ogham.sms.smpp.timeout.window                        | The maximum time to wait for window response                                   |   No     |   1 minute    |
