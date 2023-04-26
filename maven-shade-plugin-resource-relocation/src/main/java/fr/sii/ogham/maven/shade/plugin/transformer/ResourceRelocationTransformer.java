package fr.sii.ogham.maven.shade.plugin.transformer;

import lombok.Data;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ReproducibleResourceTransformer;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResourceRelocationTransformer implements ReproducibleResourceTransformer {
    private final List<ProcessedResource> processedResources = new ArrayList<>();

    private long time = Long.MIN_VALUE;

    @Setter
    private List<Include> includes;

    public ResourceRelocationTransformer() {
        super();
        this.includes = new ArrayList<>();
    }

    public ResourceRelocationTransformer(List<String> includes) {
        super();
        this.includes = includes.stream().map(Include::new).collect(Collectors.toList());
    }

    public boolean canTransformResource(String resource) {
        for (Include include : this.includes) {
            if (include.matches(resource)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void processResource(String resource, InputStream is, List<Relocator> relocators) throws IOException {
        processResource(resource, is, relocators, 0);
    }

    @Override
    public void processResource(String resource, InputStream is, final List<Relocator> relocators, long time) throws IOException {
        for (Relocator relocator : relocators) {
            if (relocator.canRelocatePath(resource)) {
                resource = relocator.relocatePath(resource);
                break;
            }
        }

        ProcessedResource processedResource = getOrCreateProcessedResource(resource);

        Scanner scanner = new Scanner(is, UTF_8.name());
        while (scanner.hasNextLine()) {
            String relContent = scanner.nextLine();
            for (Relocator relocator : relocators) {
                relContent = relocator.applyToSourceContent(relContent);
            }
            processedResource.addLine(relContent);
        }

        if (time > this.time) {
            this.time = time;
        }
    }

    public boolean hasTransformedResource() {
        return !processedResources.isEmpty();
    }

    public void modifyOutputStream(JarOutputStream jos) throws IOException {
        for (ProcessedResource processedResource : processedResources) {
            JarEntry jarEntry = new JarEntry(processedResource.getPath());
            jarEntry.setTime(time);
            jos.putNextEntry(jarEntry);

            IOUtils.writeLines(processedResource.getLines(), "\n", jos, UTF_8);
            jos.flush();
        }
    }

    private ProcessedResource getOrCreateProcessedResource(String path) {
        for (ProcessedResource resource : processedResources) {
            if (resource.isSameOutputPath(path)) {
                return resource;
            }
        }
        ProcessedResource processedResource = new ProcessedResource(path);
        processedResources.add(processedResource);
        return processedResource;
    }

    @Data
    public static class ProcessedResource {
        private final String path;
        private final List<String> lines = new ArrayList<>();

        public void addLine(String line) {
            lines.add(line);
        }

        public boolean isSameOutputPath(String path) {
            return this.path.equals(path);
        }
    }
}