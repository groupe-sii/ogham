package fr.sii.ogham.ut.core.condition.provider;

import mock.condition.annotation.AllPossibilities;
import mock.condition.annotation.NoAnnotation;
import mock.condition.annotation.OneRequiredClass;
import mock.condition.annotation.OneRequiredClassWithAlternatives;
import mock.condition.annotation.OneRequiredClassWithExcludes;
import mock.condition.annotation.SeveralRequiredClassWithAlternatives;
import mock.condition.annotation.SeveralRequiredClassWithExcludes;
import mock.condition.annotation.SeveralRequiredClasses;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.NotCondition;
import fr.sii.ogham.core.condition.OrCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;
import fr.sii.ogham.core.condition.provider.RequiredClassesAnnotationProvider;

public class RequiredClassesAnnotationProviderTest {

	private RequiredClassesAnnotationProvider provider;

	@Before
	public void setUp() {
		provider = new RequiredClassesAnnotationProvider();
	}

	@Test
	public void nothingSpecified() {
		Condition<Class<?>> condition = provider.provide(NoAnnotation.class);
		Assert.assertEquals("No annotation should allow the class to be used", new FixedCondition<>(true), condition);
	}

	@Test
	public void singleRequiredClass() {
		Condition<Class<?>> condition = provider.provide(OneRequiredClass.class);
		// @formatter:off
		Assert.assertEquals("Should generate 'class.required.1'", 
				new AndCondition<>(new RequiredClassCondition<>("class.required.1")), 
				condition);
		// @formatter:on
	}

	@Test
	public void severalRequiredClasses() {
		Condition<Class<?>> condition = provider.provide(SeveralRequiredClasses.class);
		// @formatter:off
		Assert.assertEquals("Should generate 'class.required.1' and 'class.required.2' and 'class.required.3'", 
				new AndCondition<>(
					new RequiredClassCondition<>("class.required.1"),
					new RequiredClassCondition<>("class.required.2"),
					new RequiredClassCondition<>("class.required.3")),
				condition);
		// @formatter:on
	}

	@Test
	public void oneRequiredClassWithAlternatives() {
		Condition<Class<?>> condition = provider.provide(OneRequiredClassWithAlternatives.class);
		// @formatter:off
		Assert.assertEquals("Should generate 'class.required.1' or 'class.alt.1' or 'class.alt.2'", 
				new AndCondition<>(													// and for required classes that comes from @RequiredClasses#value()
					new AndCondition<>(												// and for @RequiredClasses#classes()
						new OrCondition<>(											// or for @RequiredClass#alternatives()
								new RequiredClassCondition<>("class.alt.1"),
								new RequiredClassCondition<>("class.alt.2"),
								new RequiredClassCondition<>("class.required.1")),
						new NotCondition<>(new OrCondition<>()))),					// not for @RequiredClass#excludes()
				condition);
		// @formatter:on
	}

	@Test
	public void severalRequiredClassesWithAlternatives() {
		Condition<Class<?>> condition = provider.provide(SeveralRequiredClassWithAlternatives.class);
		// @formatter:off
		Assert.assertEquals("Should generate 'class.required.2' and 'class.required.3' and ('class.required.1' or 'class.alt.1' or 'class.alt.2')", 
				new AndCondition<>(													// and for required classes that comes from @RequiredClasses#value()
					new RequiredClassCondition<>("class.required.2"),
					new RequiredClassCondition<>("class.required.3"),
					new AndCondition<>(												// and for @RequiredClasses#classes()
						new OrCondition<>(											// or for @RequiredClass#alternatives()
								new RequiredClassCondition<>("class.alt.1"),
								new RequiredClassCondition<>("class.alt.2"),
								new RequiredClassCondition<>("class.required.1")),
						new NotCondition<>(new OrCondition<>()))),					// not for @RequiredClass#excludes()
				condition);
		// @formatter:on
	}

	@Test
	public void oneRequiredClassWithExcludes() {
		Condition<Class<?>> condition = provider.provide(OneRequiredClassWithExcludes.class);
		// @formatter:off
		Assert.assertEquals("Should generate 'class.required.1' and not ('class.exclude.1' or 'class.exclude.2')", 
				new AndCondition<>(														// and for required classes that comes from @RequiredClasses#value()
					new AndCondition<>(													// and for @RequiredClasses#classes()
							new OrCondition<>(											// or for @RequiredClass#alternatives()
									new RequiredClassCondition<>("class.required.1")),	
							new NotCondition<>(new OrCondition<>(						// not for @RequiredClass#excludes()
									new RequiredClassCondition<>("class.exclude.1"),
									new RequiredClassCondition<>("class.exclude.2"))))),
				condition);
		// @formatter:on
	}

	@Test
	public void severalRequiredClassesWithExcludes() {
		Condition<Class<?>> condition = provider.provide(SeveralRequiredClassWithExcludes.class);
		// @formatter:off
		Assert.assertEquals("Should generate 'class.required.1' and 'class.required.2' and 'class.required.3' and not ('class.exclude.1' or 'class.exclude.2')", 
				new AndCondition<>(														// and for required classes that comes from @RequiredClasses#value()
					new RequiredClassCondition<>("class.required.2"),
					new RequiredClassCondition<>("class.required.3"),
					new AndCondition<>(													// and for @RequiredClasses#classes()
							new OrCondition<>(
									new RequiredClassCondition<>("class.required.1")),	// or for @RequiredClass#alternatives()
							new NotCondition<>(new OrCondition<>(						// not for @RequiredClass#excludes()
									new RequiredClassCondition<>("class.exclude.1"),
									new RequiredClassCondition<>("class.exclude.2"))))),
				condition);
		// @formatter:on
	}

	@Test
	public void allPossibilities() {
		Condition<Class<?>> condition = provider.provide(AllPossibilities.class);
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
