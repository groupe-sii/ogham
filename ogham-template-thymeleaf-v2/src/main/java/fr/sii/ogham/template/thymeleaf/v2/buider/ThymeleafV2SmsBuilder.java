package fr.sii.ogham.template.thymeleaf.v2.buider;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.template.thymeleaf.common.adapter.FileResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.TemplateResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafBuilder;
import fr.sii.ogham.template.thymeleaf.v2.ThymeLeafV2FirstSupportingTemplateResolver;
import fr.sii.ogham.template.thymeleaf.v2.ThymeleafV2TemplateDetector;
import fr.sii.ogham.template.thymeleaf.v2.adapter.FixClassPathResolverAdapter;
import fr.sii.ogham.template.thymeleaf.v2.adapter.StringResolverAdapter;
import fr.sii.ogham.template.thymeleaf.v2.adapter.ThymeleafV2TemplateOptionsApplier;

/**
 * Configures parsing of templates using Thymeleaf.
 * 
 * Specific resource resolution can be configured to use template prefix/suffix
 * paths:
 * 
 * <pre>
 * <code>
 * .classpath()
 *   .pathPrefix("email/")
 *   .pathSuffix(".html")
 *   .and()
 * .file()
 *   .pathPrefix("/data/myapplication/templates/email")
 *   .pathSuffix(".html")
 * </code>
 * </pre>
 * 
 * You can customize default Thymeleaf {@link TemplateEngine}:
 * 
 * <pre>
 * <code>
 * .engine()
 *   .addDialect("foo", myDialect)
 *   .addMessageResolver(myMessageResolver)
 * </code>
 * </pre>
 * 
 * Or you can use a particular Thymeleaf {@link TemplateEngine}:
 * 
 * <pre>
 * <code>
 * .engine(new MyTemplateEngine())
 * </code>
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafV2SmsBuilder extends AbstractThymeleafBuilder<ThymeleafV2SmsBuilder, SmsBuilder, ThymeleafV2EngineConfigBuilder<ThymeleafV2SmsBuilder>> {
	/**
	 * Default constructor when using Thymeleaf without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public ThymeleafV2SmsBuilder() {
		super(ThymeleafV2SmsBuilder.class);
	}

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public ThymeleafV2SmsBuilder(SmsBuilder parent, BuildContext buildContext) {
		super(ThymeleafV2SmsBuilder.class, parent, buildContext);
	}

	@Override
	protected TemplateEngineDetector createTemplateDetector() {
		return buildContext.register(new ThymeleafV2TemplateDetector(buildResolver()));
	}

	@Override
	protected ITemplateResolver buildTemplateResolver(TemplateEngine builtEngine) {
		return buildContext.register(new ThymeLeafV2FirstSupportingTemplateResolver(buildResolver(), buildAdapters()));
	}

	@Override
	protected ThymeleafV2EngineConfigBuilder<ThymeleafV2SmsBuilder> getThymeleafEngineConfigBuilder() {
		return buildContext.register(new ThymeleafV2EngineConfigBuilder<>(myself, buildContext));
	}

	@Override
	protected FirstSupportingResolverAdapter buildAdapters() {
		FirstSupportingResolverAdapter adapter = buildContext.register(new FirstSupportingResolverAdapter());
		for (TemplateResolverAdapter custom : customAdapters) {
			adapter.addAdapter(custom);
		}
		ThymeleafV2TemplateOptionsApplier applier = buildContext.register(new ThymeleafV2TemplateOptionsApplier());
		adapter.addAdapter(buildContext.register(new FixClassPathResolverAdapter(applier)));
		adapter.addAdapter(buildContext.register(new FileResolverAdapter(applier)));
		adapter.addAdapter(buildContext.register(new StringResolverAdapter(applier)));
		adapter.setOptions(buildTemplateResolverOptions());
		return adapter;
	}
}
