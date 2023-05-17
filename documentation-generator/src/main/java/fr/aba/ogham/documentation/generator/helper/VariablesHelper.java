package fr.aba.ogham.documentation.generator.helper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import fr.aba.ogham.documentation.generator.properties.DocumentationSourceProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class VariablesHelper {
	private static final Pattern VARIABLE_DEFINITION = Pattern.compile("^:([^:]+):\\s*(.+)$", Pattern.MULTILINE);

	private final ReadHelper reader;
	private final IncludeHelper merger;
	private final DocumentationSourceProperties documentationProperties;

	public Variables loadVariables(Path variablesFile) throws IOException {
		String merged = loadVariableContentAndInclusions(variablesFile);
		// parse file and load variables
		Variables variables = new Variables();
		Matcher matcher = VARIABLE_DEFINITION.matcher(merged);
		while (matcher.find()) {
			variables.add(matcher.group(1), matcher.group(2));
		}
		return variables;
	}

	private String loadVariableContentAndInclusions(Path variablesFile) throws IOException {
		Path rootDirectory = documentationProperties.getRootDirectory();
		Path asciidocDirectory = rootDirectory.resolve(documentationProperties.getAsciidocDirectory());
		Variables loadVars = new Variables();
		loadVars.add("docdir", asciidocDirectory.toString());
		loadVars.add("doc-base-dir", asciidocDirectory.toString());
		String content = reader.getContent(variablesFile);
		return merger.include(asciidocDirectory, content, loadVars, 0);
	}

	public static class Variables {
		private static final Pattern VARIABLE_REFERENCE = Pattern.compile("\\{([^}\\s]+)\\}");

		private Map<String, String> vars = new HashMap<>();

		public void add(String variable, String value) {
			vars.put(variable, value);
		}

		public String evaluate(String str) {
			if (str == null) {
				return null;
			}
			Matcher m = VARIABLE_REFERENCE.matcher(str);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				String value = evaluate(vars.get(m.group(1)));
				if (value == null) {
					value = m.group();
				}
				m.appendReplacement(sb, Matcher.quoteReplacement(value));
			}
			m.appendTail(sb);
			return sb.toString();
		}

		@Override
		public String toString() {
			StringJoiner joiner = new StringJoiner("\n  * ", "\n  * ", "\n");
			for (Map.Entry<String, String> entry : vars.entrySet()) {
				joiner.add(entry.getKey()+"\n" +
						"        original="+entry.getValue()+"\n" +
						"       evaluated="+evaluate(entry.getValue()));
			}
			return joiner.toString();
		}
	}
}