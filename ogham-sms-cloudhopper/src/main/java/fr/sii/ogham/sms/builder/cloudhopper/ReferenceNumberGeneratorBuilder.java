package fr.sii.ogham.sms.builder.cloudhopper;

import java.util.Random;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.sms.splitter.RandomReferenceNumberGenerator;
import fr.sii.ogham.sms.splitter.ReferenceNumberGenerator;

/**
 * In the cellular phone industry, mobile phones and their networks sometimes
 * support concatenated short message service (or concatenated SMS) to overcome
 * the limitation on the number of characters that can be sent in a single SMS
 * text message transmission (which is usually 160). Using this method, long
 * messages are split into smaller messages by the sending device and recombined
 * at the receiving end. Each message is then billed separately. When the
 * feature works properly, it is nearly transparent to the user, appearing as a
 * single long text message.
 * 
 * <p>
 * One way of sending concatenated SMS (CSMS) is to split the message into 153
 * 7-bit character parts (134 octets), and sending each part with a User Data
 * Header (UDH) tacked onto the beginning. A UDH can be used for various
 * purposes and its contents and size varies accordingly, but a UDH for
 * concatenating SMSes look like this:
 * 
 * <ul>
 * <li>Field 1 (1 octet): Length of User Data Header, in this case 05.</li>
 * <li>Field 2 (1 octet): Information Element Identifier, equal to 00
 * (Concatenated short messages, 8-bit reference number)</li>
 * <li>Field 3 (1 octet): Length of the header, excluding the first two fields;
 * equal to 03</li>
 * <li>Field 4 (1 octet): 00-FF, CSMS reference number, must be same for all the
 * SMS parts in the CSMS</li>
 * <li>Field 5 (1 octet): 00-FF, total number of parts. The value shall remain
 * constant for every short message which makes up the concatenated short
 * message. If the value is zero then the receiving entity shall ignore the
 * whole information element</li>
 * <li>Field 6 (1 octet): 00-FF, this part's number in the sequence. The value
 * shall start at 1 and increment for every short message which makes up the
 * concatenated short message. If the value is zero or greater than the value in
 * Field 5 then the receiving entity shall ignore the whole information element.
 * [ETSI Specification: GSM 03.40 Version 5.3.0: July 1996]</li>
 * </ul>
 * 
 * <p>
 * It is possible to use a 16 bit CSMS reference number in order to reduce the
 * probability that two different concatenated messages are sent with identical
 * reference numbers to a receiver. In this case, the User Data Header shall be:
 * 
 * <ul>
 * <li>Field 1 (1 octet): Length of User Data Header (UDL), in this case
 * 06.</li>
 * <li>Field 2 (1 octet): Information Element Identifier, equal to 08
 * (Concatenated short messages, 16-bit reference number)</li>
 * <li>Field 3 (1 octet): Length of the header, excluding the first two fields;
 * equal to 04</li>
 * <li>Field 4 (2 octets): 0000-FFFF, CSMS reference number, must be same for
 * all the SMS parts in the CSMS</li>
 * <li>Field 5 (1 octet): 00-FF, total number of parts. The value shall remain
 * constant for every short message which makes up the concatenated short
 * message. If the value is zero then the receiving entity shall ignore the
 * whole information element</li>
 * <li>Field 6 (1 octet): 00-FF, this part's number in the sequence. The value
 * shall start at 1 and increment for every short message which makes up the
 * concatenated short message. If the value is zero or greater than the value in
 * Field 5 then the receiving entity shall ignore the whole information element.
 * [ETSI Specification: GSM 03.40 Version 5.3.0: July 1996]</li>
 * </ul>
 * 
 * 
 * Configures reference number generation strategy.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class ReferenceNumberGeneratorBuilder extends AbstractParent<MessageSplitterBuilder> implements Builder<ReferenceNumberGenerator> {
	private Random random;
	private ReferenceNumberGenerator custom;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method.
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public ReferenceNumberGeneratorBuilder(MessageSplitterBuilder parent) {
		super(parent);
	}

	/**
	 * Uses a random number for reference number.
	 * 
	 * It uses an instance of {@link Random} to generate random numbers.
	 * 
	 * @return this instance for fluent chaining
	 */
	@SuppressWarnings("squid:S2245")
	public ReferenceNumberGeneratorBuilder random() {
		return random(new Random());
	}

	/**
	 * Uses a random number for reference number. The provided {@link Random}
	 * instance is used to generate random numbers.
	 * 
	 * <p>
	 * If this method is called several times, only the last {@link Random}
	 * instance is used.
	 * 
	 * <p>
	 * If random parameter is {@code null} then any previously registered
	 * {@link Random} instance won't be used.
	 * 
	 * @param random
	 *            the {@link Random} instance used to generate random numbers
	 * @return this instance for fluent chaining
	 */
	public ReferenceNumberGeneratorBuilder random(Random random) {
		this.random = random;
		return this;
	}

	/**
	 * Uses a custom reference number generation strategy. This strategy takes
	 * precedence over any other generation strategy.
	 * 
	 * <p>
	 * If this method is called several times, only the last registered
	 * generator is used.
	 * 
	 * <p>
	 * If custom parameter is {@code null}, the custom strategy is disabled.
	 * Other previously configured strategies (like {@link #random()}) will be
	 * used instead.
	 * 
	 * @param custom
	 *            custom reference number generation strategy
	 * @return this instance for fluent chaining
	 */
	public ReferenceNumberGeneratorBuilder generator(ReferenceNumberGenerator custom) {
		this.custom = custom;
		return this;
	}

	@Override
	public ReferenceNumberGenerator build() {
		if (custom != null) {
			return custom;
		}
		if (random != null) {
			return new RandomReferenceNumberGenerator(random);
		}
		return new RandomReferenceNumberGenerator();
	}
}
