package fr.sii.notification.helper;



public class ExpectedEmail extends ExpectedEmailHeader {
	private ExpectedContent expectedContent;
	
	public ExpectedEmail(String subject, String body, String from, String... to) {
		super();
		this.subject = subject;
		this.expectedContent = new ExpectedContent(body, "text/plain.*");
		this.from = from;
		this.to = to;
	}

	public ExpectedEmail(String subject, ExpectedContent expectedContent, String from, String... to) {
		super();
		this.subject = subject;
		this.expectedContent = expectedContent;
		this.from = from;
		this.to = to;
	}

	public ExpectedContent getExpectedContent() {
		return expectedContent;
	}
}
