package fr.sii.ogham.email.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractAutofillDefaultValueBuilder;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.core.filler.SubjectFiller;
import fr.sii.ogham.core.subject.provider.FirstSupportingSubjectProvider;
import fr.sii.ogham.core.subject.provider.HtmlTitleSubjectProvider;
import fr.sii.ogham.core.subject.provider.MultiContentSubjectProvider;
import fr.sii.ogham.core.subject.provider.SubjectProvider;
import fr.sii.ogham.core.subject.provider.TextPrefixSubjectProvider;
import fr.sii.ogham.core.util.BuilderUtils;

public class AutofillSubjectBuilder extends AbstractAutofillDefaultValueBuilder<AutofillSubjectBuilder, AutofillEmailBuilder> implements Builder<MessageFiller> {
	private boolean enableHtmlTitle;
	private List<String> firstLinePrefixes;
	private SubjectProvider customProvider;
	private EnvironmentBuilder<?> environmentBuilder;
	
	public AutofillSubjectBuilder(AutofillEmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(AutofillSubjectBuilder.class, parent);
		this.environmentBuilder = environmentBuilder;
		firstLinePrefixes = new ArrayList<>();
	}

	public AutofillSubjectBuilder htmlTitle(boolean enable) {
		enableHtmlTitle = enable;
		return myself;
	}
	
	public AutofillSubjectBuilder text(String... firstLinePrefixes) {
		this.firstLinePrefixes.addAll(Arrays.asList(firstLinePrefixes));
		return myself;
	}
	
	public AutofillSubjectBuilder provider(SubjectProvider provider) {
		customProvider = provider;
		return myself;
	}

	@Override
	public MessageFiller build() throws BuildException {
		return new SubjectFiller(buildProvider());
	}
	
	private SubjectProvider buildProvider() {
		if(customProvider!=null) {
			return customProvider;
		}
		FirstSupportingSubjectProvider provider = new FirstSupportingSubjectProvider();
		if(!firstLinePrefixes.isEmpty()) {
			PropertyResolver propertyResolver = environmentBuilder.build();
			String prefix = BuilderUtils.evaluate(firstLinePrefixes, propertyResolver, String.class);
			if(prefix!=null) {
				provider.addProvider(new TextPrefixSubjectProvider(prefix));
			}
		}
		if(enableHtmlTitle) {
			provider.addProvider(new HtmlTitleSubjectProvider());
		}
		SubjectProvider multiContentProvider = new MultiContentSubjectProvider(provider);
		provider.addProvider(multiContentProvider);
		return provider;
	}
}
