package fr.sii.ogham.spring.email;

import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.email.sender.impl.JavaxMailSender;
import fr.sii.ogham.spring.email.condition.JavaMailClasspathConsistencyCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * There can be a mess in the classpath due to Spring Boot dependency management
 * that forces versions:
 * <ul>
 *     <li>Ogham uses the dependency {@code org.eclipse.angus:angus-mail:2.0.1+}, which imports:
 *     <ul>
 *         <li>{@code jakarta.mail:jakarta.mail-api:2.1.0+}</li>
 *         <li>{@code jakarta.activation:jakarta.activation-api:2.1.1+}</li>
 *         <li>{@code org.eclipse.angus:angus-activation:2.0.0+}</li>
 *     </ul>
 *     </li>
 *     <li>Spring Boot 2 dependency management has these rules:
 *     <ul>
 *         <li>{@code jakarta.mail:jakarta.mail-api:1.6.7-}</li>
 *         <li>{@code jakarta.activation:jakarta.activation-api:1.2.2-}</li>
 *     </ul>
 *     </li>
 * </ul>
 * <p>
 * But, there is a mess in the packages. The dependency {@code jakarta.mail:jakarta.mail-api:1.6.7-}
 * declares classes in package {@code javax.mail} while {@code jakarta.mail:jakarta.mail-api:1.6.7+}
 * declares classes in package {@code jakarta.mail}.
 * <p>
 * So, when using Ogham with Spring Boot 2, we try:
 * <ul>
 *     <li>either to use Jakarta mail using {@code org.eclipse.angus:angus-mail:2.0.1+} but it fails
 *     because it doesn't find {@code jakarta.activation.DataHandler} (since in the classpath the
 *     {@code jakarta.activation:jakarta.activation-api} is in version {@code 1.2.2-} which defines
 *     {@code javax.activation.DataHandler}.
 *     </li>
 *     <li>or to use Javax mail using {@code jakarta.mail:jakarta.mail-api:1.6.7-} but it may fail
 *     because Ogham doesn't bring Javax mail implementation dependencies to avoid classpath
 *     mess and issues at runtime. So when trying to send en email using Javax mail, there is no implementation
 *     available. But it is even worse than no implementation available. In the sources of
 *     {@code jakarta.mail:jakarta.mail-api:1.6.7-}, there is a direct link to the Sun implementation
 *     which may not be present in the classpath (at least {@code com.sun.mail.util.MailLogger}).
 *     So, instead of indicating that there is no Javax mail implementation present, it
 *     fails with {@code java.lang.NoClassDefFoundError: com/sun/mail/util/MailLogger} which
 *     is not really developer friendly and hard to understand why it happens.
 *     </li>
 * </ul>
 * <p>
 * Therefore, this class is used to help the developer to understand what to do to fix this.
 */
@Configuration
// Only check classpath consistency if ogham-email-javamail-jakarta and/or ogham-email-javamail-javax are present.
// If none is present, we don't care about classpath inconsistencies since we won't send mail with Ogham Java mail.
@Conditional({
        OghamJavaMailClasspathConsistencyCheckConfiguration.JakartaOrJavaxSenderClassPresent.class,
        JavaMailClasspathConsistencyCondition.class
})
@ConditionalOnProperty(value = "ogham.email.javamail.check-classpath-consistency", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore({OghamJavaMailConfiguration.class, OghamJavaxMailConfiguration.class})
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
public class OghamJavaMailClasspathConsistencyCheckConfiguration {
    private static Logger LOG = LoggerFactory.getLogger(OghamJavaMailClasspathConsistencyCheckConfiguration.class);

    @PostConstruct
    public void explainPossibleIssueIfUsingJavaMail() {
        try {
            LOG.warn("\n{}", IOUtils.toString(getClass().getResourceAsStream("/javamail-classpath-clash-explanation.adoc"), UTF_8));
        } catch (IOException e) {
            LOG.warn("/!\\ You may experience classpath issues while sending emails with Ogham using Java Mail and Spring Boot >= 2.2.x < 3.0.x.\n" +
                    "\n" +
                    "Failed to load explanations. Please visit Ogham website for more information.");
        }
    }


    public static class JakartaOrJavaxSenderClassPresent extends AnyNestedCondition {

        public JakartaOrJavaxSenderClassPresent() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnClass({JavaMailSender.class})
        static class JavaMailSenderPresent {
        }

        @ConditionalOnClass({JavaxMailSender.class})
        static class JavaxMailSenderPresent {
        }
    }
}
