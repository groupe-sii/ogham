package fr.sii.ogham.spring.it.autoconfigure;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cloudhopper.smpp.SmppConstants;
import com.sendgrid.SendGrid;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;
import fr.sii.ogham.sms.builder.ovh.OvhSmsBuilder;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;
import fr.sii.ogham.spring.mock.MockApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MockApplication.class, webEnvironment = NONE, 
	properties = { "ogham.sms.cloudhopper.host=localhost", 
				   "ogham.sms.ovh.password=bar"})
public class SpringPropertiesTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Autowired
	MessagingBuilder builder;
	
	@Autowired
	SendGrid springSendGridClient;

	@Test
	public void cloudhopperPropertiesDefinedInAppPropertiesOrInSystemPropertiesShouldOverrideOghamDefaultProperties() {
		CloudhopperSMPPSender sender = builder.sms().sender(CloudhopperBuilder.class).build();
		assertThat(sender.getSmppSessionConfiguration().getHost(), equalTo("localhost"));
		assertThat(sender.getSmppSessionConfiguration().getPort(), equalTo(2775));
		assertThat(sender.getSmppSessionConfiguration().getInterfaceVersion(), equalTo(SmppConstants.VERSION_5_0));
	}

	@Test
	public void ovhPropertiesDefinedInAppPropertiesOrInSystemPropertiesShouldOverrideOghamDefaultProperties() {
		OvhSmsSender sender = builder.sms().sender(OvhSmsBuilder.class).build();
		assertThat(sender.getAuthParams().getAccount(), equalTo("foo"));
		assertThat(sender.getAuthParams().getLogin(), equalTo("titi"));
		assertThat(sender.getAuthParams().getPassword(), equalTo("bar"));
	}

	@Test
	public void sendGridPropertiesDefinedInAppPropertiesOrInSystemPropertiesShouldOverrideOghamDefaultProperties() {
		SendGridV4Sender sender = builder.email().sender(SendGridV4Builder.class).build();
		DelegateSendGridClient delegate = (DelegateSendGridClient) sender.getDelegate();
		assertThat(delegate.getApiKey(), nullValue());		// null and not 'toto' because SendGrid Spring bean is used instead of Ogham default SendGrid instance
		assertThat(delegate.getDelegate(), sameInstance(springSendGridClient));
	}

}
