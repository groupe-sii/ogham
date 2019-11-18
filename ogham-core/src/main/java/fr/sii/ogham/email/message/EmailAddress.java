package fr.sii.ogham.email.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Represents an email address. javax.mail.internet.InternetAddress also
 * provides the same feature but we don't want to be sticked to a particular
 * implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public class EmailAddress {
	private static final String QUOTED = "([\"](?<personal1>[^\"]+)[\"])";
	private static final String UNQUOTED = "(?<personal2>.+)";
	private static final String ADDRESS_WITH_TAG = "<(?<address>[^>]+)>";
	private static final Pattern SIMPLE_ADDRESS_WITH_PERSONAL = Pattern.compile("\\s*("+QUOTED+"|"+UNQUOTED+")\\s* "+ADDRESS_WITH_TAG);

	/**
	 * The email address part (is of the form "user@domain.host")
	 */
	private String address;

	/**
	 * The user name part (can be anything)
	 */
	private String personal;

	/**
	 * Initialize the address with only the email address part (no personal). It
	 * is of the form "user@domain.host" or and "Personal Name
	 * &lt;user@host.domain&gt;" formats.
	 * 
	 * When using an address of the form "Personal Name
	 * &lt;user@host.domain&gt;", the address is parsed to split into two
	 * fields:
	 * <ul>
	 * <li>email address ("user@domain.host"), accessible through
	 * {@link #getAddress()}</li>
	 * <li>personal name if any ("Personal Name"), accessible through
	 * {@link #getPersonal()}</li>
	 * </ul>
	 * 
	 * <strong>IMPORTANT: The parsing of address and personal only supports
	 * simple cases. The cases defined in RFC822 and RFC2822 with comments,
	 * mailbox and group are not supported.</strong> If you need this feature,
	 * you need to use an external parser in order to extract information. If
	 * an address containing mailbox or group is provided, then no personal name
	 * is extracted but address is the full string.
	 * 
	 * @param rawAddress
	 *            the email address part
	 */
	public EmailAddress(String rawAddress) {
		super();
		EmailAddress parsed = parse(rawAddress);
		address = parsed.getAddress();
		personal = parsed.getPersonal();
	}

	/**
	 * Initialize the address with the email address and the personal parts.
	 * No parsing is applied.
	 * 
	 * @param address
	 *            the email address part, it is of the form "user@domain.host"
	 * @param personal
	 *            the personal part
	 */
	public EmailAddress(String address, String personal) {
		super();
		this.address = address;
		this.personal = personal;
	}

	public String getAddress() {
		return address;
	}

	public String getPersonal() {
		return personal;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (personal != null && !personal.isEmpty()) {
			builder.append(personal).append(" ");
		}
		builder.append("<").append(address).append(">");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(address, personal).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("address", "personal").isEqual();
	}

	public static EmailAddress parse(String rawAddress) {
		if(rawAddress == null) {
			throw new IllegalArgumentException("Address can't be null");
		}
		Matcher matcher = SIMPLE_ADDRESS_WITH_PERSONAL.matcher(rawAddress);
		if(matcher.matches()) {
			String address = matcher.group("address");
			String personal = matcher.group("personal1") != null ? matcher.group("personal1") : matcher.group("personal2");
			return new EmailAddress(address, personal);
		}
		return new EmailAddress(rawAddress, null);
	}
}
