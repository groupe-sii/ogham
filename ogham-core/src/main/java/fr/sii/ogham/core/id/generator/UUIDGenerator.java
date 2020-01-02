package fr.sii.ogham.core.id.generator;

import static java.nio.charset.StandardCharsets.UTF_8;

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
		return UUID.nameUUIDFromBytes(name.getBytes(UTF_8)).toString();
	}

}
