package fr.sii.ogham.core.subject.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.message.Message;

/**
 * Apply every {@link SubjectProvider} in order until one subject can be
 * generated.
 * 
 * @author Aurélien Baudet
 *
 */
public class FirstSupportingSubjectProvider implements SubjectProvider {
	/**
	 * The list of subject providers
	 */
	private List<SubjectProvider> providers;

	public FirstSupportingSubjectProvider(SubjectProvider... providers) {
		this(new ArrayList<>(Arrays.asList(providers)));
	}

	public FirstSupportingSubjectProvider(List<SubjectProvider> providers) {
		super();
		this.providers = providers;
	}

	@Override
	public String provide(Message message) {
		for (SubjectProvider provider : providers) {
			String subject = provider.provide(message);
			if (subject != null) {
				return subject;
			}
		}
		return null;
	}

	/**
	 * Register a new provider. The provider is added at the end. If one of the
	 * previously registered providers can provide a subject, then this provider
	 * will not be called.
	 * 
	 * @param provider
	 *            the provider to register
	 */
	public void addProvider(SubjectProvider provider) {
		providers.add(provider);
	}
}
