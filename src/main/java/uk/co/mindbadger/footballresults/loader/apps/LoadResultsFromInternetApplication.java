package uk.co.mindbadger.footballresults.loader.apps;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoader;

public class LoadResultsFromInternetApplication {

	public static void main(String[] args) {
		System.out.println("About to configure Spring");
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-web-reader.xml");

		System.out.println("About to get the loader");
		FootballResultsLoader<String,String,String> loader = (FootballResultsLoader<String,String,String>) context.getBean("loader");
		
		System.out.println("About to call the loader");
		loader.loadResultsForRecentlyPlayedFixtures();
	}
}
