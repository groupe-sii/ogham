package fr.sii.notification.core.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.content.Content;

public class ChainContentTranslator implements ContentTranslator {

	private List<ContentTranslator> translators;
	
	public ChainContentTranslator(ContentTranslator... translator) {
		this(new ArrayList<>(Arrays.asList(translator)));
	}
	
	public ChainContentTranslator(List<ContentTranslator> translator) {
		super();
		this.translators = translator;
	}

	@Override
	public Content translate(Content content) throws ContentTranslatorException {
		Content result = content;
		for(ContentTranslator translator : translators) {
			result = translator.translate(result);
		}
		return result;
	}

	public void addTranslator(ContentTranslator translator) {
		translators.add(translator);
	}
}
