package fr.sii.ogham.testing.assertion.internal;

import static fr.sii.ogham.testing.assertion.internal.helper.ImplementationFinder.findSender;
import static fr.sii.ogham.testing.assertion.util.AssertionHelper.assertThat;

import org.hamcrest.Matcher;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.testing.util.HasParent;

/**
 * Helper class to make assertions on Cloudhopper instance created by Ogham.
 * 
 * For example, to ensure that particular configuration is used for emails:
 * 
 * <pre>
 * {@code
 * cloudhopper()
 *   .host(is("localhost")
 *   .splitter()
 *      .enabled(is(true))
 * }
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class CloudhopperAssertions extends HasParent<MessagingServiceAssertions> {
	private final CloudhopperSMPPSender cloudhopperSender;

	public CloudhopperAssertions(MessagingServiceAssertions parent, CloudhopperSMPPSender cloudhopperSender) {
		super(parent);
		this.cloudhopperSender = cloudhopperSender;
	}

	/**
	 * Ensures that configured host is correct.
	 * 
	 * @param matcher
	 *            the matcher to ensure that host is correct.
	 * @return this instance for fluent chaining
	 */
	public CloudhopperAssertions host(Matcher<String> matcher) {
		assertThat(getHost(cloudhopperSender), matcher);
		return this;
	}

	/**
	 * Ensures that configured port is correct.
	 * 
	 * @param matcher
	 *            the matcher to ensure that port is correct.
	 * @return this instance for fluent chaining
	 */
	public CloudhopperAssertions port(Matcher<Integer> matcher) {
		assertThat(getPort(cloudhopperSender), matcher);
		return this;
	}

	/**
	 * Ensures that configured SMPP version is correct.
	 * 
	 * @param matcher
	 *            the matcher to ensure that SMPP version in is correct.
	 * @return this instance for fluent chaining
	 */
	public CloudhopperAssertions interfaceVersion(Matcher<Byte> matcher) {
		assertThat(cloudhopperSender.getSmppSessionConfiguration().getInterfaceVersion(), matcher);
		return this;
	}

	/**
	 * Make assertions on "User Data" to ensure that it is correctly configured.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * userData()
	 *   .useShortMessage(is(true))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public CloudhopperUserDataAssertions userData() {
		return new CloudhopperUserDataAssertions(this, cloudhopperSender);
	}

	/**
	 * Make assertions on splitter instance to ensure that it is correctly
	 * configured.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * splitter()
	 *   .enabled(is(true))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public CloudhopperSplitterAssertions splitter() {
		return new CloudhopperSplitterAssertions(this, cloudhopperSender);
	}

	/**
	 * Find instance of {@link CloudhopperSMPPSender}.
	 * 
	 * @param messagingService
	 *            the messaging service
	 * @return the found instance
	 */
	public static CloudhopperSMPPSender getCloudhopperSender(MessagingService messagingService) {
		return findSender(messagingService, CloudhopperSMPPSender.class);
	}

	private static String getHost(CloudhopperSMPPSender cloudhopperSender) {
		return cloudhopperSender.getSmppSessionConfiguration().getHost();
	}

	private static int getPort(CloudhopperSMPPSender cloudhopperSender) {
		return cloudhopperSender.getSmppSessionConfiguration().getPort();
	}
}
