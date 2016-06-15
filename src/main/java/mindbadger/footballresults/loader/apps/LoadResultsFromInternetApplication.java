package mindbadger.footballresults.loader.apps;

import mindbadger.footballresults.loader.FootballResultsLoader;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LoadResultsFromInternetApplication {

	public static void main(String[] args) {
		System.out.println("About to configure Spring");
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-web-reader.xml");

		System.out.println("About to get the loader");
		FootballResultsLoader loader = (FootballResultsLoader) context.getBean("loader");
		
		System.out.println("About to call the loader");
		loader.loadResultsForRecentlyPlayedFixtures();
	}
}
