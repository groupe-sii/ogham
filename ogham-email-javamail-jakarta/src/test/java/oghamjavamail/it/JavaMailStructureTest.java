package oghamjavamail.it;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.testing.assertion.util.EmailUtils;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import ogham.testing.jakarta.mail.MessagingException;
import ogham.testing.jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@LogTestInformation
public class JavaMailStructureTest {
	private JavaMailSender sender;
	
	@RegisterExtension public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	
	@BeforeEach
	public void setUp() throws IOException {
		sender = new JavaMailBuilder()
				.host(greenMail.getSmtp().getBindTo())
				.port(greenMail.getSmtp().getPort())
				.mimetype()
					.tika()
						.failIfOctetStream(false)
						.and()
					.and()
				.build();
	}

	@Test
	public void plainTextBody() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.body().string("text")
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		// @formatter:on
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[text/plain]");
	}

	@Test
	public void htmlBody() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.body().string(wrapHtml("html"))
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		// @formatter:on
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[text/html]");
	}

	@Test
	public void htmlBodyWithEmbeddedImage() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.body().string(wrapHtml("html + <img src='cid:1' />"))
							.embed().bytes("1", resource("attachment/ogham-grey-900x900.png"))
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[multipart/related]"
								  + "  [text/html]"
								  + "  [image/png]");
		// @formatter:on
	}
	
	@Test
	public void plainTextBodyWithAttachments() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.body().string("text")
							.attach().bytes("foo.pdf", resource("attachment/04-Java-OOP-Basics.pdf"))
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[multipart/mixed]"
								  + "  [text/plain]"
								  + "  [application/pdf]");
		// @formatter:on
	}

	@Test
	public void htmlBodyWithAttachments() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.body().string(wrapHtml("html"))
							.attach().bytes("foo.pdf", resource("attachment/04-Java-OOP-Basics.pdf"))
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[multipart/mixed]"
								  + "  [text/html]"
								  + "  [application/pdf]");
		// @formatter:on
	}

	@Test
	public void htmlBodyWithAttachmentsAndEmbeddedImages() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.body().string(wrapHtml("html + <img src='cid:1' />"))
							.embed().bytes("1", resource("attachment/ogham-grey-900x900.png"))
							.attach().bytes("foo.pdf", resource("attachment/04-Java-OOP-Basics.pdf"))
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[multipart/mixed]"
								  + "  [multipart/related]"
								  + "    [text/html]"
								  + "    [image/png]"
								  + "  [application/pdf]");
		// @formatter:on
	}

	@Test
	public void htmlAndTextBody() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.text().string("text")
							.html().string(wrapHtml("html"))
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[multipart/alternative]"
								  + "  [text/plain]"
								  + "  [text/html]");
		// @formatter:on
	}
	
	@Test
	public void htmlAndTextBodyWithEmbeddedImage() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.text().string("text")
							.html().string(wrapHtml("html + <img src='cid:1' />"))
							.embed().bytes("1", resource("attachment/ogham-grey-900x900.png"))
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[multipart/related]"
								  + "  [multipart/alternative]"
								  + "    [text/plain]"
								  + "    [text/html]"
								  + "  [image/png]");
		// @formatter:on
	}
	
	@Test
	public void htmlAndTextBodyWithAttachments() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.text().string("text")
							.html().string(wrapHtml("html"))
							.attach().bytes("foo.pdf", resource("attachment/04-Java-OOP-Basics.pdf"))
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[multipart/mixed]"
								  + "  [multipart/alternative]"
								  + "    [text/plain]"
								  + "    [text/html]"
								  + "  [application/pdf]");
		// @formatter:on
	}
	
	@Test
	public void htmlAndTextBodyWithAttachmentsAndEmbeddedImage() throws MessageException, IOException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.text().string("text")
							.html().string(wrapHtml("html + <img src='cid:1' />"))
							.embed().bytes("1", resource("attachment/ogham-grey-900x900.png"))
							.attach().bytes("foo.pdf", resource("attachment/04-Java-OOP-Basics.pdf"))
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		MimeMessage mimeMessage = greenMail.getReceivedMessages()[0];
		checkStructure(mimeMessage, "[multipart/mixed]"
								  + "  [multipart/related]"
								  + "    [multipart/alternative]"
								  + "      [text/plain]"
								  + "      [text/html]"
								  + "    [image/png]"
								  + "  [application/pdf]");
		// @formatter:on
	}
	
	private static void checkStructure(MimeMessage mimeMessage, String expectedStructure) throws IOException, MessagingException {
		assertThat("structure is different", EmailUtils.getStructure(mimeMessage), is(expectedStructure.replaceAll("\\]", "]\n")));
	}

	
	private static String wrapHtml(String partialHtml) {
		return "<html><head></head><body>"+partialHtml+"</body></html>";
	}
}
