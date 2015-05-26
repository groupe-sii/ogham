package fr.sii.notification.core.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;

import fr.sii.notification.core.converter.EmailAddressConverter;
import fr.sii.notification.core.exception.template.BeanException;
import fr.sii.notification.email.message.EmailAddress;

/**
 * Helper class for bean management:
 * <ul>
 * <li>Converts an object into a map</li>
 * <li>Fills a bean with values provided in a map</li>
 * </ul>
 * <p>
 * This work can be done by several libraries. The aim of this class is to be
 * able to change the implementation easily to use another library for example.
 * </p>
 * <p>
 * For example, we could find which library is available in the classpath and
 * use this library instead of forcing users to include Apache Commons BeanUtils
 * library.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class BeanUtils {

	static {
		// Add converter for being able to convert string address into
		// EmailAddress
		ConvertUtils.register(new EmailAddressConverter(), EmailAddress.class);
	}

	/**
	 * <p>
	 * Convert a Java object into a map. Each property of the bean is added to
	 * the map. The key of each entry is the name of the property. The value of
	 * each entry is the value of the property.
	 * </p>
	 * 
	 * @param bean
	 *            the bean to convert into a map
	 * @return the bean as map
	 * @throws BeanException
	 *             when the conversion has failed
	 */
	public static Map<String, Object> convert(Object bean) throws BeanException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			BeanInfo info = Introspector.getBeanInfo(bean.getClass());
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				Method reader = pd.getReadMethod();
				// TODO: convert recursively ?
				if (reader != null) {
					map.put(pd.getName(), reader.invoke(bean));
				}
			}
			return map;
		} catch (ReflectiveOperationException | IntrospectionException e) {
			throw new BeanException("failed to convert bean to map", bean, e);
		}
	}

	/**
	 * <p>
	 * Fills a Java object with the provided values. The key of the map
	 * corresponds to the name of the property to set. The value of the map
	 * corresponds to the value to set on the Java object.
	 * </p>
	 * <p>
	 * The keys can contain '.' to set nested values.
	 * </p>
	 * 
	 * @param bean
	 *            the bean to populate
	 * @param values
	 *            the name/value pairs
	 * @throws BeanException
	 *             when the bean couldn't be populated
	 */
	public static void populate(Object bean, Map<String, Object> values) throws BeanException {
		try {
			org.apache.commons.beanutils.BeanUtils.populate(bean, values);
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new BeanException("failed to populate bean", bean, e);
		}
	}

	private BeanUtils() {
		super();
	}
}
