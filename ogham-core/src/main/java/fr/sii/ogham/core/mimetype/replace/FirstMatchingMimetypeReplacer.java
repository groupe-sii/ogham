package fr.sii.ogham.core.mimetype.replace;

import java.util.List;

/**
 * Delegates mimetype replacement to registered replacers.
 * 
 * The first replacer is applied. If the mimetype has been replaced, then no
 * other replacer is called and the replaced mimetype is returned. If the first
 * replacer hasn't replaced the mimetype, the second replacer is applied and so
 * on until one replace has replaced the mimetype.
 * 
 * If no replacer has replaced the mimetype, the original mimetype is returned.
 * 
 * @author Aurélien Baudet
 *
 */
public class FirstMatchingMimetypeReplacer implements MimetypeReplacer {
	private final List<MimetypeReplacer> delegates;

	/**
	 * Initialize with the list of delegate replacers that are applied in order.
	 * 
	 * @param delegates
	 *            the replacers to apply
	 */
	public FirstMatchingMimetypeReplacer(List<MimetypeReplacer> delegates) {
		super();
		this.delegates = delegates;
	}

	@Override
	public String replace(String mimetype) {
		for (MimetypeReplacer replacer : delegates) {
			String replaced = replacer.replace(mimetype);
			// if replacement has been applied
			if (!replaced.equals(mimetype)) {
				return replaced;
			}
		}
		return mimetype;
	}

}
