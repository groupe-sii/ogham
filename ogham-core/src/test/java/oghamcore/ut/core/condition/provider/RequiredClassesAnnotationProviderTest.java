package oghamcore.ut.core.condition.provider;

import fr.sii.ogham.core.builder.condition.RequiredClasses;
import fr.sii.ogham.core.condition.*;
import fr.sii.ogham.core.condition.provider.RequiredClassesAnnotationProvider;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.core.util.classpath.ClasspathHelper;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import mock.condition.annotation.*;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@LogTestInformation
@MockitoSettings
public class RequiredClassesAnnotationProviderTest {

	RequiredClassesAnnotationProvider<Message> provider;

	@Mock
	ClasspathHelper helper;
	
	@Mock
	Message message;
	
	@BeforeEach
	public void setup() {
		provider = new RequiredClassesAnnotationProvider<>();
		ClasspathUtils.setHelper(helper);
	}

	@AfterEach
	public void clean() {
		ClasspathUtils.reset();
	}
	
	@Test
	public void nothingSpecified() {
		Condition<Message> condition = provider.provide(NoAnnotation.class.getAnnotation(RequiredClasses.class));
		assertThat(condition.accept(message), is(true));
	}

	@Test
	public void singleRequiredClass() {
		when(helper.exists(eq("class.required.1"))).thenReturn(false, true);
		Condition<Message> condition = provider.provide(OneRequiredClass.class.getAnnotation(RequiredClasses.class));
		assertThat("required class exists", condition.accept(message), is(false));
		assertThat("required class doesn't exists", condition.accept(message), is(true));
	}

	@Test
	public void severalRequiredClasses() {
		when(helper.exists(eq("class.required.1"))).thenReturn(false,     true, false,      true,      false,     true,   false,    true);
		when(helper.exists(eq("class.required.2"))).thenReturn(/*false,*/ false, /*true,*/  true,      /*false,*/ false,  /*true,*/ true);
		when(helper.exists(eq("class.required.3"))).thenReturn(/*false,*/ false, /*false,*/ /*false,*/ /*true,*/  true    /*true,*/ /*true*/);
		Condition<Message> condition = provider.provide(SeveralRequiredClasses.class.getAnnotation(RequiredClasses.class));
		assertThat("0/3 of required classes exist", condition.accept(message), is(false));
		assertThat("1/3 of required classes exist", condition.accept(message), is(false));
		assertThat("1/3 of required classes exist", condition.accept(message), is(false));
		assertThat("2/3 of required classes exist", condition.accept(message), is(false));
		assertThat("1/3 of required classes exist", condition.accept(message), is(false));
		assertThat("2/3 of required classes exist", condition.accept(message), is(false));
		assertThat("2/3 of required classes exist", condition.accept(message), is(false));
		assertThat("3/3 of required classes exist", condition.accept(message), is(true));
	}

	@Test
	public void oneRequiredClassWithAlternatives() {
		when(helper.exists(eq("class.required.1"))).thenReturn(false, true, false,      true,      false,     true,  false,    true);
		when(helper.exists(eq("class.alt.1"))).thenReturn(/*false,*/  false, /*true,*/  true,      /*false,*/ false, /*true,*/ true);
		when(helper.exists(eq("class.alt.2"))).thenReturn(/*false,*/  false, /*false,*/ /*false,*/ /*true,*/  true   /*true,*/ /*true*/);
		Condition<Message> condition = provider.provide(OneRequiredClassWithAlternatives.class.getAnnotation(RequiredClasses.class));
		assertThat("required class doesn't exist and 0/2 alternative classes exist", condition.accept(message), is(false));
		assertThat("required class exist and 0/2 alternative classes exist", condition.accept(message), is(true));
		assertThat("required class doesn't exist and 1/2 alternative classes exist", condition.accept(message), is(true));
		assertThat("required class exist and 1/2 alternative classes exist", condition.accept(message), is(true));
		assertThat("required class doesn't exist and 1/2 alternative classes exist", condition.accept(message), is(true));
		assertThat("required class exist and 1/2 alternative classes exist", condition.accept(message), is(true));
		assertThat("required class doesn't exist and 2/2 alternative classes exist", condition.accept(message), is(true));
		assertThat("required class exist and 2/2 alternative classes exist", condition.accept(message), is(true));
	}

	@Test
	public void severalRequiredClassesWithAlternatives() {
		when(helper.exists(eq("class.required.2"))).thenReturn(
				false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
		when(helper.exists(eq("class.required.3"))).thenReturn(
				/*false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true,*/ false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true);
		when(helper.exists(eq("class.required.1"))).thenReturn(
				/*false, false, false, false, true, true, true, true, false, false, false, false, true, true, true, true, false, false, false, false, true, true, true, true,*/ false, false, false, false, true, true, true, true);
		when(helper.exists(eq("class.alt.1"))).thenReturn(
				/*false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true,*/ false, false, true, true /*false, false, true, true*/);
		when(helper.exists(eq("class.alt.2"))).thenReturn(
				/*false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true,*/ false, true /*false,*/ /*true,*/ /*false, true, false, true*/);
		Condition<Message> condition = provider.provide(SeveralRequiredClassWithAlternatives.class.getAnnotation(RequiredClasses.class));
		for(int i=0 ; i<24 ; i++) {
			assertThat("required classes missing", condition.accept(message), is(false));
		}
		assertThat("2/2 required classes and required missing and 0/2 alternatives exist", condition.accept(message), is(false));
		assertThat("2/2 required classes and required missing but 1/2 alternatives exist", condition.accept(message), is(true));
		assertThat("2/2 required classes and required missing but 1/2 alternatives exist", condition.accept(message), is(true));
		assertThat("2/2 required classes and required missing but 1/2 alternatives exist", condition.accept(message), is(true));
		assertThat("2/2 required classes and required exist", condition.accept(message), is(true));
		assertThat("2/2 required classes and required exist", condition.accept(message), is(true));
		assertThat("2/2 required classes and required exist", condition.accept(message), is(true));
		assertThat("2/2 required classes and required exist", condition.accept(message), is(true));
	}

	@Test
	public void oneRequiredClassWithExcludes() {
		when(helper.exists(eq("class.required.1"))).thenReturn(false, false, false, false, true, true, true, true);
		when(helper.exists(eq("class.exclude.1"))).thenReturn(/*false, false, true, true,*/ false, false, true, true);
		when(helper.exists(eq("class.exclude.2"))).thenReturn(/*false, true, false, true,*/ false, true /*false, true*/);
		Condition<Message> condition = provider.provide(OneRequiredClassWithExcludes.class.getAnnotation(RequiredClasses.class));
		for(int i=0 ; i<4 ; i++) {
			assertThat("required class missing", condition.accept(message), is(false));
		}
		assertThat("required class exist and no ecluded classes exist", condition.accept(message), is(true));
		assertThat("required class exist and 1/2 excluded classes exist", condition.accept(message), is(false));
		assertThat("required class exist and 1/2 excluded classes exist", condition.accept(message), is(false));
		assertThat("required class exist and 2/2 excluded classes exist", condition.accept(message), is(false));
	}

	@Test
	public void severalRequiredClassesWithExcludes() {
		when(helper.exists(eq("class.required.2"))).thenReturn(
				false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
		when(helper.exists(eq("class.required.3"))).thenReturn(
				/*false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true,*/ false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true);
		when(helper.exists(eq("class.required.1"))).thenReturn(
				/*false, false, false, false, true, true, true, true, false, false, false, false, true, true, true, true, false, false, false, false, true, true, true, true,*/ false, false, false, false, true, true, true, true);
		when(helper.exists(eq("class.exclude.1"))).thenReturn(
				/*false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true,*/ false, false, true, true);
		when(helper.exists(eq("class.exclude.2"))).thenReturn(
				/*false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true,*/ false, true, false, true);
		Condition<Message> condition = provider.provide(SeveralRequiredClassWithExcludes.class.getAnnotation(RequiredClasses.class));
		for(int i=0 ; i<28 ; i++) {
			assertThat("missing required classes", condition.accept(message), is(false));
		}
		assertThat("required classes exist and 0/2 excluded classes exist", condition.accept(message), is(true));
		assertThat("required classes exist and 1/2 excluded classes exist", condition.accept(message), is(false));
		assertThat("required classes exist and 1/2 excluded classes exist", condition.accept(message), is(false));
		assertThat("required classes exist and 2/2 excluded classes exist", condition.accept(message), is(false));
	}

	@Test
	@Disabled("TODO")
	public void allPossibilities() {
		Condition<Message> condition = provider.provide(AllPossibilities.class.getAnnotation(RequiredClasses.class));
		// @formatter:off
		Assertions.assertEquals(new AndCondition<>(															// and for required classes that comes from @RequiredClasses#value()
					new RequiredClassCondition<>("class.required.2"),
					new RequiredClassCondition<>("class.required.3"),
					// FIXME: Java should infer Message argument
					new AndCondition<Message>(														// and for @RequiredClass number 1
							new OrCondition<>(
									new RequiredClassCondition<>("class.required.1")),		// or for @RequiredClass#alternatives()
							// FIXME: Java should infer Message argument
							new NotCondition<Message>(new OrCondition<>(							// not for @RequiredClass#excludes()
									new RequiredClassCondition<>("class.exclude.1"),
									new RequiredClassCondition<>("class.exclude.2")))),
					// FIXME: Java should infer Message argument
					new AndCondition<Message>(														// and for @RequiredClass number 2
							new OrCondition<>(
									new RequiredClassCondition<>("class.alt.1"),			// or for @RequiredClass#alternatives()
									new RequiredClassCondition<>("class.alt.2"),
									new RequiredClassCondition<>("class.required.4")),
							new NotCondition<>(new OrCondition<>())),						// not for @RequiredClass#excludes()
					// FIXME: Java should infer Message argument
					new AndCondition<Message>(														// and for @RequiredClass number 3
							new OrCondition<>(
									new RequiredClassCondition<>("class.alt.3"),			// or for @RequiredClass#alternatives()
									new RequiredClassCondition<>("class.alt.4"),
									new RequiredClassCondition<>("class.required.5")),
							// FIXME: Java should infer Message argument
							new NotCondition<Message>(new OrCondition<>(							// not for @RequiredClass#excludes()
									new RequiredClassCondition<>("class.exclude.3"),
									new RequiredClassCondition<>("class.exclude.4")))),
					// FIXME: Java should infer Message argument
					new AndCondition<Message>(														// and for @RequiredClass number 4
							new OrCondition<>(
									new RequiredClassCondition<>("class.required.6")),		// or for @RequiredClass#alternatives()
							new NotCondition<>(new OrCondition<>()))),						// not for @RequiredClass#excludes()
				condition,
				"Should generate "
						+ "'class.required.2' and "
						+ "'class.required.3' and "
						+ "('class.required.1' and not ('class.exclude.1' or 'class.exclude.2')) and "
						+ "('class.required.4' or 'class.alt.1' or 'class.alt.2') and"
						+ "('class.required.5' or 'class.alt.3' or 'class.alt.4' and not ('class.exclude.3' or 'class.exclude.4')) and"
						+ "'class.required.6'");
		// @formatter:on
	}
}
