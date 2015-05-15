package fr.sii.notification.helper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.junit.Assert;

import com.icegreen.greenmail.util.GreenMailUtil;

public class AssertEmail {
	public static void assertEquals(ExpectedEmail expectedEmail, MimeMessage[] actualEmails) throws MessagingException {
		assertEquals(new ExpectedEmail[] { expectedEmail }, actualEmails);
	}

	public static void assertEquals(ExpectedEmail[] expectedEmail, MimeMessage[] actualEmails) throws MessagingException {
		Assert.assertEquals("should have "+expectedEmail.length+" email", expectedEmail.length, actualEmails.length);
		for(int i=0 ; i<expectedEmail.length ; i++) {
			assertEquals(expectedEmail[i], actualEmails[i]);
		}
	}

	public static void assertEquals(ExpectedEmail expectedEmail, MimeMessage actualEmail) throws MessagingException {
		assertHeaders(expectedEmail, actualEmail);
		assertBody(sanitize(expectedEmail.getBody()), sanitize(GreenMailUtil.getBody(actualEmail)));
	}
	
	public static void assertStrictEquals(ExpectedEmail expectedEmail, MimeMessage actualEmail) throws MessagingException {
		assertHeaders(expectedEmail, actualEmail);
		assertBody(expectedEmail.getBody(), GreenMailUtil.getBody(actualEmail));
	}

	
	private static void assertBody(String expectedBody, String actualBody) {
		Assert.assertEquals("body should be '"+expectedBody+"'", expectedBody, actualBody);
	}

	private static void assertHeaders(ExpectedEmail expectedEmail, MimeMessage actualEmail) throws MessagingException {
		Assert.assertEquals("subject should be '"+expectedEmail.getSubject()+"'", expectedEmail.getSubject(), actualEmail.getSubject());
		Assert.assertEquals("should have only one from", 1, actualEmail.getFrom().length);
		Assert.assertEquals("should have only "+expectedEmail.getTo().length+" to", expectedEmail.getTo().length, actualEmail.getRecipients(RecipientType.TO).length);
		Assert.assertEquals("from should be '"+expectedEmail.getFrom()+"'", expectedEmail.getFrom(), actualEmail.getFrom()[0].toString());
		for(int i=0 ; i<expectedEmail.getTo().length ; i++) {
			Assert.assertEquals("to["+i+"] should be '"+expectedEmail.getTo()[i]+"'", expectedEmail.getTo()[i], actualEmail.getRecipients(RecipientType.TO)[0].toString());
		}
	}
	
	private static String sanitize(String str) {
		return str.replaceAll("\r|\n", "");
	}
}
