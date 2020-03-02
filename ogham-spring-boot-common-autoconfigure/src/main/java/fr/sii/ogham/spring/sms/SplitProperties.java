package fr.sii.ogham.spring.sms;

public class SplitProperties {
	/**
	 * Enable/disable message splitting.
	 */
	private boolean enable = true;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
