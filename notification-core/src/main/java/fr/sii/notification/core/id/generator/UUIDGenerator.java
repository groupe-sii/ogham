package fr.sii.notification.core.id.generator;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {

	@Override
	public String generate(String name) {
		// TODO: name should be exactly 5 characters
		return UUID.fromString(name).toString();
	}

}
