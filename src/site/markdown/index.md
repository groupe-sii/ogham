### Existing libraries

Several libraries for sending email already exist ([Apache Commons Email](https://commons.apache.org/proper/commons-email/), [Simple Java Mail/Vesijama](https://github.com/bbottema/simple-java-mail), [Spring Email Integration](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mail.html)...). These libraries help you send an email but if you want to use a templated content, you will have to manually integrate a template engine.

These libraries also provide only implementations based on Java Mail API. But in some environments, you might NOT want to send the email directly but to use a web service to do it for you ([SendGrid](https://sendgrid.com/) for example). Furthermore, those libraries are bound by design to frameworks or libraries that you might not want to use in your own context.

So, now you would want to find a sending library with a high level of abstraction to avoid binding issues with any template engine, design framework or sender service... Is email the only possible message type ? No, so why not sending SMS, Tweet, SNMP or anything the same way ?


### The Ogham module

This module is designed for handling any kind of message the same way. It also provides several implementations for the same message type. It selects the best implementation based on the classpath or properties for example. You can easily add your own implementation.

It also provides **templating support** and integrates natively several template engines. You can also add your own.

It is **framework and library agnostic** and provides bridges for **common frameworks integration** (Spring, JSF, ...).

When using the module to send email based on an HTML template, the templating system let you **design your HTML like a standard HTML page**. It automatically transforms the associated resources (images, css files...) to be usable in an email context (automatic inline css, embed images...). You don't need to write your HTML specifically for email.


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
		// send the email using constructor
		service.send(new Email("subject", "email content", "<recipient address>"));
		// or send email using the fluent API
		service.send(new Email().
							subject("subject").
							content("email content").
							to("<recipient address>"));
	}

}
```

[Read more about email usages &raquo;][email-usage]

[email-usage]: usage/how-to-send-email.html



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
		// send the sms using constructor
		service.send(new Sms("sms content", "<recipient phone number>"));
		// or send sms using fluent API
		service.send(new Sms().
							content("sms content").
							to("<recipient phone number>"));
	}

}
```

[Read more about SMS usages &raquo;][sms-usage]

[sms-usage]: usage/how-to-send-sms.html


---


### Get it now

To use Ogham without any framework, [add it to pom.xml][ogham-integration]:

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

To use Ogham with Spring Boot, just [add the following dependency][spring-integration]:

```xml
  ...
	<dependencies>
	  ...
		<dependency>
			<groupId>fr.sii.ogham</groupId>
			<artifactId>ogham-spring</artifactId>
			<version>${ogham-module.version}</version>
		</dependency>
		...
	</dependencies>
	...
```

[Full integration instructions &raquo;][ogham-integration]

[ogham-integration]: usage/integration.html
[spring-integration]: usage/integration.html#integrate-with-spring-boot


### Features

**Send email**

* [Basic email](usage/how-to-send-email.html)
* [Email with template](usage/how-to-send-email.html#using-a-template)
* [Email with attachments](usage/how-to-send-email.html#attachments)
* [Both HTML and text content](usage/how-to-send-email.html#both-html-and-text)
* [Extract subject from template](usage/how-to-send-email.html#sending-email-with-subject-from-template)

**Send SMS**

* [Basic SMS](usage/how-to-send-sms.html)
* [SMS with template](usage/how-to-send-sms.html#using-a-template)
* Extract subject from template

**Templating**

* Multi-template engine support
* [Internalize template CSS and images for you](features/hidden-complexity.html#inline-css-and-images)

**Managing lookup prefixes like JNDI**

* For templates
* For resources
* For attachments

**Automatic configuration**

* [Automatically detect email implementation to use](config/select-implementation.html#email)
* [Automatically detect SMS implementation to use](config/select-implementation.html#sms)
* Automatically detect template engine to use

**Integration with Spring**

* [Integration with Spring Boot](usage/integration.html#integrate-with-spring-boot)
* [Manual integration](usage/integration.html#manual-integration-with-spring)

**Extensible**

* Add your own message sender
* Integrate your own template engine
* Many other possible extensions

### Hidden complexity

One of the main aim of the library is to hide the implementation complexity. It provides automatic behaviors to simplify the development and focus on useful code.

[Read more about helpful features &raquo;](features/hidden-complexity.html)
