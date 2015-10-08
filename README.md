# Ogham

[Open, General and Highly Adaptative Messaging library.](http://groupe-sii.github.io/ogham/)

It is a reusable Java library in charge of sending any kind of message (email, SMS, mobile notification, tweet, SNMP...). The content of the message can follow any templating engine convention (Thymeleaf, Freemarker, Velocity, ...). The library also provides bridges for framework integration (Spring, JSF, ...). It is designed to be easily extended.

[Full documentation](http://groupe-sii.github.io/ogham/)

# Why ?

## Existing libraries

Several libraries for sending email already exist ([Apache Commons Email](https://commons.apache.org/proper/commons-email/), [Simple Java Mail/Vesijama](https://github.com/bbottema/simple-java-mail), [Spring Email Integration](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mail.html)...). These libraries help you send an email but if you want to use a templated content, you will have to manually integrate a template engine.

These libraries also provide only implementations based on Java Mail API. But in some environments, you might NOT want to send the email directly but to use a web service to do it for you ([SendGrid](https://sendgrid.com/) for example). Furthermore, those libraries are bound by design to frameworks or libraries that you might not want to use in your own context.

So, now you would want to find a sending library with a high level of abstraction to avoid binding issues with any template engine, design framework or sender service... Is email the only possible message type ? No, so why not sending SMS, Tweet, SNMP or anything the same way ?


## The Ogham module

This module is designed for handling any kind of message the same way. It also provides several implementations for the same message type. It selects the best implementation based on the classpath or properties for example. You can easily add your own implementation.

It also provides **templating support** and integrates natively several template engines. You can also add your own.

It is **framework and library agnostic** but provides bridges for **common frameworks integration** (Spring, JSF, ...).

When using the module to send email based on an HTML template, the templating system let you **design your HTML like a standard HTML page**. It automatically transforms the associated resources (images, css files...) to be usable in an email context (automatic inline css, embed images...). You don't need to write your HTML specifically for email.


# Features

**Send email**

* [Basic email](http://groupe-sii.github.io/ogham/usage/how-to-send-email.html)
* [Email with template](http://groupe-sii.github.io/ogham/usage/how-to-send-email.html#using-a-template)
* [Email with attachments](http://groupe-sii.github.io/ogham/usage/how-to-send-email.html#attachments)
* [Both HTML and text content](http://groupe-sii.github.io/ogham/usage/how-to-send-email.html#both-html-and-text)
* [Extract subject from template](http://groupe-sii.github.io/ogham/usage/how-to-send-email.html#sending-email-with-subject-from-template)

**Send SMS**

* [Basic SMS](http://groupe-sii.github.io/ogham/usage/how-to-send-sms.html)
* [SMS with template](http://groupe-sii.github.io/ogham/usage/how-to-send-sms.html#using-a-template)
* Extract subject from template

**Templating**

* Multi-template engine support
* [Internalize template CSS and images for you](http://groupe-sii.github.io/ogham/features/hidden-complexity.html#inline-css-and-images)

**Managing lookup prefixes like JNDI**

* For templates
* For resources
* For attachments

**Automatic configuration**

* [Automatically detect email implementation to use](http://groupe-sii.github.io/ogham/config/select-implementation.html#email)
* [Automatically detect SMS implementation to use](http://groupe-sii.github.io/ogham/config/select-implementation.html#sms)
* Automatically detect template engine to use

**Integration with Spring**

* [Integration with Spring Boot](http://groupe-sii.github.io/ogham/usage/integration.html#integrate-with-spring-boot)
* [Manual integration](http://groupe-sii.github.io/ogham/usage/integration.html#manual-integration-with-spring)

**Extensible**

* Add your own message sender
* Integrate your own template engine
* Many other possible extensions


# Standard usage

This section describes how to use the library with no framework and default behavior. 

## Maven integration

To include the library in your project, you just have to add the dependency to your pom.xml:

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

## Sending email

The samples are available in the [sample-standard-usage sub-project](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage).

All these samples show how to send email through SMTP. See [configuration](https://github.com/groupe-sii/ogham/wiki/Configuration-Guide#email-configuration) to know how to send email using another implementation.

### General

This sample shows how to send a basic email. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/BasicSample.java).

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the MessagingBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic. It only contains the subject, the textual content and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (using a templated content or adding attachments).


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

If you prefer, you can also use the fluent API:

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
		service.send(new Email().
							subject("subject").
							content("email content").
							to("<recipient address>"));
	}

}
```

### Load properties from file

This sample shows how to send a basic email. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/BasicSampleExternalProperties.java).

If you want to put properties in a configuration file, you can create a properties file (email.properties for example) in src/main/resources folder with the following content:

```ini
mail.smtp.host=<your server host>
mail.smtp.port=<your server port>
ogham.email.from=<email address to display for the sender user>
```

And then load these properties before creating messaging service:

```java
package fr.sii.ogham.sample.standard.email;

import java.io.IOException;
import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class BasicSampleExternalProperties {

	public static void main(String[] args) throws MessagingException, IOException {
		// load properties (available at src/main/resources)
		Properties properties = new Properties();
		properties.load(BasicSampleExternalProperties.class.getResourceAsStream("/email.properties"));
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", "email content", "<recipient address>"));
		// or using fluent API
		service.send(new Email().
						subject("subject").
						content("email content").
						to("<recipient address>"));
	}

}
```

### Through Gmail

#### SSL

This sample shows how to send a basic email through GMail. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/gmail/BasicGmailSSLSample.java).

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the MessagingBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic. It only contains the subject, the textual content and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (using a templated content or adding attachments).


```java
package fr.sii.ogham.sample.standard.email.gmail;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class BasicGmailSSLSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.host", "smtp.gmail.com");
		properties.setProperty("mail.smtp.port", "465");
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("ogham.email.authenticator.username", "<your gmail username>");
		properties.setProperty("ogham.email.authenticator.password", "<your gmail password>");
		properties.setProperty("ogham.email.from", "<your gmail address>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", "email content", "<recipient address>"));
		// or using fluent API
		service.send(new Email().
							subject("subject").
							content("email content").
							to("<recipient address>"));
	}

}

```

### Sending email with templated content

This sample shows how to send an email with a content following a template engine language. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlTemplateSample.java).

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the MessagingBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic too. It only contains the subject, the content based on a templated content available in the classpath, a bean to use as source of variable substitutions and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.

See other examples for advanced usages (adding attachments).


```java
package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlTemplateSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "<your server host>");
		properties.setProperty("mail.smtp.port", "<your server port>");
		properties.setProperty("ogham.email.from", "<email address to display for the sender user>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email
		service.send(new Email("subject", new TemplateContent("classpath:/template/thymeleaf/simple.html", new SimpleBean("foo", 42)), "<recipient address>"));
		// or using fluent API
		service.send(new Email().
							subject("subject").
							content(new TemplateContent("classpath:/template/thymeleaf/simple.html", new SimpleBean("foo", 42))).
							to("<recipient address>"));
	}

}

```

Here is the templated content:

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

### Sending email with subject from template

This sample is a variant of the previous one. It allows you to directly use the HTML title as subject of your email. It may be useful to use variables in the subject too, to mutualize the code and to avoid to create a new file just for one line.

```java
package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlTemplateWithSubjectSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "<your server host>");
		properties.setProperty("mail.smtp.port", "<your server port>");
		properties.setProperty("ogham.email.from", "<email address to display for the sender user>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email
		// subject is set to null to let automatic mechanism to read the title
		// of the HTML and use it as subject of your email
		service.send(new Email(null, new TemplateContent("classpath:/template/thymeleaf/simpleWithSubject.html", new SimpleBean("foo", 42)), "<recipient address>"));
		// or using fluent API (do not set subject)
		service.send(new Email().
							content(new TemplateContent("classpath:/template/thymeleaf/simpleWithSubject.html", new SimpleBean("foo", 42))).
							to("<recipient address>"));
	}

}
```

Here is the templated content :

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

You can look directly at the sample codes: [Java](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlTemplateSample.java) and [HTML](sample-standard-usage/src/main/resources/template/thymeleaf/simpleWithSubject.html).

### Sending email with attachments

This sample shows how to send an email with attached file. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/WithAttachmentSample.java)

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the MessagingBuilder to help you to create the service.
Finally, the last line sends the email. The specified email is really basic too. It only contains the subject, the textual content, the receiver address and the attachment file that is available in the classpath. You may use several attachments too. The sender address is automatically added to the email by the service based on configuration properties.


```java
package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;

public class WithAttachmentSample {

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
		service.send(new Email("subject", "content of the email", "<recipient address>", new Attachment("classpath:/attachment/test.pdf")));
		// or using fluent API
		service.send(new Email().
							subject("subject").
							content("content of the email").
							to("<recipient address>").
							attach(new Attachment("classpath:/attachment/test.pdf")));
	}

}
```

### Sending an email with both HTML and text

Sending an email with HTML content **and** text content might be really important, at least for smartphones. When a smartphone receives an email, it displays the sender, the subject and also a preview of the message, using the text alternative. If the message is only HTML, the preview might be unreadable.

This sample shows how to provide both HTML content and text content. This sample is available [here](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlAndTextSample.java).

package fr.sii.ogham.sample.standard.email;

```java
import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlAndTextSample {

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
		String html = "<!DOCTYPE html><html><head><meta charset=\"utf-8\" /></head><body><h1 class=\"title\">Hello World</h1><p class=\"text\">Foo bar</p></body></html>";
		String text = "Hello World !\r\nFoo bar";
		service.send(new Email("subject", new MultiContent(html, text), "<recipient address>"));
		// or using fluent API
		service.send(new Email().
							subject("subject").
							content(new MultiContent(html, text)).
							to("<recipient address>"));
	}

}
```

This sample shows how to provide both HTML content and text content following a template engine language. The sample shows the shorthand version that avoids specifying twice the path to the template. This sample is available [here](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlAndTextTemplateSample.java).

```java
package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlAndTextTemplateSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "<your server host>");
		properties.setProperty("mail.smtp.port", "<your server port>");
		properties.setProperty("ogham.email.from", "<email address to display for the sender user>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email
		// Note that the extension of the template is not given. This version
		// automatically takes the provided path and adds the '.html' extension
		// for the HTML template and '.txt' for text template
		service.send(new Email("subject", new MultiTemplateContent("classpath:/template/thymeleaf/simple", new SimpleBean("foo", 42)), "<recipient address>"));
		// or using fluent API
		service.send(new Email().
							subject("subject").
							content(new MultiTemplateContent("classpath:/template/thymeleaf/simple", new SimpleBean("foo", 42))).
							to("<recipient address>"));
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

And the templated content (available [here](sample-standard-usage/src/main/resources/template/thymeleaf/simple.txt)):

```html
<html xmlns:th="http://www.thymeleaf.org" th:inline="text" th:remove="tag">
[[${name}]]
[[${value}]]
</html>
```

### Full sample

This sample combines all features:

- Uses templates (HTML and text templates)
- HTML template includes page fragments
- HTML template references external CSS and images
- The subject is directly extracted from template
- The email is sent with one attachment


Here is the Java code ([available here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/FullSample.java)):


```java
package fr.sii.ogham.sample.standard.email;

import java.io.IOException;
import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;

public class FullSample {

	public static void main(String[] args) throws MessagingException, IOException {
		// configure properties from file
		Properties properties = new Properties();
		properties.load(FullSample.class.getResourceAsStream("/email-template.properties"));
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email using fluent API
		// @formatter:off
		service.send(new Email().
						content(new MultiTemplateContent("full", new SimpleBean("foo", 42))).
						to("<recipient address>").
						attach(new Attachment("/attachment/test.pdf")));
		// @formatter:on
	}

}
```

The loaded property file content:

```ini
# general SMTP server
mail.smtp.host=<your server host>
mail.smtp.port=<your server port>
# using with Gmail
#mail.smtp.auth=true
#mail.smtp.host=smtp.gmail.com
#mail.smtp.port=465
#mail.smtp.socketFactory.port=465
#mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
#ogham.email.authenticator.username=<your gmail username>
#ogham.email.authenticator.password=<your gmail password>


# ogham additional properties
ogham.email.from=<sender email address>
ogham.email.template.prefix=/template/thymeleaf/email/
```

The HTML template content is [available here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/thymeleaf/email/full.html). The content of the HTML is not displayed entirely. Just useful parts are shown here:

```html
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<!-- Load fragment that contains external CSS -->
<head th:replace="fragments/header.html :: header">&nbsp;</head>
<body id="body_2a02_0">
	...
		<!-- Use image -->
		<img src="classpath:/resources/images/h1.gif" alt="Creating Email Magic" width="300" height="230" />
	...
		<!-- Use of variables and CSS classes that will be interned -->
		<tr>
			<td id="td_2a02_3">
				<span class="name" th:text="${name}">${name}</span>
			</td>
		</tr>
		<tr>
			<td id="td_2a02_4" class="value" th:text="${value}">
			</td>
		</tr>
	...
```

Now the content of the header ([available here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/thymeleaf/email/fragments/header.html)). The header contains the subject of the email (with title tag) and references external CSS files that will be interned directly in the HTML:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
	<head th:fragment="header">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Full Sample</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
		<link href="classpath:/resources/css/external1.css" rel="stylesheet" />
		<link href="classpath:/resources/css/external2.css" rel="stylesheet" />
	</head>
</html>
```

The content of the CSS files are not displayed here but [can be found in samples](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/resources/css/). Useful classes are `name` and `value`.

The text template is [available here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/thymeleaf/email/full.txt).


## Sending SMS

### General

The [SMPP](https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer) protocol is the standard way to send SMS. This sample defines two properties mandatory (system ID and password) by this protocol in order to use it. This sample is available [here](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/sms/BasicSample.java).


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

If you prefer, you can also use the fluent API:

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
		service.send(new Sms().
							content("sms content").
							to("<recipient phone number>"));
	}

}
```

### Load properties from file

This sample shows how to send a basic email. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/sms/BasicSampleExternalProperties.java).

If you want to put properties in a configuration file, you can create a properties file (sms.properties for example) in src/main/resources folder with the following content:

```ini
ogham.sms.smpp.host<your server host>
ogham.sms.smpp.port=<your server port>
ogham.sms.smpp.systemId=<your server system ID>
ogham.sms.smpp.password=<your server password>
ogham.sms.from=<phone number to display for the sender>
```

And then load these properties before creating messaging service:

```java
package fr.sii.ogham.sample.standard.sms;

import java.io.IOException;
import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class BasicSampleExternalProperties {

	public static void main(String[] args) throws MessagingException, IOException {
		// load properties (available at src/main/resources)
		Properties properties = new Properties();
		properties.load(BasicSampleExternalProperties.class.getResourceAsStream("/sms.properties"));
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the sms
		service.send(new Sms("sms content", "<recipient phone number>"));
		// or using fluent API
		service.send(new Sms().
						content("sms content").
						to("<recipient phone number>"));
	}

}

```

### Sending SMS with templated content

Sending SMS with a templated content is exactly the same as sending email with a templated content. The sample is available [here](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/sms/TemplateSample.java).

The first lines configure the properties that will be used by the sender.
Then you must create the service. You can use the MessagingBuilder to help you to create the service.
Finally, the last line sends the SMS. The specified SMS is really basic too. It only contains the templated content available in the classpath, a bean to use as source of variable substitutions and the receiver number. The sender number is automatically added to the SMS by the service based on configuration properties.

```java
package fr.sii.ogham.sample.standard.sms;

import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class TemplateSample {

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
		service.send(new Sms(new TemplateContent("classpath:/template/thymeleaf/simple.txt", new SimpleBean("foo", 42)), "<recipient phone number>"));
		// or using fluent API
		service.send(new Sms().
							content(new TemplateContent("classpath:/template/thymeleaf/simple.txt", new SimpleBean("foo", 42))).
							to("<recipient phone number>"));
	}

}
```

### Sending a long SMS

As you may know, SMS stands for Short Message Service. Basically, the messages are limited to a maximum of 160 characters (depends of char encoding). If needed, the library will split your messages into several parts the right way to be recomposed by clients later. So the code doesn't change at all (the sample is available [here](sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/sms/LongMessageSample.java)):

```java
package fr.sii.ogham.sample.standard.sms;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class LongMessageSample {

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
		String longMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		// send the sms
		service.send(new Sms(longMessage, "<recipient phone number>"));
		// or using fluent API
		service.send(new Sms().
							content(longMessage).
							to("<recipient phone number>"));
	}

}

```


## Usage with Spring

### Integration

To use Ogham and let Spring Boot auto-configuration mechanism handle integration, just add dependency to your pom.xml:

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

It will automatically create and register MessagingService bean. It will also automatically use Spring Thymeleaf integration if it is in the classpath.
The Spring environment (configuration provided by configuration files for example) will be automatically used to configure the Ogham module.

Then to use it in your code, simply autowire the service:

```java
	public class UserService {
		@Autowired
		MessagingService messagingService;
		
		public UserDTO register(UserDTO user) {
			...
			messagingService.send(new Email("account registered", "email content", user.getEmailAddress()));
			...
		}
	}
```

For additional usage information or manual integration see [how to use the module with Spring](http://groupe-sii.github.io/ogham/usage/spring.html).


### Send email with Spring

Spring comes with very useful management of configuration properties (environment and profiles). Ogham module is able to use environment provided by Spring.

Add the following information in the application.properties (or according to profile, into the right configuration file):

```ini
mail.smtp.host=<your server host>
mail.smtp.port=<your server port>
ogham.email.from=<your gmail address>
```

To use Ogham in Spring, you can directly inject (autowire) it. Here is a full Spring Boot application serving one REST endpoint for sending email using Ogham ([sample available here](https://github.com/groupe-sii/ogham/blob/master/sample-spring-usage/src/main/java/fr/sii/ogham/sample/springboot/email/BasicSample.java)):

```java
package fr.sii.ogham.context.sample.springboot.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

@SpringBootApplication
public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(BasicSample.class, args);
	}
	
	@RestController
	public static class BasicController {
		// Messaging service is automatically created using Spring Boot features
		// The configuration can be set into application-basic.properties
		// The configuration files are stored into src/main/resources
		@Autowired
		MessagingService messagingService;
		
		@RequestMapping(value="api/email", method=RequestMethod.POST)
		@ResponseStatus(HttpStatus.CREATED)
		public void sendMail(@RequestParam("subject") String subject, @RequestParam("content") String content, @RequestParam("to") String to) throws MessagingException {
			// send the email
			messagingService.send(new Email(subject, content, to));
			// or using fluent API
			messagingService.send(new Email().
									subject(subject).
									content(content).
									to(to));
		}
	}

}
```

### Send SMS with Spring

Spring comes with very useful management of configuration properties (environment and profiles). Ogham module is able to use environment provided by Spring.

Add the following information in the application.properties (or according to profile, into the right configuration file):

```ini
ogham.sms.smpp.host<your server host>
ogham.sms.smpp.port=<your server port>
ogham.sms.smpp.systemId=<your server system ID>
ogham.sms.smpp.password=<your server password>
ogham.sms.from=<phone number to display for the sender>
```

To use Ogham in Spring, you can directly inject (autowire) it. Here is a full Spring Boot application serving one REST endpoint for sending SMS using Ogham ([sample available here](https://github.com/groupe-sii/ogham/blob/master/sample-spring-usage/src/main/java/fr/sii/ogham/sample/springboot/sms/BasicSample.java)):

```java
package fr.sii.ogham.context.sample.springboot.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

@SpringBootApplication
public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(BasicSample.class, args);
	}
	
	@RestController
	public static class BasicController {
		// Messaging service is automatically created using Spring Boot features
		// The configuration can be set into application-basic.properties
		// The configuration files are stored into src/main/resources
		@Autowired
		MessagingService messagingService;
		
		@RequestMapping(value="api/sms", method=RequestMethod.POST)
		@ResponseStatus(HttpStatus.CREATED)
		public void sendMail(@RequestParam("content") String content, @RequestParam("to") String to) throws MessagingException {
			// send the SMS
			messagingService.send(new Sms(content, to));
			// or using fluent API
			messagingService.send(new Sms().
									content(content).
									to(to));
		}
	}

}

```


# Implementation selection

See wiki to [know how to the library selects the right implementation](https://github.com/groupe-sii/ogham/wiki/Configuration-Guide#select-implementation).

# Hidden complexity

One of the main aim of the library is to hide the implementation complexity and provide automatic behaviors to simplify the development.

## Email

### Mime Type detection policy

When you send an email, it is really important to indicate what is the type of the message (html, text or maybe anything else). The library automatically detects for you the [Mime Type](https://en.wikipedia.org/wiki/Internet_media_type) of the email content and add this information into the real sent email.

Moreover, every attached file must also provide the Mime Type in order to be correctly handled by the email client. Besides, some email clients may provide a preview of attached file. This will be possible only if the correct Mime Type is provided. The library comes with a handy Mime Type Detector policy.

### Working with HTML content

To be sure that most of Email clients will handle an HTML content, there are many rules to follow when writing the content. They might be very complex and time consuming. The library do all this headache work for you.

All these features can be either disabled or use another implementation instead of the default one.

#### Inline CSS and images

For Web developers, it is important to write clean code and separate the concerns. So when writing HTML, developers want to externalize CSS files and images. This is also really important to mutualize the code of CSS files and images for reuse.

However, email clients do not handle external CSS files. Styles might be included in a `style` tag but Gmail doesn't support it. So all rules provided in the CSS *MUST* be inlined directly in the HTML. Writing code like this is just painful and error prone. Moreover, images can be referenced externally but only if you know in advance what will be the URL of the final image. And even then, the email client might block those images for safety purpose. Not mentioning offline issues.

The library will automatically inlines CSS rules directly on the HTML tags. The images are either inlined as base64 encoded in `img` `src` attribute or images are attached with the email (with inline content disposition and references in the HTML).

#### Use expanded CSS properties

CSS properties can be written using shorthand version:
```css
padding: 4px 2px;
```
This is equivalent to:
```css
padding: 4px 2px 4px 2px;
```

Some email clients do not handle shorthand properties. So all properties written in shorthand version should be expanded in order to work everywhere. 

The library will automatically expand properties from shorthand versions.

#### Add extra attributes for old email clients

Several attributes should be added on some HTML tags in order to be compliant with email clients. For example, tables and images must have attribute `border="0"`in order to prevent an ugly border on some clients.

The library will automatically add these attributes.

#### Use XHTML

It is recommended to write XHTML instead of HTML due to some email clients. The library do it for you.

#### Background images

Background images are not correctly handled by several email clients. And again, some workarounds to apply to fix this issue. 

The library will apply those workarounds to your HTML.

#### Use tables for layouts

TODO

## SMS

There are some implementation constraints for sending a SMS following SMPP protocol. The library will help you handle these constraints.

### Phone number TON and NPI policy

SMPP defines two main properties related to the sender and recipient phone number formats: [TON and NPI](https://docs.aerialink.net/api/smpp/ton-npi-settings/).

The library provides a default policy : Just pass the phone numbers and the library will guess the TON and NPI. You can also directly provide the TON and NPI in order to prevent this automatic behavior.

### Character encoding

Sending SMS with the wrong charset might cause your message to be unreadable. But SMPP Charset are quite different from the common charset definition. The charset resolution is done using a simple mapping between Java charsets and SMPP charsets. By default, Java uses UTF-8 charset and the librairy will map to the SMPP charset [GSM](https://en.wikipedia.org/wiki/GSM_03.38). The library offers you the ability to provide your own charset when sending SMS if need.

# Advanced usage

See wiki to [know how to configure the library to customize its behavior](https://github.com/groupe-sii/ogham/wiki/Configuration-Guide).



# Extend the library

See wiki to [know how to add custom implementations](https://github.com/groupe-sii/ogham/wiki/Extension-Guide)


# Appendix

## Gmail throws javax.mail.AuthenticationFailedException: 534-5.7.14

You must log in via your web browser. You may have received an email asking to allow or not the connection.
See [this post on stackoverflow](http://stackoverflow.com/questions/25341198/javax-mail-authenticationfailedexception-is-thrown-while-sending-email-in-java)

