package fr.sii.ogham.maven.shade.plugin.transformer;


import helpers.JarAssertions;
import helpers.Resource;
import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.relocation.SimpleRelocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static helpers.JarAssertions.assertThat;
import static helpers.JarHelper.generateShadedJar;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceRelocationTransformerTest {
    private final String NEWLINE = "\n";

    private List<Relocator> relocators = new ArrayList<Relocator>();

    ResourceRelocationTransformer xformer;

    Resource originalJavamailDefaultProviders;
    Resource expectedJavamailDefaultProviders;
    Resource originalJavamailProviders;
    Resource expectedJavamailProviders;
    Resource originalMailcapDefault;
    Resource expectedMailcapDefault;
    Resource originalMailcap;
    Resource expectedMailcap;
    Resource originalResourceNotIncluded;
    Resource expectedResourceNotIncluded;

    List<Resource> all = new ArrayList<>();

    @BeforeEach
    public void setup() {
        addRelocator("com.sun", "package-prefix-for-sun.com.sun");
        addRelocator("org.eclipse.angus", "package-prefix-for-eclipse.org.eclipse.angus");
        addRelocator("META-INF/javamail.default.providers", "META-INF/resource-prefix.javamail.default.providers", true);
        addRelocator("META-INF/javamail.providers", "META-INF/resource-prefix.javamail.providers", true);
        addRelocator("META-INF/mailcap.default", "META-INF/resource-prefix.mailcap.default", true);
        addRelocator("META-INF/mailcap", "META-INF/resource-prefix.mailcap", true);

        xformer = new ResourceRelocationTransformer(asList("META-INF/"));

        originalJavamailDefaultProviders = addResource("META-INF/javamail.default.providers", loadContent("javamail.default.providers.original"));
        expectedJavamailDefaultProviders = addResource("META-INF/resource-prefix.javamail.default.providers", loadContent("javamail.default.providers.expected"));
        originalJavamailProviders = addResource("META-INF/javamail.providers", loadContent("javamail.providers.original"));
        expectedJavamailProviders = addResource("META-INF/resource-prefix.javamail.providers", loadContent("javamail.providers.expected"));
        originalMailcapDefault = addResource("META-INF/mailcap.default", loadContent("mailcap.default.original"));
        expectedMailcapDefault = addResource("META-INF/resource-prefix.mailcap.default", loadContent("mailcap.default.expected"));
        originalMailcap = addResource("META-INF/mailcap", loadContent("mailcap.original"));
        expectedMailcap = addResource("META-INF/resource-prefix.mailcap", loadContent("mailcap.expected"));
        originalResourceNotIncluded = addResource("mailcap", loadContent("mailcap.original"));
        expectedResourceNotIncluded = addResource("mailcap", loadContent("mailcap.original")); // untouched
    }


    @AfterEach
    public void clean() {
        all.forEach(Resource::close);
        JarAssertions.close();
    }

    @Test
    public void relocatedClassesInResources() throws Exception {
        assertTrue(xformer.canTransformResource(originalJavamailDefaultProviders.getPath()));
        assertTrue(xformer.canTransformResource(originalJavamailProviders.getPath()));
        assertTrue(xformer.canTransformResource(originalMailcapDefault.getPath()));
        assertTrue(xformer.canTransformResource(originalMailcap.getPath()));
        assertFalse(xformer.canTransformResource(originalResourceNotIncluded.getPath()));

        xformer.processResource(originalJavamailDefaultProviders.getPath(), originalJavamailDefaultProviders.getContent(), relocators, 0);
        xformer.processResource(originalJavamailProviders.getPath(), originalJavamailProviders.getContent(), relocators, 0);
        xformer.processResource(originalMailcapDefault.getPath(), originalMailcapDefault.getContent(), relocators, 0);
        xformer.processResource(originalMailcap.getPath(), originalMailcap.getContent(), relocators, 0);

        assertThat(generateShadedJar(xformer))
                .entry(expectedJavamailDefaultProviders.getPath())
                    .exists()
                    .content(equalTo(expectedJavamailDefaultProviders.getContentAsString()))
                    .and()
                .entry(expectedJavamailProviders.getPath())
                    .exists()
                    .content(equalTo(expectedJavamailProviders.getContentAsString()))
                    .and()
                .entry(expectedMailcapDefault.getPath())
                    .exists()
                    .content(equalTo(expectedMailcapDefault.getContentAsString()))
                    .and()
                .entry(expectedMailcap.getPath())
                    .exists()
                    .content(equalTo(expectedMailcap.getContentAsString()));
    }



    private InputStream loadContent(String path) {
        return getClass().getResourceAsStream("/" + path);
    }

    private void addRelocator(String pattern, String shadedPattern) {
        addRelocator(pattern, shadedPattern, false);
    }

    private void addRelocator(String pattern, String shadedPattern, boolean rawString) {
        relocators.add(new SimpleRelocator(pattern, shadedPattern, null, null, rawString));
    }

    private Resource addResource(String path, InputStream content) {
        Resource resource = new Resource(path, content);
        all.add(resource);
        return resource;
    }

    //    @Test
//    public void mergeRelocatedFiles() throws Exception {
//        SimpleRelocator relocator =
//                new SimpleRelocator( "org.foo", "borg.foo", null, Collections.singletonList("org.foo.exclude.*"));
//        relocators.add( relocator );
//
//        String content = "org.foo.Service" + NEWLINE + "org.foo.exclude.OtherService" + NEWLINE;
//        String contentShaded = "borg.foo.Service" + NEWLINE + "org.foo.exclude.OtherService" + NEWLINE;
//        byte[] contentBytes = content.getBytes( StandardCharsets.UTF_8 );
//        String contentResource = "META-INF/services/org.foo.something.another";
//        String contentResourceShaded = "META-INF/services/borg.foo.something.another";
//
//        ResourceRelocationTransformer xformer = new ResourceRelocationTransformer();
//
//        try (InputStream contentStream = new ByteArrayInputStream( contentBytes )) {
//            xformer.processResource(contentResource, contentStream, relocators, 0);
//        }
//
//        try (InputStream contentStream = new ByteArrayInputStream( contentBytes )) {
//            xformer.processResource(contentResourceShaded, contentStream, relocators, 0);
//        }
//
//        File tempJar = File.createTempFile("shade.", ".jar");
//        tempJar.deleteOnExit();
//        FileOutputStream fos = new FileOutputStream( tempJar );
//        try ( JarOutputStream jos = new JarOutputStream( fos ) ) {
//            xformer.modifyOutputStream( jos );
//            jos.close();
//
//            JarFile jarFile = new JarFile( tempJar );
//            JarEntry jarEntry = jarFile.getJarEntry( contentResourceShaded );
//            assertNotNull( jarEntry );
//            try ( InputStream entryStream = jarFile.getInputStream( jarEntry ) ) {
//                String xformedContent = IOUtils.toString( entryStream, StandardCharsets.UTF_8);
//                assertEquals( contentShaded, xformedContent );
//            } finally {
//                jarFile.close();
//            }
//        } finally {
//            tempJar.delete();
//        }
//    }
//
//    @Test
//    public void concatanationAppliedMultipleTimes() throws Exception {
//        SimpleRelocator relocator =
//            new SimpleRelocator( "org.eclipse", "org.eclipse1234", null, null );
//        relocators.add( relocator );
//
//        String content = "org.eclipse.osgi.launch.EquinoxFactory\n";
//        byte[] contentBytes = content.getBytes( "UTF-8" );
//        InputStream contentStream = new ByteArrayInputStream( contentBytes );
//        String contentResource = "META-INF/services/org.osgi.framework.launch.FrameworkFactory";
//
//        ResourceRelocationTransformer xformer = new ResourceRelocationTransformer();
//        xformer.processResource( contentResource, contentStream, relocators, 0 );
//        contentStream.close();
//
//        File tempJar = File.createTempFile("shade.", ".jar");
//        tempJar.deleteOnExit();
//        FileOutputStream fos = new FileOutputStream( tempJar );
//        try ( JarOutputStream jos = new JarOutputStream( fos ) ) {
//            xformer.modifyOutputStream( jos );
//            jos.close();
//
//            JarFile jarFile = new JarFile( tempJar );
//            JarEntry jarEntry = jarFile.getJarEntry( contentResource );
//            assertNotNull( jarEntry );
//            try ( InputStream entryStream = jarFile.getInputStream( jarEntry ) ) {
//                String xformedContent = IOUtils.toString(entryStream, StandardCharsets.UTF_8);
//                assertEquals( "org.eclipse1234.osgi.launch.EquinoxFactory" + NEWLINE, xformedContent );
//            } finally {
//                jarFile.close();
//            }
//        } finally {
//            tempJar.delete();
//        }
//    }
//
//    @Test
//    public void concatenation() throws Exception {
//        SimpleRelocator relocator = new SimpleRelocator("org.foo", "borg.foo", null, null);
//        relocators.add( relocator );
//
//        String content = "org.foo.Service\n";
//        byte[] contentBytes = content.getBytes( StandardCharsets.UTF_8 );
//        InputStream contentStream = new ByteArrayInputStream( contentBytes );
//        String contentResource = "META-INF/services/org.something.another";
//
//        ResourceRelocationTransformer xformer = new ResourceRelocationTransformer();
//        xformer.processResource( contentResource, contentStream, relocators, 0 );
//        contentStream.close();
//
//        content = "org.blah.Service\n";
//        contentBytes = content.getBytes( StandardCharsets.UTF_8 );
//        contentStream = new ByteArrayInputStream( contentBytes );
//        contentResource = "META-INF/services/org.something.another";
//
//        xformer.processResource( contentResource, contentStream, relocators, 0 );
//        contentStream.close();
//
//        File tempJar = File.createTempFile("shade.", ".jar");
//        tempJar.deleteOnExit();
//        FileOutputStream fos = new FileOutputStream( tempJar );
//        try ( JarOutputStream jos = new JarOutputStream( fos ) ) {
//            xformer.modifyOutputStream( jos );
//            jos.close();
//
//            JarFile jarFile = new JarFile( tempJar );
//            JarEntry jarEntry = jarFile.getJarEntry( contentResource );
//            assertNotNull( jarEntry );
//            try ( InputStream entryStream = jarFile.getInputStream( jarEntry ) ) {
//                String xformedContent = IOUtils.toString(entryStream, "utf-8");
//                // must be two lines, with our two classes.
//                String[] classes = xformedContent.split("\r?\n");
//                boolean h1 = false;
//                boolean h2 = false;
//                for ( String name : classes )
//                {
//                    if ("org.blah.Service".equals( name ))
//                    {
//                        h1 = true;
//                    }
//                    else if ("borg.foo.Service".equals( name ))
//                    {
//                        h2 = true;
//                    }
//                }
//                assertTrue( h1 && h2 );
//            } finally {
//                jarFile.close();
//            }
//        } finally {
//            tempJar.delete();
//        }
//    }
}