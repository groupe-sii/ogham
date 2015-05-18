package fr.sii.notification.helper;



public class ExpectedMultiPartEmail extends ExpectedEmailHeader {
	private ExpectedContent[] expectedContents;
	
	public ExpectedMultiPartEmail(String subject, ExpectedContent[] bodies, String from, String... to) {
		super();
		this.subject = subject;
		this.expectedContents = bodies;
		this.from = from;
		this.to = to;
	}


	public ExpectedContent[] getExpectedContents() {
		return expectedContents;
	}
}
