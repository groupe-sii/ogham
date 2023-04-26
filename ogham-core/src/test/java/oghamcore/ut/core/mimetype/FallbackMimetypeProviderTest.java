package oghamcore.ut.core.mimetype;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.FallbackMimeTypeProvider;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.RawMimeType;
import fr.sii.ogham.core.mimetype.ParsedMimeType;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.internal.stubbing.answers.AnswerFunctionalInterfaces.toAnswer;

@LogTestInformation
@MockitoSettings
public class FallbackMimetypeProviderTest {

	@Mock MimeTypeProvider provider1;
	@Mock MimeTypeProvider provider2;
	@Mock File file;
	InputStream stream;
	String content;

	MimeTypeProvider fallback;

	@BeforeEach
	public void setup() {
		fallback = new FallbackMimeTypeProvider(provider1, provider2);
		stream = new ByteArrayInputStream(new byte[] {1});
		content = "a";
	}

	@Test
	public void providerFailsShouldUseNextProviderForInputStream() throws MimeTypeDetectionException {
		when(provider1.detect(any(InputStream.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.detect(any(InputStream.class))).thenReturn(new RawMimeType("foo/bar"));
		assertThat(fallback.detect(stream).toString(), is("foo/bar"));
	}

	@Test
	public void noProviderCouldDetectShouldFailForInputStream() throws MimeTypeDetectionException {
		when(provider1.detect(any(InputStream.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.detect(any(InputStream.class))).thenThrow(MimeTypeDetectionException.class);
		assertThrows(MimeTypeDetectionException.class, () -> {
			fallback.detect(stream);
		});
	}

	@Test
	public void ensureToAlwaysReadBeginningOfTheFile() throws MimeTypeDetectionException {
		when(provider1.detect(any(InputStream.class))).then(toAnswer((InputStream stream) -> {
			int next = stream.read();
			assertThat("should read first byte", next, is(1));
			throw new MimeTypeDetectionException("");
		}));
		when(provider2.detect(any(InputStream.class))).then(toAnswer((InputStream stream) -> {
			int next = stream.read();
			assertThat("should read first byte", next, is(1));
			return new ParsedMimeType("foo/bar");
		}));
		assertThat(fallback.detect(stream).toString(), is("foo/bar"));
	}

	@Test
	public void providerFailsShouldUseNextProviderForFile() throws MimeTypeDetectionException {
		when(provider1.getMimeType(any(File.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.getMimeType(any(File.class))).thenReturn(new RawMimeType("foo/bar"));
		assertThat(fallback.getMimeType(file).toString(), is("foo/bar"));
	}

	@Test
	public void noProviderCouldDetectShouldFailForFile() throws MimeTypeDetectionException {
		when(provider1.getMimeType(any(File.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.getMimeType(any(File.class))).thenThrow(MimeTypeDetectionException.class);
		assertThrows(MimeTypeDetectionException.class, () -> {
			fallback.getMimeType(file);
		});
	}

	@Test
	public void providerFailsShouldUseNextProviderForContent() throws MimeTypeDetectionException {
		when(provider1.getMimeType(any(String.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.getMimeType(any(String.class))).thenReturn(new RawMimeType("foo/bar"));
		assertThat(fallback.getMimeType(content).toString(), is("foo/bar"));
	}

	@Test
	public void noProviderCouldDetectShouldFailForContent() throws MimeTypeDetectionException {
		when(provider1.getMimeType(any(String.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.getMimeType(any(String.class))).thenThrow(MimeTypeDetectionException.class);
		assertThrows(MimeTypeDetectionException.class, () -> {
			fallback.getMimeType(content);
		});
	}
}
