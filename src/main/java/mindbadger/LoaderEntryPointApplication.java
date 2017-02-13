package mindbadger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages="mindbadger")
public class LoaderEntryPointApplication {


	@Autowired
	IntegrationTest integrationTest;

	private static final Logger log = LoggerFactory.getLogger(LoaderEntryPointApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(LoaderEntryPointApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			if (args.length > 0) {
				String command = args[0];
				
				if ("INT_TEST".equals(command)) {
					integrationTest.runIntegrationTest();
				} else {
					log.info("*** SORRY, I DON'T RECOGNISE COMMAND " + command + " ***");
				}
				
			} else {
				log.info("*** PLEASE SPECIFY A COMMAND TO RUN ***");
			}
			
		};
	}
}
