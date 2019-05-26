package fr.aba.ogham.documentation.generator.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RewriteHelper {
	private static final Pattern TAB_CONTAINER = Pattern.compile("^(\\[role=\"?tab-container.*\\]$\n)^(.+)$", Pattern.MULTILINE);
	
	public String formatTabcontainers(String content) {
		Matcher m = TAB_CONTAINER.matcher(content);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1))+"_____");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public String replaceTabsBySpaces(String content) {
		return content.replaceAll("\t", "    ");
	}

}
