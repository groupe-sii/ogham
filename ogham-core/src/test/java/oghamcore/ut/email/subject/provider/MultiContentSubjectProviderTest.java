package oghamcore.ut.email.subject.provider;

import static org.mockito.quality.Strictness.LENIENT;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.subject.provider.MultiContentSubjectProvider;
import fr.sii.ogham.core.subject.provider.SubjectProvider;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class MultiContentSubjectProviderTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule().strictness(LENIENT);
	
	private MultiContentSubjectProvider provider;
	
	@Mock
	private SubjectProvider delegate;
	
	@Before
	public void setUp() {
		provider = new MultiContentSubjectProvider(delegate);
		Mockito.when(delegate.provide(new Email().content("single content"))).thenReturn("single subject");
		Mockito.when(delegate.provide(new Email().content("txt"))).thenReturn("txt subject");
		Mockito.when(delegate.provide(new Email().content("html"))).thenReturn("html subject");
		Mockito.when(delegate.provide(new Email().content("none"))).thenReturn(null);
		Mockito.when(delegate.provide(new Email().content("empty"))).thenReturn("");
	}
	
	@Test
	public void single() {
		String subject = provider.provide(new Email().content("single content"));
		Assert.assertNull("Subject should be null", subject);
	}
	
	@Test
	public void htmlAndTxt() {
		String subject = provider.provide(new Email().content(new MultiContent(new StringContent("html"), new StringContent("txt"))));
		Assert.assertEquals("Subject should be provided by html", "html subject", subject);
	}
	
	@Test
	public void noneAndTxt() {
		String subject = provider.provide(new Email().content(new MultiContent(new StringContent("none"), new StringContent("txt"))));
		Assert.assertEquals("Subject should be provided by txt", "txt subject", subject);
	}
	
	@Test
	public void noneAndNone() {
		String subject = provider.provide(new Email().content(new MultiContent(new StringContent("none"), new StringContent("none"))));
		Assert.assertNull("No subject should be provided", subject);
	}
	
	@Test
	public void noneAndEmpty() {
		String subject = provider.provide(new Email().content(new MultiContent(new StringContent("none"), new StringContent("empty"))));
		Assert.assertEquals("Subject should be provided by empty", "", subject);
	}
}
