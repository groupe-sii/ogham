package fr.sii.ogham.testing.extension.junit.sms.config;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * Control delays to simulate a slow server.
 * 
 * @author Aurélien Baudet
 *
 */
@Documented
@Retention(RUNTIME)
public @interface Slow {
	/**
	 * Simulate slow server by waiting {@code sendAlertNotificationDelay}
	 * milliseconds before sending "AlertNotification" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendAlertNotificationDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendBindDelay} milliseconds before
	 * sending "Bind" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendBindDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendBindRespDelay} milliseconds
	 * before sending "BindResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendBindRespDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendCancelSmDelay} milliseconds
	 * before sending "CancelSm" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendCancelSmDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendCancelSmRespDelay}
	 * milliseconds before sending "CancelSmResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendCancelSmRespDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendDataSmDelay} milliseconds
	 * before sending "DataSm" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendDataSmDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendDataSmRespDelay} milliseconds
	 * before sending "DataSmResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendDataSmRespDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendDeliverSmDelay} milliseconds
	 * before sending "DeliverSm" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendDeliverSmDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendDeliverSmRespDelay}
	 * milliseconds before sending "DeliverSmResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendDeliverSmRespDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendEnquireLinkDelay} milliseconds
	 * before sending "EnquireLink" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendEnquireLinkDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendEnquireLinkRespDelay}
	 * milliseconds before sending "EnquireLinkResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendEnquireLinkRespDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendGenericNackDelay} milliseconds
	 * before sending "GenericNack" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendGenericNackDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendHeaderDelay} milliseconds
	 * before sending "Header" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendHeaderDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendOutbindDelay} milliseconds
	 * before sending "Outbind" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendOutbindDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendQuerySmDelay} milliseconds
	 * before sending "QuerySm" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendQuerySmDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendQuerySmRespDelay} milliseconds
	 * before sending "QuerySmResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendQuerySmRespDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendReplaceSmDelay} milliseconds
	 * before sending "ReplaceSm" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendReplaceSmDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendReplaceSmRespDelay}
	 * milliseconds before sending "ReplaceSmResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendReplaceSmRespDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendSubmitMultiDelay} milliseconds
	 * before sending "SubmitMulti" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendSubmitMultiDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendSubmitMultiRespDelay}
	 * milliseconds before sending "SubmitMultiResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendSubmitMultiRespDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendSubmitSmDelay} milliseconds
	 * before sending "SubmitSm" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendSubmitSmDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendSubmitSmRespDelay}
	 * milliseconds before sending "SubmitSmResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendSubmitSmRespDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendUnbindDelay} milliseconds
	 * before sending "Unbind" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendUnbindDelay() default 0L;

	/**
	 * Simulate slow server by waiting {@code sendUnbindRespDelay} milliseconds
	 * before sending "UnbindResp" data
	 * 
	 * @return the delay in milliseconds (0 by default)
	 */
	long sendUnbindRespDelay() default 0L;
}
