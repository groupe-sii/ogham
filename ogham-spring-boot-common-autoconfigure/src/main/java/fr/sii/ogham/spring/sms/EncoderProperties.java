package fr.sii.ogham.spring.sms;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class EncoderProperties {
	@NestedConfigurationProperty
	private AutoGuessProperties autoGuess = new AutoGuessProperties();
	/**
	 * Set which Cloudhopper Charset should be used if nothing else is
	 * configured.
	 */
	private String defaultCharset = "GSM";
	@NestedConfigurationProperty
	private Gsm7bitPackedProperties gsm7bitPacked = new Gsm7bitPackedProperties();
	@NestedConfigurationProperty
	private Gsm8bitProperties gsm8bit = new Gsm8bitProperties();
	@NestedConfigurationProperty
	private Latin1Properties latin1 = new Latin1Properties();
	@NestedConfigurationProperty
	private Ucs2Properties ucs2 = new Ucs2Properties();

	public static class AutoGuessProperties {
		/**
		 * Enable/disable automatic guessing of message encoding.
		 * 
		 * <p>
		 * If enables, it automatically guess the best supported encoding in
		 * order to use the minimum octets:
		 * <ul>
		 * <li>It encodes using GSM 7-bit default alphabet if the message
		 * contains only characters defined in the table. Message is packed so
		 * the message can have a maximum length of 160 characters. This is
		 * enable only if automatic guessing is enabled (using
		 * {@link #autoGuess(Boolean)}) and GSM 7-bit is enabled (using
		 * {@link #gsm7bitPacked(Integer)}).</li>
		 * <li>It encodes using GSM 8-bit data encoding if the message contains
		 * only characters that can be encoded on one octet. This is enable only
		 * if automatic guessing is enabled (using {@link #autoGuess(Boolean)}
		 * and GSM 8-bit is enabled (using {@link #gsm8bit(Integer)}).</li>
		 * <li>It encodes using Latin 1 (ISO-8859-1) data encoding if the
		 * message contains only characters that can be encoded on one octet.
		 * This is enable only if automatic guessing is enabled (using
		 * {@link #autoGuess(Boolean)} and GSM 8-bit is enabled (using
		 * {@link #latin1(Integer)}).</li>
		 * <li>It encodes using UCS-2 encoding if the message contains special
		 * characters that can't be encoded on one octet. Each character is
		 * encoded on two octets. This is enable only if automatic guessing is
		 * enabled (using {@link #autoGuess(Boolean)}) and UCS-2 is enabled
		 * (using {@link #ucs2(Integer)}).</li>
		 * </ul>
		 */
		private boolean enable = true;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}
	}

	public static class Gsm7bitPackedProperties {
		/**
		 * Set priority for encoding text messages using GSM 7-bit encoding. GSM
		 * 7-bit encoding and GSM 8-bit encoding use the same character tables.
		 * Only 7 bits are necessary to represents characters. In GSM 8-bit
		 * encoding a leading 0 is added. However, GSM 7-bit encoding is packed.
		 * Every character is "merged" with the next one in order to use more
		 * characters for the same number of octets.
		 * 
		 * <p>
		 * If priority value is 0 or negative, it disables GSM 7-bit encoding.
		 * 
		 * <p>
		 * Disabled by default as most services doesn't support it.
		 * 
		 */
		private int priority = 0;

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}
	}

	public static class Gsm8bitProperties {
		/**
		 * Set priority for encoding text messages using GSM 8-bit encoding. GSM
		 * 7-bit encoding and GSM 8-bit encoding use the same character tables.
		 * Only 7 bits are necessary to represents characters. In GSM 8-bit
		 * encoding a leading 0 is added.
		 * 
		 * <p>
		 * If priority value is 0 or negative, it disables GSM 8-bit encoding.
		 * 
		 */
		private int priority = 99000;

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}
	}

	public static class Latin1Properties {
		/**
		 * Set priority for encoding text messages using Latin-1 (ISO-8859-1).
		 * 
		 * <p>
		 * If priority value is 0 or negative, it disables Latin-1 encoding.
		 * 
		 */
		private int priority = 98000;

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}
	}

	public static class Ucs2Properties {
		/**
		 * Set priority for encoding text messages using UCS-2. UCS-2 uses two
		 * octets per character.
		 * 
		 * <p>
		 * If priority value is 0 or negative, it disables UCS-2 encoding.
		 * 
		 */
		private int priority = 90000;

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}
	}

	public AutoGuessProperties getAutoGuess() {
		return autoGuess;
	}

	public void setAutoGuess(AutoGuessProperties autoGuess) {
		this.autoGuess = autoGuess;
	}

	public String getDefaultCharset() {
		return defaultCharset;
	}

	public void setDefaultCharset(String defaultCharset) {
		this.defaultCharset = defaultCharset;
	}

	public Gsm7bitPackedProperties getGsm7bitPacked() {
		return gsm7bitPacked;
	}

	public void setGsm7bitPacked(Gsm7bitPackedProperties gsm7bitPacked) {
		this.gsm7bitPacked = gsm7bitPacked;
	}

	public Gsm8bitProperties getGsm8bit() {
		return gsm8bit;
	}

	public void setGsm8bitPacked(Gsm8bitProperties gsm8bitPacked) {
		this.gsm8bit = gsm8bitPacked;
	}

	public Latin1Properties getLatin1() {
		return latin1;
	}

	public void setLatin1(Latin1Properties latin1) {
		this.latin1 = latin1;
	}

	public Ucs2Properties getUcs2() {
		return ucs2;
	}

	public void setUcs2(Ucs2Properties ucs2) {
		this.ucs2 = ucs2;
	}
}
