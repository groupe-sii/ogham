package oghamcore.ut.core.bean.util;

import fr.sii.ogham.core.exception.util.InvalidPropertyException;
import fr.sii.ogham.core.util.bean.FieldAccessor;
import mock.util.bean.assetinventory.CrewMember;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.assertThrows;

@MockitoSettings
public class FieldAccessorTest {

	@Test
	public void makeAccessible() {
		Object value = new FieldAccessor<>(new CrewMember("Odile", "Deray", 42), "age").getValue();
		Assertions.assertEquals(42, value, "Value should be provided by field accessor");
	}

	@Test
	public void dontMakeAccessible() {
		assertThrows(InvalidPropertyException.class, () -> {
			new FieldAccessor<>(new CrewMember("Odile", "Deray", 42), "age", false).getValue();
		});
	}

	@Test
	public void invalidField() {
		assertThrows(InvalidPropertyException.class, () -> {
			new FieldAccessor<>(new CrewMember("Odile", "Deray", 42), "foo").getValue();
		});
	}

	// TODO: include powermock to be able to mock Field ?

}
