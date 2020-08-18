package fr.aba.ogham.documentation.generator.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RewriteHelper {
	private static final Pattern TAB_CONTAINER = Pattern.compile("(?<blockstart>\\[role=\"?tab-container[^\\]]*\\])\\s+(?<delim>={4,})(?<content>.+?)--\\s+\\2", Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern TAB = Pattern.compile("(--\n+)?^[.](?<title>.+)$\n(?<blockstart>\\[role=\"?tab[^\\]-]*\\]\n)\n*--", Pattern.MULTILINE);
	private static final Pattern CROSS_REFERENCE = Pattern.compile("<<([^,#]+).adoc([^,]+),([^>]+)>>");
	private static final Pattern TODO_BLOCKS = Pattern.compile("^(\\[role=\"?TODO.*?\\]$(\n={4,}))(.+?)\\2", Pattern.MULTILINE | Pattern.DOTALL);
	
	private final CodeSampleHelper sourceCodeHelper;
	
	public String rewrite(String content) {
		String out = formatTabcontainers(content);
		out = formatTabs(out);
		out = removeTodoBlocks(out);
		out = replaceTabsBySpaces(out);
		out = replaceCrossReferences(out);
		out = applySourceCodeExtensions(out);
		return out;
	}
	
	private static String removeTodoBlocks(String content) {
		Matcher m = TODO_BLOCKS.matcher(content);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static String formatTabcontainers(String content) {
		Matcher m = TAB_CONTAINER.matcher(content);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			m.appendReplacement(sb, Matcher.quoteReplacement(m.group("blockstart"))
									+ "\n_____\n"
									+ Matcher.quoteReplacement(m.group("content"))
									+ "\n_____\n");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static String formatTabs(String content) {
		Matcher m = TAB.matcher(content);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			m.appendReplacement(sb, "'''\n\n" 
									+ Matcher.quoteReplacement(m.group("blockstart"))
									+ Matcher.quoteReplacement(m.group("title")));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static String replaceTabsBySpaces(String content) {
		return content.replaceAll("\t", "    ");
	}
	
	private static String replaceCrossReferences(String content) {
		Matcher m = CROSS_REFERENCE.matcher(content);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			m.appendReplacement(sb, "{site-version-url}/"+Matcher.quoteReplacement(m.group(1))+".html"
									+Matcher.quoteReplacement(m.group(2))
									+"["+Matcher.quoteReplacement(m.group(3))+"]");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private String applySourceCodeExtensions(String content) {
		return sourceCodeHelper.applyExtensions(content);
	}
}
