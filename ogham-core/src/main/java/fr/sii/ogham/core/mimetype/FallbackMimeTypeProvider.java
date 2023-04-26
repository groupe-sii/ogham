package fr.sii.ogham.core.mimetype;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.mimetype.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.mimetype.NoMimetypeDetectorException;
import fr.sii.ogham.core.util.IOUtils;

/**
 * Implementation that will try several delegate implementations until one is
 * able to provide the Mime Type.
 * 
 * @author Aurélien Baudet
 *
 */
public class FallbackMimeTypeProvider implements MimeTypeProvider {
	private static final Logger LOG = LoggerFactory.getLogger(FallbackMimeTypeProvider.class);

	/**
	 * The list of delegate implementations to try
	 */
	private List<MimeTypeProvider> providers;

	public FallbackMimeTypeProvider(MimeTypeProvider... providers) {
		this(new ArrayList<>(Arrays.asList(providers)));
	}

	public FallbackMimeTypeProvider(List<MimeTypeProvider> providers) {
		super();
		this.providers = providers;
	}

	@Override
	public MimeType getMimeType(File file) throws MimeTypeDetectionException {
		for (MimeTypeProvider provider : providers) {
			try {
				LOG.debug("Trying to get mime type for file {} using {}", file, provider);
				MimeType mimetype = provider.getMimeType(file);
				LOG.debug("{} has detected mime type {} for file {}", provider, mimetype, file);
				return mimetype;
			} catch (MimeTypeDetectionException e) {
				// nothing to do => try next one
				LOG.debug("{} could not detect mime type for file {}. Cause: {}", provider, file, e.getMessage(), e);
			}
		}
		throw new NoMimetypeDetectorException("No mimetype provider could provide the mimetype for the file " + file);
	}

	@Override
	public MimeType getMimeType(String fileName) throws MimeTypeDetectionException {
		for (MimeTypeProvider provider : providers) {
			try {
				LOG.debug("Trying to get mime type for file {} using {}", fileName, provider);
				MimeType mimetype = provider.getMimeType(fileName);
				LOG.debug("{} has detected mime type {} for file {}", provider, mimetype, fileName);
				return mimetype;
			} catch (MimeTypeDetectionException e) {
				// nothing to do => try next one
				LOG.debug("{} could not detect mime type for file {}. Cause: {}", provider, fileName, e.getMessage(), e);
			}
		}
		throw new NoMimetypeDetectorException("No mimetype provider could provide the mimetype for the file " + fileName);
	}

	@Override
	public MimeType detect(InputStream stream) throws MimeTypeDetectionException {
		try {
			ByteArrayInputStream copy = new ByteArrayInputStream(IOUtils.toByteArray(stream));
			MimeType mimetype = detect(copy);
			if(mimetype==null) {
				throw new NoMimetypeDetectorException("No mimetype provider could provide the mimetype from the provided content");
			}
			return mimetype;
		} catch (IOException e) {
			throw new NoMimetypeDetectorException("Can't read the content of the stream", e);
		}
	}

	private MimeType detect(ByteArrayInputStream copy) {
		MimeType mimetype = null;
		for (MimeTypeProvider provider : providers) {
			try {
				LOG.debug("Trying to get mime type from stream using {}", provider);
				mimetype = provider.detect(copy);
				LOG.debug("{} has detected mime type {} from stream", provider, mimetype);
				break;
			} catch (MimeTypeDetectionException e) {
				// try next one => move read cursor to beginning
				copy.reset();
				LOG.debug("{} could not detect mime type from stream. Cause: {}", provider, e.getMessage(), e);
			}
		}
		return mimetype;
	}

	@Override
	@SuppressWarnings("squid:S1192")
	public MimeType detect(String content) throws MimeTypeDetectionException {
		for (MimeTypeProvider provider : providers) {
			try {
				LOG.debug("Trying to get mime type using {} from content", provider);
				LOG.trace("content: {}", content);
				MimeType mimetype = provider.detect(content);
				LOG.debug("{} has detected mime type {} from content", provider, mimetype);
				LOG.trace("content: {}", content);
				return mimetype;
			} catch (MimeTypeDetectionException e) {
				// nothing to do => try next one
				LOG.debug("{} could not detect mime type from content. Cause: {}", provider, e.getMessage(), e);
				LOG.trace("content: {}", content);
			}
		}
		throw new NoMimetypeDetectorException("No mimetype provider could provide the mimetype from the provided content");
	}

	/**
	 * Register a new Mime Type detection implementation. The implementation is
	 * added at the end of the list so it will be called at last.
	 * 
	 * @param provider
	 *            the implementation to register
	 */
	public void addProvider(MimeTypeProvider provider) {
		providers.add(provider);
	}

}
