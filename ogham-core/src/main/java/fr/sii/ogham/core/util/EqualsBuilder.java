package fr.sii.ogham.core.util;

import static fr.sii.ogham.core.util.StringUtils.capitalize;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.sii.ogham.core.exception.util.FieldAccessException;

/**
 * <p>
 * Assists in implementing Object.equals(Object) methods. This code is an
 * abstraction of {@link org.apache.commons.lang3.builder.EqualsBuilder}
 * provided by Apache Commons Lang. The aim is not to be sticked with Apache
 * Commons Lang and to be able to use another library or to adapt the library to
 * use according of libraries that are in the classpath.
 * </p>
 * 
 * <p>
 * This class provides methods to build a good equals method for any class. It
 * follows rules laid out in Effective Java , by Joshua Bloch. In particular the
 * rule for comparing doubles, floats, and arrays can be tricky. Also, making
 * sure that equals() and hashCode() are consistent can be difficult.
 * </p>
 * 
 * <p>
 * Two Objects that compare as equals must generate the same hash code, but two
 * Objects with the same hash code do not have to be equal.
 * </p>
 * 
 * <p>
 * All relevant fields should be included in the calculation of equals. Derived
 * fields may be ignored. In particular, any field used in generating a hash
 * code must be used in the equals method, and vice versa.
 * </p>
 * 
 * <p>
 * Typical use for the code is as follows:
 * </p>
 * 
 * <pre><code>
 * public boolean equals(Object obj) {
 * 	return new EqualsBuilder(this, obj)
 * 			.appendSuper(super.equals(obj))
 * 			.appendFields(&quot;field1&quot;, &quot;field2&quot;, &quot;field3&quot;)
 * 			.equals();
 * }
 * </code>
 * </pre>
 * <p>
 * Alternatively, there is a method that uses reflection to determine the fields
 * to test. Because these fields are usually private, the method,
 * reflectionEquals, uses AccessibleObject.setAccessible to change the
 * visibility of the fields. This will fail under a security manager, unless the
 * appropriate permissions are set up correctly. It is also slower than testing
 * explicitly. Non-primitive fields are compared using equals().
 * </p>
 * <p>
 * A typical invocation for this method would look like:
 * </p>
 * 
 * <pre>
 * public boolean equals(Object obj) {
 * 	return EqualsBuilder.reflectionEquals(this, obj);
 * }
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class EqualsBuilder {
	/**
	 * The current object
	 */
	private Object object;

	/**
	 * The other object to compare the object
	 */
	private Object other;

	/**
	 * The equality result
	 */
	private boolean equals;

	/**
	 * The delegate implementation (currently Apache Commons but could be
	 * anything)
	 */
	private org.apache.commons.lang3.builder.EqualsBuilder delegate;

	/**
	 * Are the two objects referencing the same instance
	 */
	private boolean same;

	/**
	 * Initialize the builder. This version doesn't check if:
	 * <ul>
	 * <li>the other object is null</li>
	 * <li>the other object is the same as the current object</li>
	 * <li>the two object classes are identical</li>
	 * </ul>
	 */
	public EqualsBuilder() {
		super();
		delegate = new org.apache.commons.lang3.builder.EqualsBuilder();
		equals = true;
		same = false;
	}

	/**
	 * Initialize the builder. This version checks if:
	 * <ul>
	 * <li>the other object is null</li>
	 * <li>the other object is the same as the current object</li>
	 * <li>the two object classes are identical</li>
	 * </ul>
	 * 
	 * <p>
	 * Using this version lets you use the shortcut
	 * {@link #appendFields(String...)}
	 * </p>
	 * 
	 * @param object
	 *            the current object
	 * @param other
	 *            the other object
	 */
	public EqualsBuilder(Object object, Object other) {
		super();
		this.object = object;
		this.other = other;
		same = object == other;
		equals = same || (other != null && object.getClass() == other.getClass());
		delegate = new org.apache.commons.lang3.builder.EqualsBuilder();
	}

	/**
	 * Test if two Objects are equal using their equals method.
	 * 
	 * @param objectFieldValue
	 *            the value of a field of the object
	 * @param otherFieldValue
	 *            the value of a field of the other object
	 * @return used to chain calls
	 */
	public EqualsBuilder append(Object objectFieldValue, Object otherFieldValue) {
		if (equals && !same) {
			delegate.append(objectFieldValue, otherFieldValue);
		}
		return this;
	}

	/**
	 * <p>
	 * Test if the two previously registered objects have the same value for
	 * each provided field name.
	 * </p>
	 * 
	 * <p>
	 * Because these fields are usually private, this method uses
	 * AccessibleObject.setAccessible to change the visibility of the fields.
	 * This will fail under a security manager, unless the appropriate
	 * permissions are set up correctly. It is also slower than testing
	 * explicitly. Non-primitive fields are compared using equals().
	 * </p>
	 * 
	 * @param fields
	 *            the name of fields to compare between the previously
	 *            registered objects
	 * @return used to chain calls
	 * @throws IllegalStateException
	 *             when calling this method but you haven't used the constructor
	 *             {@link #EqualsBuilder(Object, Object)}
	 * @throws FieldAccessException
	 *             when the field is not accessible or cannot be read
	 */
	public EqualsBuilder appendFields(String... fields) {
		if (object == null) {
			throw new IllegalStateException("You can't use this method if you didn't use the constructor with object and other parameters");
		}
		if (equals && !same) {
			for (String field : fields) {
				try {
					delegate.append(getFieldValue(object, field), getFieldValue(other, field));
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					throw new FieldAccessException("Failed to access field " + field, e);
				}
			}
		}
		return this;
	}

	/**
	 * Adds the result of super.equals() to this builder.
	 * 
	 * @param superEquals
	 *            the result of calling super.equals()
	 * @return used to chain calls
	 */
	public EqualsBuilder appendSuper(boolean superEquals) {
		if (equals && !same) {
			delegate.appendSuper(superEquals);
		}
		return this;
	}

	/**
	 * Returns true if the fields that have been checked are all equal.
	 * 
	 * @return true if objects are equal, false otherwise
	 */
	public boolean isEqual() {
		return same || (equals && delegate.isEquals());
	}

	/**
	 * <p>
	 * This method uses reflection to determine if the two Objects are equal.
	 * </p>
	 * 
	 * <p>
	 * If a getter exists, it uses the getter otherwise direct access to the
	 * field is used. It uses AccessibleObject.setAccessible to gain access to
	 * private fields. This means that it will throw a security exception if run
	 * under a security manager, if the permissions are not set up correctly. It
	 * is also not as efficient as testing explicitly. Non-primitive fields are
	 * compared using equals().
	 * </p>
	 * 
	 * <p>
	 * Transient members will be not be tested, as they are likely derived
	 * fields, and not part of the value of the Object.
	 * </p>
	 * 
	 * <p>
	 * Static fields will not be tested. Superclass fields will be included.
	 * </p>
	 * 
	 * @param object
	 *            this object
	 * @param other
	 *            the other object
	 * @param excludeFields
	 *            array of field names to exclude from testing
	 * @return true if the two Objects have tested equals.
	 */
	public static boolean reflectionsEquals(Object object, Object other, String... excludeFields) {
		return org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals(object, other, excludeFields);
	}

	private static Object getFieldValue(Object object, String fieldName) throws IllegalAccessException, NoSuchFieldException, InvocationTargetException {
		Method getter = findMethod(object, "is" + capitalize(fieldName));
		if (getter == null) {
			getter = findMethod(object, "get" + capitalize(fieldName));
		}
		if (getter != null) {
			return getter.invoke(object);
		}
		Field field = getField(object, fieldName);
		field.setAccessible(true);
		return field.get(object);
	}

	private static Method findMethod(Object object, String methodName) {
		try {
			return object.getClass().getMethod(methodName);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	private static Field getField(Object object, String fieldName) throws NoSuchFieldException {
		Class<?> clazz = object.getClass();
		while (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field f : fields) {
				if (fieldName.equals(f.getName())) {
					return f;
				}
			}
			clazz = clazz.getSuperclass();
		}
		throw new NoSuchFieldException("Field " + fieldName + " not found on object " + object.getClass());
	}
}
