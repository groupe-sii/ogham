package fr.sii.ogham.core.mimetype.validation;

import java.io.File;
import java.io.InputStream;
import java.util.function.Predicate;

import fr.sii.ogham.core.mimetype.MimeType;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.mimetype.UnallowedMimeTypeException;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;

/**
 * Decorator that asks delegate to detect the mimetype and then validates it
 * using a {@link Predicate}. If the mimetype is not allowed, a
 * {@link UnallowedMimeTypeException} is raised.
 * 
 * @author Aurélien Baudet
 *
 */
public class AllowedMimetypeDecorator implements MimeTypeProvider {
	private final Predicate<MimeType> allowedMimetype;
	private final MimeTypeProvider delegate;

	/**
	 * Initializes with the {@link Predicate} used to validate the detected
	 * mimetypes. Detection is delegated to the provided
	 * {@link MimeTypeProvider}.
	 * 
	 * @param allowedMimetype
	 *            the predicate used to validate the mimetypes
	 * @param delegate
	 *            the provider that really detects the mimetypes
	 */
	public AllowedMimetypeDecorator(Predicate<MimeType> allowedMimetype, MimeTypeProvider delegate) {
		super();
		this.allowedMimetype = allowedMimetype;
		this.delegate = delegate;
	}

	@Override
	public MimeType getMimeType(File file) throws MimeTypeDetectionException {
		return validate(delegate.getMimeType(file));
	}

	@Override
	public MimeType getMimeType(String filePath) throws MimeTypeDetectionException {
		return validate(delegate.getMimeType(filePath));
	}

	@Override
	public MimeType detect(InputStream stream) throws MimeTypeDetectionException {
		return validate(delegate.detect(stream));
	}

	@Override
	public MimeType detect(String content) throws MimeTypeDetectionException {
		return validate(delegate.detect(content));
	}

	private MimeType validate(MimeType detected) throws UnallowedMimeTypeException {
		if (allowedMimetype.test(detected)) {
			return detected;
		}
		throw new UnallowedMimeTypeException("mimetype '" + detected + "' is not allowed");
	}

}
