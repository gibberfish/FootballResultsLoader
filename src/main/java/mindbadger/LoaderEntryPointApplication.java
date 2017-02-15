package mindbadger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import mindbadger.footballresults.loader.commands.Command;
import mindbadger.footballresults.loader.commands.ImportDataFromJSONToDB;
import mindbadger.footballresults.loader.commands.IntegrationTest;
import mindbadger.footballresults.loader.commands.LoadRecentResultsFromInternet;
import mindbadger.footballresults.loader.commands.PrintSeasonShape;

@SpringBootApplication(scanBasePackages="**/mindbadger/**/*")
public class LoaderEntryPointApplication {

	@Autowired
	IntegrationTest integrationTest;
	
	@Autowired
	ImportDataFromJSONToDB importDataFromJSONToDB;

	@Autowired
	PrintSeasonShape printSeasonShape;
	
	@Autowired
	LoadRecentResultsFromInternet loadResultsFromInternetApplication;
	
	private static final Logger log = LoggerFactory.getLogger(LoaderEntryPointApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(LoaderEntryPointApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			if (args.length > 0) {
				String commandString = args[0];
				log.info("Command to run: " + commandString);
				
				Command command = null;
				
				if ("INT_TEST".equals(commandString)) {
					command = integrationTest;
				} else if ("IMPORT_FROM_JSON".equals(commandString)) {
					command = importDataFromJSONToDB;
				} else if ("PRINT_SEASON".equals(commandString)) {
					command = printSeasonShape;
				} else if ("LOAD_RESULTS".equals(commandString)) {
					command = loadResultsFromInternetApplication;
				} else {
					throw new IllegalArgumentException("*** SORRY, I DON'T RECOGNISE COMMAND " + commandString + " ***");
				}
				
				command.run(args);
				
			} else {
				throw new IllegalArgumentException("*** PLEASE SPECIFY A COMMAND TO RUN ***");
			}
			
		};
	}
}
