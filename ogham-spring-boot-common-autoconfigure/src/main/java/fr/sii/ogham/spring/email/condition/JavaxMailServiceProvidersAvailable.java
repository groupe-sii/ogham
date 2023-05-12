package fr.sii.ogham.spring.email.condition;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.mail.Provider;
import javax.mail.Session;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.springframework.boot.autoconfigure.condition.ConditionMessage.Style.QUOTE;
import static org.springframework.boot.autoconfigure.condition.ConditionMessage.forCondition;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.match;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.noMatch;

public class JavaxMailServiceProvidersAvailable extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            List<Provider> providers = checkMailProvidersAvailable();
            return match(forCondition("javax.mail.Provider implementations available ?")
                    .found("")
                    .items(QUOTE, providers.stream()
                            .filter(Objects::nonNull)
                            .map(Provider::getClassName)
                            .toArray()));
        } catch(Exception | NoClassDefFoundError e) {
            return noMatch(forCondition("javax.mail.Provider implementations available ?")
                    .because(e.getMessage()));
        }
    }

    // can't reuse JavaxMailConsistencyChecker since it may not be present at runtime
    private static List<Provider> checkMailProvidersAvailable() throws JavaxMailProvidersLoadFailed {
        try {
            Provider[] providers = Session.getInstance(new Properties()).getProviders();
            return asList(providers);
        } catch(Exception | NoClassDefFoundError e) {
            throw new JavaxMailProvidersLoadFailed(e);
        }
    }


    public static class JavaxMailProvidersLoadFailed extends Exception {
        public JavaxMailProvidersLoadFailed(Throwable cause) {
            super("javax.mail.Provider implementations can't be loaded. Cause: "+cause.getMessage(), cause);
        }
    }

}
