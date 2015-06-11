package fr.sii.notification.core.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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

	public SimpleResource(byte[] bytes) {
		super();
		this.bytes = bytes;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes);
	}

}
