package fr.sii.notification.core.mimetype;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.activation.MimeType;

import org.apache.commons.io.IOUtils;

import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;

public class FallbackMimeTypeProvider implements MimeTypeProvider {

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
		for(MimeTypeProvider provider : providers) {
			try {
				return provider.getMimeType(file);
			} catch(MimeTypeDetectionException e) {
				// nothing to do => try next one
			}
		}
		throw new MimeTypeDetectionException("No mimetype provider could provide the mimetype for the file "+file);
	}

	@Override
	public MimeType getMimeType(String fileName) throws MimeTypeDetectionException {
		for(MimeTypeProvider provider : providers) {
			try {
				return provider.getMimeType(fileName);
			} catch(MimeTypeDetectionException e) {
				// nothing to do => try next one
			}
		}
		throw new MimeTypeDetectionException("No mimetype provider could provide the mimetype for the file "+fileName);
	}

	@Override
	public MimeType detect(InputStream stream) throws MimeTypeDetectionException {
		try {
			ByteArrayInputStream copy = new ByteArrayInputStream(IOUtils.toByteArray(stream));
			copy.mark(Integer.MAX_VALUE);
			for(MimeTypeProvider provider : providers) {
				try {
					return provider.detect(copy);
				} catch(MimeTypeDetectionException e) {
					// try next one => move read cursor to beginning
					copy.reset();
				}
			}
			throw new MimeTypeDetectionException("No mimetype provider could provide the mimetype from the provided content");
		} catch (IOException e1) {
			throw new MimeTypeDetectionException("Can't read the content of the stream", e1);
		}
	}

	@Override
	public MimeType detect(String content) throws MimeTypeDetectionException {
		for(MimeTypeProvider provider : providers) {
			try {
				return provider.detect(content);
			} catch(MimeTypeDetectionException e) {
				// nothing to do => try next one
			}
		}
		throw new MimeTypeDetectionException("No mimetype provider could provide the mimetype from the provided content");
	}
	
	public void addProvider(MimeTypeProvider provider) {
		providers.add(provider);
	}

}
