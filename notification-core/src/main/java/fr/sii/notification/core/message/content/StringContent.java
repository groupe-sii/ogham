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
		StringBuilder builder = new StringBuilder();
		builder.append("StringContent [content=").append(content).append("]");
		return builder.toString();
	}
}
