package fr.sii.ogham.core.filler;

import java.util.ArrayList;
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

	/**
	 * Initializes with some fillers.
	 * 
	 * @param fillers
	 *            the fillers to be called (may be empty)
	 */
	public EveryFillerDecorator(MessageFiller... fillers) {
		this(new ArrayList<>(Arrays.asList(fillers)));
	}

	/**
	 * Initializes with some fillers.
	 * 
	 * @param fillers
	 *            the fillers to be called (may be empty)
	 */
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

	/**
	 * Register a new filler to be executed.
	 * 
	 * <p>
	 * The filler is registered at the end of the list.
	 * </p>
	 * 
	 * @param filler
	 *            the filler to append to the list
	 * @return this instance for fluent chaining
	 */
	public EveryFillerDecorator addFiller(MessageFiller filler) {
		fillers.add(filler);
		return this;
	}
}
