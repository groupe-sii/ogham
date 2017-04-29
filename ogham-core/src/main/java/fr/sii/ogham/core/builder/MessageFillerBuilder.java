package fr.sii.ogham.core.builder;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.filler.EveryFillerDecorator;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.core.filler.SubjectFiller;
import fr.sii.ogham.core.subject.provider.FirstSupportingSubjectProvider;
import fr.sii.ogham.core.subject.provider.HtmlTitleSubjectProvider;
import fr.sii.ogham.core.subject.provider.MultiContentSubjectProvider;
import fr.sii.ogham.core.subject.provider.SubjectProvider;
import fr.sii.ogham.core.subject.provider.TextPrefixSubjectProvider;

/**
 * Builder that help construct the message fillers. The aim of a message filler
 * is to generate some values to put into the message object.
 * 
 * @author Aurélien Baudet
 *
 */
public class MessageFillerBuilder implements Builder<MessageFiller> {
	/**
	 * The fillers to use in chain
	 */
	private List<MessageFiller> fillers;

	public MessageFillerBuilder() {
		super();
		fillers = new ArrayList<>();
	}

	@Override
	public MessageFiller build() throws BuildException {
		return new EveryFillerDecorator(fillers);
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Generate subject from HTML title or first textual line starting with
	 * <code>"Subject:"</code></li>
	 * </ul>
	 * 
	 * @return this instance for fluent use
	 */
	public MessageFillerBuilder useDefaults() {
		withSubjectFiller();
		return this;
	}

	/**
	 * Enable the generation of subject of the message. The subject can
	 * automatically be extracted from the content:
	 * <ul>
	 * <li>If content of the message is HTML, then the title is used as subject
	 * </li>
	 * <li>If content of the message is text and the first line starts with
	 * <code>"Subject:"</code>, then it is used as subject</li>
	 * </ul>
	 * <p>
	 * Automatically called by {@link #useDefaults()}
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public MessageFillerBuilder withSubjectFiller() {
		// TODO: builder for subject provider too ?
		FirstSupportingSubjectProvider provider = new FirstSupportingSubjectProvider(new TextPrefixSubjectProvider(), new HtmlTitleSubjectProvider());
		SubjectProvider multiContentProvider = new MultiContentSubjectProvider(provider);
		provider.addProvider(multiContentProvider);
		fillers.add(new SubjectFiller(provider));
		return this;
	}

	/**
	 * Registers a specific filler to add information to handled messages. The
	 * filler is added to the existing filler chain.
	 * 
	 * @param filler
	 *            the filler to register and add to the chain
	 * @return this instance for fluent use
	 */
	public MessageFillerBuilder addFiller(MessageFiller filler) {
		fillers.add(filler);
		return this;
	}

}
