package mock.condition.annotation;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;

@RequiredClasses(classes=@RequiredClass(value="class.required.1", excludes={"class.exclude.1", "class.exclude.2"}))
public class OneRequiredClassWithExcludes {

}
