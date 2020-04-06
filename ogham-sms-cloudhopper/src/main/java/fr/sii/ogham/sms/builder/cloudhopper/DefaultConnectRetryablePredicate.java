package fr.sii.ogham.sms.builder.cloudhopper;

import static com.cloudhopper.smpp.SmppConstants.STATUS_ALYBND;
import static com.cloudhopper.smpp.SmppConstants.STATUS_INVPASWD;
import static com.cloudhopper.smpp.SmppConstants.STATUS_INVSERTYP;
import static com.cloudhopper.smpp.SmppConstants.STATUS_INVSYSID;
import static java.util.Arrays.asList;

import java.util.function.Predicate;

import com.cloudhopper.smpp.type.SmppBindException;

/**
 * Default predicate used to indicate if the error raised by Cloudhopper is
 * fatal or not. It returns {@code true} if the error is not fatal (means that a
 * new retry can be attempted). It returns {@code false} if the error is fatal
 * and no retry must be attempted.
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
 * @author Aurélien Baudet
 *
 */
public class DefaultConnectRetryablePredicate implements Predicate<Throwable> {

	@Override
	public boolean test(Throwable error) {
		if (fatalJvmError(error) || invalidCredentials(error) || invalidSystemType(error) || alreadyBound(error)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the error has been raised due to a Java {@link Error}.
	 * {@link Error}s should not be ignored. For example, if there is a
	 * {@link OutOfMemoryError}, retrying may result in consuming more memory
	 * and totally crash the JVM or hang the system.
	 * 
	 * @param error
	 *            the raised error
	 * @return true if the error is fatal JVM error
	 */
	public boolean fatalJvmError(Throwable error) {
		return error instanceof Error;
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
	public boolean invalidCredentials(Throwable error) {
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
	public boolean invalidSystemType(Throwable error) {
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
	public boolean alreadyBound(Throwable error) {
		if (error instanceof SmppBindException) {
			return isCommandStatus((SmppBindException) error, STATUS_ALYBND);
		}
		return false;
	}

	private static boolean isCommandStatus(SmppBindException e, Integer... statuses) {
		return asList(statuses).contains(e.getBindResponse().getCommandStatus());
	}

}
