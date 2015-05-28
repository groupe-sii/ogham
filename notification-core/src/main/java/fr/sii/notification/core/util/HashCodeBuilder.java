package fr.sii.notification.core.util;

/**
 * <p>
 * Assists in implementing Object.hashCode() methods.
 * </p>
 * 
 * <p>
 * This class enables a good hashCode method to be built for any class. It
 * follows the rules laid out in the book Effective Java by Joshua Bloch.
 * Writing a good hashCode method is actually quite difficult. This class aims
 * to simplify the process.
 * </p>
 * 
 * <p>
 * The following is the approach taken. When appending a data field, the current
 * total is multiplied by the multiplier then a relevant value for that data
 * type is added. For example, if the current hashCode is 17, and the multiplier
 * is 37, then appending the integer 45 will create a hashcode of 674, namely 17
 * * 37 + 45.
 * </p>
 * 
 * <p>
 * All relevant fields from the object should be included in the hashCode
 * method. Derived fields may be excluded. In general, any field used in the
 * equals method must be used in the hashCode method.
 * </p>
 * 
 * <p>
 * To use this class write code as follows:
 * </p>
 * 
 * <pre>
 *  public class Person {
 *    String name;
 *    int age;
 *    boolean smoker;
 *    ...
 * 
 *    public int hashCode() {
 *      // you pick a hard-coded, randomly chosen, non-zero, odd number
 *      // ideally different for each class
 *      return new HashCodeBuilder(17, 37).
 *        append(name).
 *        append(age).
 *        append(smoker).
 *        hashCode();
 *    }
 *  }
 * </pre>
 * <p>
 * If required, the superclass hashCode() can be added using appendSuper.
 * </p>
 * 
 * <p>
 * Alternatively, there is a method that uses reflection to determine the fields
 * to test. Because these fields are usually private, the method,
 * reflectionHashCode, uses AccessibleObject.setAccessible to change the
 * visibility of the fields. This will fail under a security manager, unless the
 * appropriate permissions are set up correctly. It is also slower than testing
 * explicitly.
 * </p>
 * 
 * <p>
 * A typical invocation for this method would look like:
 * </p>
 * 
 * <pre>
 * public int hashCode() {
 * 	return HashCodeBuilder.reflectionHashCode(this);
 * }
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class HashCodeBuilder {
	private org.apache.commons.lang3.builder.HashCodeBuilder delegate;

	/**
	 * <p>
	 * Two randomly chosen, odd numbers must be passed in. Ideally these should
	 * be different for each class, however this is not vital.
	 * </p>
	 * 
	 * <p>
	 * Prime numbers are preferred, especially for the multiplier.
	 * </p>
	 * 
	 * @param initialOddNumber
	 *            an odd number used as the initial value
	 * @param multiplierOddNumber
	 *            an odd number used as the multiplier
	 * @throws IllegalArgumentException
	 *             if the number is even
	 */
	public HashCodeBuilder(int initialOddNumber, int multiplierOddNumber) {
		super();
		delegate = new org.apache.commons.lang3.builder.HashCodeBuilder(initialOddNumber, multiplierOddNumber);
	}

	/**
	 * Uses two hard coded choices for the constants needed to build a hashCode.
	 */
	public HashCodeBuilder() {
		super();
		delegate = new org.apache.commons.lang3.builder.HashCodeBuilder();
	}

	/**
	 * Append a hashCode for an Object.
	 * 
	 * @param objectValue
	 *            the Object to add to the hashCode
	 * @return used to chain calls
	 */
	public HashCodeBuilder append(Object objectValue) {
		delegate.append(objectValue);
		return this;
	}

	/**
	 * Adds the result of super.hashCode() to this builder.
	 * 
	 * @param superHashCode
	 *            the result of calling super.hashCode()
	 * @return used to chain calls
	 */
	public HashCodeBuilder appendSuper(int superHashCode) {
		delegate.appendSuper(superHashCode);
		return this;
	}

	/**
	 * The generated hash code
	 * 
	 * @return the generated hashcode
	 */
	public int hashCode() {
		return delegate.hashCode();
	}

	/**
	 * <p>
	 * Uses reflection to build a valid hash code from the fields of object.
	 * </p>
	 * 
	 * <p>
	 * This constructor uses two hard coded choices for the constants needed to
	 * build a hash code.
	 * </p>
	 * 
	 * <p>
	 * It uses AccessibleObject.setAccessible to gain access to private fields.
	 * This means that it will throw a security exception if run under a
	 * security manager, if the permissions are not set up correctly. It is also
	 * not as efficient as testing explicitly.
	 * </p>
	 * 
	 * <p>
	 * Transient members will be not be used, as they are likely derived fields,
	 * and not part of the value of the Object.
	 * </p>
	 * 
	 * <p>
	 * Static fields will not be tested. Superclass fields will be included. If
	 * no fields are found to include in the hash code, the result of this
	 * method will be constant.
	 * </p>
	 * 
	 * @param object
	 *            the Object to create a hashCode for
	 * @param excludeFields
	 *            array of field names to exclude from use in calculation of
	 *            hash code
	 * @return hash code
	 * @throws IllegalArgumentException
	 *             if the object is null
	 */
	public static int reflectionsHashCode(Object object, String... excludeFields) {
		return org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode(object, excludeFields);
	}
}
