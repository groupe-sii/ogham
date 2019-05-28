package fr.sii.ogham.test.classpath.runner.clean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(0)
public class CleanerRunner implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		boolean override = args.getOptionValues("override")!=null;
		if(!override) {
			return;
		}
		Path parentFolder = Paths.get(args.getNonOptionArgs().get(0));
		log.info("Cleaning {}", parentFolder);
		Files.createDirectories(parentFolder);
		FileUtils.cleanDirectory(parentFolder.toFile());
	}

}
