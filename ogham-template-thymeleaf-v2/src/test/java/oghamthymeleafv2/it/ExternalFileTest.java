package oghamthymeleafv2.it;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2EmailBuilder;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import mock.context.SimpleBean;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.thymeleaf.exceptions.TemplateInputException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static fr.sii.ogham.core.util.IOUtils.copy;
import static fr.sii.ogham.testing.assertion.template.AssertTemplate.assertEquals;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;

@LogTestInformation
public class ExternalFileTest {
	@TempDir File temp;
	private File folder;
	
	ThymeleafV2EmailBuilder builder;
	
	@BeforeEach
	public void setup() throws IOException {
		folder = Paths.get(temp.getPath(), "template", "thymeleaf", "source").toFile();
		folder.mkdirs();
		copy(resource("/template/thymeleaf/source/simple.html"), folder.toPath().resolve("found.html").toFile());
		copy(resource("/template/thymeleaf/source/simple.html"), folder.toPath().resolve("unreadable.html").toFile());
		copy(resource("/template/thymeleaf/source/simple.html"), folder.toPath().resolve("updated.html").toFile());
		folder.toPath().resolve("unreadable.html").toFile().setReadable(false);
		// prepare parser
		builder = new ThymeleafV2EmailBuilder();
		builder
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

		assertNotNull(content, "content should not be null");
		assertTrue(content instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content);
	}
	
	@Test
	public void fileNotFound() throws ParseException {
		TemplateParser parser = builder.build();

		ParseException e = assertThrows(ParseException.class, () -> {
			parser.parse(new UnresolvedPath("file:unexisting.html"), new BeanContext(new SimpleBean("foo", 42)));
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(TemplateInputException.class));
	}

	@Test
	public void fileUnreadable() throws ParseException {
		Assumptions.assumeFalse(isWindows(), "File.setReadable has no effect on Windows");
		TemplateParser parser = builder.build();

		ParseException e = assertThrows(ParseException.class, () -> {
			parser.parse(new UnresolvedPath("file:source/unreadable.html"), new BeanContext(new SimpleBean("foo", 42)));
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(TemplateInputException.class));
	}
	
	@Test
	public void fileUpdatedButUseThymeleafCache() throws ParseException, IOException {
		TemplateParser parser = builder.build();
		
		Content content1 = parser.parse(new UnresolvedPath("file:source/updated.html"), new BeanContext(new SimpleBean("foo", 42)));
		copy(resource("/template/thymeleaf/source/simple.txt"), folder.toPath().resolve("updated.html").toFile());
		Content content2 = parser.parse(new UnresolvedPath("file:source/updated.html"), new BeanContext(new SimpleBean("foo", 42)));
		
		assertNotNull(content1, "content should not be null");
		assertTrue(content1 instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content1);
		
		assertNotNull(content2, "content should not be null");
		assertTrue(content2 instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content2);
	}
	
	@Test
	public void fileUpdatedWithoutThymeleafCache() throws ParseException, IOException {
		builder.cache(false);
		TemplateParser parser = builder.build();
		
		Content content1 = parser.parse(new UnresolvedPath("file:source/updated.html"), new BeanContext(new SimpleBean("foo", 42)));
		copy(resource("/template/thymeleaf/source/simple.txt"), folder.toPath().resolve("updated.html").toFile());
		Content content2 = parser.parse(new UnresolvedPath("file:source/updated.html"), new BeanContext(new SimpleBean("foo", 42)));
		
		assertNotNull(content1, "content should not be null");
		assertTrue(content1 instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content1);
		
		assertNotNull(content2, "content should not be null");
		assertTrue(content2 instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/simple_foo_42.txt", content2);
	}
	
	private static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}
}
