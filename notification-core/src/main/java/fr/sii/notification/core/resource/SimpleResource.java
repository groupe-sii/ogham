package fr.sii.notification.core.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import fr.sii.notification.core.util.IOUtils;

/**
 * Basic implementation of a resource that simply stores a reference to the
 * provided bytes.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleResource implements Resource {

	private byte[] bytes;

	public SimpleResource(InputStream stream) throws IOException {
		super();
		this.bytes = IOUtils.toByteArray(stream);
	}

	/**
	 * Initialize the resource with the provided bytes. The bytes are copied
	 * into a new array to prevent security leaks.
	 * 
	 * @param bytes
	 *            the bytes of the resource
	 */
	public SimpleResource(byte[] bytes) {
		super();
		this.bytes = Arrays.copyOf(bytes, bytes.length);
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes);
	}

}
