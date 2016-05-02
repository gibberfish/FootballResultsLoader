package uk.co.mindbadger.footballresults.loader.apps;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivision;

public class CreateSeasonDivisionTeamsForExistingFixtures {

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-xml-reader.xml");

		FootballResultsAnalyserDAO dao = (FootballResultsAnalyserDAO) context.getBean("dao");
		dao.startSession();
		
		List<Fixture> fixtures = dao.getFixtures();
		
		for (Fixture fixture : fixtures) {
			SeasonDivision seasonDivision = dao.addSeasonDivision(fixture.getSeason(), fixture.getDivision(), 0);
			dao.addSeasonDivisionTeam(seasonDivision, fixture.getHomeTeam());
			dao.addSeasonDivisionTeam(seasonDivision, fixture.getAwayTeam());
		}
		
		dao.closeSession();
	}

}
