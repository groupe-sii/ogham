package oghamspringbootv3autoconfigure.it;

import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.exceptions.TemplateProcessingException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.icegreen.greenmail.junit4.GreenMailRule;

import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v3.autoconfigure.OghamSpringBoot3AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerRule;
import fr.sii.ogham.testing.extension.spring.GreenMailInitializer;
import fr.sii.ogham.testing.extension.spring.JsmppServerInitializer;
import mock.context.SimpleBean;
import oghamspringbootv3autoconfigure.it.SpringWebBeanResolutionTest.TestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {OghamSpringBoot3AutoConfiguration.class, TestApplication.class}, 
				webEnvironment=RANDOM_PORT,
				properties= {
						"spring.mail.host=127.0.0.1", 
						"spring.mail.port=${greenmail.smtp.port}",
						"mail.smtp.from=spring.test@foo.bar",
						"ogham.sms.smpp.host=localhost",
						"ogham.sms.smpp.port=${jsmpp.server.port}"})
@ContextConfiguration(initializers = {GreenMailInitializer.class, JsmppServerInitializer.class})
public class SpringWebBeanResolutionTest {
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule @Autowired public GreenMailRule greenMail;
	@Rule @Autowired public SmppServerRule<SubmitSm> smppServer;


	@Value("${local.server.port}")
	int port;

	@Test
	public void emailUsingThymeleafTemplateShouldResolveBeansAndUrls() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("email")
				.scheme("http")
				.host("localhost")
				.port(port);
		// @formatter:on
		rt.postForEntity(builder.toUriString(), new HttpEntity<>(""), Void.class);

		OghamAssertions.assertThat(greenMail)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.body()
						.contentAsString(isIdenticalHtml(resourceAsString("/thymeleaf/expected/web-beans-and-urls-resolution.html")));
	}

	@Test
	public void smsUsingThymeleafTemplateShouldResolveBeansAndUrls() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("sms")
				.scheme("http")
				.host("localhost")
				.port(port);
		// @formatter:on
		rt.postForEntity(builder.toUriString(), new HttpEntity<>(""), Void.class);

		OghamAssertions.assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is(resourceAsString("/thymeleaf/expected/web-beans-and-urls-resolution.txt")));
	}

	
	@Test
	public void emailUsingThymeleafTemplateInAsyncMethodShouldResolveBeans() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("async/email")
				.scheme("http")
				.host("localhost")
				.port(port)
				.queryParam("template", "web-beans-resolution");
		// @formatter:on
		rt.postForEntity(builder.toUriString(), new HttpEntity<>(""), Void.class);

		await().atMost(5, SECONDS).until(() -> greenMail.getReceivedMessages().length > 0);

		OghamAssertions.assertThat(greenMail)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.body()
						.contentAsString(isIdenticalHtml(resourceAsString("/thymeleaf/expected/web-beans-resolution.html")));
	}

	@Test
	public void smsUsingThymeleafTemplateInAsyncMethodShouldResolveBeans() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("async/sms")
				.scheme("http")
				.host("localhost")
				.port(port)
				.queryParam("template", "web-beans-resolution");
		// @formatter:on
		rt.postForEntity(builder.toUriString(), new HttpEntity<>(""), Void.class);
		
		await().atMost(5, SECONDS).until(() -> smppServer.getReceivedMessages().size() > 0);

		OghamAssertions.assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is(resourceAsString("/thymeleaf/expected/web-beans-resolution.txt")));
	}
	
	@Test
	public void emailUsingThymeleafTemplateInAsyncMethodCantResolveUrls() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("async/email")
				.scheme("http")
				.host("localhost")
				.port(port)
				.queryParam("template", "web-beans-and-urls-resolution");
		// @formatter:on
		rt.postForEntity(builder.toUriString(), new HttpEntity<>(""), Void.class);

		await().atMost(5, SECONDS).until(() -> hasError());

		OghamAssertions.assertThat(greenMail)
			.receivedMessages()
				.count(is(0));
		ErrorDto error = getError();
		assertThat(error.getType()).isEqualTo(MessageNotSentException.class.getSimpleName());
		ErrorDto cause = getRootCause(error.getCause());
		assertThat(cause.getType()).isEqualTo(TemplateProcessingException.class.getSimpleName());
		assertThat(cause.getMessage()).contains("Link base \"/fake/resources/foo.js\" cannot be context relative (/...) unless the context used for executing the engine implements the org.thymeleaf.context.IWebContext");
	}

	@Test
	public void smsUsingThymeleafTemplateInAsyncMethodCantResolveUrls() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("async/sms")
				.scheme("http")
				.host("localhost")
				.port(port)
				.queryParam("template", "web-beans-and-urls-resolution");
		// @formatter:on
		rt.postForEntity(builder.toUriString(), new HttpEntity<>(""), Void.class);

		await().atMost(5, SECONDS).until(() -> hasError());

		OghamAssertions.assertThat(smppServer)
			.receivedMessages()
				.count(is(0));
		ErrorDto error = getError();
		assertThat(error.getType()).isEqualTo(MessageNotSentException.class.getSimpleName());
		ErrorDto cause = getRootCause(error.getCause());
		assertThat(cause.getType()).isEqualTo(TemplateProcessingException.class.getSimpleName());
		assertThat(cause.getMessage()).contains("Link base \"/fake/resources/foo.js\" cannot be context relative (/...) unless the context used for executing the engine implements the org.thymeleaf.context.IWebContext");
	}

	

	private ErrorDto getRootCause(ErrorDto cause) {
		while (cause != null) {
			if (cause.getCause() == null) {
				return cause;
			}
			cause = cause.getCause();
		}
		return null;
	}

	private boolean hasError() {
		return getError() != null;
	}

	private ErrorDto getError() {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("async/error")
				.scheme("http")
				.host("localhost")
				.port(port);
		// @formatter:on
		return rt.postForEntity(builder.toUriString(), new HttpEntity<>(""), ErrorDto.class).getBody();		
	}
	
	@SpringBootApplication
	@EnableAsync
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}

		@Service("fakeService")
		public static class FakeService {
			public String hello(String name) {
				return "hello " + name;
			}
		}
		
		@RestController("fakeController")
		public static class FakeController {
			@RequestMapping("fake/hello/{name}")
			public String hello(@PathVariable String name) {
				return "hello " + name;
			}
			@RequestMapping("fake/resources/{file}")
			public String resource(@PathVariable String file) {
				return "resource: "+file;
			}
		}
		
		@RestController
		public static class MessagingController {
			@Autowired MessagingService messagingService;
			private Exception ex;
			
			@RequestMapping("email")
			public void email() throws MessagingException {
				messagingService.send(new Email()
						.from("foo@yopmail.com")
						.to("bar@yopmail.com")
						.content(new TemplateContent("/thymeleaf/source/web-beans-and-urls-resolution.html", new SimpleBean("world", 0))));
			}
			
			@RequestMapping("sms")
			public void sms() throws MessagingException {
				messagingService.send(new Sms()
						.from("+33102030405")
						.to("+33123456789")
						.content(new TemplateContent("/thymeleaf/source/web-beans-and-urls-resolution.txt", new SimpleBean("world", 0))));
			}
			
			@RequestMapping("async/email")
			@Async
			public void emailAsync(@RequestParam("template") String templateName) throws MessagingException {
				try {
					messagingService.send(new Email()
							.from("foo@yopmail.com")
							.to("bar@yopmail.com")
							.content(new TemplateContent("/thymeleaf/source/" + templateName + ".html", new SimpleBean("world", 0))));
				} catch(Exception e) {
					ex = e;
				}
			}
			
			@RequestMapping("async/sms")
			@Async
			public void smsAsync(@RequestParam("template") String templateName) throws MessagingException {
				try {
					messagingService.send(new Sms()
							.from("+33102030405")
							.to("+33123456789")
							.content(new TemplateContent("/thymeleaf/source/" + templateName + ".txt", new SimpleBean("world", 0))));
				} catch(Exception e) {
					ex = e;
				}
			}
			
			@RequestMapping("async/error")
			public ErrorDto getError() {
				return ex == null ? null : new ErrorDto(ex);
			}
		}
	}

	public static class ErrorDto {
		private final String type;
		private final String message;
		private final ErrorDto cause;
		
		@JsonCreator
		public ErrorDto(@JsonProperty("type") String type, @JsonProperty("message") String message, @JsonProperty("cause") ErrorDto cause) {
			super();
			this.type = type;
			this.message = message;
			this.cause = cause;
		}
		public ErrorDto(Throwable e) {
			super();
			this.type = e.getClass().getSimpleName();
			this.message = e.getMessage();
			if (e.getCause() != null) {
				this.cause = new ErrorDto(e.getCause());
			} else {
				this.cause = null;
			}
		}
		public String getType() {
			return type;
		}
		public String getMessage() {
			return message;
		}
		public ErrorDto getCause() {
			return cause;
		}
	}

}
