package fr.aba.ogham.documentation.generator.helper.parse;

import java.util.Iterator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LineIterator implements Iterator<Line> {
	private final String content;
	private int currentLineNumber = 0;
	private int currentPos = 0;

	@Override
	public boolean hasNext() {
		return currentPos < content.length();
	}

	@Override
	public Line next() {
		int start = currentPos;
		currentPos = content.indexOf('\n', start + 1);
		currentLineNumber++;
		if (currentPos == -1) {
			currentPos = content.length();
			return new Line(currentLineNumber, content.substring(start));
		}
		return new Line(currentLineNumber, content.substring(start + 1, currentPos));
	}
}