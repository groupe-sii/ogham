## Email

The samples are available in the [sample-standard-usage sub-project](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage).

All samples shown bellow are using SMTP for sending email. See [select implementation](../config/select-implementation.html) to know other ways to send email.

### Basic

This sample shows how to send a basic email. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/BasicSample.java).

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
		service.send(new Email().subject("subject").content("email content").to("<recipient address>"));
	}

}
```

### Gmail sample

#### SSL

This sample shows how to send a basic email through GMail. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/gmail/BasicGmailSSLSample.java).

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
	}

}

```

Or using fluent API:


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
		service.send(new Email().subject("subject").content("email content").to("<recipient address>"));
	}

}

```
### Using a template

#### Sending email with template

This sample shows how to send an email with a content following a template engine language. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlTemplateSample.java).

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
	}

}

```

Or using the fluent API:

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

The template is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/thymeleaf/simple.html)

#### Sending email with subject from template

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
	}

}
```

Or using fluent API:

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
		// subject is not set to let automatic mechanism to read the title
		// of the HTML and use it as subject of your email
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

You can look directly at the sample codes: [Java](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlTemplateSample.java) and [HTML](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/thymeleaf/simpleWithSubject.html).

### Attachments

This sample shows how to send an email with attached file. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/WithAttachmentSample.java)

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
	}

}
```

Or using fluent API:

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
		service.send(new Email().
							subject("subject").
							content("content of the email").
							to("<recipient address>").
							attach(new Attachment("classpath:/attachment/test.pdf")));
	}

}
```

### Both HTML and text

Sending an email with HTML content **and** text content might be really important, at least for smartphones. When a smartphone receives an email, it displays the sender, the subject and also a preview of the message, using the text alternative. If the message is only HTML, the preview might be unreadable.

This sample shows how to provide both HTML content and text content. This sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlAndTextSample.java).

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
	}

}
```

Or using fluent API:


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
		service.send(new Email().
							subject("subject").
							content(new MultiContent(html, text)).
							to("<recipient address>"));
	}

}
```


This sample shows how to provide both HTML content and text content following a template engine language. The sample shows the shorthand version that avoids specifying twice the path to the template. This sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlAndTextTemplateSample.java).

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
	}

}
```

Or using fluent API:

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
		service.send(new Email().
							subject("subject").
							content(new MultiTemplateContent("classpath:/template/thymeleaf/simple", new SimpleBean("foo", 42))).
							to("<recipient address>"));
	}

}
```


Here is the content of the HTML template (available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/thymeleaf/simple.html)):

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

And the templated content (available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/thymeleaf/simple.txt)):

```html
<html xmlns:th="http://www.thymeleaf.org" th:inline="text" th:remove="tag">
[[${name}]]
[[${value}]]
</html>
```