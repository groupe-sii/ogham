package fr.sii.ogham.core.charset;

import java.nio.charset.Charset;

public class SimpleCharsetMatch implements CharsetMatch {
	private final Charset possibleCharset;
	private final int confidence;

	public SimpleCharsetMatch(Charset possibleCharset, int confidence) {
		super();
		this.possibleCharset = possibleCharset;
		this.confidence = confidence;
	}

	@Override
	public Charset getPossibleCharset() {
		return possibleCharset;
	}

	@Override
	public int getConfidence() {
		return confidence;
	}
}
