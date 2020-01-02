package fr.sii.ogham.sms.sender.impl.cloudhopper.encoder;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;

/**
 * Wrapper of {@link Charset} that tracks the name of the charset.
 * 
 * @author Aurélien Baudet
 *
 */
public class NamedCharset {
	private final String charsetName;
	private final Charset charset;

	/**
	 * Initializes with the name and the corresponding charset.
	 * 
	 * @param charsetName
	 *            the name of the charset
	 * @param charset
	 *            the corresponding charset
	 */
	public NamedCharset(String charsetName, Charset charset) {
		super();
		this.charsetName = charsetName;
		this.charset = charset;
	}

	/**
	 * @return the charset name
	 */
	public String getCharsetName() {
		return charsetName;
	}

	/**
	 * @return the corresponding charset
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * Convenient method that creates a {@link NamedCharset} from the charset
	 * name.
	 * 
	 * @param charsetName
	 *            the charset name
	 * @return the charset name with the corresponding {@link Charset}
	 */
	public static NamedCharset from(String charsetName) {
		return new NamedCharset(charsetName, CharsetUtil.map(charsetName));
	}

	@Override
	public String toString() {
		return charsetName + " <-> " + charset;
	}
}