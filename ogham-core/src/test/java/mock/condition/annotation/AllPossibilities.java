package mock.condition.annotation;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;

@RequiredClasses(
		value={"class.required.2", "class.required.3"},
		classes= {
				@RequiredClass(value="class.required.1", excludes={"class.exclude.1", "class.exclude.2"}),
				@RequiredClass(value="class.required.4", alternatives={"class.alt.1", "class.alt.2"}),
				@RequiredClass(value="class.required.5", alternatives={"class.alt.3", "class.alt.4"}, excludes={"class.exclude.3", "class.exclude.4"}),
				@RequiredClass(value="class.required.6"),
		})
public class AllPossibilities {

}
