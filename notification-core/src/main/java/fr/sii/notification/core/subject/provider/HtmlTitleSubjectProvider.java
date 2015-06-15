package fr.sii.notification.core.subject.provider;

import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.util.HtmlUtils;

/**
 * Provider that analyzes the content of the message. It the content is HTML,
 * then the subject is extracted from the title node. The extracted subject is
 * trimmed. If the extracted subject is empty then the final subject is empty
 * string. If the HTML doesn't contain the title node, then the subject is null.
 * 
 * @author Aurélien Baudet
 *
 */
public class HtmlTitleSubjectProvider implements SubjectProvider {

	@Override
	public String provide(Message message) {
		String content = message.getContent().toString();
		if (HtmlUtils.isHtml(content)) {
			return HtmlUtils.getTitle(content);
		}
		return null;
	}

}
