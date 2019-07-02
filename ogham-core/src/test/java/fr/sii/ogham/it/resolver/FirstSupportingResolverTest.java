package fr.sii.ogham.it.resolver;

import static fr.sii.ogham.assertion.OghamAssertions.resource;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.SimpleResource;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.StringResourceResolver;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.junit.LoggingTestRule;

public class FirstSupportingResolverTest {
	private FirstSupportingResourceResolver firstSupportingResolver;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() throws ResourceResolutionException {
		firstSupportingResolver = new FirstSupportingResourceResolver(
				new StringResourceResolver("string:", "s:"),
				new FileResolver("file:"),
				new ClassPathResolver("classpath:", ""));
	}

	@Test
	public void classpath() throws ResourceResolutionException, IOException {
		ResourcePath path = new UnresolvedPath("classpath:/template/resolver/foo/bar.html");
		Assert.assertTrue("should be able to support classpath path", firstSupportingResolver.supports(path));
		Resource resource = firstSupportingResolver.getResource(path);
		Assert.assertNotNull("template should not be null", resource);
		Assert.assertSame("should be classpath resolver", resource.getClass(), ByteResource.class);
	}

	@Test
	public void file() throws ResourceResolutionException, IOException {
		File tempFile = folder.newFile("bar.html");
		IOUtils.copy(resource("/template/resolver/foo/bar.html"), tempFile);
		ResourcePath path = new UnresolvedPath("file:"+tempFile.getAbsolutePath());
		Assert.assertTrue("should be able to support file path", firstSupportingResolver.supports(path));
		Resource resource = firstSupportingResolver.getResource(path);
		Assert.assertSame("should be file resolver", resource.getClass(), FileResource.class);
	}

	@Test
	public void string() throws ResourceResolutionException {
		ResourcePath path = new UnresolvedPath("string:ma ressource");
		Assert.assertTrue("should be able to support string path", firstSupportingResolver.supports(path));
		Resource resource = firstSupportingResolver.getResource(path);
		Assert.assertSame("should be string resolver", resource.getClass(), SimpleResource.class);
	}

	@Test
	public void none() throws ResourceResolutionException {
		ResourcePath path = new UnresolvedPath("/template/resolver/foo/bar.html");
		Assert.assertTrue("should be able to support template path", firstSupportingResolver.supports(path));
		Resource resource = firstSupportingResolver.getResource(path);
		Assert.assertSame("should be classpath resolver", resource.getClass(), ByteResource.class);
	}

	@Test(expected=ResourceResolutionException.class)
	public void unknown() throws ResourceResolutionException {
		ResourcePath path = new UnresolvedPath("fake:/template/resolver/foo/bar.html");
		firstSupportingResolver.getResource(path);
	}
}
