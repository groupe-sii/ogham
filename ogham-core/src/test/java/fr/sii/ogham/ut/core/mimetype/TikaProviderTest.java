package fr.sii.ogham.ut.core.mimetype;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import javax.activation.MimeTypeParseException;

import org.apache.tika.Tika;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.junit.LoggingTestRule;

public class TikaProviderTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule();
	
	@Mock Tika tikaInstance;
	@Mock File file;
	
	TikaProvider tika;
	
	@Test(expected=MimeTypeDetectionException.class)
	public void failIfOctetStream() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		when(tikaInstance.detect(any(File.class))).thenReturn("application/octet-stream");
		tika = new TikaProvider(tikaInstance, true);
		tika.getMimeType(file);
	}
	
}
