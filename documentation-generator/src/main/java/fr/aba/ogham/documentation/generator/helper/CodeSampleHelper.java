package fr.aba.ogham.documentation.generator.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import fr.aba.ogham.documentation.generator.helper.code.SourceBlock;
import fr.aba.ogham.documentation.generator.helper.code.SourceCodeExtension;
import fr.aba.ogham.documentation.generator.helper.parse.Line;
import fr.aba.ogham.documentation.generator.helper.parse.LineIterator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CodeSampleHelper {
	private static final Pattern SOURCE_BLOCK_START = Pattern.compile("^\\[source,([^\\]])+\\]");
	private static final String SOURCE_BLOCK_DELIM = "----";

	private final List<SourceCodeExtension> extensions;

	public String applyExtensions(String content) {
		List<SourceBlock> sourceBlocks = toSourceBlocks(content);
		sourceBlocks.stream().forEach(this::applyExtensions);
		return update(content, sourceBlocks);
	}

	private static List<SourceBlock> toSourceBlocks(String content) {
		List<SourceBlock> sourceBlocks = new ArrayList<>();
		Iterator<Line> lineIterator = new LineIterator(content);
		SourceBlock current = null;
		while (lineIterator.hasNext()) {
			Line line = lineIterator.next();
			if (isSourceBlockStart(line) && lineIterator.hasNext()) {
				current = new SourceBlock();
				current.addHeader(line, lineIterator.next());
				continue;
			}
			if (isInCode(current, line)) {
				current.addLineOfCode(line);
			}
			if (isEndBlock(current, line)) {
				current.addEnd(line);
				sourceBlocks.add(current);
				current = null;
			}
		}
		return sourceBlocks;
	}

	private static boolean isSourceBlockStart(Line line) {
		return SOURCE_BLOCK_START.matcher(line.getText()).matches();
	}

	private static boolean isInCode(SourceBlock current, Line line) {
		return current != null && !SOURCE_BLOCK_DELIM.equals(line.getText());
	}

	private static boolean isEndBlock(SourceBlock current, Line line) {
		return current != null && SOURCE_BLOCK_DELIM.equals(line.getText());
	}

	private SourceBlock applyExtensions(SourceBlock source) {
		for (SourceCodeExtension ext : extensions) {
			ext.apply(source);
		}
		return source;
	}

	private String update(String content, List<SourceBlock> sourceBlocks) {
		for (SourceBlock source : sourceBlocks) {
			content = content.replace(source.getRaw().getWholeBlock(), source.toString());
		}
		return content;
	}
}
