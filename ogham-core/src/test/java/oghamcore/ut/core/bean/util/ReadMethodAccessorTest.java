package oghamcore.ut.core.bean.util;

import fr.sii.ogham.core.exception.util.InvalidPropertyException;
import fr.sii.ogham.core.util.bean.Accessor;
import fr.sii.ogham.core.util.bean.ReadMethodAccessor;
import mock.util.bean.assetinventory.CrewMember;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@MockitoSettings
public class ReadMethodAccessorTest {
	@Mock Accessor<Object> defaultAccessor;
//	@Mock Method readMethod;
	
	@Test
	public void noReadMethodAndNoDefaultAccessor() {
		assertThrows(InvalidPropertyException.class, () -> {
			new ReadMethodAccessor<>(new CrewMember("Odile", "Deray"), "age", null, null).getValue();
		});
	}

	@Test
	public void noReadMethodWithDefaultAccessor() {
		when(defaultAccessor.getValue()).thenReturn(42);
		ReadMethodAccessor<Object> accessor = new ReadMethodAccessor<>(new CrewMember("Odile", "Deray"), "age", null, defaultAccessor);
		Assertions.assertEquals(42, accessor.getValue(), "Value should be provided by default accessor");
	}


	@Test
	public void invalidField() {
		assertThrows(InvalidPropertyException.class, () -> {
			new ReadMethodAccessor<>(new CrewMember("Odile", "Deray", 42), "foo").getValue();
		});
	}

	// TODO: include powermock to be able to mock Method ?
//	@SuppressWarnings("unchecked")
//	@Test(expected=InvalidPropertyException.class)
//	public void failingReadMethod() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		when(readMethod.invoke(any(), new Object[]{})).thenThrow(InvocationTargetException.class);
//		new ReadMethodAccessor<>(new CrewMember("Odile", "Deray"), "firstName", readMethod);
//	}

}
