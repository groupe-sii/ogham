## Email

The samples are available in the [sample-standard-usage sub-project](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage).

All samples shown bellow are using SMTP for sending email. See [select implementation](../config/select-implementation.html) to know other ways to send email.

### <a name="basic"/>Basic

				This sample shows how to send a basic email. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/BasicSample.java).
				
				The first lines configure the properties that will be used by the sender.
				Then you must create the service. You can use the MessagingBuilder to help you to create the service.
				Finally, the last line sends the email. The specified email is really basic. It only contains the subject, the textual content and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.
				
				See other examples for advanced usages (using a templated content or adding attachments).
				
				
				<span class="highlight" data-irrelevant-lines="1-9"></span>
				<span class="collapse" data-lines="1-9"></span>
				
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
				
				If you prefer, you can also use the fluent API (highlighted lines):
				
				<span class="highlight" data-lines="23-26" data-irrelevant-lines="1-9,13-22"></span>
				<span class="collapse" data-lines="1-9,13-22"></span>
				
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

#### <a name="load-properties-from-file"/>Load properties from file

This sample shows how to send a basic email. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/BasicSampleExternalProperties.java).

If you want to put properties in a configuration file, you can create a properties file (email.properties for example) in src/main/resources folder with the following content:

```ini
mail.smtp.host=<your server host>
mail.smtp.port=<your server port>
ogham.email.from=<email address to display for the sender user>
```

And then load these properties before creating messaging service:

<span class="highlight" data-lines="14-16" data-irrelevant-lines="1-10,17-19"></span>
<span class="collapse" data-lines="1-10,17-19"></span>

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


#### Gmail sample

##### SSL

				This sample shows how to send a basic email through GMail. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/gmail/BasicGmailSSLSample.java).
				
				The first lines configure the properties that will be used by the sender.
				Then you must create the service. You can use the MessagingBuilder to help you to create the service.
				Finally, the last line sends the email. The specified email is really basic. It only contains the subject, the textual content and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.
				
				See other examples for advanced usages (using a templated content or adding attachments).
				
				
				<span class="highlight" data-lines="13-23" data-irrelevant-lines="1-9"></span>
				<span class="collapse" data-lines="1-9"></span>
				
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
						properties.setProperty("ogham.email.javamail.authenticator.username", "<your gmail username>");
						properties.setProperty("ogham.email.javamail.authenticator.password", "<your gmail password>");
						properties.setProperty("ogham.email.from", "<your gmail address>");
						// Instantiate the messaging service using default behavior and
						// provided properties
						MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
						// send the email using constructor
						service.send(new Email("subject", "email content", "<recipient address>"));
						// or send the email using fluent API
						service.send(new Email().
												subject("subject").
												content("email content").
												to("<recipient address>"));
					}
				
				}
				
				```

### <a name="using-a-template"/>Using templates

#### <a name="sending-email-with-template"/>Sending email with template

			This sample shows how to send an email with a content following a template engine language. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlTemplateSample.java).
			
			The first lines configure the properties that will be used by the sender.
			Then you must create the service. You can use the MessagingBuilder to help you to create the service.
			Finally, the last line sends the email. The specified email is really basic too. It only contains the subject, the content based on a templated content available in the classpath, a bean to use as source of variable substitutions and the receiver address. The sender address is automatically added to the email by the service based on configuration properties.
			
			See other examples for advanced usages (adding attachments).
			
			<span class="highlight" data-lines="25,29" data-irrelevant-lines="1-11,15-24"></span>
			<span class="collapse" data-lines="1-11,15-24"></span>
			
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
					// send the email using constructor
					service.send(new Email("subject", new TemplateContent("classpath:/template/thymeleaf/simple.html", new SimpleBean("foo", 42)), "<recipient address>"));
					// or send the email using fluent API
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

#### <a name="sending-email-with-subject-from-template"/>Sending email with subject from template

			This sample is a variant of the previous one. It allows you to directly use the HTML title as subject of your email. It may be useful to use variables in the subject too, to mutualize the code and to avoid to create a new file just for one line.
			
			<span class="highlight" data-lines="27,31-33,25-26,29-30" data-irrelevant-lines="1-11,15-23"></span>
			<span class="collapse" data-lines="1-11,15-23"></span>
			
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
					// send the email using constructor
					// subject is set to null to let automatic mechanism to read the title
					// of the HTML and use it as subject of your email
					service.send(new Email(null, new TemplateContent("classpath:/template/thymeleaf/simpleWithSubject.html", new SimpleBean("foo", 42)), "<recipient address>"));
					// or send the email using fluent API
					// subject is not set to let automatic mechanism to read the title
					// of the HTML and use it as subject of your email
					service.send(new Email().
										content(new TemplateContent("classpath:/template/thymeleaf/simpleWithSubject.html", new SimpleBean("foo", 42))).
										to("<recipient address>"));
				}
			
			}
			```
			
			Here is the templated content :
			
			<span class="highlight" data-lines="4"></span>
			
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
			
			For text templates, the subject is automatically used (like for HTML title) if the first line starts with `Subject:` (spaces can be added after colon). Other lines are used as content of the email.

#### <a name="both-html-and-text"/>Both HTML and text

			Sending an email with HTML content **and** text content might be really important, at least for smartphones. When a smartphone receives an email, it displays the sender, the subject and also a preview of the message, using the text alternative. If the message is only HTML, the preview might be unreadable.
			
			This sample shows how to provide both HTML content and text content. This sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlAndTextSample.java).
			
			<span class="highlight" data-lines="24-26,30" data-irrelevant-lines="1-9,14-21"></span>
			<span class="collapse" data-lines="1-9,14-21"></span>
			
			```java
			package fr.sii.ogham.sample.standard.email;
			
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
					// send the email using constructor
					String html = "<!DOCTYPE html><html><head><meta charset=\"utf-8\" /></head><body><h1 class=\"title\">Hello World</h1><p class=\"text\">Foo bar</p></body></html>";
					String text = "Hello World !\r\nFoo bar";
					service.send(new Email("subject", new MultiContent(html, text), "<recipient address>"));
					// or send the email using fluent API
					service.send(new Email().
										subject("subject").
										content(new MultiContent(html, text)).
										to("<recipient address>"));
				}
			
			}
			```
			
			
			This sample shows how to provide both HTML content and text content following a template engine language. The sample shows the shorthand version that avoids specifying twice the path to the template. This sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlAndTextTemplateSample.java).
			
			<span class="highlight" data-lines="28,32,25-27" data-irrelevant-lines="1-10,15-23"></span>
			<span class="collapse" data-lines="1-10,15-23"></span>
			
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
					// send the email using constructor
					// Note that the extension of the template is not given. This version
					// automatically takes the provided path and adds the '.html' extension
					// for the HTML template and '.txt' for text template
					service.send(new Email("subject", new MultiTemplateContent("classpath:/template/thymeleaf/simple", new SimpleBean("foo", 42)), "<recipient address>"));
					// or send the email using fluent API
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


#### <a name="using-freemarker"/>Using FreeMarker

If you prefer FreeMarker as template engine, you just need to create your templates using FreeMarker (templates for FreeMarker are suffixed by `.ftl` extension).


##### Sending an email with both HTML and text

Content of the HTML template (simple.html.ftl):

```html
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
    </head>
    <body>
        <h1 class="title">${name}</h1>
        <p class="text">${value}</p>
    </body>
</html>
```

Content of the text template (simple.txt.ftl):

```
${name} ${value}
```

The Java code is the same as before, no difference at all. Ogham automatically discover your templates and understands that you are using FreeMarker template files.

<span class="highlight" data-irrelevant-lines="1-11,14-27,29,34"></span>
<span class="collapse" data-lines="1-11"></span>

```java
package fr.sii.ogham.sample.standard.template.freemarker;

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
		service.send(new Email("subject", new MultiTemplateContent("classpath:/template/freemarker/simple", new SimpleBean("foo", 42)), "<recipient address>"));
		// or using fluent API
		service.send(new Email()
						.subject("subject")
						.content(new MultiTemplateContent("classpath:/template/freemarker/simple", new SimpleBean("foo", 42)))
						.to("<recipient address>"));
	}

}
```

##### Mixing Thymeleaf and FreeMarker

			It is possible to mix templates in the same application. Even better, you can use a template engine that is better suited for HTML like Thymeleaf and
			FreeMarker that is better for textual version for the same email. Just write your templates with the engine you want.
			
			See samples to [ensure that Java code is still the same](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/HtmlAndTextMixedTemplateEnginesSample.java).
			
			Only the templates are different:
			
			- [HTML template using Thymeleaf](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/mixed/simple.html)
			- [Text template using FreeMarker](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/mixed/simple.txt.ftl)



### <a name="attachments"/>Attachments

			This sample shows how to send an email with attached file. The sample is available [here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/WithAttachmentSample.java)
			
			The first lines configure the properties that will be used by the sender.
			Then you must create the service. You can use the MessagingBuilder to help you to create the service.
			Finally, the last line sends the email. The specified email is really basic too. It only contains the subject, the textual content, the receiver address and the attachment file that is available in the classpath. You may use several attachments too. The sender address is automatically added to the email by the service based on configuration properties.
			
			
			<span class="highlight" data-lines="24,30" data-irrelevant-lines="1-10,14-23"></span>
			<span class="collapse" data-lines="1-10,14-23"></span>
			
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
					// send the email using constructor
					service.send(new Email("subject", "content of the email", "<recipient address>", new Attachment("classpath:/attachment/test.pdf")));
					// or send the email using fluent API
					service.send(new Email().
										subject("subject").
										content("content of the email").
										to("<recipient address>").
										attach(new Attachment("classpath:/attachment/test.pdf")));
				}
			
			}
			```
			

### <a name="full-sample"/>Full sample

This sample combines all features:

- Uses templates (HTML and text templates)
- HTML template includes page fragments
- HTML template references external CSS and images
- The subject is directly extracted from template
- The email is sent with one attachment


Here is the Java code ([available here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/email/FullSample.java)):

<span class="highlight" data-irrelevant-lines="1-13"></span>
<span class="collapse" data-lines="1-13"></span>

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

<span class="highlight" data-irrelevant-lines="4-11"></span>

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
#ogham.email.javamail.authenticator.username=<your gmail username>
#ogham.email.javamail.authenticator.password=<your gmail password>

# ogham additional properties
ogham.email.from=<sender email address>
ogham.email.template.path-prefix=/template/thymeleaf/email/
```

The HTML template content is [available here](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage/src/main/resources/template/thymeleaf/email/full.html). The content of the HTML is not displayed entirely. Just useful parts are shown here:

<span class="highlight" data-lines="4,8,13,17"></span>

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

<span class="highlight" data-lines="5,7-8"></span>

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


### <a name="spring-boot"/>Spring Boot

See [Spring integration](integration.html#integrate-with-spring-boot) to know how to use Ogham with Spring Boot.

Spring comes with very useful management of configuration properties (environment and profiles). Ogham module is able to use environment provided by Spring.

Add the following information in the application.properties (or according to profile, into the right configuration file):

```ini
mail.smtp.host=<your server host>
mail.smtp.port=<your server port>
ogham.email.from=<your gmail address>
```

#### REST web service

##### Basic email

To use Ogham in Spring, you can directly inject (autowire) it. Here is a full Spring Boot application serving one REST endpoint for sending email using Ogham ([sample available here](https://github.com/groupe-sii/ogham/blob/master/sample-spring-usage/src/main/java/fr/sii/ogham/sample/springboot/email/BasicSample.java)):

<span class="highlight" data-lines="31-32,38-43" data-irrelevant-lines="1-17,19"></span>
<span class="collapse" data-lines="1-17"></span>

```java
package fr.sii.ogham.sample.springboot.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource("application-email-basic.properties")	// just needed to be able to run the sample
public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(BasicSample.class, args);
	}
	
	@RestController
	public static class EmailController {
		// Messaging service is automatically created using Spring Boot features
		// The configuration can be set into application-email-basic.properties
		// The configuration files are stored into src/main/resources
		@Autowired
		MessagingService messagingService;
		
		@RequestMapping(value="api/email/basic", method=RequestMethod.POST)
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


##### Email with subject in templates

This sample show how to use messaging service using both HTML and text templates. The templates also directly contain subject. The sample is available [here](https://github.com/groupe-sii/ogham/blob/master/sample-spring-usage/src/main/java/fr/sii/ogham/sample/springboot/email/HtmlAndTextTemplateWithSubjectSample.java)

<span class="highlight" data-lines="34-35,41,43-45" data-irrelevant-lines="1-19,21"></span>
<span class="collapse" data-lines="1-19"></span>

```java
package fr.sii.ogham.sample.springboot.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

@SpringBootApplication
@PropertySource("application-email-template.properties")	// just needed to be able to run the sample
public class HtmlAndTextTemplateWithSubjectSample {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(HtmlAndTextTemplateWithSubjectSample.class, args);
	}
	
	@RestController
	public static class EmailController {
		// Messaging service is automatically created using Spring Boot features
		// The configuration can be set into application-email-template.properties
		// The configuration files are stored into src/main/resources
		// The configuration file set the prefix for templates into email folder available in src/main/resources
		@Autowired
		MessagingService messagingService;
		
		@RequestMapping(value="api/email/template", method=RequestMethod.POST)
		@ResponseStatus(HttpStatus.CREATED)
		public void sendEmail(@RequestParam("to") String to, @RequestParam("name") String name, @RequestParam("value") int value) throws MessagingException {
			// send the email
			messagingService.send(new Email(null, new MultiTemplateContent("register", new SimpleBean(name, value)), to));
			// or using fluent API
			messagingService.send(new Email().
									content(new MultiTemplateContent("register", new SimpleBean(name, value))).
									to(to));
		}
	}

}
```

Here is the content of the [HTML template](https://github.com/groupe-sii/ogham/blob/master/sample-spring-usage/src/main/resources/email/register.html):

<span class="highlight" data-lines="4"></span>

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>Subject of the mail</title>
        <meta charset="utf-8" />
    </head>
    <body>
        <h1 class="title" th:text="${name}"></h1>
        <p class="text" th:text="${value}"></p>
    </body>
</html>
```

The content of the [text template](https://github.com/groupe-sii/ogham/blob/master/sample-spring-usage/src/main/resources/email/register.txt):

<span class="highlight" data-lines="1"></span>

```txt
<html xmlns:th="http://www.thymeleaf.org" th:inline="text" th:remove="tag">Subject: Subject of the email
[[${name}]] [[${value}]]</html>
```

Finally, the content of the [property file](https://github.com/groupe-sii/ogham/blob/master/sample-spring-usage/src/main/resources/application-email-template.properties) is:


```ini
# ogham configuration for email
mail.smtp.host=<your server host>
mail.smtp.port=<your server port>
ogham.email.from=<your gmail address>
ogham.email.template.path-prefix=/email/
```

We have just added the template prefix. It tells Ogham where to look for email templates. All template names or relative paths provided to `TemplateContent` or  `MultiTemplateContent` are relative to this prefix. By default, Ogham look for templates in the classpath. See [template section](../config/templates.html) for more information about prefix and suffix.

