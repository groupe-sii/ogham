package oghamspringbootautoconfigure.it;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.testing.assertion.email.AssertEmail;
import fr.sii.ogham.testing.assertion.email.ExpectedContent;
import fr.sii.ogham.testing.assertion.email.ExpectedEmail;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.spring.GreenMailInitializer;
import fr.sii.ogham.testing.extension.spring.JsmppServerInitializer;
import mock.MockApplication;
import mock.context.NestedBean;
import mock.context.SimpleBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@LogTestInformation
@SpringBootTest(classes = MockApplication.class, webEnvironment=RANDOM_PORT)
@ContextConfiguration(initializers = {GreenMailInitializer.class, JsmppServerInitializer.class})
@ActiveProfiles("spring-only")
public class OghamAutoConfigurationSpringPropertiesOnlyTest {
	private static final String BASE_URL = "/api/email";
	private static final String SIMPLE_URL = BASE_URL + "/simple";
	private static final String THYMELEAF_URL = BASE_URL + "/thymeleaf";
	private static final String FREEMARKER_URL = BASE_URL + "/freemarker";


	@RegisterExtension
	@Autowired
	public GreenMailExtension greenMail;

	@Value("${local.server.port}")
	int port;

	@Test
	public void simple() {
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
		assertEquals(HttpStatus.CREATED, response.getStatusCode(), "HTTP status should be 201: Created");
		AssertEmail.assertEquals(new ExpectedEmail("test", "test content", "spring.test@foo.bar", "recipient@foo.bar"), greenMail.getReceivedMessages());
	}

	@Test
	public void thymeleaf() throws IOException {
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
		// @formatter:on
		ResponseEntity<Void> response = rt.exchange(request, Void.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode(), "HTTP status should be 201: Created");
		AssertEmail.assertEquals(
				new ExpectedEmail("test", new ExpectedContent(getClass().getResourceAsStream("/expected/spring/register_foo_42.html"), "text/html.*"), "spring.test@foo.bar", "recipient@foo.bar"),
				greenMail.getReceivedMessages());
	}

	@Test
	public void freemarker() throws IOException {
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
		assertEquals(HttpStatus.CREATED, response.getStatusCode(), "HTTP status should be 201: Created");
		AssertEmail.assertEquals(
				new ExpectedEmail("test", new ExpectedContent(getClass().getResourceAsStream("/expected/spring/register_foo_42.html"), "text/html.*"), "spring.test@foo.bar", "recipient@foo.bar"),
				greenMail.getReceivedMessages());
	}
}
