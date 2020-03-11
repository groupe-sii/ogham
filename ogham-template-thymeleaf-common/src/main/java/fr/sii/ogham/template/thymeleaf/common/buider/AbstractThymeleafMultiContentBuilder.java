package fr.sii.ogham.template.thymeleaf.common.buider;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.template.VariantBuilder;
import fr.sii.ogham.core.builder.template.VariantWithExt;
import fr.sii.ogham.core.message.content.Variant;
import fr.sii.ogham.template.common.adapter.ExtensionMappingVariantResolver;
import fr.sii.ogham.template.common.adapter.VariantResolver;

@SuppressWarnings("squid:S00119")
public abstract class AbstractThymeleafMultiContentBuilder<MYSELF extends AbstractThymeleafMultiContentBuilder<MYSELF, P, E>, P, E extends AbstractThymeleafEngineConfigBuilder<E, MYSELF>> extends AbstractThymeleafBuilder<MYSELF, P, E> implements VariantBuilder<MYSELF> {
	private List<VariantWithExt> variants;

	protected AbstractThymeleafMultiContentBuilder(Class<?> selfType, P parent, BuildContext buildContext) {
		super(selfType, parent, buildContext);
		variants = new ArrayList<>();
	}

	@Override
	public MYSELF variant(Variant variant, String extension) {
		variants.add(new VariantWithExt(variant, extension));
		return myself;
	}

	@Override
	public VariantResolver buildVariant() {
		return buildExtMappingVariantResolver();
	}

	private ExtensionMappingVariantResolver buildExtMappingVariantResolver() {
		ExtensionMappingVariantResolver resolver = buildContext.register(new ExtensionMappingVariantResolver(buildResolver()));
		for(VariantWithExt v : variants) {
			resolver.register(v.getVariant(), v.getExtension());
		}
		return resolver;
	}
}
