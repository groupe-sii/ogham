package fr.sii.ogham.email.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.filler.EveryFillerDecorator;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.email.filler.EmailFiller;

public class AutofillEmailBuilder extends AbstractParent<EmailBuilder> implements Builder<MessageFiller> {
	private AutofillSubjectBuilder subjectBuilder;
	private AutofillDefaultEmailAddressBuilder fromBuilder;
	private AutofillDefaultEmailAddressBuilder toBuilder;
	private AutofillDefaultEmailAddressBuilder ccBuilder;
	private AutofillDefaultEmailAddressBuilder bccBuilder;
	private EnvironmentBuilder<?> environmentBuilder;
	
	public AutofillEmailBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
	}

	public AutofillSubjectBuilder subject() {
		if(subjectBuilder==null) {
			subjectBuilder = new AutofillSubjectBuilder(this, environmentBuilder);
		}
		return subjectBuilder;
	}

	public AutofillDefaultEmailAddressBuilder from() {
		if(fromBuilder==null) {
			fromBuilder = new AutofillDefaultEmailAddressBuilder(this);
		}
		return fromBuilder;
	}
	
	public AutofillDefaultEmailAddressBuilder to() {
		if(toBuilder==null) {
			toBuilder = new AutofillDefaultEmailAddressBuilder(this);
		}
		return toBuilder;
	}
	
	public AutofillDefaultEmailAddressBuilder cc() {
		if(ccBuilder==null) {
			ccBuilder = new AutofillDefaultEmailAddressBuilder(this);
		}
		return ccBuilder;
	}
	
	public AutofillDefaultEmailAddressBuilder bcc() {
		if(bccBuilder==null) {
			bccBuilder = new AutofillDefaultEmailAddressBuilder(this);
		}
		return bccBuilder;
	}

	@Override
	public MessageFiller build() throws BuildException {
		EveryFillerDecorator filler = new EveryFillerDecorator();
		if(subjectBuilder!=null) {
			filler.addFiller(subjectBuilder.build());
		}
		PropertyResolver propertyResolver = environmentBuilder.build();
		filler.addFiller(new EmailFiller(propertyResolver, buildDefaultValueProps()));
		return filler;
	}

	private Map<String, List<String>> buildDefaultValueProps() {
		Map<String, List<String>> props = new HashMap<>();
		if(subjectBuilder!=null) {
			props.put("subject", subjectBuilder.getDefaultValueProperties());
		}
		if(fromBuilder!=null) {
			props.put("from", fromBuilder.getDefaultValueProperties());
		}
		if(toBuilder!=null) {
			props.put("to", toBuilder.getDefaultValueProperties());
		}
		if(ccBuilder!=null) {
			props.put("cc", ccBuilder.getDefaultValueProperties());
		}
		if(bccBuilder!=null) {
			props.put("bcc", bccBuilder.getDefaultValueProperties());
		}
		return props;
	}
}
