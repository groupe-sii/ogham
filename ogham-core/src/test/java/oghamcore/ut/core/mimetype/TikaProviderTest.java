package oghamcore.ut.core.mimetype;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.tika.Tika;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class TikaProviderTest {
	ExpectedException thrown = ExpectedException.none();
	
	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);
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
		thrown.expect(MimeTypeDetectionException.class);
		thrown.expectCause(nullValue(Throwable.class));
		
		when(tikaInstance.detect(any(File.class))).thenReturn("application/octet-stream");
		tika = new TikaProvider(tikaInstance, true);
		tika.getMimeType(file);
	}
	
	@Test
	public void unreadableStream() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		thrown.expect(MimeTypeDetectionException.class);
		thrown.expectCause(instanceOf(IOException.class));
		
		when(tikaInstance.detect(any(InputStream.class))).thenThrow(IOException.class);
		tika = new TikaProvider(tikaInstance, true);
		tika.detect(stream);
	}
	
	@Test
	public void invalidMimetype() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		thrown.expect(MimeTypeDetectionException.class);
		thrown.expectCause(instanceOf(MimeTypeParseException.class));
		
		when(tikaInstance.detect(any(InputStream.class))).thenReturn("not a mimetype");
		tika = new TikaProvider(tikaInstance, true);
		tika.detect(stream);
	}
	
}
