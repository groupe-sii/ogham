package fr.sii.ogham.it.core.sms.splitter;

import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.CHARSET_UCS_2
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2
import static fr.sii.ogham.sms.SmsConstants.SmppSplitConstants.SEGMENT_SIZE_GSM_7BIT_SMS_PACKING_MODE
import static fr.sii.ogham.sms.SmsConstants.SmppSplitConstants.SEGMENT_SIZE_GSM_8BIT
import static fr.sii.ogham.sms.SmsConstants.SmppSplitConstants.SEGMENT_SIZE_UCS2
import static java.lang.Math.ceil
import static java.lang.Math.floor
import static java.util.Arrays.asList

import org.junit.AfterClass
import org.junit.Rule;

import com.cloudhopper.commons.charset.Charset
import com.cloudhopper.commons.charset.CharsetUtil

import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.sms.encoder.EncodedMessage
import fr.sii.ogham.sms.splitter.GsmBasicCharsetExtensionTableCounter
import fr.sii.ogham.sms.splitter.GsmMessageSplitter
import fr.sii.ogham.sms.splitter.ReferenceNumberGenerator
import fr.sii.ogham.sms.splitter.Segment
import groovy.util.logging.Slf4j
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@Slf4j
class GsmMessageSplitterTest extends Specification {
	@Rule LoggingTestRule logging;
	private static byte referenceNumber = 0x24
	
	def "GSM 7-bit #original.length() characters should be split in #expectedNumberOfSegments segments"() {
		given:
			def referenceNumberGenerator = Mock(ReferenceNumberGenerator)
			referenceNumberGenerator.generateReferenceNumber() >> referenceNumber
			def splitter = new GsmMessageSplitter({ m -> new EncodedMessage(m, CHARSET_GSM7.encode(m), NAME_GSM7)}, SEGMENT_SIZE_GSM_7BIT_SMS_PACKING_MODE, referenceNumberGenerator, new GsmBasicCharsetExtensionTableCounter())

		when:
			def segments = splitter.split(original);
			debug(CHARSET_GSM7, original, expectedSegments, segments);
			
		then:
			segments.size() == expectedNumberOfSegments
			def idx = 0
			expectedSegments.every { expectedSegment ->
				segments.get(idx++).getBytes().length <= 140
			}
			def idx2 = 0
			expectedSegments.every { expectedSegment ->
				Arrays.equals(expectedSegment as byte[], segments.get(idx2++).getBytes())
			}
			

		where:
			original							|| expectedNumberOfSegments | expectedSegments
			// message length <= 160
			// fits in one segment (no header required)
			"a" * 100  							|| 1						| [asList(CHARSET_GSM7.encode("a" * 100))]
			"a" * 159							|| 1						| [asList(CHARSET_GSM7.encode("a" * 159))]
			"a" * 160							|| 1						| [asList(CHARSET_GSM7.encode("a" * 160))]
			// 160 > message length >= 306
			// fits in two segments (153 characters * 2)
			"a" * 161							|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("a" * 8))
																			  ]
			"a" * 200							|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(2, 2) + asList(CHARSET_GSM7.encode("a" * 47))
																			  ]
			"a" * 305							|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(2, 2) + asList(CHARSET_GSM7.encode("a" * 152))
																			  ]
			"a" * 306							|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
					                     		    							header(2, 2) + asList(CHARSET_GSM7.encode("a" * 153))
																			  ]
		   // 306 > message length >= 459
		   // fit in three segments (153 characters * 3)
			"a" * 307							|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(3, 2) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(3, 3) + asList(CHARSET_GSM7.encode("a"))
																			  ]
			"a"*153 + "b"*153 + "c"				|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(3, 2) + asList(CHARSET_GSM7.encode("b" * 153)), 
																				header(3, 3) + asList(CHARSET_GSM7.encode("c"))
																			  ]
			"a" * 308							|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(3, 2) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(3, 3) + asList(CHARSET_GSM7.encode("a" * 2))
																			  ]
			"a" * 458							|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(3, 2) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(3, 3) + asList(CHARSET_GSM7.encode("a" * 152))
																			  ]
			"a" * 459							|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(3, 2) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(3, 3) + asList(CHARSET_GSM7.encode("a" * 153))
																			  ]
			// 459 > message length >= 612
			// fit in four segments (153 characters * 4)
			"a" * 460							|| 4						| [
																				header(4, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(4, 2) + asList(CHARSET_GSM7.encode("a" * 153)),  
																				header(4, 3) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(4, 4) + asList(CHARSET_GSM7.encode("a"))
																			  ]
			"a" * 461							|| 4						| [
																				header(4, 1) + asList(CHARSET_GSM7.encode("a" * 153)), 
																				header(4, 2) + asList(CHARSET_GSM7.encode("a" * 153)),  
																				header(4, 3) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(4, 4) + asList(CHARSET_GSM7.encode("a" * 2))
																			  ]
	}

	def "GSM 7-bit #original.length() characters containing #occurrences characters of extension table should be split in #expectedNumberOfSegments segments"() {
		given:
			def referenceNumberGenerator = Mock(ReferenceNumberGenerator)
			referenceNumberGenerator.generateReferenceNumber() >> referenceNumber
			def splitter = new GsmMessageSplitter({ m -> new EncodedMessage(m, CHARSET_GSM7.encode(m), NAME_GSM7)}, SEGMENT_SIZE_GSM_7BIT_SMS_PACKING_MODE, referenceNumberGenerator, new GsmBasicCharsetExtensionTableCounter())

		when:
			def segments = splitter.split(original);
			debug(CHARSET_GSM7, original, expectedSegments, segments);
			
		then:
			segments.size() == expectedNumberOfSegments
			def idx = 0
			expectedSegments.every { expectedSegment ->
				segments.get(idx++).getBytes().length <= 140
			}
			def idx2 = 0
			expectedSegments.every { expectedSegment ->
				Arrays.equals(expectedSegment as byte[], segments.get(idx2++).getBytes())
			}
			

		where:
			original							|| expectedNumberOfSegments | expectedSegments
			/**
			 * only extension characters
			 */
			"|" * 50			  				|| 1						| [asList(CHARSET_GSM7.encode("|" * 50))]
			"|" * 80			  				|| 1						| [asList(CHARSET_GSM7.encode("|" * 80))]
			"|" * 81							|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("|" * 76)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("|" * 5))
																			  ]
			"|" * 152							|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("|" * 76)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("|" * 76))
																			  ]
			"|" * 153							|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("|" * 76)),
																				header(3, 2) + asList(CHARSET_GSM7.encode("|" * 76)),
																				header(3, 3) + asList(CHARSET_GSM7.encode("|"))
																			  ]
			/**
			 * mixing basic characters and extension characters
			 */
			"a" * 158 + "|"		  				|| 1						| [asList(CHARSET_GSM7.encode("a" * 158 + "|"))]
			"|" * 79 + "a" * 2	  				|| 1						| [asList(CHARSET_GSM7.encode("|" * 79 + "a" * 2))]
			"a" * 63 + "|" * 48	  				|| 1						| [asList(CHARSET_GSM7.encode("a" * 63 + "|" * 48))]
			"a" * 64 + "|" * 48	  				|| 1						| [asList(CHARSET_GSM7.encode("a" * 64 + "|" * 48))]
			"a|a" * 40			  				|| 1						| [asList(CHARSET_GSM7.encode("a|a" * 40))]
			"a|a" * 39 + "aaaa"			  		|| 1						| [asList(CHARSET_GSM7.encode("a|a" * 39 + "aaaa"))]
			"a|a" * 39 + "||"	  				|| 1						| [asList(CHARSET_GSM7.encode("a|a" * 39 + "||"))]
			"a|a" * 39 + "|aa"	  				|| 1						| [asList(CHARSET_GSM7.encode("a|a" * 39 + "|aa"))]
			"a" * 159 + "|"		  				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("a" * 6 + "|"))
																			  ]
			"a" * 158 + "|"	* 2	  				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("a" * 5 + "|" * 2))
																			  ]
			"a" * 65 + "|" * 48	  				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 65 + "|" * 44)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("|" * 4))
																			  ]
			"a" * 153 + "|" * 11				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("|" * 11))
																			  ]
			"a|a" * 39 + "aaaaa"				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a|a" * 38 + "a")),
																				header(2, 2) + asList(CHARSET_GSM7.encode("|a" + "aaaaa"))
																			  ]
			"a|a" * 39 + "||a"					|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a|a" * 38 + "a")),
																				header(2, 2) + asList(CHARSET_GSM7.encode("|a" + "||a"))
																			  ]
			"a|a" * 39 + "|^a"	 				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a|a" * 38 + "a")),
																				header(2, 2) + asList(CHARSET_GSM7.encode("|a" + "|^a"))
																			  ]
			"a|a" * 40 + "a"	 				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a|a" * 38 + "a")),
																				header(2, 2) + asList(CHARSET_GSM7.encode("|a" + "a|a" + "a"))
																			  ]
			"a" * 161 + "|" * 11				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("a" * 8 + "|" * 11))
																			  ]
			"a" * 200 + "|" * 11				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("a" * 47 + "|" * 11))
																			  ]
			"a" * 153 + "|" * 76				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("|" * 76))
																			  ]
			"abcdefghi|klm^n" * 18 				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("abcdefghi|klm^n" * 9)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("abcdefghi|klm^n" * 9))
																			  ]
			"a" * 304 + "|"		  				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(2, 2) + asList(CHARSET_GSM7.encode("a" * 151 + "|"))
																			  ]
			"a" * 304 + "|a"	  				|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(3, 2) + asList(CHARSET_GSM7.encode("a" * 151 + "|")),
																				header(3, 3) + asList(CHARSET_GSM7.encode("a"))
																			  ]
			"a" * 305 + "|"		  				|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(3, 2) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(3, 3) + asList(CHARSET_GSM7.encode("|"))
																			  ]
			"a" * 306 + "|"		  				|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(3, 2) + asList(CHARSET_GSM7.encode("a" * 153)),
																				header(3, 3) + asList(CHARSET_GSM7.encode("|"))
																			  ]
			"abcdefghi|klm^n"*18 + "z"			|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM7.encode("abcdefghi|klm^n" * 9)),
																				header(3, 2) + asList(CHARSET_GSM7.encode("abcdefghi|klm^n" * 9)),
																				header(3, 3) + asList(CHARSET_GSM7.encode("z"))
																			  ]
			
		    occurrences = original.count("|")
	}
	
	def "GSM 7-bit #original.length() characters with 2 bytes for reference number should be split in #expectedNumberOfSegments segments"() {
		given:
			def referenceNumberGenerator = Mock(ReferenceNumberGenerator)
			referenceNumberGenerator.generateReferenceNumber() >> [referenceNumber, referenceNumber]
			def splitter = new GsmMessageSplitter({ m -> new EncodedMessage(m, CHARSET_GSM7.encode(m), NAME_GSM7)}, SEGMENT_SIZE_GSM_7BIT_SMS_PACKING_MODE, referenceNumberGenerator, new GsmBasicCharsetExtensionTableCounter())

		when:
			def segments = splitter.split(original);
			debug(CHARSET_GSM7, original, expectedSegments, segments);
			
		then:
			segments.size() == expectedNumberOfSegments
			def idx = 0
			expectedSegments.every { expectedSegment ->
				segments.get(idx++).getBytes().length <= 140
			}
			def idx2 = 0
			expectedSegments.every { expectedSegment ->
				Arrays.equals(expectedSegment as byte[], segments.get(idx2++).getBytes())
			}
			

		where:
			original							|| expectedNumberOfSegments | expectedSegments
			// message length <= 160
			// fits in one segment (no header required)
			"a" * 100  							|| 1						| [asList(CHARSET_GSM7.encode("a" * 100))]
			"a" * 159							|| 1						| [asList(CHARSET_GSM7.encode("a" * 159))]
			"a" * 160							|| 1						| [asList(CHARSET_GSM7.encode("a" * 160))]
			// 160 > message length >= 304
			// fits in two segments (152 characters * 2)
			"a" * 161							|| 2						| [
																				header(2, 1, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(2, 2, true) + asList(CHARSET_GSM7.encode("a" * 9))
																			  ]
			"a" * 200							|| 2						| [
																				header(2, 1, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(2, 2, true) + asList(CHARSET_GSM7.encode("a" * 48))
																			  ]
			"a" * 303							|| 2						| [
																				header(2, 1, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(2, 2, true) + asList(CHARSET_GSM7.encode("a" * 151))
																			  ]
			"a" * 304							|| 2						| [
																				header(2, 1, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(2, 2, true) + asList(CHARSET_GSM7.encode("a" * 152))
																			  ]
		   // 305 > message length >= 456
		   // fit in three segments (152 characters * 3)
			"a" * 305							|| 3						| [
																				header(3, 1, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(3, 2, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(3, 3, true) + asList(CHARSET_GSM7.encode("a"))
																			  ]
			"a" * 455							|| 3						| [
																				header(3, 1, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(3, 2, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(3, 3, true) + asList(CHARSET_GSM7.encode("a" * 151))
																			  ]
			"a" * 456							|| 3						| [
																				header(3, 1, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(3, 2, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(3, 3, true) + asList(CHARSET_GSM7.encode("a" * 152))
																			  ]
			// 456 > message length >= 608
			// fit in four segments (152 characters * 4)
			"a" * 457							|| 4						| [
																				header(4, 1, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(4, 2, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(4, 3, true) + asList(CHARSET_GSM7.encode("a" * 152)),
																				header(4, 4, true) + asList(CHARSET_GSM7.encode("a"))
																			  ]
	}

	def "GSM 8-bit #original.length() characters should be split in #expectedNumberOfSegments segments"() {
		given:
			def referenceNumberGenerator = Mock(ReferenceNumberGenerator)
			referenceNumberGenerator.generateReferenceNumber() >> referenceNumber
			def splitter = new GsmMessageSplitter({ m -> new EncodedMessage(m, CHARSET_GSM8.encode(m), NAME_GSM8)}, SEGMENT_SIZE_GSM_8BIT, referenceNumberGenerator, new GsmBasicCharsetExtensionTableCounter())

		when:
			def segments = splitter.split(original);
			debug(CHARSET_GSM8, original, expectedSegments, segments);
			
		then:
			segments.size() == expectedNumberOfSegments
			def idx = 0
			expectedSegments.every { expectedSegment ->
				segments.get(idx++).getBytes().length <= 140
			}
			def idx2 = 0
			expectedSegments.every { expectedSegment ->
				Arrays.equals(expectedSegment as byte[], segments.get(idx2++).getBytes())
			}
			

		where:
			original					|| expectedNumberOfSegments | expectedSegments
			// message length <= 140
			// fits in one segment (no header required)
			"b" * 100  					|| 1						| [asList(CHARSET_GSM8.encode("b" * 100))]
			"b" * 139					|| 1						| [asList(CHARSET_GSM8.encode("b" * 139))]
			"b" * 140					|| 1						| [asList(CHARSET_GSM8.encode("b" * 140))]
			// 140 > message length >= 268
			// fits in two segments (134 characters * 2)
			"b" * 141					|| 2						| [
																		header(2, 1) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(2, 2) + asList(CHARSET_GSM8.encode("b" * 7))
																	  ]
			"b" * 200					|| 2						| [
																		header(2, 1) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(2, 2) + asList(CHARSET_GSM8.encode("b" * 66))
																	  ]
			"b" * 267					|| 2						| [
																		header(2, 1) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(2, 2) + asList(CHARSET_GSM8.encode("b" * 133))
																	  ]
			"b" * 268					|| 2						| [
																		header(2, 1) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(2, 2) + asList(CHARSET_GSM8.encode("b" * 134))
																	  ]
		   // 268 > message length >= 402
		   // fit in three segments (134 characters * 3)
			"b" * 269					|| 3						| [
																		header(3, 1) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(3, 2) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(3, 3) + asList(CHARSET_GSM8.encode("b" * 1))
																	  ]
			"b" * 270					|| 3						| [
																		header(3, 1) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(3, 2) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(3, 3) + asList(CHARSET_GSM8.encode("b" * 2))
																	  ]
			"b" * 401					|| 3						| [
																		header(3, 1) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(3, 2) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(3, 3) + asList(CHARSET_GSM8.encode("b" * 133))
																	  ]
			"b" * 402					|| 3						| [
																		header(3, 1) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(3, 2) + asList(CHARSET_GSM8.encode("b" * 134)),
																		header(3, 3) + asList(CHARSET_GSM8.encode("b" * 134))
																	  ]
	}
	
	def "GSM 8-bit #original.length() containing #occurrences characters of extension table should be split in #expectedNumberOfSegments segments"() {
		given:
			def referenceNumberGenerator = Mock(ReferenceNumberGenerator)
			referenceNumberGenerator.generateReferenceNumber() >> referenceNumber
			def splitter = new GsmMessageSplitter({ m -> new EncodedMessage(m, CHARSET_GSM8.encode(m), NAME_GSM8)}, SEGMENT_SIZE_GSM_8BIT, referenceNumberGenerator, new GsmBasicCharsetExtensionTableCounter())

		when:
			def segments = splitter.split(original);
			debug(CHARSET_GSM8, original, expectedSegments, segments);
			
		then:
			segments.size() == expectedNumberOfSegments
			def idx = 0
			expectedSegments.every { expectedSegment ->
				segments.get(idx++).getBytes().length <= 140
			}
			def idx2 = 0
			expectedSegments.every { expectedSegment ->
				Arrays.equals(expectedSegment as byte[], segments.get(idx2++).getBytes())
			}
			

		where:
			original							|| expectedNumberOfSegments | expectedSegments
			/**
			 * only extension characters
			 */
			"|" * 50			  				|| 1						| [asList(CHARSET_GSM8.encode("|" * 50))]
			"|" * 70			  				|| 1						| [asList(CHARSET_GSM8.encode("|" * 70))]
			"|" * 71							|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("|" * 67)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("|" * 4))
																			  ]
			"|" * 134							|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("|" * 67)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("|" * 67))
																			  ]
			"|" * 135							|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM8.encode("|" * 67)),
																				header(3, 2) + asList(CHARSET_GSM8.encode("|" * 67)),
																				header(3, 3) + asList(CHARSET_GSM8.encode("|"))
																			  ]
			"|" * 201							|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM8.encode("|" * 67)),
																				header(3, 2) + asList(CHARSET_GSM8.encode("|" * 67)),
																				header(3, 3) + asList(CHARSET_GSM8.encode("|" * 67))
																			  ]
			/**
			 * mixing basic characters and extension characters
			 */
			"a" * 138 + "|"		  				|| 1						| [asList(CHARSET_GSM8.encode("a" * 138 + "|"))]
			"|" * 69 + "a" * 2	  				|| 1						| [asList(CHARSET_GSM8.encode("|" * 69 + "a" * 2))]
			"a" * 63 + "|" * 38	  				|| 1						| [asList(CHARSET_GSM8.encode("a" * 63 + "|" * 38))]
			"a" * 64 + "|" * 38	  				|| 1						| [asList(CHARSET_GSM8.encode("a" * 64 + "|" * 38))]
			"a|a" * 35			  				|| 1						| [asList(CHARSET_GSM8.encode("a|a" * 35))]
			"a" * 139 + "|"		  				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("a" * 5 + "|"))
																			  ]
			"a" * 138 + "|"	* 2	  				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("a" * 4 + "|" * 2))
																			  ]
			"a" * 65 + "|" * 38	  				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("a" * 65 + "|" * 34)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("|" * 4))
																			  ]
			"a" * 134 + "|" * 11				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("|" * 11))
																			  ]
			"a|a" * 35 + "a"	 				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("a|a" * 33 + "a")),
																				header(2, 2) + asList(CHARSET_GSM8.encode("|a" + "a|a" + "a"))
																			  ]
			"a" * 141 + "|" * 11				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("a" * 7 + "|" * 11))
																			  ]
			"a" * 200 + "|" * 11				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("a" * 66 + "|" * 11))
																			  ]
			"a" * 134 + "|" * 67				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("|" * 67))
																			  ]
			"abcdefghi|klm^n" * 15 				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("abcdefghi|klm^n" * 7 + "abcdefghi|klm")),
																				header(2, 2) + asList(CHARSET_GSM8.encode("^n" + "abcdefghi|klm^n" * 7))
																			  ]
			"a" * 266 + "|"		  				|| 2						| [
																				header(2, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(2, 2) + asList(CHARSET_GSM8.encode("a" * 132 + "|"))
																			  ]
			"a" * 266 + "|a"	  				|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(3, 2) + asList(CHARSET_GSM8.encode("a" * 132 + "|")),
																				header(3, 3) + asList(CHARSET_GSM8.encode("a"))
																			  ]
			"a" * 267 + "|"		  				|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(3, 2) + asList(CHARSET_GSM8.encode("a" * 133)),
																				header(3, 3) + asList(CHARSET_GSM8.encode("|"))
																			  ]
			"a" * 268 + "|"		  				|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(3, 2) + asList(CHARSET_GSM8.encode("a" * 134)),
																				header(3, 3) + asList(CHARSET_GSM8.encode("|"))
																			  ]
			"abcdefghi|klm^n" * 18 				|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM8.encode("abcdefghi|klm^n" * 7 + "abcdefghi|klm")),
																				header(3, 2) + asList(CHARSET_GSM8.encode("^n" + "abcdefghi|klm^n" * 7 + "abcdefghi|k")),
																				header(3, 3) + asList(CHARSET_GSM8.encode("lm^n" + "abcdefghi|klm^n" * 2))
																			  ]
			"abcdefghi|klm^n"*15 + "|^{a}d,0[z"	|| 3						| [
																				header(3, 1) + asList(CHARSET_GSM8.encode("abcdefghi|klm^n" * 7 + "abcdefghi|klm")),
																				header(3, 2) + asList(CHARSET_GSM8.encode("^n" + "abcdefghi|klm^n" * 7 + "|^{a}d,0")),
																				header(3, 3) + asList(CHARSET_GSM8.encode("[z"))
																			  ]
																	  
		    occurrences = original.count("|")
	}

	def "UCS-2 #original.length() characters should be split in #expectedNumberOfSegments segments"() {
		given:
			def referenceNumberGenerator = Mock(ReferenceNumberGenerator)
			referenceNumberGenerator.generateReferenceNumber() >> referenceNumber
			def splitter = new GsmMessageSplitter({ m -> new EncodedMessage(m, CHARSET_UCS_2.encode(m), NAME_UCS_2)}, SEGMENT_SIZE_UCS2, referenceNumberGenerator)

		when:
			def segments = splitter.split(original);
			debug(CHARSET_UCS_2, original, expectedSegments, segments);
			
		then:
			segments.size() == expectedNumberOfSegments
			def idx = 0
			expectedSegments.every { expectedSegment ->
				segments.get(idx++).getBytes().length <= 140
			}
			def idx2 = 0
			expectedSegments.every { expectedSegment ->
				Arrays.equals(expectedSegment as byte[], segments.get(idx2++).getBytes())
			}
			

		where:
			original					|| expectedNumberOfSegments | expectedSegments
			// message length <= 70
			// fits in one segment (no header required)
			"c" * 50  		|| 1						| [asList(CHARSET_UCS_2.encode("c" * 50))]
			"c" * 69		|| 1						| [asList(CHARSET_UCS_2.encode("c" * 69))]
			"c" * 70		|| 1						| [asList(CHARSET_UCS_2.encode("c" * 70))]
			// 70 > message length >= 134
			// fits in two segments (67 characters * 2)
			"c" * 71		|| 2						| [
															header(2, 1) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(2, 2) + asList(CHARSET_UCS_2.encode("c" * 4))
														  ]
			"c" * 100		|| 2						| [
															header(2, 1) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(2, 2) + asList(CHARSET_UCS_2.encode("c" * 33))
														  ]
			"c" * 133		|| 2						| [
															header(2, 1) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(2, 2) + asList(CHARSET_UCS_2.encode("c" * 66))
														  ]
			"c" * 134		|| 2						| [
															header(2, 1) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(2, 2) + asList(CHARSET_UCS_2.encode("c" * 67))
														  ]
		   // 134 > message length >= 201
		   // fit in three segments (67 characters * 3)
			"c" * 135		|| 3						| [
															header(3, 1) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(3, 2) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(3, 3) + asList(CHARSET_UCS_2.encode("c" * 1))
														  ]
			"c" * 136		|| 3						| [
															header(3, 1) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(3, 2) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(3, 3) + asList(CHARSET_UCS_2.encode("c" * 2))
														  ]
			"c" * 200		|| 3						| [
															header(3, 1) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(3, 2) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(3, 3) + asList(CHARSET_UCS_2.encode("c" * 66))
														  ]
			"c" * 201		|| 3						| [
															header(3, 1) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(3, 2) + asList(CHARSET_UCS_2.encode("c" * 67)),
															header(3, 3) + asList(CHARSET_UCS_2.encode("c" * 67))
														  ]
	}
	
	
	def debug(Charset charset, String original, List<Byte[]> expectedSegments, List<Segment> segments) {
		log.debug("original         :       {}", original)
		log.debug("original encoded :       {}", charset.encode(original))
		log.debug("")
		for (int i=0 ; i<expectedSegments.size() ; i++) {
			def expectedSegment = expectedSegments[i];
			def actualSegment = i>=segments.size() ? null : segments.get(i).getBytes()
			log.debug("expected segment : {} {}", ("" + (expectedSegment as byte[]).length).padRight(5), expectedSegment as byte[])
			log.debug("                 :       {}", decode(charset, expectedSegment as byte[], expectedSegments.size()))
			log.debug("actual segment   : {} {}", ("" + (actualSegment == null ? "?" : actualSegment.length)).padRight(5), actualSegment);
			log.debug("                 :       {}", decode(charset, actualSegment, expectedSegments.size()))
			def equals = Arrays.equals(expectedSegment as byte[], actualSegment)
			log.debug("                => {}  {}", equals, equals ? "" : "/!\\")
		}
	}
	
	def decode(Charset charset, byte[] bytes, int numberOfSegments) {
		if (bytes == null) {
			return null
		}
		if (numberOfSegments == 1) {
			return charset.decode(bytes);
		}
		int headerLength = bytes[0] + 1;
		return charset.decode(Arrays.copyOfRange(bytes, headerLength, bytes.length))
	}
	
	private List<Byte> header(int numberOfSegments, int messageNumber) {
		return header(numberOfSegments, messageNumber, false)
	}
	private List<Byte> header(int numberOfSegments, int messageNumber, boolean twoByteReferenceNumber) {
		if (twoByteReferenceNumber) {
			return [0x06, 0x08, 0x04, referenceNumber, referenceNumber, (byte) numberOfSegments, (byte) messageNumber]
		}
		return [0x05, 0x00, 0x03, referenceNumber, (byte) numberOfSegments, (byte) messageNumber]
	}
}
