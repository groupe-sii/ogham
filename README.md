# notification-module
Reusable Java library for sending any kind of message (email, SMS, notification mobile, tweet, SNMP...). The content of the message can comes from any templating engine (Thymeleaf, Freemarker, Velocity, ...). It also provides bridges the inclusion into frameworks (Spring, JSF, ...). It is designed to be easily extended.

# Why ?

## Existing libraries

There already exists several libraries for sending email ([Apache Commons Email](https://commons.apache.org/proper/commons-email/), [Simple Java Mail/Vesijama](https://github.com/bbottema/simple-java-mail), [Spring Email Integration](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mail.html)...). These libraries help you to send an email but you have to manually write the content. So if you want to use a template for the email content, you have to manually integrate a template engine.

These libraries also provide only implementations based on Java Mail API. But in some environments, it is possible that you don't want to send the email directly but use a web service for sending the email.

Is email the only possible message type ? No, so why not sending SMS, Tweet, SNMP or anything the same way ?

These libraries are stick to frameworks or libraries so you can't use the same code if you don't use the same framework or libraries.

## The notification-module

This module is designed for handling any kind of message the same way. It can provide several implementations for the same message type. It selects the best implementation based on the classpath or properties for example. You can add your own implementation.

It also provides templating support and integrates natively several template engines. You can also add your own.

It provides bridges for integration with frameworks and it is framework and library agnostic. It can be used with any framework (Spring, JSF, ...).

When using the module to send email based on an HTML template, the templating system let you design your HTML like a standard HTML page. It automatically transforms the associated resources (images, css files...) to be usable in an email context (automatic inline css, embed images...). You don't need to write your HTML specifically for email.


# Features

- send an email
  - basic email
  - email with templated content
  - email with attachments
- send a SMS
  - basic SMS
  - SMS with templated content
- managing lookup prefixes like JNDI
  - for templates
  - for attachments
- automatic configuration
  - automatically detect email implementation to use
  - automatically detect SMS implementation to use
  - automatically detect template engine to use


# Standard usage

This section describes how to use the library for usage with no framework and default behavior. 

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

This sample shows how to send a basic email.

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the NotificationBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic. It only contains the subject, the textual content and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (using a template or adding attachments).


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


#### Through Gmail

##### SSL

This sample shows how to send a basic email through GMail.

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the NotificationBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic. It only contains the subject, the textual content and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (using a template or adding attachments).


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

This sample shows how to send an email with a content that provides from a template.

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the NotificationBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic too. It only contains the subject, the content based on a template available in the classpath, a bean to use as source of variable substitutions and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (adding attachments).


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

Here is the content of the template:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>Thymeleaf simple</title>
        <meta charset="utf-8" />
    </head>
    <body>
        <h1 class="title" th:text="${name}"></h1>
        <p class="text" th:text="${value}"></p>
    </body>
</html>
```

## sending email with attachments

This sample shows how to send an email with attached file.

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the NotificationBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic too. It only contains the subject, the textual content, the receiver address and the attachment file that is available in the classpath. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (adding attachments).


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

This section describes how to configure the library to customize its behavior.

## Configuration

### Configure email

#### Connecting to a server

#### Sender address

### Configure Thymeleaf template engine

## Lookup resolvers

### Available lookups

## Auto detection feature

## Choose email implementation

## Choose SMS implementation

## Choose template engine


# Extend the library

## Add new email implementation

## Add new SMS implementation

## Add new template engine

## Add new lookup resolver

## Add new Message sender

## Add custom message interceptor


# Appendix

## Gmail throws javax.mail.AuthenticationFailedException: 534-5.7.14

You must log in via your web browser. You may have received an email asking to allow or not the connection.
See [this post on stackoverflow](http://stackoverflow.com/questions/25341198/javax-mail-authenticationfailedexception-is-thrown-while-sending-email-in-java)




Roadmap
=======

