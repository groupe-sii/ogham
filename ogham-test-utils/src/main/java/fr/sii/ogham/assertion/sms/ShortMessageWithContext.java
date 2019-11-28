package fr.sii.ogham.assertion.sms;

import fr.sii.ogham.assertion.context.Context;
import fr.sii.ogham.helper.sms.bean.SubmitSm;

/**
 * Context dedicated to the {@code short_message} field of a {@link SubmitSm}.
 * 
 * @author Aurélien Baudet
 *
 * @param <S>
 *            the type of the {@link SubmitSm}
 */
public class ShortMessageWithContext<S extends SubmitSm> implements Context {
	private final S request;
	private final Context parent;

	/**
	 * @param request
	 *            the sent request
	 * @param parent
	 *            the parent context
	 */
	public ShortMessageWithContext(S request, Context parent) {
		super();
		this.request = request;
		this.parent = parent;
	}

	@Override
	public String evaluate(String template) {
		return parent.evaluate(template);
	}

	/**
	 * @return the received request
	 */
	public S getRequest() {
		return request;
	}

}
