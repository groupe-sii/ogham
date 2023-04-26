package oghamcore.ut.email.subject.provider;

import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.subject.provider.MultiContentSubjectProvider;
import fr.sii.ogham.core.subject.provider.SubjectProvider;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.mockito.quality.Strictness.LENIENT;

@LogTestInformation
@MockitoSettings(strictness = LENIENT)
public class MultiContentSubjectProviderTest {

	private MultiContentSubjectProvider provider;
	
	@Mock
	private SubjectProvider delegate;
	
	@BeforeEach
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
		Assertions.assertNull( subject, "Subject should be null");
	}
	
	@Test
	public void htmlAndTxt() {
		String subject = provider.provide(new Email().content(new MultiContent(new StringContent("html"), new StringContent("txt"))));
		Assertions.assertEquals("html subject", subject, "Subject should be provided by html");
	}
	
	@Test
	public void noneAndTxt() {
		String subject = provider.provide(new Email().content(new MultiContent(new StringContent("none"), new StringContent("txt"))));
		Assertions.assertEquals("txt subject", subject, "Subject should be provided by txt");
	}
	
	@Test
	public void noneAndNone() {
		String subject = provider.provide(new Email().content(new MultiContent(new StringContent("none"), new StringContent("none"))));
		Assertions.assertNull(subject, "No subject should be provided");
	}
	
	@Test
	public void noneAndEmpty() {
		String subject = provider.provide(new Email().content(new MultiContent(new StringContent("none"), new StringContent("empty"))));
		Assertions.assertEquals("", subject, "Subject should be provided by empty");
	}
}
