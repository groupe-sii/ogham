package fr.aba.ogham.documentation.generator.helper.code;

import static java.util.Collections.sort;
import static java.util.Comparator.comparingInt;

import java.util.ArrayList;
import java.util.List;

import fr.aba.ogham.documentation.generator.helper.parse.Line;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceBlock {
	private final RawSourceBlock raw;
	private final List<Line> lines;
	private int lineOffset;
	
	public SourceBlock() {
		this(new RawSourceBlock(), new ArrayList<>());
	}

	public void addLines(List<Line> lines) {
		this.lines.addAll(lines);
		sort(this.lines, comparingInt(Line::getLineNumber));
	}

	public void removeLines(List<Line> lines) {
		this.lines.removeAll(lines);
	}

	public void addHeader(Line line, Line delim) {
		lineOffset = -delim.getLineNumber()-1;
		raw.addHeader(line, delim);
	}
	
	public void addLineOfCode(Line line) {
		raw.addLineOfCode(line);
		lines.add(line);
	}
	
	public void addEnd(Line line) {
		raw.addEnd(line);
	}
	
	public String getHeader() {
		return raw.getHeader().toString();
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(raw.getHeader());
		for (Line line : lines) {
			str.append(line.getText()).append('\n');
		}
		str.append(raw.getFooter());
		return str.toString();
	}
}