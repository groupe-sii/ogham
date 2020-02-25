package fr.sii.ogham.core.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;
import fr.sii.ogham.core.util.IOUtils;

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

	/**
	 * Initialize the resource with the provided name and bytes that are
	 * immediately read from the stream. The bytes are copied into a new array
	 * to prevent security leaks.
	 * 
	 * @param name
	 *            the name of the resource
	 * @param stream
	 *            the stream that is read
	 * @throws IOException
	 *             when the input stream can't be read
	 */
	public ByteResource(String name, InputStream stream) throws IOException {
		this(name, IOUtils.toByteArray(stream));
	}

	/**
	 * Initialize the resource with the provided name and bytes. The bytes are
	 * copied into a new array to prevent security leaks.
	 * 
	 * @param name
	 *            the name of the resource
	 * @param bytes
	 *            the bytes of the resource
	 */
	public ByteResource(String name, byte[] bytes) {
		super();
		this.name = name;
		this.bytes = Arrays.copyOf(bytes, bytes.length);
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
		return new HashCodeBuilder().append(name, bytes).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("name", "bytes").isEqual();
	}
}