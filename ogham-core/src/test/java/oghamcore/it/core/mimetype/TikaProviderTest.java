package oghamcore.it.core.mimetype;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;

import jakarta.activation.MimeTypeParseException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class TikaProviderTest {
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	
	
	TikaProvider tika;
	
	@Before
	public void setup() {
		tika = new TikaProvider();
	}
	
	
	@Test
	public void fileDoesntExist() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		MimeTypeDetectionException e = assertThrows("should throw", MimeTypeDetectionException.class, () -> {
			tika.getMimeType("INVALID_FILE");
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(FileNotFoundException.class));
		
	}
	
}
