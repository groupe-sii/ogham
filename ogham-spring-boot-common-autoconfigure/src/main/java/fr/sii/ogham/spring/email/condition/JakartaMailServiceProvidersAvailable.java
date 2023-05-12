package fr.sii.ogham.spring.email.condition;

import jakarta.mail.Provider;
import jakarta.mail.Session;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.springframework.boot.autoconfigure.condition.ConditionMessage.Style.QUOTE;
import static org.springframework.boot.autoconfigure.condition.ConditionMessage.forCondition;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.match;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.noMatch;

public class JakartaMailServiceProvidersAvailable extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            List<Provider> providers = checkMailProvidersAvailable();
            return match(forCondition("jakarta.mail.Provider implementations available ?")
                    .found("")
                    .items(QUOTE, providers.stream()
                            .filter(Objects::nonNull)
                            .map(Provider::getClassName)
                            .toArray()));
        } catch(Exception | NoClassDefFoundError e) {
            return noMatch(forCondition("jakarta.mail.Provider implementations available ?")
                    .because(e.getMessage()));
        }
    }

    // can't reuse JavaMailConsistencyChecker since it may not be present at runtime
    public static List<Provider> checkMailProvidersAvailable() throws JavaMailProvidersLoadFailed {
        try {
            Provider[] providers = Session.getInstance(new Properties()).getProviders();
            return asList(providers);
        } catch(Exception | NoClassDefFoundError e) {
            throw new JavaMailProvidersLoadFailed(e);
        }
    }

    public static class JavaMailProvidersLoadFailed extends Exception {
        public JavaMailProvidersLoadFailed(Throwable cause) {
            super("jakarta.mail.Provider implementations can't be loaded. Cause: "+cause.getMessage(), cause);
        }
    }
}
