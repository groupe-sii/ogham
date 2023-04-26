package oghamspringbootautoconfigure.it;

import com.cloudhopper.smpp.SmppConstants;
import com.sendgrid.SendGrid;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.builder.ovh.OvhSmsBuilder;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.ExtendedSmppSessionConfiguration;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import mock.MockApplication;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import utils.SendGridUtils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@LogTestInformation
@SpringBootTest(classes = MockApplication.class, webEnvironment = NONE,
	properties = { "ogham.sms.cloudhopper.host=localhost", 
				   "ogham.sms.ovh.password=bar",
				   "greenmail.smtp.port=3025",
				   "jsmpp.server.port=2775"})
@ActiveProfiles("ogham-only")
public class OghamPropertiesOnlyTest {
	@Autowired
	MessagingBuilder builder;
	
	@Autowired(required=false)
	SendGrid springSendGridClient;

	@Test
	public void cloudhopperPropertiesDefinedInAppPropertiesOrInSystemPropertiesShouldOverrideOghamDefaultProperties() throws IllegalAccessException {
		CloudhopperSMPPSender sender = builder.sms().sender(CloudhopperBuilder.class).build();
		ExtendedSmppSessionConfiguration config =  (ExtendedSmppSessionConfiguration) FieldUtils.readField(sender, "configuration", true);
		assertThat(config.getHost(), equalTo("localhost"));
		assertThat(config.getPort(), equalTo(2775));
		assertThat(config.getInterfaceVersion(), equalTo(SmppConstants.VERSION_5_0));
	}

	@Test
	public void ovhPropertiesDefinedInAppPropertiesOrInSystemPropertiesShouldOverrideOghamDefaultProperties() {
		OvhSmsSender sender = builder.sms().sender(OvhSmsBuilder.class).build();
		assertThat(sender.getAuthParams().getAccount(), equalTo("foo"));
		assertThat(sender.getAuthParams().getLogin(), equalTo("titi"));
		assertThat(sender.getAuthParams().getPassword(), equalTo("bar"));
	}

	@Test
	public void sendGridPropertiesDefinedInAppPropertiesOrInSystemPropertiesShouldOverrideOghamDefaultProperties() throws IllegalAccessException {
		SendGridV4Sender sender = builder.email().sender(SendGridV4Builder.class).build();
		DelegateSendGridClient delegate = (DelegateSendGridClient) sender.getDelegate();
		SendGrid sendGrid = (SendGrid) FieldUtils.readField(delegate, "delegate", true);
		assertThat(SendGridUtils.getApiKey(sendGrid), equalTo("ogham"));
		assertThat(sendGrid, not(sameInstance(springSendGridClient)));
	}

}
