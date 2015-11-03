package fr.sii.ogham.ut.core.condition.provider;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.builder.annotation.RequiredClasses;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.NotCondition;
import fr.sii.ogham.core.condition.OrCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;
import fr.sii.ogham.core.condition.provider.RequiredClassesAnnotationProvider;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.core.util.classpath.ClasspathHelper;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import mock.condition.annotation.AllPossibilities;
import mock.condition.annotation.NoAnnotation;
import mock.condition.annotation.OneRequiredClass;
import mock.condition.annotation.OneRequiredClassWithAlternatives;
import mock.condition.annotation.OneRequiredClassWithExcludes;
import mock.condition.annotation.SeveralRequiredClassWithAlternatives;
import mock.condition.annotation.SeveralRequiredClassWithExcludes;
import mock.condition.annotation.SeveralRequiredClasses;

public class RequiredClassesAnnotationProviderTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule();
	
	RequiredClassesAnnotationProvider<Message> provider;

	@Mock
	ClasspathHelper helper;
	
	@Mock
	Message message;
	
	@Before
	public void setup() {
		provider = new RequiredClassesAnnotationProvider<>();
		ClasspathUtils.setHelper(helper);
	}

	@After
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
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(true));
	}

	@Test
	public void severalRequiredClasses() {
		when(helper.exists(eq("class.required.1"))).thenReturn(false, true, false, true, false, true, false, true);
		when(helper.exists(eq("class.required.2"))).thenReturn(/*false,*/ false, /*true,*/ true, /*false,*/ false, /*true,*/ true);
		when(helper.exists(eq("class.required.3"))).thenReturn(/*false,*/ false, /*false,*/ /*false,*/ /*true,*/ true /*true,*/ /*true*/);
		Condition<Message> condition = provider.provide(SeveralRequiredClasses.class.getAnnotation(RequiredClasses.class));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(true));
	}

	@Test
	public void oneRequiredClassWithAlternatives() {
		when(helper.exists(eq("class.required.1"))).thenReturn(false, true, false, true, false, true, false, true);
		when(helper.exists(eq("class.alt.1"))).thenReturn(/*false,*/ false, /*true,*/ true, /*false,*/ false, /*true,*/ true);
		when(helper.exists(eq("class.alt.2"))).thenReturn(/*false,*/ false, /*false,*/ /*false,*/ /*true,*/ true /*true,*/ /*true*/);
		Condition<Message> condition = provider.provide(OneRequiredClassWithAlternatives.class.getAnnotation(RequiredClasses.class));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
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
			assertThat(condition.accept(message), is(false));
		}
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(true));
	}

	@Test
	public void oneRequiredClassWithExcludes() {
		when(helper.exists(eq("class.required.1"))).thenReturn(false, false, false, false, true, true, true, true);
		when(helper.exists(eq("class.exclude.1"))).thenReturn(/*false, false, true, true,*/ false, false, true, true);
		when(helper.exists(eq("class.exclude.2"))).thenReturn(/*false, true, false, true,*/ false, true /*false, true*/);
		Condition<Message> condition = provider.provide(OneRequiredClassWithExcludes.class.getAnnotation(RequiredClasses.class));
		for(int i=0 ; i<4 ; i++) {
			assertThat(condition.accept(message), is(false));
		}
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
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
			assertThat(condition.accept(message), is(false));
		}
		assertThat(condition.accept(message), is(true));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
		assertThat(condition.accept(message), is(false));
	}

	@Test
	@Ignore("TODO")
	public void allPossibilities() {
		Condition<Message> condition = provider.provide(AllPossibilities.class.getAnnotation(RequiredClasses.class));
		// @formatter:off
		Assert.assertEquals("Should generate "
				+ "'class.required.2' and "
				+ "'class.required.3' and "
				+ "('class.required.1' and not ('class.exclude.1' or 'class.exclude.2')) and "
				+ "('class.required.4' or 'class.alt.1' or 'class.alt.2') and"
				+ "('class.required.5' or 'class.alt.3' or 'class.alt.4' and not ('class.exclude.3' or 'class.exclude.4')) and"
				+ "'class.required.6'", 
				new AndCondition<>(															// and for required classes that comes from @RequiredClasses#value()
					new RequiredClassCondition<>("class.required.2"),
					new RequiredClassCondition<>("class.required.3"),
					new AndCondition<>(														// and for @RequiredClass number 1
							new OrCondition<>(
									new RequiredClassCondition<>("class.required.1")),		// or for @RequiredClass#alternatives()
							new NotCondition<>(new OrCondition<>(							// not for @RequiredClass#excludes()
									new RequiredClassCondition<>("class.exclude.1"),
									new RequiredClassCondition<>("class.exclude.2")))),
					new AndCondition<>(														// and for @RequiredClass number 2
							new OrCondition<>(
									new RequiredClassCondition<>("class.alt.1"),			// or for @RequiredClass#alternatives()
									new RequiredClassCondition<>("class.alt.2"),
									new RequiredClassCondition<>("class.required.4")),
							new NotCondition<>(new OrCondition<>())),						// not for @RequiredClass#excludes()
					new AndCondition<>(														// and for @RequiredClass number 3
							new OrCondition<>(
									new RequiredClassCondition<>("class.alt.3"),			// or for @RequiredClass#alternatives()
									new RequiredClassCondition<>("class.alt.4"),
									new RequiredClassCondition<>("class.required.5")),
							new NotCondition<>(new OrCondition<>(							// not for @RequiredClass#excludes()
									new RequiredClassCondition<>("class.exclude.3"),
									new RequiredClassCondition<>("class.exclude.4")))),
					new AndCondition<>(														// and for @RequiredClass number 4
							new OrCondition<>(
									new RequiredClassCondition<>("class.required.6")),		// or for @RequiredClass#alternatives()
							new NotCondition<>(new OrCondition<>()))),						// not for @RequiredClass#excludes()
				condition);
		// @formatter:on
	}
}
