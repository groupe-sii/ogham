package fr.sii.ogham.email.builder.javamail;

import fr.sii.ogham.core.exception.MessagingException;

import jakarta.activation.DataContentHandler;
import jakarta.activation.MailcapCommandMap;
import jakarta.mail.Provider;
import jakarta.mail.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class JavaMailConsistencyChecker {
    public static List<Provider> checkMailProvidersAvailable() throws JavaMailConsistencyException {
        try {
            Provider[] providers = Session.getInstance(new Properties()).getProviders();
            return asList(providers);
        } catch(Exception e) {
            throw new JavaMailProvidersLoadFailed(e);
        }
    }

    public static List<DataContentHandler> checkDataHandlersAvailable() throws JavaMailConsistencyException {
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

    public static class JavaMailConsistencyException extends MessagingException {
        public JavaMailConsistencyException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class JavaMailProvidersLoadFailed extends JavaMailConsistencyException {
        public JavaMailProvidersLoadFailed(Throwable cause) {
            super("jakarta.mail.Provider implementations can't be loaded. Cause: "+cause.getMessage(), cause);
        }
    }

    public static class JavaDataHandlersLoadFailed extends JavaMailConsistencyException {
        public JavaDataHandlersLoadFailed(Throwable cause) {
            super("jakarta.activation.DataHandler implementations can't be loaded. Cause: "+cause.getMessage(), cause);
        }
    }
}
