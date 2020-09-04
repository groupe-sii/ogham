package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat;

import fr.sii.ogham.core.util.ClasspathUtils;

/**
 * {@code sendgrid-java} 4.3.0 has classes declared in package
 * {@code com.sendgrid.helpers.mail} but in the generated JAR, the classes are
 * compiled into package {@code com.sendgrid}. This issue has been fixed since
 * 4.4.0.
 * 
 * However, Spring Boot 2.1.x uses {@code sendgrid-java} 4.3.0...
 * 
 * Therefore, this class helps to avoid classpath issues and uses the available
 * classes.
 * 
 * @author Aurélien Baudet
 *
 */
public final class CompatUtil {
	/**
	 * {@code sendgrid-java} package version 4.3.0 declares some classes in
	 * package {@code com.sendgrid.helpers.mail.objects} (according to source
	 * folder structure). However, the {@code package} declaration in every of
	 * these classes points to {@code com.sendgrid}.
	 * 
	 * This creates {@link ClassNotFoundException}s when trying to run Ogham
	 * with {@code sendgrid-java} v4.3.0. As it is a mistake from SendGrid, we
	 * could simply take the next version that fixes the issue. But some Spring
	 * Boot versions (like 2.1.x) has direct dependency to {@code sendgrid-java}
	 * v4.3.0. So we must provide a compatibility fix in order to make it work
	 * with older Spring Boot versions.
	 * 
	 * The default factory provides either
	 * {@link CorrectPackageNameCompatFactory} for {@code sendgrid-java} v4.4+
	 * and WrongPackageNameCompatFactory for {@code sendgrid-java} v4.3.0.
	 * 
	 * @return the factory that will provide instances of objects
	 */
	public static CompatFactory getDefaultCompatFactory() {
		if (ClasspathUtils.exists("com.sendgrid.Email")) {
			return createV430CompatFactoryInstance();
		}
		if (ClasspathUtils.exists("com.sendgrid.helpers.mail.objects.Email")) {
			return new CorrectPackageNameCompatFactory();
		}
		throw new IllegalStateException("Can't provide a valid CompatFactory based on sendgrid-java classpath");
	}

	private static CompatFactory createV430CompatFactoryInstance() {
		try {
			Class<?> klass = Class.forName("fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat.WrongPackageNameCompatFactory");
			return (CompatFactory) klass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("Failed to create compatibility factory for sendgrid-java v4.3.0", e);
		}
	}
	
	private CompatUtil() {
		super();
	}
}
