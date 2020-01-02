package oghamcloudhopper.it;

import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_UCS_2
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2

import fr.sii.ogham.sms.encoder.Encoder
import fr.sii.ogham.sms.encoder.SupportingEncoder
import fr.sii.ogham.sms.exception.message.EncodingException
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.CloudhopperCharsetSupportingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.GuessEncodingEncoder
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.GuessEncodingException
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class GuessEncodingEncoderSpec extends Specification {
	
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
	
	def "#desc"() {
		given:
			def encoder = new GuessEncodingEncoder(possibleEncoders)
			
		when:
			def canEncode = encoder.canEncode("")
		
		then:
			canEncode == expected
			
		where:
			desc																| possibleEncoders				|| expected
			"no encoders can't encode message"									| []							|| false
			"Encoder instance always encodes message"							| [Mock(Encoder)]				|| true
			"SupportingEncoder that can encode encodes the message"				| [supportingEncoder(true)]		|| true
			"SupportingEncoder that can't encode doesn't encode the message"	| [supportingEncoder(false)]	|| false
	}
	
	def "can't encode due to #exception should fail"() {
		given:
			Encoder always = Mock()
			always.encode(_) >> { throw exception }
			def encoder = new GuessEncodingEncoder([always])
			
		when:
			encoder.encode("")
		
		then:
			thrown(expected)
			
		where:
			exception							|| expected
			new EncodingException("foo")		|| GuessEncodingException
			new IllegalStateException("bar")	|| IllegalStateException
	}
	
	def supportingEncoder(boolean canEncode) {
		SupportingEncoder supporting = Mock()
		supporting.canEncode(_) >> canEncode
		supporting
	}

}
