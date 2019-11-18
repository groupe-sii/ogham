package fr.sii.ogham.core.util;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;

public class LogUtils {
	public static Summarizer summarize(Message message) {
		return new Summarizer(message);
	}
	
	private static class Summarizer {
		private final Message message;
		
		public Summarizer(Message message) {
			super();
			this.message = message;
		}

		@Override
		public String toString() {
			if (message instanceof Email) {
				return ((Email) message).toSummaryString();
			}
			if (message instanceof Sms) {
				return ((Sms) message).toSummaryString();
			}
			return "unknown";
		}
	}
}
