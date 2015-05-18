package fr.sii.notification.helper;

public class ExpectedEmailHeader {

	protected String subject;
	protected String from;
	protected String[] to;

	public ExpectedEmailHeader() {
		super();
	}

	public String getSubject() {
		return subject;
	}

	public String getFrom() {
		return from;
	}

	public String[] getTo() {
		return to;
	}

}