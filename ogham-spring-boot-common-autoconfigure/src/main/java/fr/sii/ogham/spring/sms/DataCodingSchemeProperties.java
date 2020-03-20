package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.gsm.DataCoding;

import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.DataCodingProvider;

public class DataCodingSchemeProperties {
	/**
	 * Use the same Data Coding Scheme value for all messages if set.
	 */
	private Byte value;
	@NestedConfigurationProperty
	private AutoProperties auto = new AutoProperties();

	public static class AutoProperties {
		/**
		 * Enable/disable automatic mode based on SMPP interface version.
		 * 
		 * <p>
		 * {@link DataCodingProvider} implementation is selected based on SMPP
		 * interface version. SMPP v3.3 Data Coding Scheme values are defined in
		 * <a href=
		 * "https://en.wikipedia.org/wiki/Data_Coding_Scheme#SMS_Data_Coding_Scheme">SMS
		 * Data Coding Scheme</a>. SMPP 3.4 introduced a new list of data_coding
		 * values (<a href=
		 * "https://en.wikipedia.org/wiki/Short_Message_Peer-to-Peer#PDU_body">PDU
		 * body</a>).
		 * </p>
		 * 
		 * <strong>SMPP v3.3</strong>
		 * <p>
		 * The text message is encoded using {@link Charset}. According to that
		 * charset, the Data Coding Scheme is determined using the
		 * <strong>General Data Coding group</strong> table. Therefore, a simple
		 * mapping is applied:
		 * <ul>
		 * <li>{@link CharsetUtil#NAME_GSM7} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
		 * <li>{@link CharsetUtil#NAME_PACKED_GSM} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
		 * <li>{@link CharsetUtil#NAME_GSM} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_8BIT}</li>
		 * <li>{@link CharsetUtil#NAME_GSM8} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_8BIT}</li>
		 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_UCS2}</li>
		 * </ul>
		 * 
		 * 
		 * <strong>SMPP v3.4+</strong>
		 * <p>
		 * The text message is encoded using {@link Charset}. According to that
		 * charset, the Data Coding Scheme is determined using only the
		 * <strong>Alphabet</strong> table. Therefore, a simple mapping is
		 * applied:
		 * <ul>
		 * <li>{@link CharsetUtil#NAME_GSM7} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
		 * <li>{@link CharsetUtil#NAME_PACKED_GSM} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_DEFAULT}</li>
		 * <li>{@link CharsetUtil#NAME_GSM} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_8BIT}</li>
		 * <li>{@link CharsetUtil#NAME_GSM8} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_8BIT}</li>
		 * <li>{@link CharsetUtil#NAME_ISO_8859_1} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_LATIN1}</li>
		 * <li>{@link CharsetUtil#NAME_UCS_2} {@literal ->}
		 * {@link DataCoding#CHAR_ENC_UCS2}</li>
		 * </ul>
		 * 
		 * Default: <i>true</i>
		 */
		private Boolean enable;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
	}

	public Byte getValue() {
		return value;
	}

	public void setValue(Byte value) {
		this.value = value;
	}

	public AutoProperties getAuto() {
		return auto;
	}

	public void setAuto(AutoProperties auto) {
		this.auto = auto;
	}

}
