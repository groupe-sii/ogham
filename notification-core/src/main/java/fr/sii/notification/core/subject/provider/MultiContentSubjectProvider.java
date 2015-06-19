package fr.sii.notification.core.subject.provider;

import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.MultiContent;

/**
 * Subject provider that is able to handle messages with {@link MultiContent}.
 * The aim is to try to generate a subject from the {@link MultiContent}. The
 * subject generation is delegated to another subject provider. Each part of the
 * {@link MultiContent} is extracted and the delegated subject provider is
 * called with the content part.
 * <ul>
 * <li>
 * If one of the content part could be used to generate the subject then this
 * subject is returned.</li>
 * <li>If several content parts could be used to generate the subject then only
 * the first generated subject is used.</li>
 * <li>If none of the content part could generate a subject then
 * <code>null</code> is returned.</li>
 * <li>If the content is not a {@link MultiContent} then null is returned.</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class MultiContentSubjectProvider implements SubjectProvider {
	/**
	 * The delegate subject provider that is applied on every content part
	 */
	private SubjectProvider provider;

	public MultiContentSubjectProvider(SubjectProvider provider) {
		super();
		this.provider = provider;
	}

	@Override
	public String provide(Message message) {
		if (message.getContent() instanceof MultiContent) {
			MultiContent multiContent = (MultiContent) message.getContent();
			for (Content content : multiContent.getContents()) {
				message.setContent(content);
				String subject = provider.provide(message);
				message.setContent(multiContent);
				if (subject != null) {
					return subject;
				}
			}
		}
		return null;
	}

}
