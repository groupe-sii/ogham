package oghamthymeleaf.ut;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterException;
import fr.sii.ogham.template.thymeleaf.common.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.TemplateResolverAdapter;
import fr.sii.ogham.testing.extension.common.LogTestInformation;

@LogTestInformation
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class FirstSupportingResolverAdapterTest {
	@Mock ResourceResolver resolver;
	@Mock TemplateResolverAdapter adapter1;
	@Mock TemplateResolverAdapter adapter2;
	@Mock ITemplateResolver tplResolver1;
	@Mock ITemplateResolver tplResolver2;
	
	FirstSupportingResolverAdapter adapter;
	
	@BeforeEach
	void setup() throws ResolverAdapterException {
		adapter = new FirstSupportingResolverAdapter(adapter1, adapter2);
		when(adapter1.adapt(resolver)).thenReturn(tplResolver1);
		when(adapter2.adapt(resolver)).thenReturn(tplResolver2);
	}
	
	@Test
	void supportsIfAtLeastOneSupports() {
		when(adapter1.supports(resolver)).thenReturn(false, true, false, true);
		when(adapter2.supports(resolver)).thenReturn(false, /*false,*/ true/*, true*/);
		assertThat("false or false", adapter.supports(resolver), is(false));
		assertThat("true or false", adapter.supports(resolver), is(true));
		assertThat("false or true", adapter.supports(resolver), is(true));
		assertThat("true or true", adapter.supports(resolver), is(true));
	}
	
	@Test
	void adaptUsingFirstSupporting() throws ResolverAdapterException {
		when(adapter1.supports(resolver)).thenReturn(true, false, true);
		when(adapter2.supports(resolver)).thenReturn(/*false,*/ true/*, true*/);
		assertThat("true or false", adapter.adapt(resolver), is(tplResolver1));
		assertThat("false or true", adapter.adapt(resolver), is(tplResolver2));
		assertThat("true or true", adapter.adapt(resolver), is(tplResolver1));
	}
	
	@Test
	void noSupportingAdapterShouldFail() throws ResolverAdapterException {
		when(adapter1.supports(resolver)).thenReturn(false);
		when(adapter2.supports(resolver)).thenReturn(false);
		assertThrows(NoResolverAdapterException.class, () -> {
			adapter.adapt(resolver);
		}, "no resolver");
	}
}
