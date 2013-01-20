package uk.co.mindbadger.footballresults.reader;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GetLatestResultsFromInternetApplication {

	public static void main(String[] args) {
      ApplicationContext context = 
            new ClassPathXmlApplicationContext("spring-web-reader.xml");

      FootballResultsReader reader =  (FootballResultsReader) context.getBean("reader");
      reader.readFixturesForSeason(2000);
	}
}
