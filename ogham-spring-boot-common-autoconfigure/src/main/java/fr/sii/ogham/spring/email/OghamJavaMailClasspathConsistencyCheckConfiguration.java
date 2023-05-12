package fr.sii.ogham.spring.email;

import fr.sii.ogham.email.builder.javamail.JavaMailConsistencyChecker;
import fr.sii.ogham.email.builder.javaxmail.JavaxMailConsistencyChecker;
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
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

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
 *     <li>Spring Boot <= 2 dependency management has these rules:
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
 * So, when using Ogham with Spring Boot <= 2, we try:
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

    private final Environment env;

    public OghamJavaMailClasspathConsistencyCheckConfiguration(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void explainPossibleIssueIfUsingJavaMail() {
        LOG.warn("\n" +
                "/!\\ You may experience classpath issues while sending emails with Ogham using Java Mail and Spring Boot >= 2.2.x < 3.0.x.\n" +
                "Explanation:\n" +
                "------------\n" +
                "\n" +
                " There can be a mess in the classpath due to Spring Boot dependency management\n" +
                " that forces versions:\n" +
                " - Ogham uses the dependency 'org.eclipse.angus:angus-mail >= 2.0.1', which imports:\n" +
                "   - 'jakarta.mail:jakarta.mail-api >= 2.1.0'\n" +
                "   - 'jakarta.activation:jakarta.activation-api >= 2.1.1'\n" +
                "   - 'org.eclipse.angus:angus-activation >= 2.0.0'\n" +
                " - Spring Boot <= 2 dependency management forces these versions:\n" +
                "   - 'jakarta.mail:jakarta.mail-api <= 1.6.7'\n" +
                "   - 'jakarta.activation:jakarta.activation-api <= 1.2.2'\n" +
                "\n" +
                " But, there is a mess in the packages. The dependency 'jakarta.mail:jakarta.mail-api' <= 1.6.7\n" +
                " declares classes in package 'javax.mail' while 'jakarta.mail:jakarta.mail-api' > 1.6.7\n" +
                " declares classes in package 'jakarta.mail'.\n" +
                "\n" +
                " So, when using Ogham with Spring Boot <= 2, Ogham tries to adapt to what is present in the classpath:\n" +
                " - either Ogham tries to use Jakarta mail using 'org.eclipse.angus:angus-mail' >= 2.0.1 but it fails\n" +
                "   because it doesn't find 'jakarta.activation.DataHandler' (since in the classpath the\n" +
                "   'jakarta.activation:jakarta.activation-api' version is <= 1.2.2 which defines\n" +
                "   'javax.activation.DataHandler' instead).\n" +
                " - or Ogham tries to use Javax mail using 'jakarta.mail:jakarta.mail-api' <= 1.6.7 but it may fail\n" +
                "   because Ogham doesn't bring Javax mail implementation dependencies to avoid classpath\n" +
                "   mess and issues at runtime. So when trying to send en email using Javax mail, there is no implementation\n" +
                "   available. But it is even worse than no implementation available. In the sources of\n" +
                "   'jakarta.mail:jakarta.mail-api' <= 1.6.7, there is a direct link to the Sun implementation\n" +
                "   (at least 'com.sun.mail.util.MailLogger') which may not be present in the classpath.\n" +
                "   So, instead of indicating that there is no Javax mail implementation present, it\n" +
                "   fails with 'java.lang.NoClassDefFoundError: com/sun/mail/util/MailLogger' which\n" +
                "   is not really developer friendly and hard to understand why it happens.\n" +
                "\n" +
                "Solutions:\n" +
                "----------\n" +
                "\n" +
                " - If you prefer using Javax mail in order to avoid possible classpath clashes by mixing both javax and jakarta, you can:\n" +
                "   - either add 'org.springframework.boot:spring-boot-starter-mail' dependency  BEFORE 'fr.sii.ogham:ogham-email-javamail' dependency.\n" +
                "   - or add explicitly the dependency 'com.sun.mail:jakarta.mail' BEFORE 'fr.sii.ogham:ogham-email-javamail' dependency\n" +
                "\n" +
                "   /!\\ Order is important in order to let Java pick the service configuration of the 'com.sun.mail:jakarta-mail' dependency first.\n" +
                "       You can also exclude 'org.eclipse.angus:angus-mail' from 'fr.sii.ogham:ogham-email-javamail' (so that order doesn't matter).\n" +
                " - If you prefer using the new Jakarta mail, you can:\n" +
                "   - force Spring Boot dependency management to use newer versions by setting properties:\n" +
                "     '<jakarta-mail.version>2.1.1</jakarta-mail.version>'\n" +
                "     '<jakarta-activation.version>2.1.1</jakarta-activation.version>'\n" +
                "     '<sun-mail.version>2.0.1</sun-mail.version>'\n" +
                "     '<sun-activation.version>2.0.1</sun-activation.version>'\n" +
                "\n" +
                "     Spring Boot dependency management uses the same property '${jakarta-activation.version}'\n" +
                "     for both 'jakarta.activation:jakarta.activation-api' and 'com.sun.activation:jakarta.activation'.\n" +
                "     So you also have to override dependency management by adding:\n" +
                "       <dependencyManagement>\n" +
                "         <dependencies>\n" +
                "           <dependency>\n" +
                "             <groupId>com.sun.activation</groupId>\n" +
                "             <artifactId>jakarta.activation</artifactId>\n" +
                "             <version>${sun-activation.version}</version>\n" +
                "           </dependency>\n" +
                "         </dependencies>\n" +
                "       </dependencyManagement>\n" +
                "     Therefore, the property '${sun-activation.version}' has effect.\n" +
                "   - explicitly add dependencies to override Spring Boot inherited dependency management:\n" +
                "     - 'jakarta.activation:jakarta.mail-api:2.1.1'\n" +
                "     - 'jakarta.activation:jakarta.activation-api:2.1.1'\n" +
                "\n" +
                "   /!\\ Be careful yet, other Spring Boot modules may require javax.activation packages.\n" +
                "\n" +
                "\n" +
                "(?) You can disable this message by setting the property 'ogham.email.javamail.check-classpath-consistency=false'\n");
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
