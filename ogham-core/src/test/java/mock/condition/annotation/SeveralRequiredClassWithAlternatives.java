package mock.condition.annotation;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;

@RequiredClasses(value={"class.required.2", "class.required.3"}, classes=@RequiredClass(value="class.required.1", alternatives={"class.alt.1", "class.alt.2"}))
public class SeveralRequiredClassWithAlternatives {

}
