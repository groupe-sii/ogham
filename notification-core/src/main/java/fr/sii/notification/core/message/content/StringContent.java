package fr.sii.notification.core.message.content;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public class StringContent implements Content {

	private String content;
	
	private MimeType mimetype;
	
	public StringContent(String content, MimeType mimetype) {
		super();
		this.content = content;
		this.mimetype = mimetype;
	}
	
	public StringContent(String content, String mimetype) {
		this(content, toMimeType(mimetype));
	}

	public StringContent(String content) {
		this(content, "text/plain");
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return content;
	}

	public MimeType getMimetype() {
		return mimetype;
	}

	public void setMimetype(MimeType mimetype) {
		this.mimetype = mimetype;
	}
	
	private static MimeType toMimeType(String mimetype) {
		try {
			return new MimeType(mimetype);
		} catch (MimeTypeParseException e) {
			throw new IllegalArgumentException("Cannot initialize message content due to invalid mimetype", e);
		}
	}

}
