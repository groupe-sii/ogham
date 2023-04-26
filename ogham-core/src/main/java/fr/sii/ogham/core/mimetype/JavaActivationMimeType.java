package fr.sii.ogham.core.mimetype;

import fr.sii.ogham.core.exception.mimetype.MimeTypeParseException;
import jakarta.activation.MimeTypeParameterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * Wraps Jakarta Activation MimeType.
 */
public class JavaActivationMimeType implements MimeType {
    private static final Logger LOG = LoggerFactory.getLogger(JavaActivationMimeType.class);

    private final jakarta.activation.MimeType delegate;

    public JavaActivationMimeType(jakarta.activation.MimeType delegate) {
        this.delegate = delegate;
    }

    public static JavaActivationMimeType fromString(String mimetype) throws MimeTypeParseException {
        try {
            return new JavaActivationMimeType(new jakarta.activation.MimeType(mimetype));
        } catch (jakarta.activation.MimeTypeParseException e) {
            throw new MimeTypeParseException(e, mimetype);
        }
    }

    @Override
    public String getPrimaryType() {
        return delegate.getPrimaryType();
    }

    @Override
    public String getSubType() {
        return delegate.getSubType();
    }

    @Override
    public List<MimeTypeParameter> getParameters() {
        List<MimeTypeParameter> params = new ArrayList<>();
        Enumeration<String> names = delegate.getParameters().getNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.add(new JavaActivationMimeTypeParameter(delegate.getParameters(), name));
        }
        return params;
    }

    @Override
    public String getParameter(String name) {
        return delegate.getParameter(name);
    }

    @Override
    public String getBaseType() {
        return delegate.getBaseType();
    }

    @Override
    public boolean matches(MimeType other) {
        if (other instanceof JavaActivationMimeType) {
            return delegate.match(((JavaActivationMimeType) other).delegate);
        }
        try {
            return delegate.match(other.toString());
        } catch (jakarta.activation.MimeTypeParseException e) {
            LOG.warn("Can't compare mimetypes since given mimetype can't be parsed. " +
                    "This mimetype is valid because it could be parsed. The other one" +
                    "is not so they can't match.", e);
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavaActivationMimeType)) return false;
        JavaActivationMimeType that = (JavaActivationMimeType) o;
        return Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    private static class JavaActivationMimeTypeParameter implements MimeTypeParameter {
        private final MimeTypeParameterList parameters;
        private final String name;

        private JavaActivationMimeTypeParameter(MimeTypeParameterList parameters, String name) {
            this.parameters = parameters;
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return parameters.get(name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof JavaActivationMimeTypeParameter)) return false;
            JavaActivationMimeTypeParameter that = (JavaActivationMimeTypeParameter) o;
            return Objects.equals(getName(), that.getName()) && Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getValue());
        }

        @Override
        public String toString() {
            return getName() + "=" + MimeTypeParser.quoteParameterValue(getValue());
        }
    }
}
