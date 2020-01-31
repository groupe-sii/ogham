package oghamcore.ut.core.mimetype;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.internal.stubbing.answers.AnswerFunctionalInterfaces.toAnswer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.FallbackMimeTypeProvider;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class FallbackMimetypeProviderTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule();
	
	@Mock MimeTypeProvider provider1;
	@Mock MimeTypeProvider provider2;
	@Mock File file;
	InputStream stream;
	String content;
	
	MimeTypeProvider fallback;
	
	@Before
	public void setup() {
		fallback = new FallbackMimeTypeProvider(provider1, provider2);
		stream = new ByteArrayInputStream(new byte[] {1});
		content = "a";
	}
	
	@Test
	public void providerFailsShouldUseNextProviderForInputStream() throws MimeTypeDetectionException, MimeTypeParseException {
		when(provider1.detect(any(InputStream.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.detect(any(InputStream.class))).thenReturn(new MimeType("foo/bar"));
		assertThat(fallback.detect(stream).toString(), is("foo/bar"));
	}
	
	@Test(expected=MimeTypeDetectionException.class)
	public void noProviderCouldDetectShouldFailForInputStream() throws MimeTypeDetectionException {
		when(provider1.detect(any(InputStream.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.detect(any(InputStream.class))).thenThrow(MimeTypeDetectionException.class);
		fallback.detect(stream);
	}
	
	@Test
	public void ensureToAlwaysReadBeginningOfTheFile() throws MimeTypeDetectionException, MimeTypeParseException {
		when(provider1.detect(any(InputStream.class))).then(toAnswer((InputStream stream) -> {
			int next = stream.read();
			assertThat("should read first byte", next, is(1));
			throw new MimeTypeDetectionException("");
		}));
		when(provider2.detect(any(InputStream.class))).then(toAnswer((InputStream stream) -> {
			int next = stream.read();
			assertThat("should read first byte", next, is(1));
			return new MimeType("foo/bar");
		}));
		assertThat(fallback.detect(stream).toString(), is("foo/bar"));
	}
	
	@Test
	public void providerFailsShouldUseNextProviderForFile() throws MimeTypeDetectionException, MimeTypeParseException {
		when(provider1.getMimeType(any(File.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.getMimeType(any(File.class))).thenReturn(new MimeType("foo/bar"));
		assertThat(fallback.getMimeType(file).toString(), is("foo/bar"));
	}
	
	@Test(expected=MimeTypeDetectionException.class)
	public void noProviderCouldDetectShouldFailForFile() throws MimeTypeDetectionException {
		when(provider1.getMimeType(any(File.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.getMimeType(any(File.class))).thenThrow(MimeTypeDetectionException.class);
		fallback.getMimeType(file);
	}
	
	@Test
	public void providerFailsShouldUseNextProviderForContent() throws MimeTypeDetectionException, MimeTypeParseException {
		when(provider1.getMimeType(any(String.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.getMimeType(any(String.class))).thenReturn(new MimeType("foo/bar"));
		assertThat(fallback.getMimeType(content).toString(), is("foo/bar"));
	}
	
	@Test(expected=MimeTypeDetectionException.class)
	public void noProviderCouldDetectShouldFailForContent() throws MimeTypeDetectionException {
		when(provider1.getMimeType(any(String.class))).thenThrow(MimeTypeDetectionException.class);
		when(provider2.getMimeType(any(String.class))).thenThrow(MimeTypeDetectionException.class);
		fallback.getMimeType(content);
	}
}
