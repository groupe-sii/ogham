package fr.sii.ogham.core.filler;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.WithSubject;
import fr.sii.ogham.core.subject.provider.SubjectProvider;

/**
 * Message filler that provides a subject to the message. If the message has
 * subject capability (implements {@link WithSubject}) and if the current
 * subject is null, then this filler asks to a {@link SubjectProvider} to
 * generate a subject.
 * 
 * @author Aurélien Baudet
 *
 */
public class SubjectFiller implements MessageFiller {
	/**
	 * The subject provider used to generate the subject
	 */
	private SubjectProvider subjectProvider;

	public SubjectFiller(SubjectProvider subjectProvider) {
		super();
		this.subjectProvider = subjectProvider;
	}

	@Override
	public void fill(Message message) {
		if (message instanceof WithSubject) {
			WithSubject msg = (WithSubject) message;
			// only try to set the subject if none is provided
			if (msg.getSubject() == null) {
				String subject = subjectProvider.provide(message);
				if (subject != null) {
					msg.setSubject(subject);
				}
			}
		}
	}

}
