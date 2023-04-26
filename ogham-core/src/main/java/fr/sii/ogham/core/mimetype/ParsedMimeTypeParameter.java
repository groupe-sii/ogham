package fr.sii.ogham.core.mimetype;

import java.util.Objects;

public class ParsedMimeTypeParameter implements MimeType.MimeTypeParameter {
    private final String rawParameter;
    private final String name;
    private final String value;

    public ParsedMimeTypeParameter(String rawParameter, String name, String value) {
        this.rawParameter = rawParameter;
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParsedMimeTypeParameter)) return false;
        ParsedMimeTypeParameter that = (ParsedMimeTypeParameter) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return rawParameter;
    }
}
