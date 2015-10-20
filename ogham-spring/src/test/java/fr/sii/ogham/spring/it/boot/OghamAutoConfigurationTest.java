package fr.sii.ogham.spring.it.boot;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.mail.MessagingException;

import org.apache.tomcat.websocket.server.UriTemplate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.helper.email.AssertEmail;
import fr.sii.ogham.helper.email.ExpectedContent;
import fr.sii.ogham.helper.email.ExpectedEmail;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.mock.context.NestedBean;
import fr.sii.ogham.mock.context.SimpleBean;
import fr.sii.ogham.spring.mock.MockApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockApplication.class)
@WebIntegrationTest("server.port=0")
public class OghamAutoConfigurationTest {
	private static final String BASE_URL = "/api/email";
	
	private static final String SIMPLE_URL = BASE_URL+"/simple";
	
	private static final String THYMELEAF_URL = BASE_URL+"/thymeleaf";
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Value("${local.server.port}")
	int port;
	
	@Test
	public void simple() throws MessagingException {
		RestTemplate rt = new RestTemplate();
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath(SIMPLE_URL)
				.scheme("http")
				.host("localhost")
				.port(port)
				.queryParam("subject", "test")
				.queryParam("to", "recipient@foo.bar");
		ResponseEntity<Void> response = rt.postForEntity(builder.toUriString(), new HttpEntity<String>("test content"), Void.class);
		assertEquals("HTTP status should be 201: Created", HttpStatus.CREATED, response.getStatusCode());
		AssertEmail.assertEquals(new ExpectedEmail("test", "test content", "spring.test@foo.bar", "recipient@foo.bar"), greenMail.getReceivedMessages());
	}
	
	@Test
	public void thymeleaf() throws MessagingException, IOException {
		RestTemplate rt = new RestTemplate();
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath(THYMELEAF_URL)
				.scheme("http")
				.host("localhost")
				.port(port)
				.queryParam("subject", "test")
				.queryParam("template", "register.html")
				.queryParam("to", "recipient@foo.bar");
		RequestEntity<NestedBean> request = RequestEntity.
				post(builder.build().toUri()).
				contentType(MediaType.APPLICATION_JSON).
				body(new NestedBean(new SimpleBean("foo", 42)));
		ResponseEntity<Void> response = rt.exchange(request, Void.class);
		assertEquals("HTTP status should be 201: Created", HttpStatus.CREATED, response.getStatusCode());
		AssertEmail.assertEquals(new ExpectedEmail("test", new ExpectedContent(getClass().getResourceAsStream("/expected/email/register_foo_42.html"), "text/html.*"), "spring.test@foo.bar", "recipient@foo.bar"), greenMail.getReceivedMessages());
	}
}
