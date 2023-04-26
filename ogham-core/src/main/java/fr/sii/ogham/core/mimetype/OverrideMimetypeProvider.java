package fr.sii.ogham.core.mimetype;

import fr.sii.ogham.core.exception.mimetype.InvalidMimetypeException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeParseException;
import fr.sii.ogham.core.mimetype.replace.MimetypeReplacer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

/**
 * A mimetype provider implementation that request mimetype detection to a
 * delegate and then check returned mimetype and apply corrections if needed.
 * 
 * @author Aurélien Baudet
 *
 */
public class OverrideMimetypeProvider implements MimeTypeProvider {
	private static final Logger LOG = LoggerFactory.getLogger(OverrideMimetypeProvider.class);

	private final MimeTypeProvider detector;
	private final MimetypeReplacer replacer;

	/**
	 * Initialize with the delegate mimetype detector and replacement strategy.
	 * 
	 * @param detector
	 *            the mimetype detector whose result may be replaced
	 * @param replacer
	 *            the replacer that may replace some returned values
	 */
	public OverrideMimetypeProvider(MimeTypeProvider detector, MimetypeReplacer replacer) {
		super();
		this.detector = detector;
		this.replacer = replacer;
	}

	@Override
	public MimeType getMimeType(File file) throws MimeTypeDetectionException {
		MimeType mimeType = detector.getMimeType(file);
		return override(mimeType);
	}

	@Override
	public MimeType getMimeType(String filePath) throws MimeTypeDetectionException {
		MimeType mimeType = detector.getMimeType(filePath);
		return override(mimeType);
	}

	@Override
	public MimeType detect(InputStream stream) throws MimeTypeDetectionException {
		MimeType mimeType = detector.detect(stream);
		return override(mimeType);
	}

	@Override
	public MimeType detect(String content) throws MimeTypeDetectionException {
		MimeType mimeType = detector.detect(content);
		return override(mimeType);
	}

	private MimeType override(MimeType mimeType) throws MimeTypeDetectionException {
		String original = mimeType.toString();
		String replaced = replacer.replace(original);
		if (!replaced.equals(original)) {
			LOG.debug("Detected mime type {} overriden by: {}", original, replaced);
			return toMimetype(original, replaced);
		}
		return mimeType;
	}

	private static MimeType toMimetype(String original, String replaced) throws MimeTypeDetectionException {
		try {
			return new ParsedMimeType(replaced);
		} catch (MimeTypeParseException e) {
			throw new InvalidMimetypeException("Replacing mimetype " + original + " by " + replaced + " failed because " + replaced + " is not valid", e);
		}
	}

}
