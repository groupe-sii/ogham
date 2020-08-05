package fr.sii.ogham.core.builder.env.props;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.builder.BuildException;

public class PropsPath extends AbstractProps {
	private static final Logger LOG = LoggerFactory.getLogger(PropsPath.class);
	private static final String FILE_PREFIX = "file:";
	private static final String CLASSPATH_PREFIX = "classpath:";
	private static final Pattern OPTIONAL_MARKER = Pattern.compile("[?]");
	private final String path;
	private final boolean optional;

	public PropsPath(String path, int priority, int index) {
		super(priority, index);
		this.path = OPTIONAL_MARKER.matcher(path).replaceAll("");
		this.optional = path.contains("?");
	}

	@Override
	public Properties getProps() {
		try {
			return load();
		} catch (FileNotFoundException e) {
			return failOrSkip(e, new Properties());
		} catch (IOException e) {
			throw new BuildException("Failed to load properties file " + path, e);
		}
	}

	private Properties load() throws IOException {
		if (path.startsWith(CLASSPATH_PREFIX)) {
			return loadFromClasspath(path.substring(CLASSPATH_PREFIX.length()));
		}
		if (path.startsWith(FILE_PREFIX)) {
			return loadFromExternalFile(path.substring(FILE_PREFIX.length()));
		}
		return loadFromClasspath(path);
	}

	private Properties loadFromExternalFile(String path) throws IOException {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(Paths.get(path).toFile()));
			return props;
		} catch (FileNotFoundException e) {
			return failOrSkip(e, props);
		}
	}

	private static String getClasspathPath(String path) {
		return path.startsWith("/") ? path.substring(1) : path;
	}

	private Properties loadFromClasspath(String path) throws IOException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream(getClasspathPath(path));
		if (stream == null) {
			throw new FileNotFoundException("Properties file not found in classpath");
		}
		Properties props = new Properties();
		props.load(stream);
		return props;
	}

	private Properties failOrSkip(FileNotFoundException e, Properties properties) {
		if (optional) {
			LOG.debug("Properties file {} is missing but marked as optional", path, e);
			return properties;
		}
		throw new BuildException("Properties file is required and missing: " + path, e);
	}
}