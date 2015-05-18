package fr.sii.notification.core.template;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class SimpleTemplate implements Template {

	private InputStream stream;
	
	private int readLimit;

	private boolean marked;

	private byte[] bytes;
	
	public SimpleTemplate(InputStream stream, int readLimit) {
		super();
		this.stream = stream;
		this.readLimit = readLimit;
		this.marked = false;
	}

	public SimpleTemplate(InputStream stream) {
		this(stream, Integer.MAX_VALUE);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if(stream.markSupported()) {
			// mark the stream to be read again from start if asked again
			if(!marked) {
				stream.mark(readLimit);
				marked = true;
			}
			stream.reset();
			return stream;
		} else {
			// if reset can't be used => read fully and store into a byte array
			if(bytes==null) {
				bytes = IOUtils.toByteArray(stream);
			}
			return new ByteArrayInputStream(bytes);
		}
	}

}
