package uk.co.mindbadger.footballresults.loader.apps;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoader;

public class LoadSeasonFromInternetApplication {

	public static void main(String[] args) {
      ApplicationContext context = new ClassPathXmlApplicationContext("spring-web-reader.xml");

      Integer season = Integer.parseInt(args[0]);
      
      FootballResultsLoader<String> loader = (FootballResultsLoader<String>) context.getBean("loader");
		loader.loadResultsForSeason(season);
	}
}
