package mock.condition.annotation;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;

@RequiredClasses(classes=@RequiredClass(value="class.required.1", alternatives={"class.alt.1", "class.alt.2"}))
public class OneRequiredClassWithAlternatives {

}
