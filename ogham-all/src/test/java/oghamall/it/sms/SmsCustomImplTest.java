package oghamall.it.sms;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.IOException;

import static fr.sii.ogham.sms.message.addressing.NumberingPlanIndicator.ISDN_TELEPHONE;
import static fr.sii.ogham.sms.message.addressing.TypeOfNumber.INTERNATIONAL;
import static fr.sii.ogham.sms.message.addressing.TypeOfNumber.UNKNOWN;

@LogTestInformation
@MockitoSettings
public class SmsCustomImplTest {
	private static final String NATIONAL_PHONE_NUMBER = "0203040506";

	private static final String INTERNATIONAL_PHONE_NUMBER = "+33203040506";

	private MessagingService oghamService;

	@Mock
	MessageSender customSender;
	
	@BeforeEach
	public void setUp() throws IOException {
		oghamService = MessagingBuilder.standard()
				.environment()
					.systemProperties()
					.properties("/application.properties")
					.and()
				.sms()
					.customSender(customSender)
					.and()
				.build();
	}
	
	@Test
	public void simple() throws MessagingException {
		oghamService.send(new Sms()
							.content("sms content")
							.to(NATIONAL_PHONE_NUMBER));
		Mockito.verify(customSender).send(new Sms()
							.content("sms content")
							.from(new Sender(new AddressedPhoneNumber(INTERNATIONAL_PHONE_NUMBER, INTERNATIONAL, ISDN_TELEPHONE)))
							.to(new AddressedPhoneNumber(NATIONAL_PHONE_NUMBER, UNKNOWN, ISDN_TELEPHONE)));
	}
}
