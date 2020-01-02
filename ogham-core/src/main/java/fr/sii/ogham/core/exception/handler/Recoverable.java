package fr.sii.ogham.core.exception.handler;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fr.sii.ogham.core.translator.content.MultiContentTranslator;

/**
 * Just a marker to indicate that the exception may be ignored.
 * 
 * <p>
 * This is used for example by {@link MultiContentTranslator} to when an
 * exception is thrown for one content but it can be ignored because other
 * contents may be used instead.
 * 
 * @author Aurélien Baudet
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Inherited
public @interface Recoverable {

}
