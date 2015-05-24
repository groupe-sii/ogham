package fr.sii.notification.ut.template.resolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import fr.sii.notification.core.exception.template.TemplateResolutionException;
import fr.sii.notification.core.template.Template;
import fr.sii.notification.core.template.resolver.ConditionalResolver;
import fr.sii.notification.core.template.resolver.LookupMappingResolver;
import fr.sii.notification.core.template.resolver.TemplateResolver;
import fr.sii.notification.helper.rule.LoggingTestRule;

public class LookupMappingResolverTest {
	private LookupMappingResolver lookupResolver;
	private Map<String, TemplateResolver> resolvers;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Before
	public void setUp() throws TemplateResolutionException {
		resolvers = new HashMap<>();
		addResolver("classpath");
		addResolver("file");
		addResolver("", "default");
		addConditionalResolver("supported", true);
		addConditionalResolver("unsupported", false);
		lookupResolver = new LookupMappingResolver(resolvers);
	}

	@Test
	public void classpath() throws TemplateResolutionException, IOException {
		String path = "classpath:/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support template path", lookupResolver.supports(path));
		TemplateResolver resolver = lookupResolver.getResolver(path);
		Assert.assertSame("should be classpath resolver", resolvers.get("classpath"), resolver);
		Assert.assertNotSame("should not be file resolver", resolvers.get("file"), resolver);
		Assert.assertNotSame("should not be default resolver", resolvers.get(""), resolver);
		Template template = lookupResolver.getTemplate(path);
		Assert.assertNotNull("template should not be null", template);
	}

	@Test
	public void file() {
		String path = "file:/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support template path", lookupResolver.supports(path));
		TemplateResolver resolver = lookupResolver.getResolver(path);
		Assert.assertNotSame("should not be classpath resolver", resolvers.get("classpath"), resolver);
		Assert.assertSame("should be file resolver", resolvers.get("file"), resolver);
		Assert.assertNotSame("should not be default resolver", resolvers.get(""), resolver);
	}

	@Test
	public void none() {
		String path = "/template/resolver/foo/bar.html";
		Assert.assertTrue("should be able to support template path", lookupResolver.supports(path));
		TemplateResolver resolver = lookupResolver.getResolver(path);
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

	private void addResolver(String lookup) throws TemplateResolutionException {
		addResolver(lookup, lookup);
	}
	
	private void addResolver(String lookup, String name) throws TemplateResolutionException {
		TemplateResolver resolver = Mockito.mock(TemplateResolver.class, name);
		Mockito.when(resolver.getTemplate(Mockito.anyString())).thenReturn(Mockito.mock(Template.class));
		resolvers.put(lookup, resolver);
	}
	
	private void addConditionalResolver(String lookup, boolean support) throws TemplateResolutionException {
		addConditionalResolver(lookup, lookup, support);
	}
	
	private void addConditionalResolver(String lookup, String name, boolean support) throws TemplateResolutionException {
		ConditionalResolver resolver = Mockito.mock(ConditionalResolver.class, name);
		Mockito.when(resolver.supports(Mockito.anyString())).thenReturn(support);
		Mockito.when(resolver.getTemplate(Mockito.anyString())).thenReturn(Mockito.mock(Template.class));
		resolvers.put(lookup, resolver);
	}

}
