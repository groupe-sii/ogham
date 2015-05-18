package fr.sii.notification.core.message.content;


public class StringContent implements Content {

	private String content;
	
	public StringContent(String content) {
		super();
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return content;
	}

}
