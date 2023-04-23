package oghamspringbootautoconfigure.it;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.IOException;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.icegreen.greenmail.junit4.GreenMailRule;

import fr.sii.ogham.testing.assertion.email.AssertEmail;
import fr.sii.ogham.testing.assertion.email.ExpectedContent;
import fr.sii.ogham.testing.assertion.email.ExpectedEmail;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.spring.GreenMailInitializer;
import fr.sii.ogham.testing.extension.spring.JsmppServerInitializer;
import jakarta.mail.MessagingException;
import mock.MockApplication;
import mock.context.NestedBean;
import mock.context.SimpleBean;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MockApplication.class, webEnvironment=RANDOM_PORT)
@ContextConfiguration(initializers = {GreenMailInitializer.class, JsmppServerInitializer.class})
@ActiveProfiles("ogham-only")
public class OghamAutoConfigurationOghamPropertiesOnlyTest {
	private static final String BASE_URL = "/api/email";
	private static final String SIMPLE_URL = BASE_URL + "/simple";
	private static final String THYMELEAF_URL = BASE_URL + "/thymeleaf";
	private static final String FREEMARKER_URL = BASE_URL + "/freemarker";

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	@Autowired
	public GreenMailRule greenMail;

	@Value("${local.server.port}")
	int port;

	@Test
	public void simple() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath(SIMPLE_URL)
				.scheme("http")
				.host("localhost")
				.port(port)
				.queryParam("subject", "test")
				.queryParam("to", "recipient@foo.bar");
		// @formatter:on
		ResponseEntity<Void> response = rt.postForEntity(builder.toUriString(), new HttpEntity<>("test content"), Void.class);
		assertEquals("HTTP status should be 201: Created", HttpStatus.CREATED, response.getStatusCode());
		AssertEmail.assertEquals(new ExpectedEmail("test", "test content", "ogham.test@foo.bar", "recipient@foo.bar"), greenMail.getReceivedMessages());
	}

	@Test
	public void thymeleaf() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath(THYMELEAF_URL)
				.scheme("http")
				.host("localhost")
				.port(port)
				.queryParam("subject", "test")
				.queryParam("template", "register-thymeleaf")
				.queryParam("to", "recipient@foo.bar");
		RequestEntity<NestedBean> request = RequestEntity.
				post(builder.build().toUri()).
				contentType(MediaType.APPLICATION_JSON).
				body(new NestedBean(new SimpleBean("foo", 42)));
		// @formatter:onogham
		ResponseEntity<Void> response = rt.exchange(request, Void.class);
		assertEquals("HTTP status should be 201: Created", HttpStatus.CREATED, response.getStatusCode());
		AssertEmail.assertEquals(
				new ExpectedEmail("test", new ExpectedContent(getClass().getResourceAsStream("/expected/ogham/register_foo_42.html"), "text/html.*"), "ogham.test@foo.bar", "recipient@foo.bar"),
				greenMail.getReceivedMessages());
	}

	@Test
	public void freemarker() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		// @formatter:off
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath(FREEMARKER_URL)
				.scheme("http")
				.host("localhost")
				.port(port)
				.queryParam("subject", "test")
				.queryParam("template", "register-freemarker.html")
				.queryParam("to", "recipient@foo.bar");
		RequestEntity<NestedBean> request = RequestEntity.
						post(builder.build().toUri()).
						contentType(MediaType.APPLICATION_JSON).
						body(new NestedBean(new SimpleBean("foo", 42)));
		// @formatter:on

		ResponseEntity<Void> response = rt.exchange(request, Void.class);
		assertEquals("HTTP status should be 201: Created", HttpStatus.CREATED, response.getStatusCode());
		AssertEmail.assertEquals(
				new ExpectedEmail("test", new ExpectedContent(getClass().getResourceAsStream("/expected/ogham/register_foo_42.html"), "text/html.*"), "ogham.test@foo.bar", "recipient@foo.bar"),
				greenMail.getReceivedMessages());
	}
}
