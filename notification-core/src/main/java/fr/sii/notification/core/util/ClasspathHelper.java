package fr.sii.notification.core.util;

public class ClasspathHelper {
	public static boolean exists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch(ClassNotFoundException e) {
			return false;
		}
	}
}
