package fr.sii.ogham.core.mimetype;

import fr.sii.ogham.core.exception.mimetype.MimeTypeParseException;
import fr.sii.ogham.core.mimetype.MimeType.MimeTypeParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Collections.emptyList;

/**
 * Code borrowed from Jakarta Activation
 */
public class MimeTypeParser {

    /**
     * A string that holds all the special chars.
     */
    private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";

    /**
     * A routine for parsing the MIME type out of a String.
     */
    public static MimeType parse(String rawdata) throws MimeTypeParseException {
        String primaryType;
        String subType;
        List<MimeTypeParameter> parameters = new ArrayList<>();

        int slashIndex = rawdata.indexOf('/');
        int semIndex = rawdata.indexOf(';');
        if ((slashIndex < 0) && (semIndex < 0)) {
            //    neither character is present, so treat it
            //    as an error
            throw new MimeTypeParseException("Unable to find a sub type.", rawdata);
        } else if ((slashIndex < 0) && (semIndex >= 0)) {
            //    we have a ';' (and therefore a parameter list),
            //    but no '/' indicating a sub type is present
            throw new MimeTypeParseException("Unable to find a sub type.", rawdata);
        } else if ((slashIndex >= 0) && (semIndex < 0)) {
            //    we have a primary and sub type but no parameter list
            primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase(Locale.ENGLISH);
            subType = rawdata.substring(slashIndex + 1).trim().toLowerCase(Locale.ENGLISH);
        } else if (slashIndex < semIndex) {
            //    we have all three items in the proper sequence
            primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase(Locale.ENGLISH);
            subType = rawdata.substring(slashIndex + 1, semIndex).trim().toLowerCase(Locale.ENGLISH);
            parameters = parseParameters(rawdata, rawdata.substring(semIndex));
        } else {
            // we have a ';' lexically before a '/' which means we
            // have a primary type and a parameter list but no sub type
            throw new MimeTypeParseException("Unable to find a sub type.", rawdata);
        }

        //    now validate the primary and sub types

        //    check to see if primary is valid
        if (!isValidToken(primaryType)) throw new MimeTypeParseException("Primary type is invalid.", rawdata);

        //    check to see if sub is valid
        if (!isValidToken(subType)) throw new MimeTypeParseException("Sub type is invalid.", rawdata);

        return new ParsedMimeType(rawdata, primaryType, subType, parameters);
    }

    /**
     * Determine whether or not a given character belongs to a legal token.
     */
    private static boolean isTokenChar(char c) {
        return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
    }

    /**
     * Determine whether or not a given string is a legal token.
     */
    private static boolean isValidToken(String s) {
        int len = s.length();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                char c = s.charAt(i);
                if (!isTokenChar(c)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * A routine for parsing the parameter list out of a String.
     *
     * @param parameterList an RFC 2045, 2046 compliant parameter list.
     * @throws MimeTypeParseException if the MIME type can't be parsed
     */
    private static List<MimeTypeParameter> parseParameters(String mimetype, String parameterList) throws MimeTypeParseException {
        if (parameterList == null) return emptyList();

        int length = parameterList.length();
        if (length <= 0) return emptyList();

        List<MimeTypeParameter> parameters = new ArrayList<>();

        int i;
        char c;
        for (i = skipWhiteSpace(parameterList, 0); i < length && (c = parameterList.charAt(i)) == ';'; i = skipWhiteSpace(parameterList, i)) {
            int lastIndex;
            String name;
            String value;

            //    eat the ';'
            i++;

            //    now parse the parameter name

            //    skip whitespace
            i = skipWhiteSpace(parameterList, i);

            // tolerate trailing semicolon, even though it violates the spec
            if (i >= length) return parameters;

            //    find the end of the token char run
            lastIndex = i;
            while ((i < length) && isTokenChar(parameterList.charAt(i))) i++;

            name = parameterList.substring(lastIndex, i).toLowerCase(Locale.ENGLISH);

            //    now parse the '=' that separates the name from the value
            i = skipWhiteSpace(parameterList, i);

            if (i >= length || parameterList.charAt(i) != '=')
                throw new MimeTypeParseException("Couldn't find the '=' that separates a " + "parameter name from its value.", mimetype);

            //    eat it and parse the parameter value
            i++;
            i = skipWhiteSpace(parameterList, i);

            if (i >= length)
                throw new MimeTypeParseException("Couldn't find a value for parameter named " + name, mimetype);

            //    now find out whether or not we have a quoted value
            c = parameterList.charAt(i);
            if (c == '"') {
                //    yup it's quoted so eat it and capture the quoted string
                i++;
                if (i >= length)
                    throw new MimeTypeParseException("Encountered unterminated quoted parameter value.", mimetype);

                lastIndex = i;

                //    find the next unescaped quote
                while (i < length) {
                    c = parameterList.charAt(i);
                    if (c == '"') break;
                    if (c == '\\') {
                        //    found an escape sequence
                        //    so skip this and the
                        //    next character
                        i++;
                    }
                    i++;
                }
                if (c != '"') throw new MimeTypeParseException("Encountered unterminated quoted parameter value.", mimetype);

                value = unquoteParameterValue(parameterList.substring(lastIndex, i));
                //    eat the quote
                i++;
            } else if (isTokenChar(c)) {
                //    nope it's an ordinary token so it
                //    ends with a non-token char
                lastIndex = i;
                while (i < length && isTokenChar(parameterList.charAt(i))) i++;
                value = parameterList.substring(lastIndex, i);
            } else {
                //    it ain't a value
                throw new MimeTypeParseException("Unexpected character encountered at index " + i, mimetype);
            }

            //    now put the data into the hashtable
            parameters.add(new ParsedMimeTypeParameter(name+"="+ quoteParameterValue(value), name, value));
        }
        if (i < length) {
            throw new MimeTypeParseException("More characters encountered in input than expected.", mimetype);
        }
        return parameters;
    }

    /**
     * return the index of the first non white space character in
     * rawdata at or after index i.
     */
    private static int skipWhiteSpace(String rawdata, int i) {
        int length = rawdata.length();
        while ((i < length) && Character.isWhitespace(rawdata.charAt(i))) i++;
        return i;
    }

    /**
     * A routine that knows how and when to quote and escape the given value.
     */
    public static String quoteParameterValue(String value) {
        boolean needsQuotes = false;

        //    check to see if we actually have to quote this thing
        int length = value.length();
        for (int i = 0; (i < length) && !needsQuotes; i++) {
            needsQuotes = !isTokenChar(value.charAt(i));
        }

        if (needsQuotes) {
            StringBuffer buffer = new StringBuffer();
            buffer.ensureCapacity((int) (length * 1.5));

            //    add the initial quote
            buffer.append('"');

            //    add the properly escaped text
            for (int i = 0; i < length; ++i) {
                char c = value.charAt(i);
                if ((c == '\\') || (c == '"')) buffer.append('\\');
                buffer.append(c);
            }

            //    add the closing quote
            buffer.append('"');

            return buffer.toString();
        } else {
            return value;
        }
    }

    /**
     * A routine that knows how to strip the quotes and
     * escape sequences from the given value.
     */
    public static String unquoteParameterValue(String value) {
        int valueLength = value.length();
        StringBuffer buffer = new StringBuffer();
        buffer.ensureCapacity(valueLength);

        boolean escaped = false;
        for (int i = 0; i < valueLength; ++i) {
            char currentChar = value.charAt(i);
            if (!escaped && (currentChar != '\\')) {
                buffer.append(currentChar);
            } else if (escaped) {
                buffer.append(currentChar);
                escaped = false;
            } else {
                escaped = true;
            }
        }

        return buffer.toString();
    }

}
