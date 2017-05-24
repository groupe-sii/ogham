package fr.sii.ogham.core.charset;

import java.nio.charset.Charset;

/**
 * Provider that always gives the same charset. The charset is provided at
 * construction. If no charset is specified then UTF-8 is used.
 * 
 * @author Aurélien Baudet
 *
 */
public class FixedCharsetProvider implements CharsetProvider {
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * The charset to use
	 */
	private final Charset charset;

	/**
	 * Initialize the provider with the default charset (UTF-8)
	 */
	public FixedCharsetProvider() {
		this(Charset.forName(DEFAULT_CHARSET));
	}

	/**
	 * Initialize the provider with the given charset.
	 * 
	 * @param charset
	 *            the charset to use
	 */
	public FixedCharsetProvider(Charset charset) {
		super();
		this.charset = charset;
	}

	@Override
	public Charset getCharset(String str) {
		return charset;
	}
}
