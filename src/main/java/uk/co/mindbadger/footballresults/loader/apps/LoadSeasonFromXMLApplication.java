package uk.co.mindbadger.footballresults.loader.apps;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoader;

public class LoadSeasonFromXMLApplication {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-xml-reader.xml");

		Integer season = Integer.parseInt(args[0]);
		
		FootballResultsLoader<String,String,String> loader = (FootballResultsLoader<String,String,String>) context.getBean("loader");
		loader.loadResultsForSeason(season);
	}
}
