package fr.sii.ogham.core.subject.provider;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.content.StringContent;

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
		if(message.getContent() instanceof StringContent) {
			String content = message.getContent().toString();
			int idx = content.indexOf(NEW_LINE);
			if (idx > 0 && content.startsWith(prefix)) {
				// remove the subject from the content
				message.setContent(new StringContent(content.substring(idx+1)));
				return content.substring(prefix.length(), idx).trim();
			}
		}
		return null;
	}

}
