package fr.sii.ogham.core.mimetype;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Basic representation of a mimetype.
 * There is no parsing involved, so we trust the user to provide a valid
 * mimetype.
 * <p>
 * The parts are extracted without any control and may return invalid
 * values if original mimetype is not valid.
 * <p>
 * It can only work with simple mimetypes and doesn't totally honor RFCs.
 */
public class RawMimeType implements MimeType {
    private final String mimeType;

    public RawMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String getPrimaryType() {
        return mimeType.substring(0, mimeType.indexOf("/"));
    }

    @Override
    public String getSubType() {
        return mimeType.substring(mimeType.indexOf("/") + 1)
                .replaceAll(";.+$", "");
    }

    @Override
    public List<MimeTypeParameter> getParameters() {
        int idx = mimeType.indexOf(";");
        if (idx <= 0) {
            return emptyList();
        }
        String parameters = mimeType.substring(idx + 1);
        return Arrays.stream(parameters.split(";"))
                .map(RawMimeTypeParameter::new)
                .collect(toList());
    }


    @Override
    public String getParameter(String name) {
        return getParameters().stream()
                .filter((p) -> p.getName().equals(name))
                .map(MimeTypeParameter::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getBaseType() {
        return getPrimaryType() + "/" + getSubType();
    }

    @Override
    public boolean matches(MimeType other) {
        return isSamePrimaryType(other)
                && (isSameSubType(other) || matchesSubType(other));
    }

    private boolean isSamePrimaryType(MimeType other) {
        return getPrimaryType().equals(other.getPrimaryType());
    }

    private boolean isSameSubType(MimeType other) {
        return getSubType().equals(other.getSubType());
    }

    private boolean matchesSubType(MimeType other) {
        return getSubType().equals("*") || other.getSubType().equals("*");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RawMimeType)) return false;
        RawMimeType that = (RawMimeType) o;
        return Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mimeType);
    }

    @Override
    public String toString() {
        return mimeType;
    }
}
