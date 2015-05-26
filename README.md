# notification-module
Reusable Java library for sending any kind of message (email, SMS, tweet, SNMP...). The content of the message can comes from any templating engine (Thymeleaf, Freemarker, Velocity, ...). It also provides bridges for inclusion into frameworks (Spring, JSF, ...). It is designed to be easily extended.

# Why ?

# Features

- send an email
  - basic email
  - email with templated content
  - email with attachments
- send a SMS
  - basic SMS
  - SMS with templated content
- automatic configuration
  - automatically detect email implementation to use
  - automatically detect SMS implementation to use
  - automatically detect template engine


# Standard usage

This section describes how to use the library for usage with no framework and default bahvior. 

## Maven integration

Add the dependency to your pom.xml:
```xml
  ...
	<dependencies>
	  ...
		<dependency>
			<groupId>fr.sii.notification</groupId>
			<artifactId>notification-core</artifactId>
			<version>${notification-module.version}</version>
		</dependency>
		...
	</dependencies>
	...
```

## Sending email

### General

```java
package fr.sii.notification.sample.standard;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class BasicEmailSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "<your server host>");
		properties.put("mail.smtp.port", "<your server port>");
		properties.put("notification.email.from", "<email address to display for the sender user>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", "email content", "<recipient address>"));
	}

}
```


### Through Gmail

#### SSL

```java
package fr.sii.notification.sample.standard;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.email.message.EmailAddress;

public class BasicGmailSSLSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("notification.email.authenticator.username", "<your gmail account>");
		properties.put("notification.email.authenticator.password", "<your gmail password>");
		properties.put("notification.email.from", "<your gmail address>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", "email content", "<recipient address>"));
	}

}
```

## Sending email with template


```java
package fr.sii.notification.sample.standard;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.mock.context.SimpleBean;

public class HtmlTemplateEmailSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "<your server host>");
		properties.put("mail.smtp.port", "<your server port>");
		properties.put("notification.email.from", "<email address to display for the sender user>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", new TemplateContent("classpath:/template/thymeleaf/simple.html", new SimpleBean("foo", 42)), "<recipient address>"));
	}

}
```

### sending email with attachments

```java
package fr.sii.notification.sample.standard;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.message.Email;

public class EmailWithAttachmentSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "<your server host>");
		properties.put("mail.smtp.port", "<your server port>");
		properties.put("notification.email.from", "<email address to display for the sender user>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", "content of the email", "<recipient address>", new Attachment("classpath:/attachment/test.pdf")));
	}

}
```

# With Spring

## Spring 3

### Integration

### Configuration

## Spring Boot

### Integration

### Configuration

# Advanced usage

## Auto detection feature

## Choose email implementation

## Choose SMS implementation

## Choose template engine

## Configuration

### Configure email

#### Connecting to a server

#### Sender address



# Appendix

## Gmail throws javax.mail.AuthenticationFailedException: 534-5.7.14

You must log in via your web browser. You may have received an email asking to allow or not the connection.
See [this post on stackoverflow](http://stackoverflow.com/questions/25341198/javax-mail-authenticationfailedexception-is-thrown-while-sending-email-in-java)




Roadmap
=======

