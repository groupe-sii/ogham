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
public class EmailDifferentPrefixesTest {
	private MessagingService oghamService;
	
	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	
	@TempDir
	File temp;
	
	@BeforeEach
	public void setUp() throws IOException {
		// copy files
		Path folder = Paths.get(temp.getPath(), "template", "mixed", "source");
		File thymeleafFolder = folder.resolve("thymeleaf").toFile();
		thymeleafFolder.mkdirs();
		File freemarkerFolder = folder.resolve("freemarker").toFile();
		freemarkerFolder.mkdirs();
		IOUtils.copy(resource("template/thymeleaf/source/simple.html"), thymeleafFolder.toPath().resolve("simple.html").toFile());
		IOUtils.copy(resource("template/freemarker/source/simple.txt.ftl"), freemarkerFolder.toPath().resolve("simple.txt.ftl").toFile());
		// prepare ogham
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties()
						.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
						.set("mail.smtp.port", greenMail.getSmtp().getPort())
						.set("ogham.email.thymeleaf.classpath.path-prefix", "/template/thymeleaf/source/")
						.set("ogham.email.thymeleaf.file.path-prefix", thymeleafFolder.getAbsolutePath()+"/")
						.set("ogham.email.freemarker.classpath.path-prefix", "/template/freemarker/source/")
						.set("ogham.email.freemarker.file.path-prefix", freemarkerFolder.getAbsolutePath()+"/")
						.and()
					.and()
				.build();
	}

	@Test
	public void thymeleafHtmlFreemarkerText() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
							.subject("Content from files")
							.content(new MultiTemplateContent("file:simple", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Content from files"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
		// @formatter:off
		oghamService.send(new Email()
							.subject("Content from classpath")
							.content(new MultiTemplateContent("simple", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(2))
			.message(1)
				.subject(is("Content from classpath"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}


}
