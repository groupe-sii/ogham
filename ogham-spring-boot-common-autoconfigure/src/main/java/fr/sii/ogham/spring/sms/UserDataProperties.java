package fr.sii.ogham.spring.sms;

public class UserDataProperties {
	/**
	 * Enable/disable use of short_message field to carry text message (named
	 * User Data).<br />
	 * <br />
	 * Default: <i>true</i>
	 */
	private Boolean useShortMessage;
	/**
	 * Enable/disable use of {@code message_payload} optional TLV
	 * (Tag-Value-Length) parameter to carry text message (named User
	 * Data).<br />
	 * <br />
	 * Default: <i>false</i>
	 */
	private Boolean useTlvMessagePayload;

	public Boolean getUseShortMessage() {
		return useShortMessage;
	}

	public void setUseShortMessage(Boolean useShortMessage) {
		this.useShortMessage = useShortMessage;
	}

	public Boolean getUseTlvMessagePayload() {
		return useTlvMessagePayload;
	}

	public void setUseTlvMessagePayload(Boolean useTlvMessagePayload) {
		this.useTlvMessagePayload = useTlvMessagePayload;
	}

}
