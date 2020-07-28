package fr.aba.ogham.documentation.generator.helper.code;

import fr.aba.ogham.documentation.generator.helper.parse.Line;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RawSourceBlock {
	private int startLineNumber;
	private int endLineNumber;
	private final StringBuilder header;
	private final StringBuilder footer;
	private final StringBuilder wholeBlock;
	
	public RawSourceBlock() {
		this(new StringBuilder(), new StringBuilder(), new StringBuilder());
	}
	
	public void addHeader(Line line, Line delim) {
		startLineNumber = line.getLineNumber();
		header.append(line.getText()).append('\n').append(delim.getText()).append('\n');
		wholeBlock.append(header);
	}
	
	public void addEnd(Line line) {
		footer.append(line.getText()).append('\n');
		wholeBlock.append(footer);
		endLineNumber = line.getLineNumber();
	}

	public void addLineOfCode(Line line) {
		wholeBlock.append(line.getText()).append('\n');
	}
}