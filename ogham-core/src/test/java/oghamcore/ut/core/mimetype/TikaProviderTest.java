package oghamcore.ut.core.mimetype;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeParseException;
import fr.sii.ogham.core.mimetype.MimeType;
import fr.sii.ogham.core.mimetype.RawMimeType;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@LogTestInformation
@MockitoSettings
public class TikaProviderTest {
	@Mock Tika tikaInstance;
	@Mock File file;
	@Mock InputStream stream;
	
	TikaProvider tika;

	@Test
	public void fromSteam() throws MimeTypeDetectionException, IOException {
		when(tikaInstance.detect(any(InputStream.class))).thenReturn("application/json");
		tika = new TikaProvider(tikaInstance, true);
		MimeType mimetype = tika.detect(stream);
		assertThat("detected", mimetype.matches(new RawMimeType("application/json")), is(true));
	}
	
	@Test
	public void fromFile() throws MimeTypeDetectionException, IOException {
		when(tikaInstance.detect(any(File.class))).thenReturn("application/json");
		tika = new TikaProvider(tikaInstance, true);
		MimeType mimetype = tika.getMimeType(file);
		assertThat("detected", mimetype.matches(new RawMimeType("application/json")), is(true));
	}
	
	@Test
	public void fromString() throws MimeTypeDetectionException {
		when(tikaInstance.detect(any(byte[].class))).thenReturn("application/json");
		tika = new TikaProvider(tikaInstance, true);
		MimeType mimetype = tika.detect("");
		assertThat("detected", mimetype.matches(new RawMimeType("application/json")), is(true));
	}
	
	
	@Test
	public void failIfOctetStream() throws IOException {
		when(tikaInstance.detect(any(File.class))).thenReturn("application/octet-stream");
		tika = new TikaProvider(tikaInstance, true);

		MimeTypeDetectionException e = assertThrows(MimeTypeDetectionException.class, () -> {
			tika.getMimeType(file);
		});
		assertThat("should not have cause", e.getCause(), nullValue(Throwable.class));
	}
	
	@Test
	public void unreadableStream() throws IOException {
		when(tikaInstance.detect(any(InputStream.class))).thenThrow(IOException.class);
		tika = new TikaProvider(tikaInstance, true);

		MimeTypeDetectionException e = assertThrows(MimeTypeDetectionException.class, () -> {
			tika.detect(stream);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(IOException.class));
	}
	
	@Test
	public void invalidMimetype() throws IOException {
		when(tikaInstance.detect(any(InputStream.class))).thenReturn("not a mimetype");
		tika = new TikaProvider(tikaInstance, true);

		MimeTypeDetectionException e = assertThrows(MimeTypeDetectionException.class, () -> {
			tika.detect(stream);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(MimeTypeParseException.class));
	}
	
}
