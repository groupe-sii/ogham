package fr.sii.ogham.ut.template.resolver;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.builder.FirstSupportingResolverBuilder;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.SimpleResource;
import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.StringResourceResolver;
import fr.sii.ogham.helper.rule.LoggingTestRule;

public class FirstSupportingResolverTest {
	private FirstSupportingResourceResolver firstSupportingResolver;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Before
	public void setUp() throws ResourceResolutionException {
		firstSupportingResolver = new FirstSupportingResolverBuilder().useDefaults().build();
	}

	@Test
	public void classpath() throws ResourceResolutionException, IOException {
		String path = "classpath:/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support classpath path", firstSupportingResolver.supports(path));
		Resource resource = firstSupportingResolver.getResource(path);
		Assert.assertNotNull("template should not be null", resource);
		Assert.assertSame("should be classpath resolver", resource.getClass(), ByteResource.class);
	}

	@Test(expected = ResourceResolutionException.class)
	public void file() throws ResourceResolutionException, IOException {
		String path = "file:/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support file path", firstSupportingResolver.supports(path));
		Resource resource = firstSupportingResolver.getResource(path);
		Assert.assertSame("should be classpath resolver", resource.getClass(), FileResource.class);
	}

	@Test
	public void string() throws ResourceResolutionException {
		String path = "string:ma ressource";
		Assert.assertTrue("should be able to support string path", firstSupportingResolver.supports(path));
		Resource resource = firstSupportingResolver.getResource(path);
		Assert.assertSame("should be string resolver", resource.getClass(), SimpleResource.class);
	}

	@Test
	public void none() throws ResourceResolutionException {
		String path = "/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support template path", firstSupportingResolver.supports(path));
		Resource resource = firstSupportingResolver.getResource(path);
		Assert.assertSame("should be classpath resolver", resource.getClass(), ByteResource.class);
	}

	@Test
	public void unknown() {
		firstSupportingResolver = new FirstSupportingResolverBuilder().withResourceResolver(new FileResolver("file:")).withResourceResolver(new StringResourceResolver("string:"))
				.withResourceResolver(new ClassPathResolver("classpath:")).build();
		String path = "fake:/template/resolver/foo/bar.html";
		Assert.assertFalse("should not be able to support template path", firstSupportingResolver.supports(path));
	}
}
