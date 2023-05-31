package fr.sii.ogham.core.mimetype;

import fr.sii.ogham.core.exception.mimetype.MimeTypeParseException;

import java.util.List;

/**
 * Implementation that parses at construction time the mimetype string
 * in order to control that it is valid and also to directly extract parts.
 * <p>
 * If the mimetype is not valid a {@link MimeTypeParseException} is thrown.
 */
public class ParsedMimeType implements MimeType {
    private final String rawMimetype;
    private final String primaryType;
    private final String subType;
    private final List<MimeTypeParameter> parameters;

    /**
     * Constructor to use when parsing is done externally.
     *
     * @param rawMimetype The original mimetype that have been parsed
     * @param primaryType The extracted primary type
     * @param subType     The extracted sub-type
     * @param parameters  The extracted parameters
     */
    public ParsedMimeType(String rawMimetype, String primaryType, String subType, List<MimeTypeParameter> parameters) {
        this.rawMimetype = rawMimetype;
        this.primaryType = primaryType;
        this.subType = subType;
        this.parameters = parameters;
    }

    /**
     * Parse the given mimetype string in order to control that it is valid
     * and to extract parts.
     * <p>
     * If the mimetype is not valid a {@link MimeTypeParseException} is thrown.
     *
     * @param mimeType The raw mimetype to parse
     * @throws MimeTypeParseException When the mimetype is not valid.
     */
    public ParsedMimeType(String mimeType) throws MimeTypeParseException {
        MimeType parsed = MimeTypeParser.parse(mimeType);
        this.rawMimetype = mimeType;
        this.primaryType = parsed.getPrimaryType();
        this.subType = parsed.getSubType();
        this.parameters = parsed.getParameters();
    }

    @Override
    public String getPrimaryType() {
        return primaryType;
    }

    @Override
    public String getSubType() {
        return subType;
    }

    @Override
    public List<MimeTypeParameter> getParameters() {
        return parameters;
    }

    @Override
    public String getParameter(String name) {
        return parameters.stream()
                .filter(p -> p.getName().equals(name))
                .map(MimeTypeParameter::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getBaseType() {
        return primaryType + "/" + subType;
    }

    @Override
    public boolean matches(MimeType other) {
        return isSamePrimaryType(other)
                && (isSameSubType(other) || matchesSubType(other));
    }

    @Override
    public String toString() {
        return rawMimetype;
    }

    private boolean isSamePrimaryType(MimeType other) {
        return primaryType.equals(other.getPrimaryType());
    }

    private boolean isSameSubType(MimeType other) {
        return subType.equals(other.getSubType());
    }

    private boolean matchesSubType(MimeType other) {
        return subType.equals("*") || other.getSubType().equals("*");
    }
}
