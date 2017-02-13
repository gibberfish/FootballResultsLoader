package mindbadger.footballresults.loader.apps;

import mindbadger.footballresults.loader.FootballResultsLoader;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LoadSeasonFromXMLApplication {

//	public static void main(String[] args) {
//		ApplicationContext context = new ClassPathXmlApplicationContext("spring-xml-reader.xml");
//
//		Integer season = Integer.parseInt(args[0]);
//		
//		FootballResultsLoader loader = (FootballResultsLoader) context.getBean("loader");
//		loader.loadResultsForSeason(season);
//		
//		((ConfigurableApplicationContext)context).close();
//	}
}
