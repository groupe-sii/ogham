package fr.sii.ogham.sms.sender.impl.ovh;


public class OvhOptions {
	private int noStop;
	
	private String tag;
	
	private SmsCoding smsCoding;
	
	public OvhOptions() {
		this(true, null, null);
	}

	public OvhOptions(boolean noStop, String tag, SmsCoding smsCoding) {
		super();
		this.noStop = noStop ? 1 : 0;
		this.tag = tag;
		this.smsCoding = smsCoding;
	}

	public int getNoStop() {
		return noStop;
	}

	public String getTag() {
		return tag;
	}

	public SmsCoding getSmsCoding() {
		return smsCoding;
	}

	public void setNoStop(boolean noStop) {
		this.noStop = noStop ? 1 : 0;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setSmsCoding(SmsCoding smsCoding) {
		this.smsCoding = smsCoding;
	}
}