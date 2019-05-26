package fr.aba.ogham.documentation.generator.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class VariablesHelper {
	private static final Pattern VARIABLE_DEFINITION = Pattern.compile("^:([^:]+):\\s*(.+)$");

	public Variables loadVariables(Path variablesFile) throws IOException {
		// parse file and load variables
		Variables variables = new Variables();
		for (String line : Files.readAllLines(variablesFile)) {
			Matcher matcher = VARIABLE_DEFINITION.matcher(line);
			if (matcher.matches()) {
				variables.add(matcher.group(1), matcher.group(2));
			}
		}
		return variables;
	}

	public static class Variables {
		private static final Pattern VARIABLE_REFERENCE = Pattern.compile("\\{([^}]+)\\}");

		private Map<String, String> variables = new HashMap<>();

		public void add(String variable, String value) {
			variables.put(variable, value);
		}

		public String evaluate(String str) {
			if (str == null) {
				return null;
			}
			Matcher m = VARIABLE_REFERENCE.matcher(str);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				String value = evaluate(variables.get(m.group(1)));
				if (value == null) {
					value = m.group(1);
				}
				m.appendReplacement(sb, Matcher.quoteReplacement(value));
			}
			m.appendTail(sb);
			return sb.toString();
		}

	}
}