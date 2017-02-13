package mindbadger.footballresults.loader.apps;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.Fixture;

@Component
public class ConvertDateFormatApplication {

//	public static void main(String[] args) {
//		ApplicationContext context = new ClassPathXmlApplicationContext("spring-web-reader.xml");
//
//		FootballResultsAnalyserDAO dao = (FootballResultsAnalyserDAO) context.getBean("dao");
//		dao.startSession();
//		
//		List<Fixture> fixtures = dao.getFixtures();
//		for (Fixture fixture : fixtures) {
//			
//			Fixture fixtureAdded = dao.addFixture(fixture.getSeason(), fixture.getFixtureDate(), fixture.getDivision(),
//					fixture.getHomeTeam(), fixture.getAwayTeam(), fixture.getHomeGoals(), fixture.getAwayGoals());
//		}
//		
//		dao.closeSession();
//		((ConfigurableApplicationContext) context).close();
//	}
}
