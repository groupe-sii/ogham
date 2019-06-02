package fr.aba.ogham.documentation.generator.helper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import fr.aba.ogham.documentation.generator.helper.VariablesHelper.Variables;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class IncludeHelper {
	private static final Pattern INCLUDE = Pattern.compile("include::(.+)\\[(.*)\\]");
	private static final Pattern LEVEL_OFFSET = Pattern.compile("leveloffset=([+-][0-9]+)");
	private static final Pattern TITLE = Pattern.compile("^(=+)( +.*)$", Pattern.MULTILINE);
	
	private final ReadHelper reader;

	public String include(Path asciidocDir, String content, Variables variables, int currentOffset) throws IOException {
		Matcher m = INCLUDE.matcher(content);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			String includeContent = reader.getContent(resolve(asciidocDir, m.group(1), variables));
			int offset = getLevelOffset(m);
			includeContent = applyLeveloffset(includeContent, currentOffset + offset);
			includeContent = include(asciidocDir, includeContent, variables, currentOffset + offset);
			m.appendReplacement(sb, Matcher.quoteReplacement(includeContent));
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	private int getLevelOffset(Matcher m) {
		if(m.groupCount()>1) {
			Matcher lvloffsetMatcher = LEVEL_OFFSET.matcher(m.group(2));
			if(lvloffsetMatcher.matches()) {
				return Integer.parseInt(lvloffsetMatcher.group(1));
			}
		}
		return 0;
	}
	
	private String applyLeveloffset(String includeContent, int offset) {
		if(offset!=0) {
			includeContent = updateTitles(includeContent, offset);
		}
		return includeContent;
	}

	private String updateTitles(String content, int offset) {
		Matcher matcher = TITLE.matcher(content);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			int equalSigns = matcher.group(1).length();
			StringBuilder newTitle = new StringBuilder();
			for(int i = 0 ; i<equalSigns+offset ; i++) {
				newTitle.append("=");
			}
			newTitle.append(matcher.group(2));
			matcher.appendReplacement(sb, Matcher.quoteReplacement(newTitle.toString()));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	private Path resolve(Path asciidocDir, String includePath, Variables variables) {
		return asciidocDir.resolve(variables.evaluate(includePath));
	}
}
