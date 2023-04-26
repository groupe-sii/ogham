package fr.sii.ogham.core.mimetype;


import java.util.List;

public interface MimeType {
    /**
     * Retrieve the primary type of this object.
     *
     * @return the primary MIME type
     */
    String getPrimaryType();

    /**
     * Retrieve the subtype of this object.
     *
     * @return the MIME subtype
     */
    String getSubType();

    /**
     * Retrieve this object's parameter list.
     *
     * @return the parameters
     */
    List<MimeTypeParameter> getParameters();

    /**
     * Retrieve the value associated with the given name, or null if there
     * is no current association.
     *
     * @param name the parameter name
     * @return the paramter's value
     */
    String getParameter(String name);

    /**
     * Return a String representation of this object
     * without the parameter list.
     *
     * @return the MIME type and sub-type
     */
    String getBaseType();

    /**
     * Determine if the primary and sub type of this object is
     * the same as what is in the given type.
     *
     * @param other the MimeType object to compare with
     * @return true if they match
     */
    boolean matches(MimeType other);


    interface MimeTypeParameter {
        /**
         * Get the parameter name.
         *
         * @return the parameter's name
         */
        String getName();

        /**
         * Get the parameter value.
         *
         * @return the parameter's value
         */
        String getValue();
    }
}
