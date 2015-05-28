package fr.sii.notification.core.template;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.sii.notification.core.util.IOUtils;


/**
 * Basic implementation of a template that simply stores a reference to the
 * provided bytes.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleTemplate implements Template {

	private byte[] bytes;

	public SimpleTemplate(InputStream stream) throws IOException {
		super();
		this.bytes = IOUtils.toByteArray(stream);
	}

	public SimpleTemplate(byte[] bytes) {
		super();
		this.bytes = bytes;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes);
	}

}
