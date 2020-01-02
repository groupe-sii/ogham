package oghamcore.it.core.mimetype;

import static org.hamcrest.Matchers.instanceOf;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.activation.MimeTypeParseException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
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
	
	
	TikaProvider tika;
	
	@Before
	public void setup() {
		tika = new TikaProvider();
	}
	
	
	@Test
	public void fileDoesntExist() throws MimeTypeDetectionException, MimeTypeParseException, IOException {
		thrown.expect(MimeTypeDetectionException.class);
		thrown.expectCause(instanceOf(FileNotFoundException.class));
		
		tika.getMimeType("INVALID_FILE");
	}
	
}
