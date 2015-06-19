package fr.sii.ogham.core.filler;

import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.exception.filler.FillMessageException;
import fr.sii.ogham.core.message.Message;

/**
 * Decorator that calls every decorated filler in order to fill the message.
 * 
 * @author Aurélien Baudet
 *
 */
public class EveryFillerDecorator implements MessageFiller {
	/**
	 * The decorated fillers
	 */
	private List<MessageFiller> fillers;

	public EveryFillerDecorator(MessageFiller... fillers) {
		this(Arrays.asList(fillers));
	}

	public EveryFillerDecorator(List<MessageFiller> fillers) {
		super();
		this.fillers = fillers;
	}

	@Override
	public void fill(Message message) throws FillMessageException {
		for (MessageFiller filler : fillers) {
			filler.fill(message);
		}
	}

}
