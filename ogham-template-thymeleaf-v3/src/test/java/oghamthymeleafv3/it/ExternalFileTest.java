package oghamthymeleafv3.it;

import static fr.sii.ogham.core.util.IOUtils.copy;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static fr.sii.ogham.testing.assertion.template.AssertTemplate.assertEquals;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.thymeleaf.common.exception.TemplateResolutionException;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3EmailBuilder;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import mock.context.SimpleBean;

public class ExternalFileTest {
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	@Rule public final TemporaryFolder temp = new TemporaryFolder();
	private File folder;
	
	ThymeleafV3EmailBuilder builder;
	
	@Before
	public void setup() throws IOException {
		folder = temp.newFolder("template", "thymeleaf", "source");
		copy(resource("/template/thymeleaf/source/simple.html"), folder.toPath().resolve("found.html").toFile());
		copy(resource("/template/thymeleaf/source/simple.html"), folder.toPath().resolve("unreadable.html").toFile());
		copy(resource("/template/thymeleaf/source/simple.html"), folder.toPath().resolve("updated.html").toFile());
		folder.toPath().resolve("unreadable.html").toFile().setReadable(false);
		// prepare parser
		builder = new ThymeleafV3EmailBuilder();
		builder
			.environment()
				.systemProperties()
				.and()
			.classpath()
				.lookup("classpath:", "")
				.and()
			.file()
				.lookup("file:")
				.pathPrefix(folder.getParent()+"/")
				.and()
			.cache(true);
	}
	
	@Test
	public void fileFound() throws ParseException, IOException {
		TemplateParser parser = builder.build();
		
		Content content = parser.parse(new UnresolvedPath("file:source/found.html"), new BeanContext(new SimpleBean("foo", 42)));
		
		assertNotNull("content should not be null", content);
		assertTrue("content should be MayHaveStringContent", content instanceof MayHaveStringContent);
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content);
	}
	
	@Test
	public void fileNotFound() throws ParseException {
		TemplateParser parser = builder.build();
		
		ParseException e = assertThrows("should throw", ParseException.class, () -> {
			parser.parse(new UnresolvedPath("file:unexisting.html"), new BeanContext(new SimpleBean("foo", 42)));
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(TemplateProcessingException.class));
		assertThat("should indicate cause", e.getCause(), hasAnyCause(TemplateResolutionException.class, hasMessage(containsString("Failed to find template file:unexisting.html"))));
	}

	@Test
	public void fileUnreadable() throws ParseException {
		TemplateParser parser = builder.build();

		ParseException e = assertThrows("should throw", ParseException.class, () -> {
			parser.parse(new UnresolvedPath("file:source/unreadable.html"), new BeanContext(new SimpleBean("foo", 42)));
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(TemplateInputException.class));
		assertThat("should indicate cause", e.getCause(), hasAnyCause(FileNotFoundException.class, hasMessage(containsString("unreadable.html"))));
	}
	
	@Test
	public void fileUpdatedButUseThymeleafCache() throws ParseException, IOException {
		TemplateParser parser = builder.build();

		Content content1 = parser.parse(new UnresolvedPath("file:source/updated.html"), new BeanContext(new SimpleBean("foo", 42)));
		copy(resource("/template/thymeleaf/source/simple.txt"), folder.toPath().resolve("updated.html").toFile());
		Content content2 = parser.parse(new UnresolvedPath("file:source/updated.html"), new BeanContext(new SimpleBean("foo", 42)));
		
		assertNotNull("content should not be null", content1);
		assertTrue("content should be MayHaveStringContent", content1 instanceof MayHaveStringContent);
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content1);
		
		assertNotNull("content should not be null", content2);
		assertTrue("content should be MayHaveStringContent", content2 instanceof MayHaveStringContent);
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content2);
	}
	
	@Test
	public void fileUpdatedWithoutThymeleafCache() throws ParseException, IOException {
		builder.cache(false);
		TemplateParser parser = builder.build();

		Content content1 = parser.parse(new UnresolvedPath("file:source/updated.html"), new BeanContext(new SimpleBean("foo", 42)));
		copy(resource("/template/thymeleaf/source/simple.txt"), folder.toPath().resolve("updated.html").toFile());
		Content content2 = parser.parse(new UnresolvedPath("file:source/updated.html"), new BeanContext(new SimpleBean("foo", 42)));
		
		assertNotNull("content should not be null", content1);
		assertTrue("content should be MayHaveStringContent", content1 instanceof MayHaveStringContent);
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content1);
		
		assertNotNull("content should not be null", content2);
		assertTrue("content should be MayHaveStringContent", content2 instanceof MayHaveStringContent);
		assertEquals("/template/thymeleaf/expected/simple_foo_42.txt", content2);
	}
}
