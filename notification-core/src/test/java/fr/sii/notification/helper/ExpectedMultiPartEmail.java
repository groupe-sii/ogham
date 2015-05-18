package fr.sii.notification.helper;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;


public class ExpectedMultiPartEmail extends ExpectedEmailHeader {
	private String[] bodies;
	
	public ExpectedMultiPartEmail(String subject, String[] bodies, String from, String... to) {
		super();
		this.subject = subject;
		this.bodies = bodies;
		this.from = from;
		this.to = to;
	}

	public ExpectedMultiPartEmail(String subject, InputStream[] bodies, String from, String... to) throws IOException {
		this(subject, toString(bodies), from, to);
	}

	private static String[] toString(InputStream[] bodies) throws IOException {
		String[] result = new String[bodies.length];
		for(int i=0 ; i<bodies.length ; i++) {
			result[i] = IOUtils.toString(bodies[i]);
		}
		return result;
	}

	public String[] getBodies() {
		return bodies;
	}
}
