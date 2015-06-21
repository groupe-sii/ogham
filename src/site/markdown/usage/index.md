## Usage

### Get it now

To use Ogham, [add it to pom.xml][ogham-integration]:

```xml
  ...
	<dependencies>
	  ...
		<dependency>
			<groupId>fr.sii.ogham</groupId>
			<artifactId>ogham-core</artifactId>
			<version>${ogham-module.version}</version>
		</dependency>
		...
	</dependencies>
	...
```

[Full usage instructions &raquo;][ogham-integration]

[ogham-integration]: integration.html


---


### Send email

```java
package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "<your server host>");
		properties.put("mail.smtp.port", "<your server port>");
		properties.put("ogham.email.from", "<email address to display for the sender user>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", "email content", "<recipient address>"));
	}

}
```

[Read more about email usages &raquo;][email-usage]

[email-usage]: how-to-send-email.html


### Send SMS

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
		properties.setProperty("ogham.sms.smpp.host", "<your server host>");
		properties.setProperty("ogham.sms.smpp.port", "<your server port>");
		properties.setProperty("ogham.sms.smpp.systemId", "<your server system ID>");
		properties.setProperty("ogham.sms.smpp.password", "<your server password>");
		properties.setProperty("ogham.sms.from", "<phone number to display for the sender>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the sms
		service.send(new Sms("sms content", "<recipient phone number>"));
	}

}
```

[Read more about SMS usages &raquo;][sms-usage]

[sms-usage]: how-to-send-sms.html


