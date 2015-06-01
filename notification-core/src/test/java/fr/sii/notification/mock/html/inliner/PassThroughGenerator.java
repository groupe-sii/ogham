package fr.sii.notification.mock.html.inliner;

import fr.sii.notification.html.inliner.IdGenerator;


public class PassThroughGenerator implements IdGenerator {

	@Override
	public String generate(String name) {
		return name;
	}

}
