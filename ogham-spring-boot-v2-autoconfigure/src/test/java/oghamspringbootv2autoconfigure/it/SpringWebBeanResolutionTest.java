package oghamspringbootv2autoconfigure.it;

import static fr.sii.ogham.testing.assertion.OghamMatchers.isSimilarHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
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
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v2.autoconfigure.OghamSpringBoot2AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import mock.context.SimpleBean;
import oghamspringbootv2autoconfigure.it.SpringWebBeanResolutionTest.TestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {OghamSpringBoot2AutoConfiguration.class, TestApplication.class}, 
				webEnvironment=RANDOM_PORT,
				properties= {
						"spring.mail.host=127.0.0.1", 
						"spring.mail.port=3025",
						"mail.smtp.from=spring.test@foo.bar",
						"ogham.sms.smpp.host=localhost",
						"ogham.sms.smpp.port=2775"})
public class SpringWebBeanResolutionTest {
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule(2775);


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
						.contentAsString(isSimilarHtml(resourceAsString("/thymeleaf/expected/web-bean-resolution.html")));
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
				.content(is(resourceAsString("/thymeleaf/expected/web-bean-resolution.txt")));
	}


	
	@SpringBootApplication
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
			
			@RequestMapping("email")
			public void email() throws MessagingException {
				messagingService.send(new Email()
						.from("foo@yopmail.com")
						.to("bar@yopmail.com")
						.content(new TemplateContent("/thymeleaf/source/web-bean-resolution.html", new SimpleBean("world", 0))));
			}
			
			@RequestMapping("sms")
			public void sms() throws MessagingException {
				messagingService.send(new Sms()
						.from("+33102030405")
						.to("+33123456789")
						.content(new TemplateContent("/thymeleaf/source/web-bean-resolution.txt", new SimpleBean("world", 0))));
			}
		}

	}

}
