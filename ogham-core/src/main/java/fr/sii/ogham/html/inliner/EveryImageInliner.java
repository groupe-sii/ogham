package fr.sii.ogham.html.inliner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Applies in sequence all provided decorated inliners. This may be useful to
 * allow several strategies to be applied on the same message content according
 * to the images.
 * 
 * @author Aurélien Baudet
 *
 */
public class EveryImageInliner implements ImageInliner {
	/**
	 * The list of inliners to apply in sequence
	 */
	private List<ImageInliner> inliners;

	public EveryImageInliner(ImageInliner... inliners) {
		this(new ArrayList<>(Arrays.asList(inliners)));
	}

	public EveryImageInliner(List<ImageInliner> inliners) {
		super();
		this.inliners = inliners;
	}

	@Override
	public ContentWithImages inline(String htmlContent, List<ImageResource> images) {
		ContentWithImages combined = new ContentWithImages(htmlContent);
		for (ImageInliner inliner : inliners) {
			ContentWithImages partial = inliner.inline(combined.getContent(), images);
			combined.setContent(partial.getContent());
			combined.addAttachments(partial.getAttachments());
		}
		return combined;
	}

	public EveryImageInliner addInliner(ImageInliner inliner) {
		inliners.add(inliner);
		return this;
	}
}
