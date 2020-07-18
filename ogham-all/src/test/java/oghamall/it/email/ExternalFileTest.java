package oghamall.it.email;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.icegreen.greenmail.junit.GreenMailRule;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.greenmail.RandomPortGreenMailRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import mock.context.SimpleBean;

public class ExternalFileTest {
	private MessagingService oghamService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new RandomPortGreenMailRule();
	
	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();
	
	@Before
	public void setUp() throws IOException {
		// copy files
		File folder = temp.newFolder("template", "mixed", "source");
		IOUtils.copy(resource("template/mixed/source/simple.html"), folder.toPath().resolve("simple.html").toFile());
		IOUtils.copy(resource("template/mixed/source/simple.txt.ftl"), folder.toPath().resolve("simple.txt.ftl").toFile());
		// prepare ogham
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties()
						.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
						.set("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()))
						.set("ogham.email.template.path-prefix", folder.getParentFile().getParent()+"/")
						.and()
					.and()
				.build();
	}

	@Test
	public void thymeleafHtmlFileFreemarkerTextFile() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
							.subject("Template")
							.content(new MultiTemplateContent("file:mixed/source/simple", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/mixed/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/mixed/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}


}
