package fr.sii.notification.core.template;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * Basic implementation of a template that simply stores a reference to the
 * provided {@link InputStream}. As the {@link Template} contract indicates that
 * the stream MUST be read several times, there are two ways of handling it
 * according to the stream implementation:
 * <ul>
 * <li>Use the reset feature (only possible if
 * {@link InputStream#markSupported()} returns true</li>
 * <li>Completely read the stream and store it in memory (in byte array)
 * otherwise</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
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
		if (stream.markSupported()) {
			// mark the stream to be read again from start if asked again
			if (!marked) {
				stream.mark(readLimit);
				marked = true;
			}
			stream.reset();
			return stream;
		} else {
			// if reset can't be used => read fully and store into a byte array
			if (bytes == null) {
				bytes = IOUtils.toByteArray(stream);
			}
			return new ByteArrayInputStream(bytes);
		}
	}

}
