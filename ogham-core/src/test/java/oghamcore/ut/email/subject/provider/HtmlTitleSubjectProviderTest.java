package oghamcore.ut.email.subject.provider;

import fr.sii.ogham.core.subject.provider.HtmlTitleSubjectProvider;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@LogTestInformation
public class HtmlTitleSubjectProviderTest {
	private HtmlTitleSubjectProvider subjectProvider;
	
	@BeforeEach
	public void setUp() {
		subjectProvider = new HtmlTitleSubjectProvider();
	}

	@Test
	public void withTitle() throws IOException {
		String html = IOUtils.toString(getClass().getResourceAsStream("/subject/withTitle.html"));
		String subject = subjectProvider.provide(new Email().content(html));
		Assertions.assertEquals("this is the subject", subject, "subject should be 'this is the subject'");
	}
	
	@Test
	public void trim() throws IOException {
		String html = IOUtils.toString(getClass().getResourceAsStream("/subject/trim.html"));
		String subject = subjectProvider.provide(new Email().content(html));
		Assertions.assertEquals("this is the subject", subject, "subject should be 'this is the subject'");
	}
	
	@Test
	public void emptyTitle() throws IOException {
		String html = IOUtils.toString(getClass().getResourceAsStream("/subject/emptyTitle.html"));
		String subject = subjectProvider.provide(new Email().content(html));
		Assertions.assertTrue(subject.isEmpty(), "subject should be empty");
	}
	
	@Test
	public void noTitle() throws IOException {
		String html = IOUtils.toString(getClass().getResourceAsStream("/subject/noTitle.html"));
		String subject = subjectProvider.provide(new Email().content(html));
		Assertions.assertNull(subject, "subject should be null");
	}
}
