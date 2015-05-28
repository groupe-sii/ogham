package fr.sii.notification.email.attachment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;
import fr.sii.notification.core.util.IOUtils;


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
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(bytes).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("name", "bytes").isEqual();
	}
}