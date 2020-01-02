package oghamcore.ut.core.bean.util;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.util.InvalidPropertyException;
import fr.sii.ogham.core.util.bean.FieldAccessor;
import mock.util.bean.assetinventory.CrewMember;

public class FieldAccessorTest {
	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();
	

	@Test
	public void makeAccessible() {
		Object value = new FieldAccessor<>(new CrewMember("Odile", "Deray", 42), "age").getValue();
		Assert.assertEquals("Value should be provided by field accessor", 42, value);
	}

	@Test(expected=InvalidPropertyException.class)
	public void dontMakeAccessible() {
		new FieldAccessor<>(new CrewMember("Odile", "Deray", 42), "age", false).getValue();
	}

	@Test(expected=InvalidPropertyException.class)
	public void invalidField() {
		new FieldAccessor<>(new CrewMember("Odile", "Deray", 42), "foo").getValue();
	}

	// TODO: include powermock to be able to mock Field ?

}
