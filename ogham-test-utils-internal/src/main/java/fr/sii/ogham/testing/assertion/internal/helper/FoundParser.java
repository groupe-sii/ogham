package fr.sii.ogham.testing.assertion.internal.helper;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.template.parser.TemplateParser;

/**
 * Class that wraps found parsers and the associated message type.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of template parser implementation
 */
public class FoundParser<T extends TemplateParser> {
	private final T parser;
	private final Class<? extends Message> messageType;

	public FoundParser(T parser, Class<? extends Message> messageType) {
		super();
		this.parser = parser;
		this.messageType = messageType;
	}

	public T getParser() {
		return parser;
	}

	public Class<? extends Message> getMessageType() {
		return messageType;
	}
}