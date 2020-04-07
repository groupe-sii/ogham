package fr.sii.ogham.core.retry;

/**
 * This interface is intended to be used when the action doesn't return any
 * value. {@link Runnable} can't be used in case the action may throw a checked
 * exception.
 * 
 * @author Aurélien Baudet
 */
public interface Executable {
	/**
	 * Execute some action. The action may throw an exception.
	 * 
	 * @throws Exception
	 *             if unable to execute the action
	 */
	@SuppressWarnings("squid:S00112")
	void execute() throws Exception;
}
