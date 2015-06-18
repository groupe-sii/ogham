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

To include the library in your project, you just have to include the dependency to your pom.xml:
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

The samples are available in the [sample-standard-usage sub-project](https://github.com/groupe-sii/notification-module/tree/master/sample-standard-usage).

### General

This sample shows how to send a basic email. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/email/BasicSample.java).

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the NotificationBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic. It only contains the subject, the textual content and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (using a template or adding attachments).


```java
package fr.sii.notification.sample.standard.email;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class BasicSample {

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

This sample shows how to send a basic email through GMail. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/email/gmail/BasicGmailSSLSample.java).

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the NotificationBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic. It only contains the subject, the textual content and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (using a template or adding attachments).


```java
package fr.sii.notification.sample.standard.email.gmail;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class BasicGmailSSLSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.port", "465");
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("notification.email.authenticator.username", "<your gmail username>");
		properties.setProperty("notification.email.authenticator.password", "<your gmail password>");
		properties.setProperty("notification.email.from", "<your gmail address>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", "email content", "<recipient address>"));
	}

}

```

## Sending email with template

This sample shows how to send an email with a content that provides from a template. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/email/HtmlTemplateSample.java).

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the NotificationBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic too. It only contains the subject, the content based on a template available in the classpath, a bean to use as source of variable substitutions and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (adding attachments).


```java
package fr.sii.notification.sample.standard.email;

import java.util.Properties;

import fr.sii.notification.context.SimpleBean;
import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class HtmlTemplateSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "<your server host>");
		properties.setProperty("mail.smtp.port", "<your server port>");
		properties.setProperty("notification.email.from", "<email address to display for the sender user>");
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
        <meta charset="utf-8" />
    </head>
    <body>
        <h1 class="title" th:text="${name}"></h1>
        <p class="text" th:text="${value}"></p>
    </body>
</html>

```

The template is available [here](sample-standard-usage/src/main/resources/template/thymeleaf/simple.html)

## Sending email with subject from template

This sample is a variant of the previous one. It allows you to directly use the HTML title as subject of your email. It may be useful to use variables in the subject too, mutualize the code and avoiding to create a new file just for one line.

```java
package fr.sii.notification.sample.standard.email;

import java.util.Properties;

import fr.sii.notification.context.SimpleBean;
import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class HtmlTemplateWithSubjectSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "<your server host>");
		properties.setProperty("mail.smtp.port", "<your server port>");
		properties.setProperty("notification.email.from", "<email address to display for the sender user>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the email
		// subject is set to null to let automatic mechanism to read the title
		// of the HTML and use it as subject of your email
		service.send(new Email(null, new TemplateContent("classpath:/template/thymeleaf/simpleWithSubject.html", new SimpleBean("foo", 42)), "<recipient address>"));
	}

}
```

Here is the content of the template:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>Subject of the email</title>
        <meta charset="utf-8" />
    </head>
    <body>
        <h1 class="title" th:text="${name}"></h1>
        <p class="text" th:text="${value}"></p>
    </body>
</html>

```

You can look directly at the sample codes: [Java](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/email/HtmlTemplateSample.java) and [HTML](sample-standard-usage/src/main/resources/template/thymeleaf/simpleWithSubject.html).

## sending email with attachments

This sample shows how to send an email with attached file. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/email/WithAttachmentSample.java)

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the NotificationBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic too. It only contains the subject, the textual content, the receiver address and the attachment file that is available in the classpath. You may use several attachments too. The sender address is automatically added to the email by the service based on configuration properties.


```java
package fr.sii.notification.sample.standard.email;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.message.Email;

public class WithAttachmentSample {

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

## Sending an email with both HTML and text

Sending an email with HTML content AND text content is really important for smartphones. When a smartphone receives an email it displays the sender, the subject and also a preview of the message. If the message is only HTML, the preview may be unreadable. That's why the smartphones are using the text alternative for the preview.

This sample shows how to provide both HTML content and text content. This sample is available [here](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/email/HtmlAndTextSample.java).

package fr.sii.notification.sample.standard.email;

```java
import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class HtmlAndTextSample {

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
		String html = "<!DOCTYPE html><html><head><meta charset=\"utf-8\" /></head><body><h1 class=\"title\">Hello World</h1><p class=\"text\">Foo bar</p></body></html>";
		String text = "Hello World !\r\nFoo bar";
		service.send(new Email("subject", new MultiContent(html, text), "<recipient address>"));
	}

}
```

This sample shows how to provide both HTML content and text content that comes from templates. The sample shows the shorthand version that avoids specifying twice the path to the template. This sample is available [here](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/email/HtmlAndTextTemplateSample.java).

```java
package fr.sii.notification.sample.standard.email;

import java.util.Properties;

import fr.sii.notification.context.SimpleBean;
import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.MultiTemplateContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class HtmlAndTextTemplateSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "<your server host>");
		properties.setProperty("mail.smtp.port", "<your server port>");
		properties.setProperty("notification.email.from", "<email address to display for the sender user>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the email
		// Note that the extension of the template is not given. This version
		// automatically takes the provided path and adds the '.html' extension
		// for the HTML template and '.txt' for text template
		service.send(new Email("subject", new MultiTemplateContent("classpath:/template/thymeleaf/simple", new SimpleBean("foo", 42)), "<recipient address>"));
	}

}
```

Here is the content of the HTML template (available [here](sample-standard-usage/src/main/resources/template/thymeleaf/simple.html)):

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="utf-8" />
    </head>
    <body>
        <h1 class="title" th:text="${name}"></h1>
        <p class="text" th:text="${value}"></p>
    </body>
</html>
```

And the content of the text template (available [here](sample-standard-usage/src/main/resources/template/thymeleaf/simple.txt)):

```html
<html xmlns:th="http://www.thymeleaf.org" th:inline="text" th:remove="tag">
[[${name}]] [[${value}]]
</html>
```
## Sending SMS

### General

The [SMPP](https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer) protocol is the standard way to send SMS. This sample defines two properties mandatory (system ID and password) by this protocol in order to use it. This sample is available [here](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/sms/BasicSample.java).


```java
package fr.sii.notification.sample.standard.sms;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.sms.message.Sms;

public class BasicSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("notification.sms.smpp.host", "<your server host>");
		properties.setProperty("notification.sms.smpp.port", "<your server port>");
		properties.setProperty("notification.sms.smpp.systemId", "<your server system ID>");
		properties.setProperty("notification.sms.smpp.password", "<your server password>");
		properties.setProperty("notification.sms.from", "<phone number to display for the sender>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the sms
		service.send(new Sms("sms content", "<recipient phone number>"));
	}

}
```

### Sending SMS with template

Sending SMS with a template is exactly the same as sending email with a template. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/sms/TemplateSample.java).

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the NotificationBuilder to help you to create the service.
Finally, the last line sends the SMS. The specified SMS is really basic too. It only contains the content based on a template available in the classpath, a bean to use as source of variable substitutions and the receiver number. The sender number is automatically added to the SMS by the service based on configuration properties.

```java
package fr.sii.notification.sample.standard.sms;

import java.util.Properties;

import fr.sii.notification.context.SimpleBean;
import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.sms.message.Sms;

public class TemplateSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("notification.sms.smpp.host", "<your server host>");
		properties.setProperty("notification.sms.smpp.port", "<your server port>");
		properties.setProperty("notification.sms.smpp.systemId", "<your server system ID>");
		properties.setProperty("notification.sms.smpp.password", "<your server password>");
		properties.setProperty("notification.sms.from", "<phone number to display for the sender>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the sms
		service.send(new Sms(new TemplateContent("classpath:/template/thymeleaf/simple.txt", new SimpleBean("foo", 42)), "<recipient phone number>"));
	}

}
```

### Sending a long SMS

As you may know, SMS stands for Short Message Service. Basically, the messages are limited to a maximum of 160 characters (depends of char encoding). The library handles for you the split of messages into several parts the right way to be recomposed by clients later. So the code doesn't change at all (the sample is available [here](sample-standard-usage/src/main/java/fr/sii/notification/sample/standard/sms/LongMessageSample.java):

```java
package fr.sii.notification.sample.standard.sms;

import java.util.Properties;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.sms.message.Sms;

public class LongMessageSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("notification.sms.smpp.host", "<your server host>");
		properties.setProperty("notification.sms.smpp.port", "<your server port>");
		properties.setProperty("notification.sms.smpp.systemId", "<your server system ID>");
		properties.setProperty("notification.sms.smpp.password", "<your server password>");
		properties.setProperty("notification.sms.from", "<phone number to display for the sender>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the sms
		String longMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		service.send(new Sms(longMessage, "<recipient phone number>"));
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


# Hidden complexity

One of the main aim of the library is to hide the implementation complexity and provide automatic behaviors to simplify the development.

## Email

### Mime Type detection

When you send an email, it is really important to indicate what is the type of the message (html, text or maybe anything else). The library automatically detects for you the [Mime Type](https://en.wikipedia.org/wiki/Internet_media_type) of the email content and add this information into the real sent mail for you.

Moreover, every attached file must also provide the Mime Type in order to be correctly handled by the email client. Besides, some email clients can provide a preview of attached file. This can be possible only by providing the right Mime Type. The library will detect the Mime Type for every attached file for you.

### Working with HTML content

Email clients have many constraints that make writing HTML email very complex and time consuming. In order to ensure that your email can be read in most of the email clients, there are several rules to follow. The library do all this headache work for you.

All these features can be either disabled or use another implementation instead of the default one.

#### Inline CSS and images

For Web developers, it is important to write clean code and separate the concerns. So when writing HTML, developers want to externalize CSS files and images. This is also really important to mutualize the code of CSS files and images for reuse.

However, email clients doesn't handle external CSS files. Styles can be included in a `style` tag but Gmail doesn't support it. So all rules provided in the CSS *MUST* be inlined directly in the HTML. Writing code like this is just awful and error prone. Moreover, images can be references externally but there are many constraints to use it this way. You have to know in advance what is the URL of the final image. The image can also be blocked by the email client and not loaded if offline.

That's why the library do this job for you. It automatically inlines CSS rules directly on the HTML tags. The images are either inlined as base64 encoded in `img` `src` attribute or images are attached with the email (with inline content disposition and references in the HTML).

#### Use expanded CSS properties

CSS properties can be written using shorthand version:
```css
padding: 4px 2px;
```
This is equivalent to:
```css
padding: 4px 2px 4px 2px;
```

Some email clients doesn't understand shorthand properties. So all properties written in shorthand version must be expanded in order to work everywhere.

The library handles this case for you and will automatically expand properties from shorthand versions.

#### Add extra attributes for old email clients

Several attributes must be added on some HTML tags in order to be compliant with email clients. For example, tables and images must have attribute `border="0"`in order to prevent an ugly border on some clients.

The library automatically adds these attributes in order to help you to have the email displaying identically in whole clients.

#### Use XHTML

It is recommended to write XHTML instead of HTML due to some mail clients. The library do it for you.

#### Background images

Background images are not correctly handled by several mail clients. There are some techniques to make background images working for everyone. These techniques are handled by the library and will be applied to your HTML.

#### Use tables for layouts

TODO

## SMS

There are some tricky implementation constraints for sending a SMS. The library helps you handle these constraints.

### Phone number management

There exists several phone number formats. According to the format, some information about the number ([TON and NPI](https://docs.aerialink.net/api/smpp/ton-npi-settings/)) must be defined when sending the message.

The library helps you to generate this information for you. Just give the phone numbers and the library will guess the TON and NPI. You can also directly provide the TON and NPI in order to prevent this automatic behavior.

### Character encoding

Sending SMS with the wrong charset will cause your message to be unreadable. The charset resolution is done using a simple mapping between Java charsets and SMPP charsets. By default, Java uses UTF-8 charset and it mapped to the charset [GSM](https://en.wikipedia.org/wiki/GSM_03.38). The library offers you the ability to provide your own charset when sending SMS.

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

