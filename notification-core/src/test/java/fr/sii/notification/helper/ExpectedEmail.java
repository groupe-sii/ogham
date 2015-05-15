package fr.sii.notification.helper;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;


public class ExpectedEmail {
	private String subject;
	
	private String body;
	
	private String from;
	
	private String[] to;

	public ExpectedEmail(String subject, String body, String from, String... to) {
		super();
		this.subject = subject;
		this.body = body;
		this.from = from;
		this.to = to;
	}

	public ExpectedEmail(String subject, InputStream body, String from, String... to) throws IOException {
		this(subject, IOUtils.toString(body), from, to);
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public String getFrom() {
		return from;
	}

	public String[] getTo() {
		return to;
	}
}
