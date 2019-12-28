package fr.sii.ogham.core.id.generator;

/**
 * Interface to abstract identifier string generation.
 * 
 * <p>
 * A name may be used to generate the final identifier.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public interface IdGenerator {
	/**
	 * Generate an identifier as a string. The name may be used in the generated
	 * identifier (depending on the implementation).
	 * 
	 * @param name
	 *            a name that may be used in the generated identifier
	 * @return the generated identifier
	 */
	String generate(String name);
}
