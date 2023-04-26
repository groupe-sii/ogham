package fr.sii.ogham.testing.assertion;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import ogham.testing.com.icegreen.greenmail.util.GreenMail;
import fr.sii.ogham.testing.assertion.email.FluentEmailsAssert;
import fr.sii.ogham.testing.assertion.email.FluentReceivedEmailsAssert;
import fr.sii.ogham.testing.assertion.sms.FluentReceivedSmsAssert;
import fr.sii.ogham.testing.assertion.sms.FluentSmsListAssert;
import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.assertion.util.FailAtEndRegistry;
import fr.sii.ogham.testing.assertion.util.FailImmediatelyRegistry;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;
import fr.sii.ogham.testing.sms.simulator.jsmpp.SubmitSmAdapter;
import ogham.testing.jakarta.mail.internet.MimeMessage;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Utility class that helps writing message assertions. For emails, you can
 * write something like this:
 * 
 * <pre>
 * {@code
 * assertThat(greenMail)
 *   .receivedMessages()
 *     .count(is(1))
 *       .message(0)
 *         .subject(is("Test"))
 *         .from()
 *           .address(hasItems("test.sender@sii.fr"))
 *           .and()
 *         .to()
 *           .address(hasItems("recipient@sii.fr"))
 *         .and()
 *       .body()
 * 		   .contentAsString(is("body"))
 *         .contentType(startsWith("text/plain"))
 *         .and()
 *       .alternative(nullValue())
 *       .attachments(hasSize(1))
 *         .attachment("04-Java-OOP-Basics.pdf")
 * 		     .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
 *           .contentType(startsWith("application/pdf"))
 *           .filename(is("04-Java-OOP-Basics.pdf"))
 *           .disposition(is(ATTACHMENT_DISPOSITION));
 * }
 * </pre>
 * 
 * For sms, you can write something like this:
 * 
 * <pre>
 * {@code
 * assertThat(smppServer)
 *   .receivedMessages()
 *     .count(is(1))
 *       .message(0)
 *         .content(is("sms content"))
 *         .from()
 *           .number(is(INTERNATIONAL_PHONE_NUMBER))
 *           .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
 * 		     .numberPlanIndicator(is(NumberingPlanIndicator.ISDN))
 *           .and()
 *         .to()
 *           .number(is(NATIONAL_PHONE_NUMBER))
 *           .typeOfNumber(is(TypeOfNumber.UNKNOWN))
 * 		     .numberPlanIndicator(is(NumberingPlanIndicator.ISDN));
 * }
 * </pre>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public final class OghamAssertions {

	/**
	 * Helper method to write assertions on mails using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
	 * assertThat(greenMail).receivedMessages()
	 *   .count(is(1))
	 *   .message(0)
	 *     .subject(is("Test"))
	 *     .from().address(hasItems("test.sender@sii.fr")).and()
	 *     .to().address(hasItems("recipient@sii.fr")).and()
	 *   .body()
	 *      .contentAsString(is("body"))
	 *      .contentType(startsWith("text/plain")).and()
	 *   .alternative(nullValue())
	 *   .attachments(hasSize(1))
	 *   .attachment("04-Java-OOP-Basics.pdf")
	 *      .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
	 *      .contentType(startsWith("application/pdf"))
	 *      .filename(is("04-Java-OOP-Basics.pdf"))
	 *      .disposition(is(ATTACHMENT_DISPOSITION));
	 * }
	 * </pre>
	 * 
	 * @param greenMail
	 *            email server that stores received messages
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentReceivedEmailsAssert assertThat(GreenMailExtension greenMail) {
		return assertThat(greenMail, new FailImmediatelyRegistry());
	}

	/**
	 * Helper method to write assertions on mails using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
	 * assertAll(registry -> 
	 *   assertThat(greenMail, registry).receivedMessages()
	 *     .count(is(1))
	 *     .message(0)
	 *       .subject(is("Test"))
	 *       .from().address(hasItems("test.sender@sii.fr")).and()
	 *       .to().address(hasItems("recipient@sii.fr")).and()
	 *     .body()
	 *        .contentAsString(is("body"))
	 *        .contentType(startsWith("text/plain")).and()
	 *     .alternative(nullValue())
	 *     .attachments(hasSize(1))
	 *     .attachment("04-Java-OOP-Basics.pdf")
	 *        .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
	 *        .contentType(startsWith("application/pdf"))
	 *        .filename(is("04-Java-OOP-Basics.pdf"))
	 *        .disposition(is(ATTACHMENT_DISPOSITION)));
	 * }
	 * </pre>
	 * 
	 * <p>
	 * This method is used in combination of {@link #assertAll(Consumer...)} in
	 * order to report all exceptions/assertion failures at the end instead of
	 * stopping at the first failure.
	 * 
	 * @param greenMail
	 *            email server that stores received messages
	 * @param registry
	 *            the registry used to register assertions
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentReceivedEmailsAssert assertThat(GreenMailExtension greenMail, AssertionRegistry registry) {
		return new FluentReceivedEmailsAssert(asList(greenMail.getReceivedMessages()), registry);
	}

	/**
	 * Helper method to write assertions on mails using fluent API. For example:
	 *
	 * <pre>
	 * {@code
	 * assertThat(greenMail).receivedMessages()
	 *   .count(is(1))
	 *   .message(0)
	 *     .subject(is("Test"))
	 *     .from().address(hasItems("test.sender@sii.fr")).and()
	 *     .to().address(hasItems("recipient@sii.fr")).and()
	 *   .body()
	 *      .contentAsString(is("body"))
	 *      .contentType(startsWith("text/plain")).and()
	 *   .alternative(nullValue())
	 *   .attachments(hasSize(1))
	 *   .attachment("04-Java-OOP-Basics.pdf")
	 *      .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
	 *      .contentType(startsWith("application/pdf"))
	 *      .filename(is("04-Java-OOP-Basics.pdf"))
	 *      .disposition(is(ATTACHMENT_DISPOSITION));
	 * }
	 * </pre>
	 *
	 * @param greenMail
	 *            email server that stores received messages
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentReceivedEmailsAssert assertThat(GreenMail greenMail) {
		return assertThat(greenMail, new FailImmediatelyRegistry());
	}

	/**
	 * Helper method to write assertions on mails using fluent API. For example:
	 *
	 * <pre>
	 * {@code
	 * assertAll(registry ->
	 *   assertThat(greenMail, registry).receivedMessages()
	 *     .count(is(1))
	 *     .message(0)
	 *       .subject(is("Test"))
	 *       .from().address(hasItems("test.sender@sii.fr")).and()
	 *       .to().address(hasItems("recipient@sii.fr")).and()
	 *     .body()
	 *        .contentAsString(is("body"))
	 *        .contentType(startsWith("text/plain")).and()
	 *     .alternative(nullValue())
	 *     .attachments(hasSize(1))
	 *     .attachment("04-Java-OOP-Basics.pdf")
	 *        .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
	 *        .contentType(startsWith("application/pdf"))
	 *        .filename(is("04-Java-OOP-Basics.pdf"))
	 *        .disposition(is(ATTACHMENT_DISPOSITION)));
	 * }
	 * </pre>
	 *
	 * <p>
	 * This method is used in combination of {@link #assertAll(Consumer...)} in
	 * order to report all exceptions/assertion failures at the end instead of
	 * stopping at the first failure.
	 *
	 * @param greenMail
	 *            email server that stores received messages
	 * @param registry
	 *            the registry used to register assertions
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentReceivedEmailsAssert assertThat(GreenMail greenMail, AssertionRegistry registry) {
		return new FluentReceivedEmailsAssert(asList(greenMail.getReceivedMessages()), registry);
	}

	/**
	 * Helper method to write assertions on mails using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
	 * assertThat(greenMail.getReceivedMessages())
	 *   .count(is(1))
	 *   .message(0)
	 *     .subject(is("Test"))
	 *     .from().address(hasItems("test.sender@sii.fr")).and()
	 *     .to().address(hasItems("recipient@sii.fr")).and()
	 *   .body()
	 *      .contentAsString(is("body"))
	 *      .contentType(startsWith("text/plain")).and()
	 *   .alternative(nullValue())
	 *   .attachments(hasSize(1))
	 *   .attachment("04-Java-OOP-Basics.pdf")
	 *      .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
	 *      .contentType(startsWith("application/pdf"))
	 *      .filename(is("04-Java-OOP-Basics.pdf"))
	 *      .disposition(is(ATTACHMENT_DISPOSITION));
	 * }
	 * </pre>
	 * 
	 * @param receivedEmails
	 *            list of messages received by the email server
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentEmailsAssert<Void> assertThat(MimeMessage[] receivedEmails) {
		return assertThat(receivedEmails, new FailImmediatelyRegistry());
	}

	/**
	 * Helper method to write assertions on mails using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
	 * assertAll(registry -> 
	 *   assertThat(greenMail.getReceivedMessages(), registry)
	 *     .count(is(1))
	 *     .message(0)
	 *       .subject(is("Test"))
	 *       .from().address(hasItems("test.sender@sii.fr")).and()
	 *       .to().address(hasItems("recipient@sii.fr")).and()
	 *     .body()
	 *        .contentAsString(is("body"))
	 *        .contentType(startsWith("text/plain")).and()
	 *     .alternative(nullValue())
	 *     .attachments(hasSize(1))
	 *     .attachment("04-Java-OOP-Basics.pdf")
	 *        .content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
	 *        .contentType(startsWith("application/pdf"))
	 *        .filename(is("04-Java-OOP-Basics.pdf"))
	 *        .disposition(is(ATTACHMENT_DISPOSITION)));
	 * }
	 * </pre>
	 * 
	 * <p>
	 * This method is used in combination of {@link #assertAll(Consumer...)} in
	 * order to report all exceptions/assertion failures at the end instead of
	 * stopping at the first failure.
	 * 
	 * @param receivedEmails
	 *            list of messages received by the email server
	 * @param registry
	 *            the registry used to register assertions
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentEmailsAssert<Void> assertThat(MimeMessage[] receivedEmails, AssertionRegistry registry) {
		return new FluentEmailsAssert<>(asList(receivedEmails), null, registry);
	}

	/**
	 * Helper method to write assertions on sms using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
	 * assertThat(smppServer).receivedMessages()
	 *   .count(is(1))
	 *   .message(0)
	 *     .content(is("sms content"))
	 *     .from()
	 *       .number(is(INTERNATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
	 *     .to()
	 *       .number(is(NATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.UNKNOWN))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN));
	 * }
	 * </pre>
	 * 
	 * @param smsServer
	 *            SMS server that stores received messages
	 * @param <M>
	 *            the type of messages handled by the server
	 * @return builder for fluent assertions on received messages
	 */
	public static <M> FluentReceivedSmsAssert<SubmitSm> assertThat(SmppServerExtension<M> smsServer) {
		return assertThat(smsServer, new FailImmediatelyRegistry());
	}


	/**
	 * Helper method to write assertions on sms using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
	 * assertAll(registry ->
	 *   assertThat(smppServer, registry).receivedMessages()
	 *     .count(is(1))
	 *     .message(0)
	 *       .content(is("sms content"))
	 *       .from()
	 *         .number(is(INTERNATIONAL_PHONE_NUMBER))
	 *         .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 *         .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
	 *       .to()
	 *         .number(is(NATIONAL_PHONE_NUMBER))
	 *         .typeOfNumber(is(TypeOfNumber.UNKNOWN))
	 *         .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)));
	 * }
	 * </pre>
	 * 
	 * <p>
	 * This method is used in combination of {@link #assertAll(Consumer...)} in
	 * order to report all exceptions/assertion failures at the end instead of
	 * stopping at the first failure.
	 * 
	 * @param smsServer
	 *            SMS server that stores received messages
	 * @param registry
	 *            the registry used to register assertions
	 * @param <M>
	 *            the type of messages handled by the server
	 * @return builder for fluent assertions on received messages
	 */
	public static <M> FluentReceivedSmsAssert<SubmitSm> assertThat(SmppServerExtension<M> smsServer, AssertionRegistry registry) {
		return new FluentReceivedSmsAssert<>(smsServer.getReceivedMessages(), registry);
	}

	/**
	 * Helper method to write assertions on sms using fluent API. For example:
	 *
	 * <pre>
	 * {@code
	 * assertThat(smppServer).receivedMessages()
	 *   .count(is(1))
	 *   .message(0)
	 *     .content(is("sms content"))
	 *     .from()
	 *       .number(is(INTERNATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
	 *     .to()
	 *       .number(is(NATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.UNKNOWN))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN));
	 * }
	 * </pre>
	 *
	 * @param smsServer
	 *            SMS server that stores received messages
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentReceivedSmsAssert<SubmitSm> assertThat(SmppServerSimulator<ogham.testing.org.jsmpp.bean.SubmitSm> smsServer) {
		return assertThat(smsServer, new FailImmediatelyRegistry());
	}


	/**
	 * Helper method to write assertions on sms using fluent API. For example:
	 *
	 * <pre>
	 * {@code
	 * assertAll(registry ->
	 *   assertThat(smppServer, registry).receivedMessages()
	 *     .count(is(1))
	 *     .message(0)
	 *       .content(is("sms content"))
	 *       .from()
	 *         .number(is(INTERNATIONAL_PHONE_NUMBER))
	 *         .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 *         .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
	 *       .to()
	 *         .number(is(NATIONAL_PHONE_NUMBER))
	 *         .typeOfNumber(is(TypeOfNumber.UNKNOWN))
	 *         .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)));
	 * }
	 * </pre>
	 *
	 * <p>
	 * This method is used in combination of {@link #assertAll(Consumer...)} in
	 * order to report all exceptions/assertion failures at the end instead of
	 * stopping at the first failure.
	 *
	 * @param smsServer
	 *            SMS server that stores received messages
	 * @param registry
	 *            the registry used to register assertions
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentReceivedSmsAssert<SubmitSm> assertThat(SmppServerSimulator<ogham.testing.org.jsmpp.bean.SubmitSm> smsServer, AssertionRegistry registry) {
		List<SubmitSm> receivedMessages = smsServer.getReceivedMessages().stream()
				.map(SubmitSmAdapter::new)
				.collect(Collectors.toList());
		return new FluentReceivedSmsAssert<>(receivedMessages, registry);
	}

	/**
	 * Helper method to write assertions on sms using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
	 * assertThat(smppServer.getReceivedMessages())
	 *   .count(is(1))
	 *   .message(0)
	 *     .content(is("sms content"))
	 *     .from()
	 *       .number(is(INTERNATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
	 *     .to()
	 *       .number(is(NATIONAL_PHONE_NUMBER))
	 *       .typeOfNumber(is(TypeOfNumber.UNKNOWN))
	 *       .numberPlanIndicator(is(NumberingPlanIndicator.ISDN));
	 * }
	 * </pre>
	 * 
	 * @param receivedSms
	 *            The list of messages received by the SMS server
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentSmsListAssert<Void, SubmitSm> assertThat(List<SubmitSm> receivedSms) {
		return assertThat(receivedSms, new FailImmediatelyRegistry());
	}

	/**
	 * Helper method to write assertions on sms using fluent API. For example:
	 * 
	 * <pre>
	 * {@code
	 * assertAll(registry ->
	 *   assertThat(smppServer.getReceivedMessages(), registry)
	 *     .count(is(1))
	 *     .message(0)
	 *       .content(is("sms content"))
	 *       .from()
	 *         .number(is(INTERNATIONAL_PHONE_NUMBER))
	 *         .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 *         .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
	 *       .to()
	 *         .number(is(NATIONAL_PHONE_NUMBER))
	 *         .typeOfNumber(is(TypeOfNumber.UNKNOWN))
	 *         .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)));
	 * }
	 * </pre>
	 * 
	 * <p>
	 * This method is used in combination of {@link #assertAll(Consumer...)} in
	 * order to report all exceptions/assertion failures at the end instead of
	 * stopping at the first failure.
	 * 
	 * @param receivedSms
	 *            The list of messages received by the SMS server
	 * @param registry
	 *            the registry used to register assertions
	 * @return builder for fluent assertions on received messages
	 */
	public static FluentSmsListAssert<Void, SubmitSm> assertThat(List<SubmitSm> receivedSms, AssertionRegistry registry) {
		return new FluentSmsListAssert<>(receivedSms, null, registry);
	}

	/**
	 * Register all assertions in order to report all failures/failed assertions
	 * at once instead of reporting error one by one.
	 * 
	 * <pre>
	 * {@code
	 * assertAll(registry -> 
	 *   assertThat(smppServer.getReceivedMessages(), registry)
	 *     .count(is(1))
	 *     .message(0)
	 *       .content(is("sms content"))
	 *       .from()
	 *         .number(is(INTERNATIONAL_PHONE_NUMBER))
	 *         .typeOfNumber(is(TypeOfNumber.INTERNATIONAL))
	 *         .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)).and()
	 *       .to()
	 *         .number(is(NATIONAL_PHONE_NUMBER))
	 *         .typeOfNumber(is(TypeOfNumber.UNKNOWN))
	 *         .numberPlanIndicator(is(NumberingPlanIndicator.ISDN)));
	 * }
	 * </pre>
	 * 
	 * 
	 * @param executables
	 *            the list of functions to register
	 */
	@SafeVarargs
	public static void assertAll(Consumer<AssertionRegistry>... executables) {
		AssertionRegistry registry = new FailAtEndRegistry();
		for (Consumer<AssertionRegistry> executable : executables) {
			executable.accept(registry);
		}
		registry.execute();
	}

	private OghamAssertions() {
		super();
	}

}
