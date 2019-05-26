package fr.aba.ogham.documentation.generator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GenerateWiki implements ApplicationRunner {
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if(args.containsOption("wiki")) {
			generate();
		}
	}

	private void generate() {
		// TODO Auto-generated method stub
		
	}
}
