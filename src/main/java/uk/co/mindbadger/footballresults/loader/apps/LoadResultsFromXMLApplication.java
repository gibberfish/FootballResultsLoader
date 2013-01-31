package uk.co.mindbadger.footballresults.loader.apps;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoader;
import uk.co.mindbadger.footballresults.reader.FootballResultsReader;

public class LoadResultsFromXMLApplication {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-xml-reader.xml");

		FootballResultsLoader loader = (FootballResultsLoader) context.getBean("loader");
		loader.loadResultsForSeason(2003);
	}
}
