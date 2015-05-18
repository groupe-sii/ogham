package fr.sii.notification.helper;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage.RecipientType;

import org.junit.Assert;

import com.icegreen.greenmail.util.GreenMailUtil;

public class AssertEmail {
	
	public static void assertEquals(ExpectedMultiPartEmail expectedEmail, Message actualEmail, boolean strict) throws MessagingException, IOException {
		assertHeaders(expectedEmail, actualEmail);
		Object content = actualEmail.getContent();
		Assert.assertTrue("should be multipart message", content instanceof Multipart);
		Multipart mp = (Multipart) content;
		Assert.assertEquals("should have "+expectedEmail.getExpectedContents().length+" parts", expectedEmail.getExpectedContents().length, mp.getCount());
		for(int i=0 ; i<expectedEmail.getExpectedContents().length ; i++) {
			assertBody(expectedEmail.getExpectedContents()[i].getBody(), GreenMailUtil.getBody(mp.getBodyPart(i)), strict);
			assertMimetype(expectedEmail.getExpectedContents()[i], mp.getBodyPart(i).getContentType());
		}
	}
	
	public static void assertEquals(ExpectedMultiPartEmail expectedEmail, Message actualEmail) throws MessagingException, IOException {
		assertEquals(expectedEmail, actualEmail, false);
	}

	public static void assertEquals(ExpectedMultiPartEmail expectedEmail, Message[] actualEmails) throws MessagingException, IOException {
		assertEquals(new ExpectedMultiPartEmail[] { expectedEmail }, actualEmails);
	}

	public static void assertEquals(ExpectedMultiPartEmail[] expectedEmail, Message[] actualEmails) throws MessagingException, IOException {
		Assert.assertEquals("should have "+expectedEmail.length+" email", expectedEmail.length, actualEmails.length);
		for(int i=0 ; i<expectedEmail.length ; i++) {
			assertEquals(expectedEmail[i], actualEmails[i]);
		}
	}

	public static void assertEquals(ExpectedEmail expectedEmail, Message[] actualEmails) throws MessagingException {
		assertEquals(new ExpectedEmail[] { expectedEmail }, actualEmails);
	}

	public static void assertEquals(ExpectedEmail[] expectedEmail, Message[] actualEmails) throws MessagingException {
		Assert.assertEquals("should have "+expectedEmail.length+" email", expectedEmail.length, actualEmails.length);
		for(int i=0 ; i<expectedEmail.length ; i++) {
			assertEquals(expectedEmail[i], actualEmails[i]);
		}
	}

	public static void assertEquals(ExpectedEmail expectedEmail, Message actualEmail) throws MessagingException {
		assertEquals(expectedEmail, actualEmail, false);
	}
	
	public static void assertEquals(ExpectedEmail expectedEmail, Message actualEmail, boolean strict) throws MessagingException {
		assertHeaders(expectedEmail, actualEmail);
		assertBody(expectedEmail.getExpectedContent().getBody(), GreenMailUtil.getBody(actualEmail), strict);
		assertMimetype(expectedEmail.getExpectedContent(), actualEmail);
	}


	
	private static void assertMimetype(ExpectedContent expectedContent, Message actualEmail) throws MessagingException {
		assertMimetype(expectedContent, actualEmail.getContentType());
	}
	
	private static void assertMimetype(ExpectedContent expectedContent, String contentType) throws MessagingException {
		Assert.assertTrue("mimetype should be "+expectedContent.getMimetype()+" instead of "+contentType, contentType.matches(expectedContent.getMimetype()));
	}
	
	private static void assertBody(String expectedBody, String actualBody, boolean strict) {
		Assert.assertEquals("body should be '"+expectedBody+"'", strict ? expectedBody : sanitize(expectedBody), strict ? actualBody : sanitize(actualBody));
	}

	private static void assertHeaders(ExpectedEmailHeader expectedEmail, Message actualEmail) throws MessagingException {
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
