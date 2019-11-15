package fr.sii.ogham.spring.v1.template;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.thymeleaf.spring4.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;

import fr.sii.ogham.spring.email.OghamEmailProperties;
import fr.sii.ogham.spring.sms.OghamSmsProperties;
import fr.sii.ogham.spring.template.OghamCommonTemplateProperties;
import fr.sii.ogham.spring.template.OghamThymeleafProperties;
import fr.sii.ogham.spring.template.ThymeLeafConfigurer;
import fr.sii.ogham.spring.template.thymeleaf.ContextMerger;
import fr.sii.ogham.spring.template.thymeleaf.RequestContextHolderWebContextProvider;
import fr.sii.ogham.spring.template.thymeleaf.SpringStandaloneThymeleafContextConverter;
import fr.sii.ogham.spring.template.thymeleaf.SpringWebThymeleafContextConverter;
import fr.sii.ogham.spring.template.thymeleaf.StaticVariablesProvider;
import fr.sii.ogham.spring.template.thymeleaf.TemplateEngineSupplier;
import fr.sii.ogham.spring.template.thymeleaf.ThymeleafEvaluationContextProvider;
import fr.sii.ogham.spring.template.thymeleaf.WebContextProvider;
import fr.sii.ogham.spring.v1.template.thymeleaf.NoOpRequestContextWrapper;
import fr.sii.ogham.spring.v1.template.thymeleaf.SpringWebContextProvider;
import fr.sii.ogham.spring.v1.template.thymeleaf.UpdateCurrentContextMerger;
import fr.sii.ogham.template.thymeleaf.common.SimpleThymeleafContextConverter;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafContextConverter;
import fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2EmailBuilder;
import fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2SmsBuilder;

@Configuration
@ConditionalOnClass({org.thymeleaf.spring4.SpringTemplateEngine.class, fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2EmailBuilder.class})
@EnableConfigurationProperties(OghamThymeleafProperties.class)
public class OghamThymeleafV2Configuration {
	@Bean
	@ConditionalOnMissingBean(TemplateEngineSupplier.class)
	public TemplateEngineSupplier oghamTemplateEngineSupplier(@Autowired(required=false) org.thymeleaf.spring4.SpringTemplateEngine springTemplateEngine) {
		return () -> springTemplateEngine;
	}

	@Bean
	@ConditionalOnMissingBean(ThymeleafContextConverter.class)
	@ConditionalOnNotWebApplication
	public ThymeleafContextConverter springStandaloneThymeleafContextConverter(
			StaticVariablesProvider staticVariablesProvider, 
			ThymeleafEvaluationContextProvider thymeleafEvaluationContextProvider,
			ContextMerger contextMerger) {
		return springThymeleafContextConverter(staticVariablesProvider, thymeleafEvaluationContextProvider, contextMerger);
	}

	@Bean
	@ConditionalOnMissingBean(ThymeleafContextConverter.class)
	@ConditionalOnWebApplication
	public ThymeleafContextConverter springWebThymeleafContextConverter(
			StaticVariablesProvider staticVariablesProvider, 
			ThymeleafEvaluationContextProvider thymeleafEvaluationContextProvider,
			ContextMerger contextMerger,
			ApplicationContext applicationContext,
			WebContextProvider webContextProvider) {
		return new SpringWebThymeleafContextConverter(
				springThymeleafContextConverter(staticVariablesProvider, thymeleafEvaluationContextProvider, contextMerger), 
				SpringContextVariableNames.SPRING_REQUEST_CONTEXT, 
				applicationContext, 
				webContextProvider,
				new NoOpRequestContextWrapper(), 
				new SpringWebContextProvider(),
				contextMerger);
	}
	
	@Bean
	@ConditionalOnWebApplication
	public WebContextProvider webContextProvider() {
		return new RequestContextHolderWebContextProvider();
	}

	@Bean
	@ConditionalOnMissingBean(ThymeleafEvaluationContextProvider.class)
	public ThymeleafEvaluationContextProvider springThymeleafEvaluationContextProvider(
			OghamThymeleafProperties props,
			BeanFactory beanFactory, 
			@Autowired(required=false) ConversionService conversionService) {
		if (props.isEnableSpringBeans()) {
			return c -> new ThymeleafEvaluationContext(beanFactory, conversionService);
		}
		return c -> null;
	}

	@Bean
	@ConditionalOnMissingBean(StaticVariablesProvider.class)
	public StaticVariablesProvider springThymeleafStaticVariablesProvider() {
		return c -> null;
	}
	
	@Bean
	@ConditionalOnMissingBean(ContextMerger.class)
	public ContextMerger contextMerger() {
		return new UpdateCurrentContextMerger();
	}

	@Bean
	@ConditionalOnMissingBean(ThymeLeafConfigurer.class)
	public ThymeLeafConfigurer thymeleafConfigurer(
			TemplateEngineSupplier springTemplateEngineSupplier,
			@Autowired(required=false) ThymeleafContextConverter contextConverter,
			@Autowired(required=false) OghamCommonTemplateProperties templateProperties,
			@Autowired(required=false) OghamEmailProperties emailProperties,
			@Autowired(required=false) OghamSmsProperties smsProperties,
			@Autowired(required=false) ThymeleafProperties thymeleafProperties) {
		return new ThymeLeafConfigurer(
				springTemplateEngineSupplier, 
				contextConverter, 
				templateProperties, 
				emailProperties, 
				smsProperties, 
				thymeleafProperties, 
				ThymeleafV2EmailBuilder.class, 
				ThymeleafV2SmsBuilder.class);
	}

	private ThymeleafContextConverter springThymeleafContextConverter(
			StaticVariablesProvider staticVariablesProvider, 
			ThymeleafEvaluationContextProvider thymeleafEvaluationContextProvider,
			ContextMerger contextMerger) {
		return new SpringStandaloneThymeleafContextConverter(
				new SimpleThymeleafContextConverter(), 
				ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, 
				staticVariablesProvider, 
				thymeleafEvaluationContextProvider,
				contextMerger);
	}

}