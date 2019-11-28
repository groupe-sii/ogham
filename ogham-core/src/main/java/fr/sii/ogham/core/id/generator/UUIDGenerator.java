package fr.sii.ogham.core.id.generator;

import java.util.UUID;

/**
 * Generates a {@link UUID} from the provided name.
 * 
 * @author Aurélien Baudet
 *
 */
public class UUIDGenerator implements IdGenerator {

	@Override
	public String generate(String name) {
		// TODO: name should be exactly 5 characters
		return UUID.fromString(name).toString();
	}

}
