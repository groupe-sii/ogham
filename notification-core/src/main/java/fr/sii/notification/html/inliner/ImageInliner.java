package fr.sii.notification.html.inliner;

import java.util.List;



public interface ImageInliner {
	public ContentWithImages inline(String htmlContent, List<ImageResource> images);
}
