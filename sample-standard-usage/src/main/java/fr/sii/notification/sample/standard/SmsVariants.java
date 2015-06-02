package fr.sii.notification.sample.standard;

import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.sms.message.PhoneNumber;
import fr.sii.notification.sms.message.Recipient;
import fr.sii.notification.sms.message.Sms;

public class SmsVariants {
	public static void main(String[] args) {
		new Sms(new StringContent("sms content"), new Sender("11111"), "000000000", "000000000");
		new Sms(new StringContent("sms content"), new Sender("11111"), "000000000");
		new Sms(new StringContent("sms content"), new Sender("11111"));
		new Sms("sms content", new Sender("11111"), "000000000", "000000000");
		new Sms("sms content", new Sender("11111"), "000000000");
		new Sms("sms content", new Sender("11111"), new PhoneNumber("000000000"), new PhoneNumber("000000000"));
		new Sms("sms content", new Sender("11111"), new PhoneNumber("000000000"));
		new Sms("sms content", new Sender("11111"), new Recipient(new PhoneNumber("000000000")), new Recipient(new PhoneNumber("000000000")));
		new Sms("sms content", new Sender("11111"), new Recipient(new PhoneNumber("000000000")));
		
		new Sms(new StringContent("sms content"), "000000000", "000000000");
		new Sms(new StringContent("sms content"), "000000000");
		new Sms(new StringContent("sms content"));
		new Sms("sms content", "000000000", "000000000");
		new Sms("sms content", "000000000");
		new Sms("sms content", new PhoneNumber("000000000"), new PhoneNumber("000000000"));
		new Sms("sms content", new PhoneNumber("000000000"));
		new Sms("sms content", new Recipient(new PhoneNumber("000000000")), new Recipient(new PhoneNumber("000000000")));
		new Sms("sms content", new Recipient(new PhoneNumber("000000000")));
		new Sms("sms content");
	}
}
