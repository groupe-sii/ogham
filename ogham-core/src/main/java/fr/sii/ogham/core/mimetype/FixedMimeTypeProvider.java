package fr.sii.ogham.core.mimetype;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeParseException;

import java.io.File;
import java.io.InputStream;

/**
 * This mime type provider always return a fixed Mime Type. It is to be used as
 * fallback when Mime Type can't be detected. By default, the provided Mime Type
 * is 'text/plain'. You can also provide your own Mime Type
 */
public final class FixedMimeTypeProvider implements MimeTypeProvider {

	private final MimeType mimetype;

	/**
	 * Initialize the provider with <code>text/plain</code> Mime Type
	 */
	public FixedMimeTypeProvider() {
		try {
			mimetype = new ParsedMimeType("text/plain");
		} catch (MimeTypeParseException e) {
			throw new AssertionError("This should never happen as 'text/plain' is a valid MIME type", e);
		}
	}

	/**
	 * Constructor that uses the provided Mime Type.
	 * 
	 * @param mimetype
	 *            the Mime Type to use as string
	 * @throws MimeTypeParseException
	 *             when Mime Type is not valid
	 */
	public FixedMimeTypeProvider(String mimetype) throws MimeTypeParseException {
		this(new ParsedMimeType(mimetype));
	}

	/**
	 * Constructor that uses the provided Mime Type.
	 * 
	 * @param mimetype
	 *            the Mime Type to use
	 */
	public FixedMimeTypeProvider(MimeType mimetype) {
		this.mimetype = mimetype;
	}

	@Override
	public MimeType getMimeType(final File file) throws MimeTypeDetectionException {
		return mimetype;
	}

	@Override
	public MimeType getMimeType(final String filePath) throws MimeTypeDetectionException {
		return mimetype;
	}

	@Override
	public MimeType detect(final InputStream stream) throws MimeTypeDetectionException {
		return mimetype;
	}

	@Override
	public MimeType detect(final String content) throws MimeTypeDetectionException {
		return mimetype;
	}

}
