package mock;

public class UtilityClass {
	public static String hello(String name) {
		return "hello " + name;
	}

	public static class NestedClass {
		public static String hello(String name) {
			return "Hello " + name;
		}
	}
}
