package fr.sii.ogham.it.optional;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.helper.email.AssertAttachment;
import fr.sii.ogham.helper.email.AssertEmail;
import fr.sii.ogham.helper.email.ExpectedAttachment;
import fr.sii.ogham.helper.email.ExpectedContent;
import fr.sii.ogham.helper.email.ExpectedEmail;
import fr.sii.ogham.helper.email.ExpectedMultiPartEmail;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.mock.context.SimpleBean;

@RunWith(MockitoJUnitRunner.class)
public class ImplementationSelectionTests {

	private MessagingService oghamService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Before
	public void setUp() throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		props.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		props.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		oghamService = new MessagingBuilder().useAllDefaults(props).build();
	}
	
	@Test
	public void javaMailAvailable() {
		
	}
}
