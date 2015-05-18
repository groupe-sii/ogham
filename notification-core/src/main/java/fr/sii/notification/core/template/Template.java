package fr.sii.notification.core.template;

import java.io.IOException;
import java.io.InputStream;

public interface Template {
	/**
	 * Stream that contains the template content. The input stream MUST be
	 * readable several times. You MUST close the input stream after using it.
	 * 
	 * @return an input stream that points to the template content
	 * @throws IOException
	 *             when the input stream can't be accessed or read
	 */
	public InputStream getInputStream() throws IOException;
}
