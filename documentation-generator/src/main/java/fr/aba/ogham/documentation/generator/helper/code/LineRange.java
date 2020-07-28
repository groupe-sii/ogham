package fr.aba.ogham.documentation.generator.helper.code;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.aba.ogham.documentation.generator.helper.parse.Line;
import lombok.Data;

@Data
public class LineRange {
	private final Integer start;
	private final Integer end;
	
	public List<Line> selectLines(SourceBlock source) {
		return source.getLines().stream()
				.filter(l -> isInRange(l.getLineNumber() + source.getLineOffset() + 1))
				.collect(toList());
	}

	private boolean isInRange(int lineNumber) {
		int start = this.start == null ? 1 : this.start;
		return lineNumber >= start && (end == null || lineNumber < end + 1);
	}
	
	public static List<LineRange> parse(String header, String prefix) {
		Matcher m = Pattern.compile(Pattern.quote(prefix)+"([^ \"]+)").matcher(header);
		if (m.find()) {
			String rangesStr = m.group(1);
			return Arrays.stream(rangesStr.split(","))
					.map(LineRange::parse)
					.collect(toList());
		}
		return Collections.emptyList();
	}

	public static LineRange parse(String rangeStr) {
		if (rangeStr.contains("-")) {
			int idx = rangeStr.indexOf("-");
			Integer start = idx > 0 ? Integer.valueOf(rangeStr.substring(0, idx)) : 1;
			Integer end = idx < rangeStr.length()-1 ? Integer.valueOf(rangeStr.substring(idx+1)) : null;
			return new LineRange(start, end);
		}
		Integer lineNumber = Integer.valueOf(rangeStr);
		return new LineRange(lineNumber, lineNumber);
	}
}