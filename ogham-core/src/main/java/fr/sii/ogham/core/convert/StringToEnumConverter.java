package fr.sii.ogham.core.convert;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.sii.ogham.core.exception.convert.ConversionException;

/**
 * Converts a string to a {@link Enum} value. It uses
 * {@link Enum#valueOf(Class, String)} to get the enum value by default.
 * 
 * <p>
 * If the enum is annotated with {@link FactoryMethod}, the name of the static
 * method is used to create the enum instance:
 * 
 * <pre>
 * {@code
 * {@literal @}FactoryMethod(name="fromNameOrValue")
 * enum MyEnum {
 *   A("1"),
 *   B("2");
 *   
 *   private final String value;
 *   MyEnum(String value) {
 *     this.value = value;
 *   }
 *   
 *   public String value() {
 *     return value;
 *   }
 *   
 *   public static MyEnum fromNameOrValue(String nameOrValue) {
 *      for (MyEnum e : values()) {
 *        if (e.value().equals(nameOrValue))
 *          return e;
 *        if (e.name().equals(nameOrValue))
 *          return e;
 *        throw new IllegalArgumentException("Unknown name or value: "+nameOrValue);
 *      }
 *   }
 * }
 * }
 * </pre>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
@SuppressWarnings("squid:S1192")
public class StringToEnumConverter implements SupportingConverter {

	/**
	 * Idicates which method to use to instantiate the {@link Enum} instead of
	 * using {@link Enum#valueOf(Class, String)}.
	 * 
	 * @author Aurélien Baudet
	 */
	@Target(TYPE)
	@Retention(RUNTIME)
	@Documented
	@Inherited
	public static @interface FactoryMethod {
		/**
		 * The name of the factory method to use for instantiating the
		 * {@link Enum} instead of {@link Enum#valueOf(Class, String)}.
		 * 
		 * @return the name of the factory method
		 */
		String name();
	}

	@Override
	public <T> T convert(Object source, Class<T> targetType) {
		String name = (String) source;
		if (name == null || name.isEmpty()) {
			return null;
		}
		FactoryMethod annotation = targetType.getAnnotation(FactoryMethod.class);
		if (annotation != null) {
			return create(targetType, annotation.name(), name);
		}
		return valueOf(targetType, name);
	}

	@Override
	public boolean supports(Class<?> sourceType, Class<?> targetType) {
		return String.class.isAssignableFrom(sourceType) && Enum.class.isAssignableFrom(targetType);
	}

	@SuppressWarnings("unchecked")
	private static <T> T create(Class<T> targetType, String methodName, String value) {
		try {
			Method method = targetType.getMethod(methodName, String.class);
			return (T) method.invoke(null, value);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ConversionException("Failed to convert " + value + " into Enum using custom factory method", e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T valueOf(Class<T> targetType, String name) {
		try {
			return (T) Enum.valueOf((Class<Enum>) targetType, name.trim());
		} catch (IllegalArgumentException e) {
			throw new ConversionException("Failed to convert " + name + " into Enum", e);
		}
	}

}
