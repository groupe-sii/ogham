package fr.aba.ogham.documentation.generator.helper.code;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.stereotype.Component;

import fr.aba.ogham.documentation.generator.helper.parse.Line;

@Component
public class CollapseLinesExtension implements SourceCodeExtension {
	private static final String COLLAPSE_LINE_PREFIX = "collapse-lines:";
	private static final String COLLAPSED = "...";
	
	@Override
	public void apply(SourceBlock source) {
		for (LineRange range : getCollapseLines(source)) {
			source.removeLines(range.selectLines(source));
			source.addLines(asList(new Line(range.getStart()-source.getLineOffset()-1, COLLAPSED)));
		}
	}

	private List<LineRange> getCollapseLines(SourceBlock source) {
		return LineRange.parse(source.getHeader(), COLLAPSE_LINE_PREFIX);
	}
}