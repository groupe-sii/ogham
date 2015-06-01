package fr.sii.notification.html.inliner;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator {

	@Override
	public String generate(String name) {
		return UUID.fromString(name).toString();
	}

}
