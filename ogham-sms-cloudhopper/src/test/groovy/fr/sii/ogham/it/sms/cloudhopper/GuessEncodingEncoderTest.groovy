package fr.sii.ogham.it.sms.cloudhopper;

import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_UCS_2
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2
import static java.util.Arrays.asList

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.cloudhopper.commons.charset.CharsetUtil

import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.CloudhopperCharsetSupportingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.GuessEncodingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class GuessEncodingEncoderTest extends Specification {
	@Rule LoggingTestRule logging;
	
	def "'#original' should be encoded using #expectedCharsetName"() {
		given:
			def gsm7bitEncoder = new CloudhopperCharsetSupportingEncoder(NamedCharset.from(NAME_GSM7))
			def gsm8bitEncoder = new CloudhopperCharsetSupportingEncoder(NamedCharset.from(NAME_GSM8))
			def ucs2Encoder = new CloudhopperCharsetSupportingEncoder(NamedCharset.from(NAME_UCS_2))
			def encoder = new GuessEncodingEncoder([gsm7bitEncoder, gsm8bitEncoder, ucs2Encoder])

		when:
			def encodedMessage = encoder.encode(original)
			def encoded = encodedMessage.getBytes()
			def guessedCharsetName = encodedMessage.getCharsetName()
			def decoded = expectedCharset.decode(encoded)

		then:
			expectedCharsetName.equals(guessedCharsetName)
			Arrays.equals(expected as byte[], encoded)
			original.equals(decoded)

		where:
			original                                                                    || expectedCharset     | expectedCharsetName   | expected
			// GSM 7-bit default alphabet
			"normal string without special characters"                                  || CHARSET_GSM7        | NAME_GSM7             | CHARSET_GSM7.encode("normal string without special characters")
			"abcdefghijklmnopqrstuvwxyz0123456789 "                                     || CHARSET_GSM7        | NAME_GSM7             | CHARSET_GSM7.encode("abcdefghijklmnopqrstuvwxyz0123456789 ")
			"@£\$¥\nØø\rΔ_ΦΓΛΩΠΨΣΘΞÆæß!\"#¤%&'()*+,-./:;<=>?¡¿§"                        || CHARSET_GSM7        | NAME_GSM7             | CHARSET_GSM7.encode("@£\$¥\nØø\rΔ_ΦΓΛΩΠΨΣΘΞÆæß!\"#¤%&'()*+,-./:;<=>?¡¿§")
			"èéùìòÇÅåÉÄÖÑÜäöñüà"                                                        || CHARSET_GSM7        | NAME_GSM7             | CHARSET_GSM7.encode("èéùìòÇÅåÉÄÖÑÜäöñüà")
			// GSM 7-bit Basic Character Set Extension
			"|^€{}[~]\\"                                                                || CHARSET_GSM7        | NAME_GSM7             | CHARSET_GSM7.encode("|^€{}[~]\\")
			// french words (ê character needs UCS_2)
			"êtes à l'évènement çà et là où vôtre jeûne île hôpital €"                  || CHARSET_UCS_2       | NAME_UCS_2            | CHARSET_UCS_2.encode("êtes à l'évènement çà et là où vôtre jeûne île hôpital €")
			// TODO: add more tests strings to test other encodings and languages
	}

	def "[GSM 7-bit disabled] '#original' should be encoded using #expectedCharsetName"() {
		given:
		    def gsm8bitEncoder = new CloudhopperCharsetSupportingEncoder(NamedCharset.from(NAME_GSM8))
		    def ucs2Encoder = new CloudhopperCharsetSupportingEncoder(NamedCharset.from(NAME_UCS_2))
			def encoder = new GuessEncodingEncoder([gsm8bitEncoder, ucs2Encoder])

		when:
			def encodedMessage = encoder.encode(original)
			def encoded = encodedMessage.getBytes()
			def guessedCharsetName = encodedMessage.getCharsetName()
			def decoded = expectedCharset.decode(encoded)

		then:
			expectedCharsetName.equals(guessedCharsetName)
			Arrays.equals(expected as byte[], encoded)
			original.equals(decoded)

		where:
			original                                                                    || expectedCharset     | expectedCharsetName   | expected
			// GSM 7-bit default alphabet
			"normal string without special characters"                                  || CHARSET_GSM8        | NAME_GSM8             | CHARSET_GSM8.encode("normal string without special characters")
			"abcdefghijklmnopqrstuvwxyz0123456789 "                                     || CHARSET_GSM8        | NAME_GSM8             | CHARSET_GSM8.encode("abcdefghijklmnopqrstuvwxyz0123456789 ")
			"@£\$¥\nØø\rΔ_ΦΓΛΩΠΨΣΘΞÆæß!\"#¤%&'()*+,-./:;<=>?¡¿§"                        || CHARSET_GSM8        | NAME_GSM8             | CHARSET_GSM8.encode("@£\$¥\nØø\rΔ_ΦΓΛΩΠΨΣΘΞÆæß!\"#¤%&'()*+,-./:;<=>?¡¿§")
			"èéùìòÇÅåÉÄÖÑÜäöñüà"                                                        || CHARSET_GSM8        | NAME_GSM8             | CHARSET_GSM8.encode("èéùìòÇÅåÉÄÖÑÜäöñüà")
			// GSM 7-bit Basic Character Set Extension
			"|^€{}[~]\\"                                                                || CHARSET_GSM8        | NAME_GSM8             | CHARSET_GSM8.encode("|^€{}[~]\\")
			// french words (ê character needs UCS_2)
			"êtes à l'évènement çà et là où vôtre jeûne île hôpital €"                  || CHARSET_UCS_2       | NAME_UCS_2            | CHARSET_UCS_2.encode("êtes à l'évènement çà et là où vôtre jeûne île hôpital €")
			// TODO: add more tests strings to test other encodings and languages
	}
}
