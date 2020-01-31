package fr.sii.ogham.testing.sms.simulator.decode;

/**
 * Wrapper that decodes bytes using Cloudhopper
 * {@link com.cloudhopper.commons.charset.Charset}
 * 
 * @author Aurélien Baudet
 *
 */
public class CloudhopperCharsetAdapter implements Charset {
	private final com.cloudhopper.commons.charset.Charset cloudhopperCharset;

	public CloudhopperCharsetAdapter(com.cloudhopper.commons.charset.Charset cloudhopperCharset) {
		super();
		this.cloudhopperCharset = cloudhopperCharset;
	}

	@Override
	public String decode(byte[] bytes) {
		return cloudhopperCharset.decode(bytes);
	}
}
