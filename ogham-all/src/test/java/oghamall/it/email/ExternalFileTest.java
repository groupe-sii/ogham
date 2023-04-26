package oghamall.it.email;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import mock.context.SimpleBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.*;

@LogTestInformation
public class ExternalFileTest {
	private MessagingService oghamService;
	
	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	
	@TempDir
	File temp;
	
	@BeforeEach
	public void setUp() throws IOException {
		// copy files
		Path folder = Paths.get(temp.getPath(), "template", "mixed", "source");
		folder.toFile().mkdirs();
		IOUtils.copy(resource("template/mixed/source/simple.html"), folder.resolve("simple.html").toFile());
		IOUtils.copy(resource("template/mixed/source/simple.txt.ftl"), folder.resolve("simple.txt.ftl").toFile());
		// prepare ogham
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties()
						.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
						.set("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()))
						.set("ogham.email.template.path-prefix", folder.getParent().getParent()+"/")
						.and()
					.and()
				.build();
	}

	@Test
	public void thymeleafHtmlFileFreemarkerTextFile() throws MessagingException, jakarta.mail.MessagingException, IOException {
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
