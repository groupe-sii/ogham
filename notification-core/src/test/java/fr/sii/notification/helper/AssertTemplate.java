package fr.sii.notification.helper;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import fr.sii.notification.core.message.content.StringContent;

public class AssertTemplate {
	public static void assertEquals(String expectedContentPath, String expectedMimetype, StringContent content) throws IOException {
		Assert.assertEquals("mimetype should be "+expectedMimetype, expectedMimetype, content.getMimetype().toString());
		Assert.assertEquals("parsed template is different to expected content", IOUtils.toString(AssertTemplate.class.getResourceAsStream(expectedContentPath)), content.toString());
	}
}
