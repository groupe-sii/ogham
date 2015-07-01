package fr.sii.ogham.sms.sender.impl.ovh;

/**
 * The optional parameters used to customize OVH SMS:
 * <ul>
 * <li>noStop: do not display STOP footer. Disabled by default</li>
 * <li>tag: mark the sent SMS with a tag (20 characters max)</li>
 * <li>smsCoding: the SMS encoding. 7bits by default. If you change to UTF-8 (8bits),
 * the message limit will be 70 instead of 160</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class OvhOptions {
	/**
	 * Option to skip displaying STOP message
	 */
	private int noStop;

	/**
	 * Option to tag the sent SMS (20 characters max)
	 */
	private String tag;

	/**
	 * Option to select encoding
	 */
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