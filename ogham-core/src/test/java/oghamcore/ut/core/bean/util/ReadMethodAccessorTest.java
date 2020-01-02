package oghamcore.ut.core.bean.util;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.util.InvalidPropertyException;
import fr.sii.ogham.core.util.bean.Accessor;
import fr.sii.ogham.core.util.bean.ReadMethodAccessor;
import mock.util.bean.assetinventory.CrewMember;

public class ReadMethodAccessorTest {
	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();
	
	@Mock Accessor<Object> defaultAccessor;
//	@Mock Method readMethod;
	
	@Test(expected=InvalidPropertyException.class)
	public void noReadMethodAndNoDefaultAccessor() {
		new ReadMethodAccessor<>(new CrewMember("Odile", "Deray"), "age", null, null).getValue();
	}

	@Test
	public void noReadMethodWithDefaultAccessor() {
		when(defaultAccessor.getValue()).thenReturn(42);
		ReadMethodAccessor<Object> accessor = new ReadMethodAccessor<>(new CrewMember("Odile", "Deray"), "age", null, defaultAccessor);
		Assert.assertEquals("Value should be provided by default accessor", 42, accessor.getValue());
	}


	@Test(expected=InvalidPropertyException.class)
	public void invalidField() {
		new ReadMethodAccessor<>(new CrewMember("Odile", "Deray", 42), "foo").getValue();
	}

	// TODO: include powermock to be able to mock Method ?
//	@SuppressWarnings("unchecked")
//	@Test(expected=InvalidPropertyException.class)
//	public void failingReadMethod() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		when(readMethod.invoke(any(), new Object[]{})).thenThrow(InvocationTargetException.class);
//		new ReadMethodAccessor<>(new CrewMember("Odile", "Deray"), "firstName", readMethod);
//	}

}
