package oghamcore.ut.core.mimetype;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jakarta.activation.MimeType;
import jakarta.activation.MimeTypeParseException;

import org.apache.tika.Tika;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class TikaProviderTest {
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();

	
	@Mock Tika tikaInstance;
	@Mock File file;
	@Mock InputStream stream;
	
	TikaProvider tika;

	@Test
	public void fromSteam() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		when(tikaInstance.detect(any(InputStream.class))).thenReturn("application/json");
		tika = new TikaProvider(tikaInstance, true);
		MimeType mimetype = tika.detect(stream);
		assertThat("detected", mimetype.match("application/json"), is(true));
	}
	
	@Test
	public void fromFile() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		when(tikaInstance.detect(any(File.class))).thenReturn("application/json");
		tika = new TikaProvider(tikaInstance, true);
		MimeType mimetype = tika.getMimeType(file);
		assertThat("detected", mimetype.match("application/json"), is(true));
	}
	
	@Test
	public void fromString() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		when(tikaInstance.detect(any(byte[].class))).thenReturn("application/json");
		tika = new TikaProvider(tikaInstance, true);
		MimeType mimetype = tika.detect("");
		assertThat("detected", mimetype.match("application/json"), is(true));
	}
	
	
	@Test
	public void failIfOctetStream() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		when(tikaInstance.detect(any(File.class))).thenReturn("application/octet-stream");
		tika = new TikaProvider(tikaInstance, true);

		MimeTypeDetectionException e = assertThrows("should throw", MimeTypeDetectionException.class, () -> {
			tika.getMimeType(file);
		});
		assertThat("should not have cause", e.getCause(), nullValue(Throwable.class));
	}
	
	@Test
	public void unreadableStream() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		when(tikaInstance.detect(any(InputStream.class))).thenThrow(IOException.class);
		tika = new TikaProvider(tikaInstance, true);

		MimeTypeDetectionException e = assertThrows("should throw", MimeTypeDetectionException.class, () -> {
			tika.detect(stream);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(IOException.class));
	}
	
	@Test
	public void invalidMimetype() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		when(tikaInstance.detect(any(InputStream.class))).thenReturn("not a mimetype");
		tika = new TikaProvider(tikaInstance, true);

		MimeTypeDetectionException e = assertThrows("should throw", MimeTypeDetectionException.class, () -> {
			tika.detect(stream);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(MimeTypeParseException.class));
	}
	
}
