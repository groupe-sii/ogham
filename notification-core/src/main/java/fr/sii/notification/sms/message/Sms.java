package fr.sii.notification.sms.message;


import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;

public class Sms implements Message {
	private Recipient to;
	
	private Content content;

	public Sms(String to, Content content) {
		this(new Recipient(to), content);
	}
	
	public Sms(String to, String content) {
		this(new Recipient(to), content);
	}
	
	public Sms(Recipient to, String content) {
		this(to, new StringContent(content));
	}
	
	public Sms(Recipient to, Content content) {
		super();
		this.to = to;
		this.content = content;
	}

	public Recipient getTo() {
		return to;
	}

	public void setTo(Recipient to) {
		this.to = to;
	}

	@Override
	public Content getContent() {
		return content;
	}

	@Override
	public void setContent(Content content) {
		this.content = content;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sms [to=").append(to).append(", content=").append(content).append("]");
		return builder.toString();
	}
}
