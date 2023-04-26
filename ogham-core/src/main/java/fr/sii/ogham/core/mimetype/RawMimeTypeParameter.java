package fr.sii.ogham.core.mimetype;

import java.util.Objects;

public class RawMimeTypeParameter implements MimeType.MimeTypeParameter {
    private final String rawParameter;

    public RawMimeTypeParameter(String rawParameter) {
        this.rawParameter = rawParameter;
    }

    @Override
    public String getName() {
        return rawParameter.substring(0, rawParameter.indexOf("=")).trim();
    }

    @Override
    public String getValue() {
        return rawParameter.substring(rawParameter.indexOf("=") + 1).trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RawMimeTypeParameter)) return false;
        RawMimeTypeParameter that = (RawMimeTypeParameter) o;
        return Objects.equals(rawParameter, that.rawParameter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawParameter);
    }

    @Override
    public String toString() {
        return rawParameter;
    }
}
