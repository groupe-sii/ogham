package fr.sii.ogham.test.classpath.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.test.classpath.core.ProjectInitializer;
import fr.sii.ogham.test.classpath.ogham.OghamDependency;
import fr.sii.ogham.test.classpath.runner.common.ParallelProjectsCreator;
import fr.sii.ogham.test.classpath.runner.common.ProjectsCreator;
import fr.sii.ogham.test.classpath.runner.common.SequentialProjectsCreator;
import fr.sii.ogham.test.classpath.runner.springboot.RunnerProperties;
import fr.sii.ogham.test.classpath.runner.standalone.StandaloneProjectParams;
import fr.sii.ogham.test.classpath.runner.standalone.StandaloneSingleProjectCreator;
import fr.sii.ogham.test.classpath.runner.standalone.TemplatedProjectInitializer;

@Configuration
public class StandaloneRunnerConfig {
	@Bean
	public ProjectInitializer<StandaloneProjectParams> standaloneProjectInitializer() {
		freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_28);
		cfg.setClassForTemplateLoading(TemplatedProjectInitializer.class, "/");
		cfg.setDefaultEncoding("UTF-8");
		return new TemplatedProjectInitializer(cfg);
	}
	
	@Bean
	public ProjectsCreator<StandaloneProjectParams, OghamDependency> standaloneProjectsCreator(RunnerProperties props, StandaloneSingleProjectCreator projectCreator) {
		if(props.isParallel()) {
			return new ParallelProjectsCreator<>(projectCreator, props.getNumThreads());
		} else {
			return new SequentialProjectsCreator<>(projectCreator);
		}
	}

}
