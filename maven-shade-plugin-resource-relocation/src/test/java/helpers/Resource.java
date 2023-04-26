package helpers;

import lombok.Data;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Data
public class Resource {
    private final String path;
    private final InputStream content;

    public Resource(String path, InputStream content) {
        this.path = path;
        this.content = content;
    }

    public String getContentAsString() throws IOException {
        return IOUtils.toString(content, UTF_8);
    }

    public void close() {
        IOUtils.closeQuietly(content);
    }
}
