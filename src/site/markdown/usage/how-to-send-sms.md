## SMS

The samples are available in the [sample-standard-usage sub-project](https://github.com/groupe-sii/ogham/tree/master/sample-standard-usage).

All samples shown bellow are using SMPP for sending SMS. See [select implementation](../config/select-implementation.html) to know other ways to send SMS.

### Basic

The [SMPP](https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer) protocol is the standard way to send SMS. This sample defines two properties mandatory (system ID and password) by this protocol in order to use it. This sample is available [here](https://github.com/groupe-sii/ogham/blob/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/sms/BasicSample.java).


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


### Using a template

Sending SMS with a templated content is exactly the same as sending email with a templated content. The sample is available [here](https://github.com/groupe-sii/ogham/blob/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/sms/TemplateSample.java).

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
	}

}
```

Or using fluent API:

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
		service.send(new Sms().
							content(new TemplateContent("classpath:/template/thymeleaf/simple.txt", new SimpleBean("foo", 42))).
							to("<recipient phone number>"));
	}

}
```


### Sending a long SMS

As you may know, SMS stands for Short Message Service. Basically, the messages are limited to a maximum of 160 characters (depends of char encoding). If needed, the library will split your messages into several parts the right way to be recomposed by clients later. So the code doesn't change at all (the sample is available [here](https://github.com/groupe-sii/ogham/blob/master/sample-standard-usage/src/main/java/fr/sii/ogham/sample/standard/sms/LongMessageSample.java)):

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
		// send the sms
		String longMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		service.send(new Sms(longMessage, "<recipient phone number>"));
	}

}

```