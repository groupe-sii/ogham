package fr.sii.ogham.spring.general;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;

@ConfigurationProperties("ogham")
public class MessagingProperties {
	@NestedConfigurationProperty
	private WrapUncaughtExceptionsProperties wrapUncaughtExceptions = new WrapUncaughtExceptionsProperties();

	public WrapUncaughtExceptionsProperties getWrapUncaughtExceptions() {
		return wrapUncaughtExceptions;
	}

	public void setWrapUncaughtExceptions(WrapUncaughtExceptionsProperties wrapUncaughtExceptions) {
		this.wrapUncaughtExceptions = wrapUncaughtExceptions;
	}

	public static class WrapUncaughtExceptionsProperties {
		/**
		 * There are technical exceptions that are thrown by libraries used by
		 * Ogham. Those exceptions are often {@link RuntimeException}s. It can
		 * be difficult for developers of a big application to quickly identify
		 * what caused this {@link RuntimeException}. The stack trace doesn't
		 * always help to find the real source of the error. If enables, this
		 * option ensures that work done by Ogham will always throw a
		 * {@link MessagingException} even if it was a {@link RuntimeException}
		 * thrown by any component. It then helps the developer to know that the
		 * error comes from Ogham or a any used library and not something else
		 * in its application. The other benefit is that in your code you only
		 * catch a {@link MessagingException} and you are sure that it will
		 * handle all cases, no surprise with an unchecked exception that could
		 * make a big failure in your system because you didn't know this could
		 * happen. Sending a message is often not critical (if message can't be
		 * sent now, it can be sent later or manually). It it fails the whole
		 * system must keep on working. With this option enabled, your system
		 * will never fail due to an unchecked exception and you can handle the
		 * failure the same way as with checked exceptions.
		 * 
		 * Concretely, call of
		 * {@link MessagingService#send(fr.sii.ogham.core.message.Message)}
		 * catches all exceptions including {@link RuntimeException}. It wraps
		 * any exceptions into a {@link MessagingException}.
		 * 
		 */
		private boolean enable = true;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}
	}
}
