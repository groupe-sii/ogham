package fr.sii.ogham.runtime.checker;

import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import fr.sii.ogham.testing.sms.simulator.bean.NumberingPlanIndicator;
import fr.sii.ogham.testing.sms.simulator.bean.TypeOfNumber;
import ogham.testing.org.jsmpp.bean.SubmitSm;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;


public class CloudhopperChecker {
	private final SmppServerExtension<SubmitSm> smppServer;
	
	public CloudhopperChecker(SmppServerExtension<SubmitSm> smppServer) {
		super();
		this.smppServer = smppServer;
	}

	public void assertSmsWithoutTemplate() {
		assertThat(smppServer).receivedMessages()
			.count(is(1))
				.message(0)
					.content(is("Hello world !!"))
					.from()
						.number(is("+33601020304"))
						.typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
						.numberingPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
					.to()
						.number(is("0709080706"))
						.typeOfNumber(is(TypeOfNumber.UNKNOWN))
						.numberingPlanIndicator(is(NumberingPlanIndicator.ISDN));
	}
	
	public void assertSmsWithThymeleaf() throws IOException {
		assertSmsWithTemplates("thymeleaf");
	}

	public void assertSmsWithFreemarker() throws IOException {
		assertSmsWithTemplates("freemarker");
	}
	
	private void assertSmsWithTemplates(String templateEngine) throws IOException {
		assertThat(smppServer).receivedMessages()
			.count(is(1))
				.message(0)
					.content(is("Hello foo !\nYou have 42€"))
					.from()
						.number(is("+33601020304"))
						.typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
						.numberingPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
					.to()
						.number(is("0709080706"))
						.typeOfNumber(is(TypeOfNumber.UNKNOWN))
						.numberingPlanIndicator(is(NumberingPlanIndicator.ISDN));
	}
}
