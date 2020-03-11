package fr.sii.ogham.template.thymeleaf.v3.buider;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.sms.builder.SmsBuilder;
import fr.sii.ogham.template.thymeleaf.common.adapter.ClassPathResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.FileResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.TemplateResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafBuilder;
import fr.sii.ogham.template.thymeleaf.v3.ThymeLeafV3FirstSupportingTemplateResolver;
import fr.sii.ogham.template.thymeleaf.v3.ThymeleafV3TemplateDetector;
import fr.sii.ogham.template.thymeleaf.v3.adapter.StringResolverAdapter;
import fr.sii.ogham.template.thymeleaf.v3.adapter.ThymeleafV3TemplateOptionsApplier;

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
public class ThymeleafV3SmsBuilder extends AbstractThymeleafBuilder<ThymeleafV3SmsBuilder, SmsBuilder, ThymeleafV3EngineConfigBuilder<ThymeleafV3SmsBuilder>> {
	/**
	 * Default constructor when using Thymeleaf without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public ThymeleafV3SmsBuilder() {
		super(ThymeleafV3SmsBuilder.class);
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
	public ThymeleafV3SmsBuilder(SmsBuilder parent, BuildContext buildContext) {
		super(ThymeleafV3SmsBuilder.class, parent, buildContext);
	}

	@Override
	protected TemplateEngineDetector createTemplateDetector() {
		return buildContext.register(new ThymeleafV3TemplateDetector(buildResolver()));
	}

	@Override
	protected ITemplateResolver buildTemplateResolver(TemplateEngine builtEngine) {
		return buildContext.register(new ThymeLeafV3FirstSupportingTemplateResolver(buildResolver(), buildAdapters()));
	}

	@Override
	protected ThymeleafV3EngineConfigBuilder<ThymeleafV3SmsBuilder> getThymeleafEngineConfigBuilder() {
		return buildContext.register(new ThymeleafV3EngineConfigBuilder<>(myself, buildContext));
	}

	@Override
	protected FirstSupportingResolverAdapter buildAdapters() {
		FirstSupportingResolverAdapter adapter = buildContext.register(new FirstSupportingResolverAdapter());
		for (TemplateResolverAdapter custom : customAdapters) {
			adapter.addAdapter(custom);
		}
		ThymeleafV3TemplateOptionsApplier applier = buildContext.register(new ThymeleafV3TemplateOptionsApplier());
		adapter.addAdapter(buildContext.register(new ClassPathResolverAdapter(applier)));
		adapter.addAdapter(buildContext.register(new FileResolverAdapter(applier)));
		adapter.addAdapter(buildContext.register(new StringResolverAdapter(applier)));
		adapter.setOptions(buildTemplateResolverOptions());
		return adapter;
	}
}
