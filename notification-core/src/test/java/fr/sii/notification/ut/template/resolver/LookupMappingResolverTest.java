package fr.sii.notification.ut.template.resolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import fr.sii.notification.core.exception.resource.ResourceResolutionException;
import fr.sii.notification.core.resource.Resource;
import fr.sii.notification.core.resource.resolver.ConditionalResolver;
import fr.sii.notification.core.resource.resolver.LookupMappingResolver;
import fr.sii.notification.core.resource.resolver.ResourceResolver;
import fr.sii.notification.helper.rule.LoggingTestRule;

public class LookupMappingResolverTest {
	private LookupMappingResolver lookupResolver;
	private Map<String, ResourceResolver> resolvers;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Before
	public void setUp() throws ResourceResolutionException {
		resolvers = new HashMap<>();
		addResolver("classpath");
		addResolver("file");
		addResolver("", "default");
		addConditionalResolver("supported", true);
		addConditionalResolver("unsupported", false);
		lookupResolver = new LookupMappingResolver(resolvers);
	}

	@Test
	public void classpath() throws ResourceResolutionException, IOException {
		String path = "classpath:/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support template path", lookupResolver.supports(path));
		ResourceResolver resolver = lookupResolver.getResolver(path);
		Assert.assertSame("should be classpath resolver", resolvers.get("classpath"), resolver);
		Assert.assertNotSame("should not be file resolver", resolvers.get("file"), resolver);
		Assert.assertNotSame("should not be default resolver", resolvers.get(""), resolver);
		Resource template = lookupResolver.getResource(path);
		Assert.assertNotNull("template should not be null", template);
	}

	@Test
	public void file() {
		String path = "file:/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support template path", lookupResolver.supports(path));
		ResourceResolver resolver = lookupResolver.getResolver(path);
		Assert.assertNotSame("should not be classpath resolver", resolvers.get("classpath"), resolver);
		Assert.assertSame("should be file resolver", resolvers.get("file"), resolver);
		Assert.assertNotSame("should not be default resolver", resolvers.get(""), resolver);
	}

	@Test
	public void none() {
		String path = "/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support template path", lookupResolver.supports(path));
		ResourceResolver resolver = lookupResolver.getResolver(path);
		Assert.assertNotSame("should not be classpath resolver", resolvers.get("classpath"), resolver);
		Assert.assertNotSame("should not be file resolver", resolvers.get("file"), resolver);
		Assert.assertSame("should be default resolver", resolvers.get(""), resolver);
	}

	@Test
	public void unknown() {
		String path = "fake:/template/resolver/foo/bar.html";
		Assert.assertFalse("should not be able to support template path", lookupResolver.supports(path));
	}

	@Test
	public void conditional() {
		String path = "unsupported:/template/resolver/foo/bar.html";
		Assert.assertFalse("should not be able to support template path", lookupResolver.supports(path));
		path = "supported:/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support template path", lookupResolver.supports(path));
	}

	private void addResolver(String lookup) throws ResourceResolutionException {
		addResolver(lookup, lookup);
	}
	
	private void addResolver(String lookup, String name) throws ResourceResolutionException {
		ResourceResolver resolver = Mockito.mock(ResourceResolver.class, name);
		Mockito.when(resolver.getResource(Mockito.anyString())).thenReturn(Mockito.mock(Resource.class));
		resolvers.put(lookup, resolver);
	}
	
	private void addConditionalResolver(String lookup, boolean support) throws ResourceResolutionException {
		addConditionalResolver(lookup, lookup, support);
	}
	
	private void addConditionalResolver(String lookup, String name, boolean support) throws ResourceResolutionException {
		ConditionalResolver resolver = Mockito.mock(ConditionalResolver.class, name);
		Mockito.when(resolver.supports(Mockito.anyString())).thenReturn(support);
		Mockito.when(resolver.getResource(Mockito.anyString())).thenReturn(Mockito.mock(Resource.class));
		resolvers.put(lookup, resolver);
	}

}
