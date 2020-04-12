package fr.sii.ogham.core.builder.configurer;

import static fr.sii.ogham.core.util.ExceptionUtils.fatalJvmError;
import static fr.sii.ogham.core.util.ExceptionUtils.hasAnyCause;

import java.util.function.Predicate;

import fr.sii.ogham.core.exception.InvalidMessageException;
import fr.sii.ogham.core.exception.filler.FillMessageException;
import fr.sii.ogham.core.exception.handler.NoContentException;
import fr.sii.ogham.core.exception.handler.TemplateNotFoundException;
import fr.sii.ogham.core.exception.handler.TemplateParsingFailedException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.exception.template.BeanException;
import fr.sii.ogham.core.exception.template.ContextException;
import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.util.ExceptionUtils;

/**
 * Predicate that skip retry if one of theses condition is met:
 * 
 * <ul>
 * <li>The error is a JVM error that should not be ignored</li>
 * <li>If the error is due to a preparation error (not sending). In this case,
 * retrying will result in the same behavior so it will fail again:
 * <ul>
 * <li>It is a parsing error</li>
 * <li>The message in not valid</li>
 * <li>A resource associated to the message can't be resolved</li>
 * <li>The mimetype of a resource couldn't be determined</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * <p>
 * In other situations, the message may be sent again.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public final class SendMessageRetryablePredicates {

	private SendMessageRetryablePredicates() {
		super();
	}

	/**
	 * Predicate that skip retry if one of theses condition is met:
	 * 
	 * <ul>
	 * <li>The error is a JVM error that should not be ignored</li>
	 * <li>If the error is due to a preparation error (not sending). In this
	 * case, retrying will result in the same behavior so it will fail again:
	 * <ul>
	 * <li>It is a parsing error</li>
	 * <li>The message in not valid</li>
	 * <li>A resource associated to the message can't be resolved</li>
	 * <li>The mimetype of a resource couldn't be determined</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * <p>
	 * In other situations, the message may be sent again.
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if the message can be sent again
	 */
	@SuppressWarnings("squid:S1126")
	public static boolean canResendMessage(Throwable error) {
		if (fatalJvmError(error) || isMessagePreparationError(error)) {
			return false;
		}
		if (hasAnyCause(error, ExceptionUtils::fatalJvmError) || hasAnyCause(error, SendMessageRetryablePredicates::isMessagePreparationError)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks is raised during preparation of the message.
	 * 
	 * <p>
	 * The error may be due to:
	 * <ul>
	 * <li>Either a parsing error</li>
	 * <li>Or the message in not valid</li>
	 * <li>Or a resource can't be resolved</li>
	 * <li>Or the mimetype of a resource couldn't be determined</li>
	 * </ul>
	 * 
	 * <p>
	 * In these cases, retrying will always give the same result so there is no
	 * point in retrying.
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if the error is raised during preparation of the message
	 */
	public static boolean isMessagePreparationError(Throwable error) {
		// @formatter:off
		return parsingFailed(error) 
				|| messageIsInvalid(error) 
				|| resourceIsUnresolved(error) 
				|| mimetypeIsUndetectable(error)
				|| developerError(error);
		// @formatter:on
	}

	/**
	 * Indicates if the error is raised during template parsing.
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if it is a parsing error
	 */
	public static boolean parsingFailed(Throwable error) {
		// @formatter:off
		return error instanceof ParseException 
				|| error instanceof BeanException 
				|| error instanceof EngineDetectionException 
				|| error instanceof ContextException
				|| error instanceof TemplateParsingFailedException 
				|| error instanceof TemplateNotFoundException
				|| error instanceof NoContentException;
		// @formatter:on
	}

	/**
	 * Indicates if the error is raised because the message is invalid.
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if the message is invalid
	 */
	public static boolean messageIsInvalid(Throwable error) {
		// @formatter:off
		return error instanceof InvalidMessageException 
				|| error instanceof FillMessageException;
		// @formatter:on
	}

	/**
	 * Indicates if the error is raised because a resource can't be resolved.
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if a resource can't be resolved
	 */
	public static boolean resourceIsUnresolved(Throwable error) {
		return error instanceof ResourceResolutionException;
	}

	/**
	 * Indicates if the error is raised because a resource mimetype can't be
	 * determined.
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if a resource mimetype can't be determined
	 */
	public static boolean mimetypeIsUndetectable(Throwable error) {
		return error instanceof MimeTypeDetectionException;
	}

	/**
	 * Indicates if the error is raised because the developer has misconfigured
	 * or has used an illegal value ( {@link IllegalArgumentException},
	 * {@link NullPointerException}, ...).
	 * 
	 * @param error
	 *            the error to analyze
	 * @return true if developer has used an illegal value
	 */
	public static boolean developerError(Throwable error) {
		// @formatter:off
		return error instanceof IllegalArgumentException
				|| error instanceof NullPointerException;
		// @formatter:on
	}

	/**
	 * Predicate that skip retry if one of theses condition is met:
	 * 
	 * <ul>
	 * <li>The error is a JVM error that should not be ignored</li>
	 * <li>If the error is due to a preparation error (not sending). In this
	 * case, retrying will result in the same behavior so it will fail again:
	 * <ul>
	 * <li>It is a parsing error</li>
	 * <li>The message in not valid</li>
	 * <li>A resource associated to the message can't be resolved</li>
	 * <li>The mimetype of a resource couldn't be determined</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * <p>
	 * In other situations, the message may be sent again.
	 * 
	 * @return the predicate
	 */
	public static Predicate<Throwable> canResendMessage() {
		return SendMessageRetryablePredicates::canResendMessage;
	}
}