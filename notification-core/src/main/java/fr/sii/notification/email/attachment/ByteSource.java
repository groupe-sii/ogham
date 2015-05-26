package fr.sii.notification.email.attachment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * Basic implementation of a Source that simply stores a reference to the
 * provided bytes.
 * 
 * @author Aurélien Baudet
 *
 */
public class ByteSource implements Source {
	/**
	 * The content of the attachment as array of bytes
	 */
	private byte[] bytes;

	/**
	 * The name of the attachment
	 */
	private String name;

	public ByteSource(String name, InputStream stream) throws IOException {
		super();
		this.name = name;
		this.bytes = IOUtils.toByteArray(stream);
	}

	public ByteSource(String name, byte[] bytes) {
		super();
		this.name = name;
		this.bytes = bytes;
	}

	public InputStream getStream() {
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public String getName() {
		return name;
	}

	public byte[] getBytes() {
		return bytes;
	}
}