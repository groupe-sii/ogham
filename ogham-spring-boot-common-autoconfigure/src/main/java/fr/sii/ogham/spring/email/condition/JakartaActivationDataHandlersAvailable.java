package fr.sii.ogham.spring.email.condition;

import jakarta.activation.DataContentHandler;
import jakarta.activation.MailcapCommandMap;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.boot.autoconfigure.condition.ConditionMessage.Style.QUOTE;
import static org.springframework.boot.autoconfigure.condition.ConditionMessage.forCondition;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.match;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.noMatch;

public class JakartaActivationDataHandlersAvailable extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            List<DataContentHandler> handlers = checkDataHandlersAvailable();
            return match(forCondition("jakarta.activation.DataHandler implementations available ?")
                    .found("")
                    .items(QUOTE, handlers.stream()
                            .filter(Objects::nonNull)
                            .map(DataContentHandler::getClass)
                            .map(Class::getName)
                            .toArray()));
        } catch(Exception e) {
            return noMatch(forCondition("jakarta.activation.DataHandler implementations available ?")
                    .because(e.getMessage()));
        }
    }

    // can't reuse JavaMailConsistencyChecker since it may not be present at runtime
    private static List<DataContentHandler> checkDataHandlersAvailable() throws JavaDataHandlersLoadFailed {
        try {
            MailcapCommandMap commandMap = new MailcapCommandMap();
            List<DataContentHandler> found = new ArrayList<>();
            for (String mimetype : commandMap.getMimeTypes()) {
                DataContentHandler dataContentHandler = commandMap.createDataContentHandler(mimetype);
                if (dataContentHandler != null) {
                    found.add(dataContentHandler);
                }
            }
            return found;
        } catch(Exception e) {
            throw new JavaDataHandlersLoadFailed(e);
        }
    }


    public static class JavaDataHandlersLoadFailed extends Exception {
        public JavaDataHandlersLoadFailed(Throwable cause) {
            super("jakarta.activation.DataHandler implementations can't be loaded. Cause: "+cause.getMessage(), cause);
        }
    }
}
