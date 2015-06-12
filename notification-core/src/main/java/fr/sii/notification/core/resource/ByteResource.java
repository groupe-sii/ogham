package fr.sii.notification.core.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;
import fr.sii.notification.core.util.IOUtils;

/**
 * Basic implementation of a {@link NamedResource} that simply stores a
 * reference to the provided bytes.
 * 
 * @author Aurélien Baudet
 *
 */
public class ByteResource implements NamedResource {
	/**
	 * The content of the resource as array of bytes
	 */
	private byte[] bytes;

	/**
	 * The name of the resource
	 */
	private String name;

	public ByteResource(String name, InputStream stream) throws IOException {
		super();
		this.name = name;
		this.bytes = IOUtils.toByteArray(stream);
	}

	public ByteResource(String name, byte[] bytes) {
		super();
		this.name = name;
		this.bytes = bytes;
	}

	@Override
	public InputStream getInputStream() {
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