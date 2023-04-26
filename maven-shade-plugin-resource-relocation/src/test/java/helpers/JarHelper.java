package helpers;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarOutputStream;

@Slf4j
public class JarHelper {
    public static File generateShadedJar(ResourceTransformer xformer) throws IOException {
        File tempJar = File.createTempFile("shade.", ".jar");
        tempJar.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempJar);
        try (JarOutputStream jos = new JarOutputStream(fos)) {
            xformer.modifyOutputStream(jos);
        }
        log.info("Shaded JAR generated at "+tempJar.getAbsolutePath());
        return tempJar;
    }
}
