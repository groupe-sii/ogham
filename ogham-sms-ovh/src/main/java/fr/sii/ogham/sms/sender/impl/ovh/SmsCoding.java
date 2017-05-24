package fr.sii.ogham.sms.sender.impl.ovh;

public enum SmsCoding {
	/**
	 * 7bit encoding
	 */
	NORMAL(1),
	/**
	 * 8bit encoding (UTF-8)
	 */
	UTF_8(2);

	private final int value;

	private SmsCoding(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}