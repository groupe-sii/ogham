package oghamall.it.resolver;

import static fr.sii.ogham.email.attachment.ContentDisposition.INLINE;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit4.GreenMailRule;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.id.generator.SequentialIdGenerator;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.core.util.classpath.SimpleClasspathHelper;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailRule;
import fr.sii.ogham.testing.mock.classloader.FilterableClassLoader;
import mock.context.SimpleBean;

public class ThymeleafRelativeResourcesTest {

	private MessagingService oghamService;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final GreenMailRule greenMail = new RandomPortGreenMailRule();

	// TODO: test prefix alone
	// TODO: test suffix alone
	// TODO: test prefix with partial path
	public void setup(Property... specificProps) throws IOException {
		// disable freemarker to be sure to use thymeleaf
		disableFreemarker();
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		additionalProps.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));
		if(specificProps != null) {
			for(Property prop : specificProps) {
				additionalProps.setProperty(prop.getKey(), prop.getValue());
			}
		}
		oghamService = MessagingBuilder.standard()
				.email()
				.images()
					.inline()
						.attach()
							.cid()
								.generator(new SequentialIdGenerator(true))
								.and().and().and().and().and()
				.environment()
					.properties("/application.properties")
					.properties(additionalProps)
					.and()
				.build();
	}

	@Test
	public void relativeToAbsolutePath() throws MessagingException, javax.mail.MessagingException, IOException {
		setup();
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new TemplateContent("classpath:/template/thymeleaf/source/relative_resources.html", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/resources_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(hasSize(5))
				.attachment("h1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/h1.gif")))
					.disposition(is(INLINE)).and()
				.attachment("fb.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/fb.gif")))
					.disposition(is(INLINE)).and()
				.attachment("left.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/left.gif")))
					.disposition(is(INLINE)).and()
				.attachment("right1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/right1.gif")))
					.disposition(is(INLINE)).and()
				.attachment("tw.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/tw.gif")))
					.disposition(is(INLINE));
		// @formatter:on
	}

	@Test
	public void relativeToPrefixAndSuffix() throws MessagingException, javax.mail.MessagingException, IOException {
		setup(new Property("ogham.email.thymeleaf.path-prefix", "/template/thymeleaf/source/"), new Property("ogham.email.thymeleaf.path-suffix", ".html"));
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new TemplateContent("relative_resources", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/resources_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(hasSize(5))
				.attachment("h1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/h1.gif")))
					.disposition(is(INLINE)).and()
				.attachment("fb.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/fb.gif")))
					.disposition(is(INLINE)).and()
				.attachment("left.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/left.gif")))
					.disposition(is(INLINE)).and()
				.attachment("right1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/right1.gif")))
					.disposition(is(INLINE)).and()
				.attachment("tw.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/tw.gif")))
					.disposition(is(INLINE));
		// @formatter:on
	}


	@Test
	public void relativeToPrefixSuffixAndPath() throws MessagingException, javax.mail.MessagingException, IOException {
		setup(new Property("ogham.email.thymeleaf.path-prefix", "/template/"), new Property("ogham.email.thymeleaf.path-suffix", ".html"));
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new TemplateContent("thymeleaf/source/relative_resources", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/resources_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(hasSize(5))
				.attachment("h1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/h1.gif")))
					.disposition(is(INLINE)).and()
				.attachment("fb.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/fb.gif")))
					.disposition(is(INLINE)).and()
				.attachment("left.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/left.gif")))
					.disposition(is(INLINE)).and()
				.attachment("right1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/right1.gif")))
					.disposition(is(INLINE)).and()
				.attachment("tw.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/tw.gif")))
					.disposition(is(INLINE));
		// @formatter:on
	}


	@Test
	public void relativeToMultiTemplateName() throws MessagingException, javax.mail.MessagingException, IOException {
		setup(new Property("ogham.email.thymeleaf.path-prefix", "/template/thymeleaf/source/"));
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new MultiTemplateContent("relative_resources", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/resources_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/resources_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(hasSize(5))
				.attachment("h1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/h1.gif")))
					.disposition(is(INLINE)).and()
				.attachment("fb.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/fb.gif")))
					.disposition(is(INLINE)).and()
				.attachment("left.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/left.gif")))
					.disposition(is(INLINE)).and()
				.attachment("right1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/right1.gif")))
					.disposition(is(INLINE)).and()
				.attachment("tw.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/tw.gif")))
					.disposition(is(INLINE));
		// @formatter:on
	}
	
	@After
	public void reset() {
		ClasspathUtils.reset();
	}


	private void disableFreemarker() {
		SimpleClasspathHelper helper = new SimpleClasspathHelper();
		helper.setClassLoader(new FilterableClassLoader(getClass().getClassLoader(), c -> !c.equals("freemarker.template.Configuration")));
		ClasspathUtils.setHelper(helper);
	}

	private static class Property {
		private final String key;
		private final String value;
		public Property(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}
		public String getKey() {
			return key;
		}
		public String getValue() {
			return value;
		}
	}

}
