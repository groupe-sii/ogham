package fr.sii.ogham.core.subject.provider;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.content.UpdatableStringContent;

/**
 * Provider that analyzes the content of the message. If the first line contains
 * the provided prefix (<code>"Subject:"</code> by default), then the subject is
 * the first line without the prefix. The extracted subject is trimmed. If the
 * extracted subject is empty then the final subject is empty string. If the
 * first line doesn't contain the prefix, then the subject is null.
 * 
 * @author Aurélien Baudet
 *
 */
public class TextPrefixSubjectProvider implements SubjectProvider {
	private static final String NEW_LINE = "\n";
	private static final String DEFAULT_PREFIX = "Subject:";

	/**
	 * The prefix
	 */
	private String prefix;

	public TextPrefixSubjectProvider() {
		this(DEFAULT_PREFIX);
	}

	public TextPrefixSubjectProvider(String prefix) {
		super();
		this.prefix = prefix;
	}

	@Override
	public String provide(Message message) {
		Content msgContent = message.getContent();
		if(msgContent instanceof MayHaveStringContent && ((MayHaveStringContent) msgContent).canProvideString()) {
			String content = ((MayHaveStringContent) msgContent).asString();
			int idx = content.indexOf(NEW_LINE);
			if (idx > 0 && content.startsWith(prefix)) {
				// remove the subject from the content and update the content
				String bodyContent = content.substring(idx+1);
				if(msgContent instanceof UpdatableStringContent) {
					((UpdatableStringContent) msgContent).setStringContent(bodyContent);
				} else {
					message.setContent(new StringContent(bodyContent));
				}
				// returns the subject
				return content.substring(prefix.length(), idx).trim();
			}
		}
		return null;
	}

}
