package fr.sii.notification.core.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import fr.sii.notification.core.exception.template.BeanException;

public class BeanUtils {
	public static Map<String, Object> convert(Object bean) throws BeanException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			BeanInfo info = Introspector.getBeanInfo(bean.getClass());
			for(PropertyDescriptor pd : info.getPropertyDescriptors()) {
				Method reader = pd.getReadMethod();
				if (reader!=null) {
					map.put(pd.getName(), reader.invoke(bean));
				}
			}
			return map;
		} catch(ReflectiveOperationException | IntrospectionException e) {
			throw new BeanException("failed to convert bean to map", bean, e);
		}
	}
	
	public static void populate(Object bean, Map<String, Object> values) throws BeanException {
		try {
			org.apache.commons.beanutils.BeanUtils.populate(bean, values);
		} catch(InvocationTargetException | IllegalAccessException e) {
			throw new BeanException("failed to populate bean", bean, e);
		}
	}
}
