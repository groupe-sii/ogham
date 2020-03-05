package oghamfremarker.ut;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterException;
import fr.sii.ogham.template.freemarker.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.freemarker.adapter.TemplateLoaderAdapter;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import freemarker.cache.TemplateLoader;

public class FirstSupportingResolverAdapterTest {
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	
	@Mock ResourceResolver resolver;
	@Mock TemplateLoaderAdapter adapter1;
	@Mock TemplateLoaderAdapter adapter2;
	@Mock TemplateLoader loader1;
	@Mock TemplateLoader loader2;
	
	FirstSupportingResolverAdapter adapter;
	
	@Before
	public void setup() throws ResolverAdapterException {
		adapter = new FirstSupportingResolverAdapter(adapter1, adapter2);
		when(adapter1.adapt(resolver)).thenReturn(loader1);
		when(adapter2.adapt(resolver)).thenReturn(loader2);
	}
	
	@Test
	public void supportsIfAtLeastOneSupports() {
		when(adapter1.supports(resolver)).thenReturn(false, true, false, true);
		when(adapter2.supports(resolver)).thenReturn(false, /*false,*/ true/*, true*/);
		assertThat("false or false", adapter.supports(resolver), is(false));
		assertThat("true or false", adapter.supports(resolver), is(true));
		assertThat("false or true", adapter.supports(resolver), is(true));
		assertThat("true or true", adapter.supports(resolver), is(true));
	}
	
	@Test
	public void adaptUsingFirstSupporting() throws ResolverAdapterException {
		when(adapter1.supports(resolver)).thenReturn(true, false, true);
		when(adapter2.supports(resolver)).thenReturn(/*false,*/ true/*, true*/);
		assertThat("true or false", adapter.adapt(resolver), is(loader1));
		assertThat("false or true", adapter.adapt(resolver), is(loader2));
		assertThat("true or true", adapter.adapt(resolver), is(loader1));
	}
	
	@Test
	public void noSupportingAdapterShouldFail() throws ResolverAdapterException {
		when(adapter1.supports(resolver)).thenReturn(false);
		when(adapter2.supports(resolver)).thenReturn(false);

		assertThrows("should throw", NoResolverAdapterException.class, () -> {
			adapter.adapt(resolver);
		});
	}
}
