package fr.sii.ogham.core.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.template.BeanException;
import fr.sii.ogham.core.util.converter.EmailAddressConverter;
import fr.sii.ogham.core.util.converter.SmsSenderConverter;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.sms.message.Sender;

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
public final class BeanUtils {
	private static final Logger LOG = LoggerFactory.getLogger(BeanUtils.class);

	static {
		registerDefaultConverters();
	}

	public static void registerDefaultConverters() {
		// TODO: auto-detect converters in the classpath ?
		// Add converter for being able to convert string address into
		// EmailAddress
		ConvertUtils.register(new EmailAddressConverter(), EmailAddress.class);
		// Add converter for being able to convert string into
		// SMS sender
		ConvertUtils.register(new SmsSenderConverter(), Sender.class);
		BeanUtilsBean.getInstance().getConvertUtils().register(true, false, 0);
	}
	
	/**
	 * <p>
	 * Convert a Java object into a map. Each property of the bean is added to
	 * the map. The key of each entry is the name of the property. The value of
	 * each entry is the value of the property.
	 * 
	 * <p>
	 * If the provided object is already a Map then it is returned as-is
	 * 
	 * @param bean
	 *            the bean to convert into a map
	 * @return the bean as map
	 * @throws BeanException
	 *             when the conversion has failed
	 */
	public static Map<String, Object> convert(Object bean) throws BeanException {
		try {
			Map<String, Object> map;
			if(bean instanceof Map) {
				// TODO: handle Map with object keys
				map = (Map<String, Object>) bean;
			} else {
				map = convertBean(bean);
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
	 * Override parameter allows to indicate which source has higher priority:
	 * <ul>
	 * <li>If true, then all values provided in the map will be always set on
	 * the bean</li>
	 * <li>If false then there are two cases:
	 * <ul>
	 * <li>If the property value of the bean is null, then the value that comes
	 * from the map is used</li>
	 * <li>If the property value of the bean is not null, then this value is
	 * unchanged and the value in the map is not used</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * Skip unknown parameter allows to indicate if execution should fail or
	 * not:
	 * <ul>
	 * <li>If true and a property provided in the map doesn't exist, then there
	 * is no failure and no change is applied to the bean</li>
	 * <li>If false and a property provided in the map doesn't exist, then the
	 * method fails immediately.</li>
	 * </ul>
	 * 
	 * @param bean
	 *            the bean to populate
	 * @param values
	 *            the name/value pairs
	 * @param options
	 *            options used to
	 * @throws BeanException
	 *             when the bean couldn't be populated
	 */
	public static void populate(Object bean, Map<String, Object> values, Options options) throws BeanException {
		try {
			for (Entry<String, Object> entry : values.entrySet()) {
				populate(bean, entry, options);
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new BeanException("Failed to populate bean", bean, e);
		}
	}

	/**
	 * <p>
	 * Fills a Java object with the provided value. The key of the entry
	 * corresponds to the name of the property to set. The value of the entry
	 * corresponds to the value to set on the Java object.
	 * </p>
	 * <p>
	 * The keys can contain '.' to set nested values.
	 * </p>
	 * Override parameter allows to indicate which source has higher priority:
	 * <ul>
	 * <li>If true, then the value provided in the entry will be always set on
	 * the bean</li>
	 * <li>If false then there are two cases:
	 * <ul>
	 * <li>If the property value of the bean is null, then the value that comes
	 * from the entry is used</li>
	 * <li>If the property value of the bean is not null, then this value is
	 * unchanged and the value in the entry is not used</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * Skip unknown parameter allows to indicate if execution should fail or
	 * not:
	 * <ul>
	 * <li>If true and a property provided in the entry doesn't exist, then
	 * there is no failure and no change is applied to the bean</li>
	 * <li>If false and a property provided in the entry doesn't exist, then the
	 * method fails immediately.</li>
	 * </ul>
	 * 
	 * @param bean
	 *            the bean to populate
	 * @param entry
	 *            the name/value pair
	 * @param options
	 *            options used to
	 * @throws BeanException
	 *             when the bean couldn't be populated
	 * @throws InvocationTargetException
	 *             when the setter method can't be called
	 * @throws IllegalAccessException
	 *             when the field can't be accessed due to security restrictions
	 */
	public static void populate(Object bean, Entry<String, Object> entry, Options options) throws BeanException, IllegalAccessException, InvocationTargetException {
		try {
			String property = org.apache.commons.beanutils.BeanUtils.getProperty(bean, entry.getKey());
			if (options.isOverride() || property == null) {
				org.apache.commons.beanutils.BeanUtils.setProperty(bean, entry.getKey(), entry.getValue());
			}
		} catch (NestedNullException | NoSuchMethodException e) {
			handleUnknown(bean, options, entry, e);
		} catch (ConversionException e) {
			handleConversion(bean, options, entry, e);
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
	 * It doesn't override the value of properties of the bean that are not
	 * null. For example, if the bean looks like:
	 * 
	 * <pre>
	 * public class SampleBean {
	 * 	private String foo = &quot;foo&quot;;
	 * 	private String bar = null;
	 * 
	 * 	// ...
	 * 	// getters and setters
	 * 	// ...
	 * }
	 * </pre>
	 * 
	 * If the map is:
	 * 
	 * <pre>
	 * Map&lt;String, Object&gt; map = new HashMap&lt;&gt;();
	 * map.put(&quot;foo&quot;, &quot;newfoo&quot;);
	 * map.put(&quot;bar&quot;, &quot;newbar&quot;);
	 * </pre>
	 * 
	 * Then the bean will be:
	 * 
	 * <pre>
	 * System.out.println(bean.getFoo());
	 * // foo
	 * System.out.println(bean.getBar());
	 * // newbar
	 * </pre>
	 * 
	 * <p>
	 * It doesn't fail if a property doesn't exist.
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
		populate(bean, values, new Options(false, true));
	}

	
	private static Map<String, Object> convertBean(Object bean) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		Map<String, Object> map;
		map = new HashMap<String, Object>();
		BeanInfo info = Introspector.getBeanInfo(bean.getClass());
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if(!"class".equals(pd.getName())) {
				Method reader = pd.getReadMethod();
				// TODO: convert recursively ?
				if (reader != null) {
					map.put(pd.getName(), reader.invoke(bean));
				}
			}
		}
		return map;
	}

	private static void handleUnknown(Object bean, Options options, Entry<String, Object> entry, Exception e) throws BeanException {
		if (options.isSkipUnknown()) {
			LOG.debug("skipping property " + entry.getKey() + ": it doesn't exist or is not accessible", e);
		} else {
			throw new BeanException("Failed to populate bean due to unknown property", bean, e);
		}
	}

	private static void handleConversion(Object bean, Options options, Entry<String, Object> entry, ConversionException e) throws BeanException {
		if (options.isSkipUnknown()) {
			LOG.debug("skipping property " + entry.getKey() + ": can't convert value", e);
		} else {
			throw new BeanException("Failed to populate bean due to conversion error", bean, e);
		}
	}


	public static class Options {
		private boolean override;

		private boolean skipUnknown;

		public Options(boolean override, boolean skipUnknown) {
			super();
			this.override = override;
			this.skipUnknown = skipUnknown;
		}

		public boolean isOverride() {
			return override;
		}

		public boolean isSkipUnknown() {
			return skipUnknown;
		}
	}

	private BeanUtils() {
		super();
	}
}
