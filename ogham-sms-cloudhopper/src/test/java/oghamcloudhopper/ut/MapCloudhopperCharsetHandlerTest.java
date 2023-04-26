package oghamcloudhopper.ut;

import com.cloudhopper.commons.charset.CharsetUtil;
import fr.sii.ogham.core.charset.CharsetDetector;
import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.MapCloudhopperCharsetEncoder;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.defaultanswers.ReturnsSmartNulls;
import org.mockito.junit.jupiter.MockitoSettings;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
@MockitoSettings
public class MapCloudhopperCharsetHandlerTest {
	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private CharsetDetector charsetProviderMock;

	private MapCloudhopperCharsetEncoder charsetHandler;

	@BeforeEach
	public void before() {
		charsetHandler = new MapCloudhopperCharsetEncoder(charsetProviderMock);
	}
	
	@Test
	public void buildWithMap() throws EncodingException {
		Map<String, String> mapCloudhopperNameByNioName = new HashMap<String, String>();
		mapCloudhopperNameByNioName.put("UTF-8", CharsetUtil.NAME_UTF_8);
		mapCloudhopperNameByNioName.put("ISO-8859-1", CharsetUtil.NAME_ISO_8859_1);

		charsetHandler = new MapCloudhopperCharsetEncoder(charsetProviderMock, mapCloudhopperNameByNioName);
	}

	@Test
	public void addInvalidCharset() throws EncodingException {
		assertThrows(EncodingException.class, () -> {
			charsetHandler.addCharset("charset", "invalid");
		});
	}

	@Test
	public void addValidCharset() throws EncodingException {
		charsetHandler.addCharset("charset", CharsetUtil.NAME_AIRWIDE_GSM);
	}

	@Test
	public void encodeWithKnownCharset() throws EncodingException {
		//given
		String givenContent = "méss@ge àvec des acçènts & d€$ cara©tères spécïaùx";
		String givenNioCharsetName = "charset";

		com.cloudhopper.commons.charset.Charset cloudhopperCharsetMock = Mockito.mock(com.cloudhopper.commons.charset.Charset.class, new ReturnsSmartNulls());
		charsetHandler.addCharset(givenNioCharsetName, new NamedCharset("", cloudhopperCharsetMock));

		Charset nioCharsetMock = new Charset(givenNioCharsetName, null) {
			@Override
			public CharsetEncoder newEncoder() {
				return null;
			}

			@Override
			public CharsetDecoder newDecoder() {
				return null;
			}

			@Override
			public boolean contains(Charset cs) {
				return false;
			}
		};

		BDDMockito.given(charsetProviderMock.detect(givenContent)).willReturn(nioCharsetMock);

		String expectedEncodedStr = "ok";
		BDDMockito.given(cloudhopperCharsetMock.encode(givenContent)).willReturn(expectedEncodedStr.getBytes());
		
		//when
		Encoded result = charsetHandler.encode(givenContent);
		
		//then
		assertArrayEquals(expectedEncodedStr.getBytes(), result.getBytes());
	}

	@Test
	public void encodeWithUnknownNioCharset() throws EncodingException {
		// given
		String givenContent = "méss@ge àvec des acçènts & d€$ cara©tères spécïaùx";
		String givenNioCharsetName = "charset";

		com.cloudhopper.commons.charset.Charset cloudhopperCharsetMock = Mockito.mock(com.cloudhopper.commons.charset.Charset.class, new ReturnsSmartNulls());
		charsetHandler.addCharset(givenNioCharsetName, new NamedCharset("", cloudhopperCharsetMock));

		BDDMockito.given(charsetProviderMock.detect(givenContent)).willReturn(null);

		// when
		assertThrows(EncodingException.class, () -> {
			charsetHandler.encode(givenContent);
		});
	}

	@Test
	public void encodeWithUnmappedCloudhopperCharset() throws EncodingException {
		// given
		String givenContent = "méss@ge àvec des acçènts & d€$ cara©tères spécïaùx";
		String givenNioCharsetName = "charset";

		Charset nioCharsetMock = new Charset(givenNioCharsetName, null) {
			@Override
			public CharsetEncoder newEncoder() {
				return null;
			}

			@Override
			public CharsetDecoder newDecoder() {
				return null;
			}

			@Override
			public boolean contains(Charset cs) {
				return false;
			}
		};

		BDDMockito.given(charsetProviderMock.detect(givenContent)).willReturn(nioCharsetMock);

		// when
		assertThrows(EncodingException.class, () -> {
			charsetHandler.encode(givenContent);
		});
	}
}
