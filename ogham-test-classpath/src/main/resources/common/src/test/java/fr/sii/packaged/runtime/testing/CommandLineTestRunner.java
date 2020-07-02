package fr.sii.packaged.runtime.testing;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandLineTestRunner {
	private static final Logger LOG = LoggerFactory.getLogger(CommandLineTestRunner.class);
	
	public static void run(Class<?> klass, String method, Properties properties) throws Exception {
		String action = klass.getName()+"."+method;
		List<String> args = new ArrayList<>(asList("java"));
		args.addAll(toCommandLineArgs(properties));
		args.addAll(asList("-jar", "target/app.jar"));
		args.add(action);
		String argsAsString = args.stream().collect(joining(" "));
		try {
			LOG.info("Running '{}'", argsAsString);
			Process process = new ProcessBuilder(args).start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			StringBuffer output = new StringBuffer();
			
			while ((line = br.readLine()) != null) {
				LOG.debug("{} |    {}", argsAsString, line);
				output.append(line);
				output.append("\n");
			}
			
			process.waitFor(10, TimeUnit.SECONDS);
			process.destroy();
			if (process.exitValue() != 0) {
				throw new CommandLineException("'"+argsAsString+"' exited with status code "+process.exitValue(), output.toString());
			}
		} catch(Exception e) {
			LOG.error("Failed to run '{}'", argsAsString, e);
			throw e;
		}
	}
	
	private static List<String> toCommandLineArgs(Properties properties) {
		return properties.entrySet().stream()
				.map(e -> "-D"+e.getKey()+"="+e.getValue())
				.collect(Collectors.toList());
	}
	
	public static class CommandLineException extends Exception {
		private final String output;
		
		public CommandLineException(String message, String output) {
			super(message+"\n"+format(output));
			this.output = output;
		}
		
		private static String format(String output) {
			return output.replaceAll("^", "\t");
		}
		
		public String getOutput() {
			return output;
		}
	}
}
