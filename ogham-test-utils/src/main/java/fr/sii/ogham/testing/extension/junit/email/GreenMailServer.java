package fr.sii.ogham.testing.extension.junit.email;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Register JUnit 5 extension to start a {@link ogham.testing.com.icegreen.greenmail.util.GreenMail}
 * server on a random port.
 * <p>
 * The server is (re)started before running each test.
 * <p>
 * You can get the port of the server by using a parameter of {@link ogham.testing.com.icegreen.greenmail.util.GreenMail}
 * type on your test method. Example:
 * <pre>
 * <code>
 *
 *  &#64;BeforeEach
 *  public void setup(GreenMail greenMail) {
 *     builder = MessagingBuilder.empty();
 *     builder
 *       .environment()
 *         .properties()
 *           .set("mail.smtp.host", greenMail.getSmtp().getBindTo())
 *           .set("mail.smtp.port", greenMail.getSmtp().getPort())
 *         ...
 *  }
 * </code>
 * </pre>
 *
 * @author Aurélien Baudet
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, TYPE})
@ExtendWith(RandomPortGreenMailExtension.class)
public @interface GreenMailServer {

}
