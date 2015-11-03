package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.id.generator.IdGenerator;

public class CidBuilder extends AbstractParent<AttachImageBuilder> implements Builder<IdGenerator> {
	private IdGenerator idGenerator;
	
	public CidBuilder(AttachImageBuilder parent) {
		super(parent);
	}

	public CidBuilder generator(IdGenerator generator) {
		idGenerator = generator;
		return this;
	}

	@Override
	public IdGenerator build() throws BuildException {
		return idGenerator;
	}
}
