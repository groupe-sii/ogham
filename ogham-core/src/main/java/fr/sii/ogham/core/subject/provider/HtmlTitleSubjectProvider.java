package fr.sii.ogham.core.subject.provider;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.util.HtmlUtils;

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
		Content content = message.getContent();
		if(content instanceof StringContent) {
			String stringContent = content.toString();
			if (HtmlUtils.isHtml(stringContent)) {
				return HtmlUtils.getTitle(stringContent);
			}
		}
		return null;
	}

}
