package mock.condition.annotation;

import fr.sii.ogham.core.builder.condition.RequiredClass;
import fr.sii.ogham.core.builder.condition.RequiredClasses;

@RequiredClasses(classes=@RequiredClass(value="class.required.1", excludes={"class.exclude.1", "class.exclude.2"}))
public class OneRequiredClassWithExcludes {

}
