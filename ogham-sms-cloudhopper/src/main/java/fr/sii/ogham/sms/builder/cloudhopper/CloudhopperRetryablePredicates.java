package fr.sii.ogham.sms.builder.cloudhopper;

import static com.cloudhopper.smpp.SmppConstants.STATUS_ALYBND;
import static com.cloudhopper.smpp.SmppConstants.STATUS_INVPASWD;
import static com.cloudhopper.smpp.SmppConstants.STATUS_INVSERTYP;
import static com.cloudhopper.smpp.SmppConstants.STATUS_INVSYSID;
import static fr.sii.ogham.core.util.ExceptionUtils.fatalJvmError;
import static fr.sii.ogham.core.util.ExceptionUtils.hasAnyCause;
import static java.util.Arrays.asList;

import java.util.function.Predicate;

import com.cloudhopper.commons.gsm.DataCoding;
import com.cloudhopper.smpp.type.SmppBindException;

import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.DataCodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.MessagePreparationException;

/**
 * 
 * @author Aurélien Baudet
 *
 */
public final class CloudhopperRetryablePredicates {

	private CloudhopperRetryablePredicates() {
		super();
	}

	/**
	 * Default predicate used to indicate if the error raised by Cloudhopper is
	 * fatal or not. It returns {@code true} if the error is not fatal (means
	 * that a new retry can be attempted). It returns {@code false} if the error
	 * is fatal and no retry must be attempted.
	 * 
	 * <p>
	 * Here is the list of cases where there should have no retries:
	 * <ul>
	 * <li>There is a fatal JVM {@link Error} (like {@link OutOfMemoryError} for
	 * example).</li>
	 * <li>A bind request has been sent to the SMSC and it has responded with an
	 * error indicating that the credentials are invalid</li>
	 * <li>A bind request has been sent to the SMSC and it has responded with an
	 * error indicating that the {@code system_type} is invalid</li>
	 * <li>A bind request has been sent to the SMSC and it has responded with an
	 * error indicating that client is already bound</li>
	 * </ul>
	 * 
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if a connect may be retried
	 */
	public static boolean canRetryConnecting(Throwable error) {
		if (fatalJvmError(error) || invalidCredentials(error) || invalidSystemType(error) || alreadyBound(error)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the error has been raised because the SMSC has sent a
	 * response to a bind request indicating that the credentials are invalid
	 * (wrong {@code system_id} or {@code password}).
	 * 
	 * <p>
	 * If the credentials are invalid, there is no point in retrying to connect.
	 * 
	 * @param error
	 *            the raised error
	 * @return true if the error is issued due to a bind failure (wrong
	 *         credentials)
	 */
	public static boolean invalidCredentials(Throwable error) {
		if (error instanceof SmppBindException) {
			return isCommandStatus((SmppBindException) error, STATUS_INVPASWD, STATUS_INVSYSID);
		}
		return false;
	}

	/**
	 * Checks whether the error has been raised because the SMSC has sent a
	 * response to a bind request indicating that the {@code system_type} field
	 * is invalid.
	 * 
	 * <p>
	 * If the {@code system_type} field is invalid, there is no point in
	 * retrying to connect.
	 * 
	 * @param error
	 *            the raised error
	 * @return true if the error is issued due to a bind failure (wrong
	 *         {@code system_type})
	 */
	public static boolean invalidSystemType(Throwable error) {
		if (error instanceof SmppBindException) {
			return isCommandStatus((SmppBindException) error, STATUS_INVSERTYP);
		}
		return false;
	}

	/**
	 * Checks whether the error has been raised because the SMSC has sent a
	 * response to a bind request indicating that the client is already bound.
	 * 
	 * <p>
	 * If the client is already bound, there is no point in retrying to connect.
	 * 
	 * @param error
	 *            the raised error
	 * @return true if the error is issued due to a bind failure (already bound)
	 */
	public static boolean alreadyBound(Throwable error) {
		if (error instanceof SmppBindException) {
			return isCommandStatus((SmppBindException) error, STATUS_ALYBND);
		}
		return false;
	}

	/**
	 * Default predicate used to indicate if the error raised by Cloudhopper is
	 * fatal or not. It returns {@code true} if the error is not fatal (means
	 * that a new retry can be attempted). It returns {@code false} if the error
	 * is fatal and no retry must be attempted.
	 * 
	 * <p>
	 * Here is the list of cases where there should have no retries:
	 * <ul>
	 * <li>There is a fatal JVM {@link Error} (like {@link OutOfMemoryError} for
	 * example).</li>
	 * <li>A bind request has been sent to the SMSC and it has responded with an
	 * error indicating that the credentials are invalid</li>
	 * <li>A bind request has been sent to the SMSC and it has responded with an
	 * error indicating that the {@code system_type} is invalid</li>
	 * <li>A bind request has been sent to the SMSC and it has responded with an
	 * error indicating that client is already bound</li>
	 * </ul>
	 * 
	 * @return the predicate
	 */
	public static Predicate<Throwable> canRetryConnecting() {
		return CloudhopperRetryablePredicates::canRetryConnecting;
	}

	/**
	 * Predicate that skip retry if one of theses condition is met:
	 * 
	 * If the error is due to a preparation error (not sending). In this case,
	 * retrying will result in the same behavior so it will fail again:
	 * <ul>
	 * <li>Data coding couldn't be determined</li>
	 * <li>Encoding couldn't be determined</li>
	 * <li>Message preparation has failed</li>
	 * </ul>
	 * 
	 * <p>
	 * In other situations, the message may be sent again.
	 * 
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if a connect may be retried
	 */
	public static boolean canResendMessage(Throwable error) {
		if (isDataCodingError(error) || isEncodingError(error) || messagePreparationFailed(error)) {
			return false;
		}
		// @formatter:off
		if (hasAnyCause(error, CloudhopperRetryablePredicates::isDataCodingError)
				|| hasAnyCause(error, CloudhopperRetryablePredicates::isEncodingError)
				|| hasAnyCause(error, CloudhopperRetryablePredicates::messagePreparationFailed)) {
			return false;
		}
		// @formatter:on
		return true;
	}

	/**
	 * Indicates if the error is due to a {@link DataCoding} detection error.
	 * 
	 * <p>
	 * In this case, retrying will lead to the same error.
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if it is a data coding error
	 */
	public static boolean isDataCodingError(Throwable error) {
		return error instanceof DataCodingException;
	}

	/**
	 * Indicates if the error is due to an encoding detection error.
	 * 
	 * <p>
	 * In this case, retrying will lead to the same error.
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if it is an encoding error
	 */
	public static boolean isEncodingError(Throwable error) {
		return error instanceof EncodingException;
	}

	/**
	 * Indicates if the error is due to an error during preparation of the
	 * message.
	 * 
	 * <p>
	 * In this case, retrying will lead to the same error.
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if it is a preparation error
	 */
	public static boolean messagePreparationFailed(Throwable error) {
		return error instanceof MessagePreparationException;
	}

	/**
	 * Predicate that skip retry if one of theses condition is met:
	 * 
	 * If the error is due to a preparation error (not sending). In this case,
	 * retrying will result in the same behavior so it will fail again:
	 * <ul>
	 * <li>Data coding couldn't be determined</li>
	 * <li>Encoding couldn't be determined</li>
	 * <li>Message preparation has failed</li>
	 * </ul>
	 * 
	 * <p>
	 * In other situations, the message may be sent again.
	 * 
	 * 
	 * @return the predicate that indicates if the message can be sent again
	 */
	public static Predicate<Throwable> canResendMessage() {
		return CloudhopperRetryablePredicates::canResendMessage;
	}

	private static boolean isCommandStatus(SmppBindException e, Integer... statuses) {
		return asList(statuses).contains(e.getBindResponse().getCommandStatus());
	}
}
