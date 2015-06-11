package fr.sii.notification.core.id.generator;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {

	@Override
	public String generate(String name) {
		return UUID.fromString(name).toString();
	}

}
