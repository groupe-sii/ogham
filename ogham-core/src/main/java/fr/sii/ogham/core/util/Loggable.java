package fr.sii.ogham.core.util;

/**
 * Messages may carry a lot of information so logging may result in too long
 * string and logging contains too much information.
 * 
 * <p>
 * The default {@link Object#toString()} method should hide by default some
 * information that is not really needed in logs.
 * 
 * <p>
 * However, sometimes it is necessary to be able to log whole message for
 * debugging purpose. This interface provides another method used to log the
 * whole message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Loggable {
	/**
	 * Generates a string that contains the whole information.
	 * 
	 * @return the string with all information
	 */
	String toLogString();
}
