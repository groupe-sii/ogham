package fr.sii.ogham.ut.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rits.cloning.Cloner;

import fr.sii.ogham.core.exception.template.BeanException;
import fr.sii.ogham.core.util.BeanUtils;
import fr.sii.ogham.core.util.BeanUtils.Options;
import fr.sii.ogham.mock.context.NestedBean;
import fr.sii.ogham.mock.context.SimpleBean;

public class BeanUtilsTest {
	private List<TestParam> params = new ArrayList<>();
	private Date overrideDate;
	private Date initialDate;
	
	@Before
	public void setUp() {
		Calendar initialCal = Calendar.getInstance();
		initialCal.set(2015, 6, 16);
		initialDate = initialCal.getTime();
		Calendar overrideCal = Calendar.getInstance();
		overrideCal.set(2015, 6, 20);
		overrideDate = overrideCal.getTime();
		// try to override all values
		params.add(new TestParam(new SimpleBean("initial", 10, initialDate)).addMapEntry("name", "override").addMapEntry("value", 42).addMapEntry("date", overrideDate));
		params.add(new TestParam(new SimpleBean(null, 10, initialDate)).addMapEntry("name", "override").addMapEntry("value", 42).addMapEntry("date", overrideDate));
		params.add(new TestParam(new SimpleBean(null, 0, initialDate)).addMapEntry("name", "override").addMapEntry("value", 42).addMapEntry("date", overrideDate));
		params.add(new TestParam(new SimpleBean("initial", 10)).addMapEntry("name", "override").addMapEntry("value", 42).addMapEntry("date", overrideDate));
		params.add(new TestParam(new SimpleBean(null, 10)).addMapEntry("name", "override").addMapEntry("value", 42).addMapEntry("date", overrideDate));
		params.add(new TestParam(new SimpleBean(null, 0)).addMapEntry("name", "override").addMapEntry("value", 42).addMapEntry("date", overrideDate));
		// try to override only name and value
		params.add(new TestParam(new SimpleBean("initial", 10, initialDate)).addMapEntry("name", "override").addMapEntry("value", 42));
		params.add(new TestParam(new SimpleBean(null, 10, initialDate)).addMapEntry("name", "override").addMapEntry("value", 42));
		params.add(new TestParam(new SimpleBean(null, 0, initialDate)).addMapEntry("name", "override").addMapEntry("value", 42));
		params.add(new TestParam(new SimpleBean("initial", 10)).addMapEntry("name", "override").addMapEntry("value", 42));
		params.add(new TestParam(new SimpleBean(null, 10)).addMapEntry("name", "override").addMapEntry("value", 42));
		params.add(new TestParam(new SimpleBean(null, 0)).addMapEntry("name", "override").addMapEntry("value", 42));
		// same with nested bean
		// try to override all values
		params.add(new TestParam(new NestedBean(new SimpleBean("initial", 10, initialDate))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42).addMapEntry("nested.date", overrideDate));
		params.add(new TestParam(new NestedBean(new SimpleBean(null, 10, initialDate))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42).addMapEntry("nested.date", overrideDate));
		params.add(new TestParam(new NestedBean(new SimpleBean(null, 0, initialDate))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42).addMapEntry("nested.date", overrideDate));
		params.add(new TestParam(new NestedBean(new SimpleBean("initial", 10))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42).addMapEntry("nested.date", overrideDate));
		params.add(new TestParam(new NestedBean(new SimpleBean(null, 10))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42).addMapEntry("nested.date", overrideDate));
		params.add(new TestParam(new NestedBean(new SimpleBean(null, 0))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42).addMapEntry("nested.date", overrideDate));
		// try to override only name and  value
		params.add(new TestParam(new NestedBean(new SimpleBean("initial", 10, initialDate))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42));
		params.add(new TestParam(new NestedBean(new SimpleBean(null, 10, initialDate))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42));
		params.add(new TestParam(new NestedBean(new SimpleBean(null, 0, initialDate))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42));
		params.add(new TestParam(new NestedBean(new SimpleBean("initial", 10))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42));
		params.add(new TestParam(new NestedBean(new SimpleBean(null, 10))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42));
		params.add(new TestParam(new NestedBean(new SimpleBean(null, 0))).addMapEntry("nested.name", "override").addMapEntry("nested.value", 42));
	}
	
	@Test
	public void overrideSkipUnknown() throws BeanException {
		// generate expected values
		List<Object> expecteds = new ArrayList<>();
		// try to override all values
		expecteds.add(new SimpleBean("override", 42, overrideDate));
		expecteds.add(new SimpleBean("override", 42, overrideDate));
		expecteds.add(new SimpleBean("override", 42, overrideDate));
		expecteds.add(new SimpleBean("override", 42, overrideDate));
		expecteds.add(new SimpleBean("override", 42, overrideDate));
		expecteds.add(new SimpleBean("override", 42, overrideDate));
		// try to override only name and value
		expecteds.add(new SimpleBean("override", 42, initialDate));
		expecteds.add(new SimpleBean("override", 42, initialDate));
		expecteds.add(new SimpleBean("override", 42, initialDate));
		expecteds.add(new SimpleBean("override", 42, null));
		expecteds.add(new SimpleBean("override", 42, null));
		expecteds.add(new SimpleBean("override", 42, null));
		// same with nested bean
		// try to override all values
		expecteds.add(new NestedBean(new SimpleBean("override", 42, overrideDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, overrideDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, overrideDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, overrideDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, overrideDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, overrideDate)));
		// try to override only name and  value
		expecteds.add(new NestedBean(new SimpleBean("override", 42, initialDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, initialDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, initialDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, null)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, null)));
		expecteds.add(new NestedBean(new SimpleBean("override", 42, null)));
		// execute the test and gather results
		List<TestResult> results = run(params, new Options(true, true), expecteds);
		verify(results);
	}

	@Test
	public void noOverrideSkipUnknown() throws BeanException {
		// generate expected values
		List<Object> expecteds = new ArrayList<>();
		// try to override all values
		expecteds.add(new SimpleBean("initial", 10, initialDate));
		expecteds.add(new SimpleBean("override", 10, initialDate));
		expecteds.add(new SimpleBean("override", 0, initialDate));
		expecteds.add(new SimpleBean("initial", 10, overrideDate));
		expecteds.add(new SimpleBean("override", 10, overrideDate));
		expecteds.add(new SimpleBean("override", 0, overrideDate));
		// try to override only name and value
		expecteds.add(new SimpleBean("initial", 10, initialDate));
		expecteds.add(new SimpleBean("override", 10, initialDate));
		expecteds.add(new SimpleBean("override", 0, initialDate));
		expecteds.add(new SimpleBean("initial", 10, null));
		expecteds.add(new SimpleBean("override", 10, null));
		expecteds.add(new SimpleBean("override", 0, null));
		// same with nested bean
		// try to override all values
		expecteds.add(new NestedBean(new SimpleBean("initial", 10, initialDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 10, initialDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 0, initialDate)));
		expecteds.add(new NestedBean(new SimpleBean("initial", 10, overrideDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 10, overrideDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 0, overrideDate)));
		// try to override only name and  value
		expecteds.add(new NestedBean(new SimpleBean("initial", 10, initialDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 10, initialDate)));
		expecteds.add(new NestedBean(new SimpleBean("override", 0, initialDate)));
		expecteds.add(new NestedBean(new SimpleBean("initial", 10, null)));
		expecteds.add(new NestedBean(new SimpleBean("override", 10, null)));
		expecteds.add(new NestedBean(new SimpleBean("override", 0, null)));
		// execute the test and gather results
		List<TestResult> results = run(params, new Options(false, true), expecteds);
		verify(results);
	}
	
	@Test
	public void unknownPropertyNoOverrideSkip() throws BeanException {
		Map<String, Object> map = new HashMap<>();
		map.put("unknown", "foo");
		map.put("unknown.property", "bar");
		SimpleBean bean = new SimpleBean("initial", 10);
		BeanUtils.populate(bean, map, new Options(false, true));
		Assert.assertEquals("Should not have any effect", new SimpleBean("initial", 10), bean);
	}
	
	@Test
	public void unknownPropertyOverrideSkip() throws BeanException {
		Map<String, Object> map = new HashMap<>();
		map.put("unknown", "foo");
		map.put("unknown.property", "bar");
		NestedBean bean = new NestedBean(new SimpleBean("initial", 10));
		BeanUtils.populate(bean, map, new Options(true, true));
		Assert.assertEquals("Should not have any effect", new NestedBean(new SimpleBean("initial", 10)), bean);
	}
	
	@Test
	public void nullNestedBeanSkip() throws BeanException {
		Map<String, Object> map = new HashMap<>();
		map.put("nested.name", "foo");
		NestedBean bean = new NestedBean(null);
		BeanUtils.populate(bean, map, new Options(false, true));
		Assert.assertEquals("Should not have any effect", new NestedBean(null), bean);
	}
	
	@Test
	public void wrongTypeSkip() throws BeanException {
		Map<String, Object> map = new HashMap<>();
		map.put("nested.value", "foo");
		NestedBean bean = new NestedBean(new SimpleBean("initial", 10));
		BeanUtils.populate(bean, map, new Options(false, true));
		Assert.assertEquals("Should not have any effect", new NestedBean(new SimpleBean("initial", 10)), bean);
	}
	
	@Test(expected=BeanException.class)
	public void nullNestedBean() throws BeanException {
		Map<String, Object> map = new HashMap<>();
		map.put("nested.name", "foo");
		NestedBean bean = new NestedBean(null);
		BeanUtils.populate(bean, map, new Options(true, false));
	}
	
	@Test(expected=BeanException.class)
	public void wrongType() throws BeanException {
		Map<String, Object> map = new HashMap<>();
		map.put("nested.value", "foo");
		NestedBean bean = new NestedBean(new SimpleBean("initial", 10));
		BeanUtils.populate(bean, map, new Options(true, false));
	}
	
	
	
	
	//---------------------------------------------------------------//
	//                           Utilities                           //
	//---------------------------------------------------------------//
	
	private static class TestParam {
		private Object bean;
		private Map<String, Object> map;
		
		public TestParam(Object bean) {
			this(bean, new HashMap<String, Object>());
		}

		public TestParam(Object bean, Map<String, Object> map) {
			super();
			this.bean = bean;
			this.map = map;
		}

		public TestParam addMapEntry(String key, Object value) {
			map.put(key, value);
			return this;
		}
		
		public Object getBean() {
			return bean;
		}

		public Map<String, Object> getMap() {
			return map;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("{\"bean\":").append(bean).append(", \"map\":").append(map).append("}");
			return builder.toString();
		}
	}
	
	private static class TestResult {
		private TestParam param;
		private Object actual;
		private Object expected;
		
		public TestResult(TestParam param, Object actual, Object expected) {
			super();
			this.param = param;
			this.actual = actual;
			this.expected = expected;
		}

		public TestParam getParam() {
			return param;
		}

		public Object getActual() {
			return actual;
		}

		public Object getExpected() {
			return expected;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("{\"param\":").append(param).append(", \"actual\":").append(actual).append(", \"expected\":").append(expected).append("}");
			return builder.toString();
		}
	}
	
	private static List<TestResult> run(List<TestParam> params, Options options, List<Object> expecteds) throws BeanException {
		List<TestResult> results = new ArrayList<>();
		for(int i=0 ; i<params.size() ; i++) {
			TestParam testParam = params.get(i);
			Object bean = new Cloner().deepClone(testParam.getBean());
			BeanUtils.populate(bean, testParam.getMap(), options);
			results.add(new TestResult(testParam, bean, expecteds.get(i)));
		}
		return results;
	}

	private void verify(List<TestResult> results) {
		int i=0;
		for(TestResult result : results) {
			Assert.assertEquals("Test "+i+" with param "+result.getParam().toString()+" not valid", result.getExpected(), result.getActual());
		}
		
	}

}
