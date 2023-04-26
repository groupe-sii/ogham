package oghamcore.it.core.resource.resolver;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.SimpleResource;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.StringResourceResolver;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.File;
import java.io.IOException;

import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static org.junit.jupiter.api.Assertions.*;

@LogTestInformation
@MockitoSettings
public class FirstSupportingResolverTest {
	private FirstSupportingResourceResolver firstSupportingResolver;

	@TempDir
	File folder;

	@BeforeEach
	public void setUp() throws ResourceResolutionException {
		firstSupportingResolver = new FirstSupportingResourceResolver(
				new StringResourceResolver("string:", "s:"),
				new FileResolver("file:"),
				new ClassPathResolver("classpath:", ""));
	}

	@Test
	public void classpath() throws ResourceResolutionException, IOException {
		ResourcePath path = new UnresolvedPath("classpath:/template/resolver/foo/bar.html");
		assertTrue(firstSupportingResolver.supports(path), "should be able to support classpath path");
		Resource resource = firstSupportingResolver.getResource(path);
		assertNotNull(resource, "template should not be null");
		assertSame(resource.getClass(), ByteResource.class, "should be classpath resolver");
	}

	@Test
	public void file() throws ResourceResolutionException, IOException {
		File tempFile = new File(folder, "bar.html");
		IOUtils.copy(resource("/template/resolver/foo/bar.html"), tempFile);
		ResourcePath path = new UnresolvedPath("file:"+tempFile.getAbsolutePath());
		assertTrue(firstSupportingResolver.supports(path), "should be able to support file path");
		Resource resource = firstSupportingResolver.getResource(path);
		assertSame(resource.getClass(), FileResource.class, "should be file resolver");
	}

	@Test
	public void string() throws ResourceResolutionException {
		ResourcePath path = new UnresolvedPath("string:ma ressource");
		assertTrue(firstSupportingResolver.supports(path), "should be able to support string path");
		Resource resource = firstSupportingResolver.getResource(path);
		assertSame(resource.getClass(), SimpleResource.class, "should be string resolver");
	}

	@Test
	public void none() throws ResourceResolutionException {
		ResourcePath path = new UnresolvedPath("/template/resolver/foo/bar.html");
		assertTrue(firstSupportingResolver.supports(path), "should be able to support template path");
		Resource resource = firstSupportingResolver.getResource(path);
		assertSame(resource.getClass(), ByteResource.class, "should be classpath resolver");
	}

	@Test
	public void unknown() throws ResourceResolutionException {
		assertThrows(ResourceResolutionException.class, () -> {
			ResourcePath path = new UnresolvedPath("fake:/template/resolver/foo/bar.html");
			firstSupportingResolver.getResource(path);
		});
	}
}
