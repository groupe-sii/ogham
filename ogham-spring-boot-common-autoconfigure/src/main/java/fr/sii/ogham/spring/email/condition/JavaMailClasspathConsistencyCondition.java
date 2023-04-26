package fr.sii.ogham.spring.email.condition;

import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.boot.autoconfigure.condition.ConditionMessage.of;

public class JavaMailClasspathConsistencyCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // TODO: only if configuration properties are set ?
        ConditionOutcome javaxOutcome = checkJavaxMailConsistency(context, metadata);
        ConditionOutcome jakartaOutcome = checkJakartaMailConsistency(context, metadata);
        return new ConditionOutcome(
                !isClasspathConsistent(javaxOutcome, jakartaOutcome),
                of(asList(javaxOutcome.getConditionMessage(), jakartaOutcome.getConditionMessage())));
    }

    private static boolean isClasspathConsistent(ConditionOutcome javaxOutcome, ConditionOutcome jakartaOutcome) {
        return javaxOutcome.isMatch() || jakartaOutcome.isMatch();
    }


    private static ConditionOutcome checkJavaxMailConsistency(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionInfoMerger merger = new ConditionInfoMerger();
        merger.addCondition(isJavaxMailApiPresent(context),
                "javax.mail package is present in the classpath",
                "javax.mail is not present in the classpath => ignoring");
        if (isJavaxMailApiPresent(context)) {
            // possible version downgrade with Sprint Boot <= 2
            // => possible clash with Ogham
            // => check classpath
            merger.addCondition(isJavaxActivationApiPresent(context),
                    "javax.activation.DataHandler is present in the classpath",
                    "javax.activation.DataHandler is not present in the classpath (dependency jakarta.activation:jakarta.activation-api <= 1.2.2 may not be present in the classpath)");
            merger.addCondition(isSunMailImplementationPresent(context),
                    "com.sun.mail package is present in the classpath",
                    "com.sun.mail package is not present in the classpath (dependency com.sun.mail:javax.mail <= 1.6.2 or com.sun.mail:jakarta.mail <= 1.6.7 may not be present in the classpath)");
            merger.addOutcome(new JavaxMailServiceProvidersAvailable().getMatchOutcome(context, metadata));
            merger.addOutcome(new JavaxActivationDataHandlersAvailable().getMatchOutcome(context, metadata));
        }
        return merger.get();
    }


    private static boolean isJavaxMailApiPresent(ConditionContext context) {
        return exists(context, "javax.mail.internet.MimeMessage");
    }

    private static boolean isJavaxActivationApiPresent(ConditionContext context) {
        return exists(context, "javax.activation.MimeType")
                && exists(context, "javax.activation.DataHandler");
    }

    private static boolean isSunMailImplementationPresent(ConditionContext context) {
        return exists(context, "com.sun.mail.util.MailLogger");
    }

    private static ConditionOutcome checkJakartaMailConsistency(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionInfoMerger merger = new ConditionInfoMerger();
        merger.addCondition(isAngusMailImplementationPresent(context),
                "org.eclipse.angus:angus-mail is present in the classpath",
                "org.eclipse.angus:angus-mail is not present in the classpath => ignoring");
        if (isAngusMailImplementationPresent(context)) {
            // possible version downgrade with Sprint Boot <= 2
            // => possible clash with Ogham
            // => check classpath
            merger.addCondition(isJakartaMailApiPresent(context),
                    "jakarta.mail package is present in the classpath",
                    "jakarta.mail package is not present in the classpath (dependency jakarta.mail:jakarta.mail-api >= 2.0.0 is not present in the classpath)");
            merger.addCondition(isJakartaActivationApiPresent(context),
                    "jakarta.activation.DataHandler is present in the classpath",
                    "jakarta.activation.DataHandler is not present in the classpath (dependency jakarta.activation:jakarta.activation-api >= 2.0.0 is not present in the classpath)");
            merger.addOutcome(new JakartaMailServiceProvidersAvailable().getMatchOutcome(context, metadata));
            merger.addOutcome(new JakartaActivationDataHandlersAvailable().getMatchOutcome(context, metadata));
        }
        return merger.get();
    }

    private static boolean isAngusMailImplementationPresent(ConditionContext context) {
        return exists(context, "org.eclipse.angus.mail.imap.IMAPProvider");
    }

    private static boolean isJakartaMailApiPresent(ConditionContext context) {
        return exists(context, "jakarta.mail.internet.MimeMessage");
    }

    private static boolean isJakartaActivationApiPresent(ConditionContext context) {
        return exists(context, "jakarta.activation.MimeType")
                && exists(context, "jakarta.activation.DataHandler");
    }

    private static boolean exists(ConditionContext context, String className) {
        try {
            Class.forName(className, false, context.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (NoClassDefFoundError e) {
            return !isForSameClass(className, e);
        }
    }

    private static boolean isForSameClass(String className, NoClassDefFoundError e) {
        return className.equals(e.getMessage().replace("/", "."));
    }


    private static class ConditionInfoMerger {
        private final List<ConditionOutcome> outcomes = new ArrayList<>();

        public void addCondition(boolean condition, String messageIfTrue, String messageIfFalse) {
            addOutcome(new ConditionOutcome(condition, condition ? messageIfTrue : messageIfFalse));
        }

        public void addOutcome(ConditionOutcome outcome) {
            outcomes.add(outcome);
        }

        public ConditionOutcome get() {
            boolean matchAll = outcomes.stream().allMatch(ConditionOutcome::isMatch);
            List<ConditionMessage> messages = outcomes.stream().map(ConditionOutcome::getConditionMessage).collect(toList());
            return new ConditionOutcome(matchAll, ConditionMessage.of(messages));
        }
    }
}
