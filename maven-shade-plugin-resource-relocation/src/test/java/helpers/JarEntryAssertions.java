package helpers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class JarEntryAssertions {
    private final JarAssertions parent;
    private final JarFile jar;
    private final JarEntry jarEntry;

    public JarEntryAssertions exists() {
        assertNotNull(jarEntry);
        return this;
    }

    public JarEntryAssertions content(Matcher<String> matcher) throws IOException {
        try (InputStream entryStream = jar.getInputStream(jarEntry)) {
            String xformedContent = IOUtils.toString(entryStream, UTF_8);
            MatcherAssert.assertThat("content of "+jarEntry.getName(), xformedContent, matcher);
        }
        return this;
    }

    public JarAssertions and() {
        return parent;
    }
}
