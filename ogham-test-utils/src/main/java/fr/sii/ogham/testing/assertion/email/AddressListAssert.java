package fr.sii.ogham.testing.assertion.email;

import static fr.sii.ogham.testing.assertion.AssertionHelper.assertThat;
import static fr.sii.ogham.testing.assertion.OghamAssertions.usingContext;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.assertion.HasParent;

public class AddressListAssert<P> extends HasParent<P> {
	/**
	 * The list of addresses that will be used for assertions
	 */
	private List<AddressesWithContext> actual;

	public AddressListAssert(List<AddressesWithContext> actual, P parent) {
		super(parent);
		this.actual = actual;
	}

	/**
	 * Make assertions on the email addresses (without personal).
	 * 
	 * <p>
	 * Email address parts can be checked independently:
	 * <ul>
	 * <li>address: "recipient@sii.fr"</li>
	 * <li>personal: "Recipient Name"</li>
	 * <li>textual: {@code "Recipient Name <recipient@sii.fr>"}</li>
	 * </ul>
	 * 
	 * <pre>
	 * .receivedMessages().message(0).to()
	 *    .address(hasItems("recipient@sii.fr"))
	 * </pre>
	 * 
	 * Will check if the list of recipient email addresses of the first message
	 * contains exactly "recipient@sii.fr".
	 * 
	 * <pre>
	 * .receivedMessages().every().to()
	 *    .address(hasItems("recipient@sii.fr"))
	 * </pre>
	 * 
	 * Will check if the list of recipient email addresses of every message
	 * contains exactly "recipient@sii.fr".
	 * 
	 * @param matcher
	 *            the assertion to apply on the email addresses
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public AddressListAssert<P> address(Matcher<? super Iterable<String>> matcher) {
		String desc = "email addresses of '${fieldName}' field of message ${messageIndex}";
		for (AddressesWithContext addresses : actual) {
			List<String> addressesStr = new ArrayList<>();
			for (InternetAddress address : addresses.getAddresses()) {
				addressesStr.add(address.getAddress());
			}
			assertThat(addressesStr, usingContext(desc, addresses, matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the email addresses (textual form).
	 * 
	 * <p>
	 * Email address parts can be checked independently:
	 * <ul>
	 * <li>address: "recipient@sii.fr"</li>
	 * <li>personal: "Recipient Name"</li>
	 * <li>textual: {@code "Recipient Name <recipient@sii.fr>"}</li>
	 * </ul>
	 * 
	 * <pre>
	 * .receivedMessages().message(0).to()
	 *    .textual(hasItems("Recipient Name &lt;recipient@sii.fr&gt;"))
	 * </pre>
	 * 
	 * Will check if the list of recipient email addresses (personal with
	 * address) of the first message contains exactly {@code "Recipient Name
	 * <recipient@sii.fr>"}.
	 * 
	 * <pre>
	 * .receivedMessages().every().to()
	 *    .textual(hasItems("Recipient Name &lt;recipient@sii.fr&gt;"))
	 * </pre>
	 * 
	 * Will check if the list of recipient email addresses (personal with
	 * address) of every message contains exactly {@code "Recipient Name
	 * <recipient@sii.fr>"}.
	 * 
	 * @param matcher
	 *            the assertion to apply on the email addresses
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public AddressListAssert<P> textual(Matcher<? super Iterable<String>> matcher) {
		String desc = "textual addresses of '${fieldName}' field of message ${messageIndex}";
		for (AddressesWithContext addresses : actual) {
			List<String> addressesStr = new ArrayList<>();
			for (InternetAddress address : addresses.getAddresses()) {
				addressesStr.add(address.toString());
			}
			assertThat(addressesStr, usingContext(desc, addresses, matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the email address types.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).to()
	 *    .type(hasItems("rfc822"))
	 * </pre>
	 * 
	 * Will check if the list of recipient email address types of the first
	 * message contains exactly "rfc822".
	 * 
	 * <pre>
	 * .receivedMessages().every().to()
	 *    .type(hasItems("rfc822"))
	 * </pre>
	 * 
	 * Will check if the list of recipient email address types of every message
	 * contains exactly "rfc822".
	 * 
	 * @param matcher
	 *            the assertion to apply on the email addresses
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public AddressListAssert<P> type(Matcher<? super Iterable<String>> matcher) {
		String desc = "address types of '${fieldName}' field of message ${messageIndex}";
		for (AddressesWithContext addresses : actual) {
			List<String> addressesStr = new ArrayList<>();
			for (InternetAddress address : addresses.getAddresses()) {
				addressesStr.add(address.getType());
			}
			assertThat(addressesStr, usingContext(desc, addresses, matcher));
		}
		return this;
	}

	/**
	 * Make assertions on the email addresses (personal information only).
	 * 
	 * <p>
	 * Email address parts can be checked independently:
	 * <ul>
	 * <li>address: "recipient@sii.fr"</li>
	 * <li>personal: "Recipient Name"</li>
	 * <li>textual: {@code "Recipient Name <recipient@sii.fr>"}</li>
	 * </ul>
	 * 
	 * <pre>
	 * .receivedMessages().message(0).to()
	 *    .personal(hasItems("Recipient Name"))
	 * </pre>
	 * 
	 * Will check if the list of recipient email addresses (personal
	 * information) of the first message contains exactly "Recipient Name".
	 * 
	 * <pre>
	 * .receivedMessages().every().to()
	 *    .personal(hasItems("Recipient Name"))
	 * </pre>
	 * 
	 * Will check if the list of recipient email addresses (personal
	 * information) of every message contains exactly "Recipient Name".
	 * 
	 * @param matcher
	 *            the assertion to apply on the email addresses
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public AddressListAssert<P> personal(Matcher<? super Iterable<String>> matcher) {
		String desc = "personal of '${fieldName}' field of message ${messageIndex}";
		for (AddressesWithContext addresses : actual) {
			List<String> addressesStr = new ArrayList<>();
			for (InternetAddress address : addresses.getAddresses()) {
				addressesStr.add(address.getPersonal());
			}
			assertThat(addressesStr, usingContext(desc, addresses, matcher));
		}
		return this;
	}

}
