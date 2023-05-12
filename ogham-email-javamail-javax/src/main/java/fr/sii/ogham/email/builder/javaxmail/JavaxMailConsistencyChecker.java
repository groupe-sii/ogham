package fr.sii.ogham.email.builder.javaxmail;

import fr.sii.ogham.core.exception.MessagingException;

import javax.activation.DataContentHandler;
import javax.activation.MailcapCommandMap;
import javax.mail.Provider;
import javax.mail.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class JavaxMailConsistencyChecker {
    public static List<Provider> checkMailProvidersAvailable() throws JavaxMailConsistencyException {
        try {
            Provider[] providers = Session.getInstance(new Properties()).getProviders();
            return asList(providers);
        } catch(Exception | NoClassDefFoundError e) {
            throw new JavaxMailProvidersLoadFailed(e);
        }
    }

    public static List<DataContentHandler> checkDataHandlersAvailable() throws JavaxMailConsistencyException {
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
        } catch(Exception | NoClassDefFoundError e) {
            throw new JavaxDataHandlersLoadFailed(e);
        }
    }

    public static class JavaxMailConsistencyException extends MessagingException {
        public JavaxMailConsistencyException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class JavaxMailProvidersLoadFailed extends JavaxMailConsistencyException {
        public JavaxMailProvidersLoadFailed(Throwable cause) {
            super("javax.mail.Provider implementations can't be loaded. Cause: "+cause.getMessage(), cause);
        }
    }

    public static class JavaxDataHandlersLoadFailed extends JavaxMailConsistencyException {
        public JavaxDataHandlersLoadFailed(Throwable cause) {
            super("javax.activation.DataHandler implementations can't be loaded. Cause: "+cause.getMessage(), cause);
        }
    }
}
