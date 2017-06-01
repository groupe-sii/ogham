package fr.sii.ogham.core.charset;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * Detector that always gives the same charset. The charset is provided at
 * construction. If no charset is specified then UTF-8 is used.
 * 
 * @author Aurélien Baudet
 *
 */
public class FixedCharsetDetector implements CharsetDetector {
	private static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * The charset to use
	 */
	private final Charset charset;

	/**
	 * Initialize the provider with the default charset (UTF-8)
	 */
	public FixedCharsetDetector() {
		this(Charset.forName(DEFAULT_CHARSET));
	}

	/**
	 * Initialize the provider with the given charset.
	 * 
	 * @param charset
	 *            the charset to use
	 */
	public FixedCharsetDetector(Charset charset) {
		super();
		this.charset = charset;
	}

	@Override
	public Charset detect(String str) {
		return charset;
	}

	@Override
	public Charset detect(byte[] bytes) {
		return charset;
	}

	@Override
	public Charset detect(InputStream stream) {
		return charset;
	}

	@Override
	public List<CharsetMatch> detectAll(String str) {
		return Arrays.<CharsetMatch>asList(new SimpleCharsetMatch(charset, 0));
	}

	@Override
	public List<CharsetMatch> detectAll(byte[] bytes) {
		return Arrays.<CharsetMatch>asList(new SimpleCharsetMatch(charset, 0));
	}

	@Override
	public List<CharsetMatch> detectAll(InputStream stream) {
		return Arrays.<CharsetMatch>asList(new SimpleCharsetMatch(charset, 0));
	}
}
