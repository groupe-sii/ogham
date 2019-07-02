package fr.sii.ogham.it.email;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.isSimilarHtml;
import static fr.sii.ogham.assertion.OghamAssertions.resource;
import static fr.sii.ogham.assertion.OghamAssertions.resourceAsString;
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
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.mock.context.SimpleBean;

public class EmailDifferentPrefixesTest {
	private MessagingService oghamService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();
	
	@Before
	public void setUp() throws IOException {
		// copy files
		File folder = temp.newFolder("template", "mixed", "source");
		File thymeleafFolder = folder.toPath().resolve("thymeleaf").toFile();
		thymeleafFolder.mkdirs();
		File freemarkerFolder = folder.toPath().resolve("freemarker").toFile();
		freemarkerFolder.mkdirs();
		IOUtils.copy(resource("template/thymeleaf/source/simple.html"), thymeleafFolder.toPath().resolve("simple.html").toFile());
		IOUtils.copy(resource("template/freemarker/source/simple.txt.ftl"), freemarkerFolder.toPath().resolve("simple.txt.ftl").toFile());
		// prepare ogham
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties()
						.set("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress())
						.set("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()))
						.set("ogham.email.thymeleaf.classpath.path-prefix", "/template/thymeleaf/source/")
						.set("ogham.email.thymeleaf.file.path-prefix", thymeleafFolder.getAbsolutePath()+"/")
						.set("ogham.email.freemarker.classpath.path-prefix", "/template/freemarker/source/")
						.set("ogham.email.freemarker.file.path-prefix", freemarkerFolder.getAbsolutePath()+"/")
						.and()
					.and()
				.build();
	}

	@Test
	public void thymeleafHtmlFreemarkerText() throws MessagingException, javax.mail.MessagingException, IOException {
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
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isSimilarHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.txt")))
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
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isSimilarHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}


}
