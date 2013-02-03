package uk.co.mindbadger.footballresults.loader.apps;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoader;

public class GetLatestResultsFromInternetApplication {

	public static void main(String[] args) {
      ApplicationContext context = new ClassPathXmlApplicationContext("spring-web-reader.xml");

      FootballResultsLoader loader = (FootballResultsLoader) context.getBean("loader");
		loader.loadResultsForSeason(2005);
	}
}
