package oghamcore.ut.core.env;

import fr.sii.ogham.core.builder.env.SimplePropertiesBuilder;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@LogTestInformation
public class SimplePropertiesTest {
	SimplePropertiesBuilder<?> builder;
	
	@BeforeEach
	public void setup() {
		builder = new SimplePropertiesBuilder<>(null);
	}
	
	@Test
	public void asDeveloperIDefineAStringProperty() {
		builder.set("key", "value");
		Properties props = builder.build();
		assertThat("not empty", props.isEmpty(), is(false));
		assertThat("contains", props.getProperty("key"), is("value"));
	}
	
	@Test
	public void asDeveloperIDefineABooleanProperty() {
		builder.set("key", true);
		Properties props = builder.build();
		assertThat("not empty", props.isEmpty(), is(false));
		assertThat("contains", props.getProperty("key"), is("true"));
	}
	
	@Test
	public void asDeveloperIDefineAnIntegerProperty() {
		builder.set("key", 10);
		Properties props = builder.build();
		assertThat("not empty", props.isEmpty(), is(false));
		assertThat("contains", props.getProperty("key"), is("10"));
	}
	
	@Test
	public void asDeveloperIDefineANullValue() {
		builder.set("key", null);
		Properties props = builder.build();
		assertThat("not empty", props.isEmpty(), is(true));
		assertThat("doesn't contains", props.getProperty("key"), nullValue());
	}
	
	@Test
	public void asDeveloperIUnsetAValue() {
		builder.set("key", "value");
		builder.set("key", null);
		Properties props = builder.build();
		assertThat("not empty", props.isEmpty(), is(true));
		assertThat("doesn't contains", props.getProperty("key"), nullValue());
	}
}
