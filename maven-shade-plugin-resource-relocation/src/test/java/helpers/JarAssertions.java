package helpers;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class JarAssertions {
    private static List<JarFile> jars = new ArrayList<>();

    private final JarFile jar;

    private JarAssertions(JarFile jar) {
        this.jar = jar;
        jars.add(jar);
    }


    public JarEntryAssertions entry(String path) {
        return new JarEntryAssertions(this, jar, jar.getJarEntry(path));
    }

    public static JarAssertions assertThat(File jarFile) throws IOException {
        return new JarAssertions(new JarFile(jarFile));
    }

    public static void close() {
        for (JarFile jar : jars) {
            IOUtils.closeQuietly(jar);
        }
    }
}
