package fr.sii.ogham.sms.splitter;

import fr.sii.ogham.sms.encoder.Encoded;

/**
 * Represents a segment that has been encoded.
 * 
 * @author Aurélien Baudet
 *
 */
public class EncodedSegment implements Segment {
	private final Encoded encoded;

	/**
	 * Initializes the segment with the encoded content.
	 * 
	 * @param encoded
	 *            the encoded content for the segment
	 */
	public EncodedSegment(Encoded encoded) {
		super();
		this.encoded = encoded;
	}

	/**
	 * @return the encoded content
	 */
	public Encoded getEncoded() {
		return encoded;
	}

	@Override
	public byte[] getBytes() {
		return encoded.getBytes();
	}
}
