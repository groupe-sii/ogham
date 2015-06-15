package fr.sii.notification.ut.subject.provider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.subject.provider.MultiContentSubjectProvider;
import fr.sii.notification.core.subject.provider.SubjectProvider;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.helper.rule.LoggingTestRule;

@RunWith(MockitoJUnitRunner.class)
public class MultiContentSubjectProviderTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	private MultiContentSubjectProvider provider;
	
	@Mock
	private SubjectProvider delegate;
	
	@Before
	public void setUp() {
		provider = new MultiContentSubjectProvider(delegate);
		Mockito.when(delegate.provide(new Email(null, "single content"))).thenReturn("single subject");
		Mockito.when(delegate.provide(new Email(null, "txt"))).thenReturn("txt subject");
		Mockito.when(delegate.provide(new Email(null, "html"))).thenReturn("html subject");
		Mockito.when(delegate.provide(new Email(null, "none"))).thenReturn(null);
		Mockito.when(delegate.provide(new Email(null, "empty"))).thenReturn("");
	}
	
	@Test
	public void single() {
		String subject = provider.provide(new Email(null, "single content"));
		Assert.assertEquals("Subject should be 'single subject'", "single subject", subject);
	}
	
	@Test
	public void htmlAndTxt() {
		String subject = provider.provide(new Email(null, new MultiContent(new StringContent("html"), new StringContent("txt"))));
		Assert.assertEquals("Subject should be provided by html", "html subject", subject);
	}
	
	@Test
	public void noneAndTxt() {
		String subject = provider.provide(new Email(null, new MultiContent(new StringContent("none"), new StringContent("txt"))));
		Assert.assertEquals("Subject should be provided by txt", "txt subject", subject);
	}
	
	@Test
	public void noneAndNone() {
		String subject = provider.provide(new Email(null, new MultiContent(new StringContent("none"), new StringContent("none"))));
		Assert.assertNull("No subject should be provided", subject);
	}
	
	@Test
	public void noneAndEmpty() {
		String subject = provider.provide(new Email(null, new MultiContent(new StringContent("none"), new StringContent("empty"))));
		Assert.assertEquals("Subject should be provided by empty", "", subject);
	}
}
