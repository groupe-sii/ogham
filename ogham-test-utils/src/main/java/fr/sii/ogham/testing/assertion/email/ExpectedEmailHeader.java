package fr.sii.ogham.testing.assertion.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class used in tests for ensuring that the email is respected. It provides the
 * following information:
 * <ul>
 * <li>The expected subject</li>
 * <li>The expected sender address</li>
 * <li>The expected recipients (to, cc, bcc)</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class ExpectedEmailHeader {
	/**
	 * The expected subject
	 */
	protected String subject;
	
	/**
	 * The expected sender address
	 */
	protected String from;
	
	/**
	 * The expected list of recipients for the "to" field
	 */
	protected List<String> to = new ArrayList<>();

	/**
	 * The expected list of recipients for the "cc" field
	 */
	protected List<String> cc = new ArrayList<>();

	/**
	 * The expected list of recipients for the "bcc" field
	 */
	protected List<String> bcc = new ArrayList<>();

	public ExpectedEmailHeader(String subject, String from, String... to) {
		this(subject, from, new ArrayList<>(Arrays.asList(to)));
	}

	public ExpectedEmailHeader(String subject, String from, List<String> to) {
		super();
		this.subject = subject;
		this.from = from;
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public String getFrom() {
		return from;
	}

	public List<String> getTo() {
		return to;
	}

	public List<String> getCc() {
		return cc;
	}

	public List<String> getBcc() {
		return bcc;
	}

	public void setCc(String... cc) {
		this.cc = new ArrayList<>(Arrays.asList(cc));
	}

	public void setBcc(String... bcc) {
		this.bcc = new ArrayList<>(Arrays.asList(bcc));
	}
}