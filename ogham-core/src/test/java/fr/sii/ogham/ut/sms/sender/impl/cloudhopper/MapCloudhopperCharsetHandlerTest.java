package fr.sii.ogham.ut.sms.sender.impl.cloudhopper;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.defaultanswers.ReturnsSmartNulls;
import org.mockito.runners.MockitoJUnitRunner;

import com.cloudhopper.commons.charset.CharsetUtil;

import fr.sii.ogham.core.charset.CharsetProvider;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.MapCloudhopperCharsetHandler;

@RunWith(MockitoJUnitRunner.class)
public class MapCloudhopperCharsetHandlerTest {
	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	private CharsetProvider charsetProviderMock;

	private MapCloudhopperCharsetHandler charsetHandler;
	
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Before
	public void before() {
		charsetHandler = new MapCloudhopperCharsetHandler(charsetProviderMock);
	}
	
	@Test
	public void buildWithMap() throws EncodingException {
		Map<String, String> mapCloudhopperNameByNioName = new HashMap<String, String>();
		mapCloudhopperNameByNioName.put("UTF-8", CharsetUtil.NAME_UTF_8);
		mapCloudhopperNameByNioName.put("ISO-8859-1", CharsetUtil.NAME_ISO_8859_1);

		charsetHandler = new MapCloudhopperCharsetHandler(charsetProviderMock, mapCloudhopperNameByNioName);
	}

	@Test(expected = EncodingException.class)
	public void addInvalidCharset() throws EncodingException {
		charsetHandler.addCharset("charset", "invalid");
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
		charsetHandler.addCharset(givenNioCharsetName, cloudhopperCharsetMock);

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

		BDDMockito.given(charsetProviderMock.getCharset(givenContent)).willReturn(nioCharsetMock);

		String expectedEncodedStr = "ok";
		BDDMockito.given(cloudhopperCharsetMock.encode(givenContent)).willReturn(expectedEncodedStr.getBytes());
		
		//when
		byte[] result = charsetHandler.encode(givenContent);
		
		//then
		Assert.assertArrayEquals(expectedEncodedStr.getBytes(), result);
	}

	@Test(expected = EncodingException.class)
	public void encodeWithUnknownNioCharset() throws EncodingException {
		// given
		String givenContent = "méss@ge àvec des acçènts & d€$ cara©tères spécïaùx";
		String givenNioCharsetName = "charset";

		com.cloudhopper.commons.charset.Charset cloudhopperCharsetMock = Mockito.mock(com.cloudhopper.commons.charset.Charset.class, new ReturnsSmartNulls());
		charsetHandler.addCharset(givenNioCharsetName, cloudhopperCharsetMock);

		BDDMockito.given(charsetProviderMock.getCharset(givenContent)).willReturn(null);

		// when
		charsetHandler.encode(givenContent);
	}

	@Test(expected = EncodingException.class)
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

		BDDMockito.given(charsetProviderMock.getCharset(givenContent)).willReturn(nioCharsetMock);

		// when
		charsetHandler.encode(givenContent);
	}
}
