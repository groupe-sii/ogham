package fr.sii.ogham.core.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface that defines the behavior of a template. The most basic feature is
 * to provide access to the template content. The template content can be
 * anything to read that's why it provides an {@link InputStream}.
 * 
 * @author Aurélien Baudet
 */
public interface Resource {
	/**
	 * Stream that contains the template content. The input stream MUST be
	 * readable several times so every time this method is called, a new
	 * {@link InputStream} is created. You MUST close the input stream after
	 * using it.
	 * 
	 * @return an input stream that points to the template content
	 * @throws IOException
	 *             when the input stream can't be accessed or read
	 */
	public InputStream getInputStream() throws IOException;
}
