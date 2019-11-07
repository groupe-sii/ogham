package fr.sii.ogham.core.util;

import java.util.function.Supplier;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;

public class LogUtils {
	public static Supplier<String> summarize(Message message) {
		if (message instanceof Email) {
			return summarize((Email) message);
		}
		if (message instanceof Sms) {
			return summarize((Sms) message);
		}
		return () -> "unknown message";
	}
	
	public static Supplier<String> summarize(Email message) {
		return () -> message.toSummaryString();
	}
	
	public static Supplier<String> summarize(Sms message) {
		return () -> message.toSummaryString();
	}
}
