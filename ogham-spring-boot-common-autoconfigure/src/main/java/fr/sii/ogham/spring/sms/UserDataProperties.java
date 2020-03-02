package fr.sii.ogham.spring.sms;

public class UserDataProperties {
	/**
	 * Enable/disable use of short_message field to carry text message (named
	 * User Data).
	 */
	private boolean useShortMessage = true;
	/**
	 * Enable/disable use of {@code message_payload} optional TLV
	 * (Tag-Value-Length) parameter to carry text message (named User Data).
	 */
	private boolean useTlvMessagePayload = false;

	public boolean isUseShortMessage() {
		return useShortMessage;
	}

	public void setUseShortMessage(boolean useShortMessage) {
		this.useShortMessage = useShortMessage;
	}

	public boolean isUseTlvMessagePayload() {
		return useTlvMessagePayload;
	}

	public void setUseTlvMessagePayload(boolean useTlvMessagePayload) {
		this.useTlvMessagePayload = useTlvMessagePayload;
	}
}
