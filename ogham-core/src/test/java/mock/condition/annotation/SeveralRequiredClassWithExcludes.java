package mock.condition.annotation;

import fr.sii.ogham.core.builder.condition.RequiredClass;
import fr.sii.ogham.core.builder.condition.RequiredClasses;

@RequiredClasses(value={"class.required.2", "class.required.3"}, classes=@RequiredClass(value="class.required.1", excludes={"class.exclude.1", "class.exclude.2"}))
public class SeveralRequiredClassWithExcludes {

}
