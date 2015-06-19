package fr.sii.ogham.html.inliner;

import java.util.List;

public interface CssInliner {
	public String inline(String htmlContent, List<ExternalCss> cssContents);
}
