package oghamcore.it.core.mimetype;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.FileNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
@MockitoSettings
public class TikaProviderTest {

	TikaProvider tika;
	
	@BeforeEach
	public void setup() {
		tika = new TikaProvider();
	}
	
	
	@Test
	public void fileDoesntExist() {
		MimeTypeDetectionException e = assertThrows(MimeTypeDetectionException.class, () -> {
			tika.getMimeType("INVALID_FILE");
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(FileNotFoundException.class));
	}
	
}
