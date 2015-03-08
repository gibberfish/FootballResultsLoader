package uk.co.mindbadger.footballresults.loader.apps;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoader;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserMongoDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivision;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;

public class CreateSeasonDivisionTeamsForExistingFixtures {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-xml-reader.xml");

		FootballResultsAnalyserDAO<String,String, String> dao = (FootballResultsAnalyserDAO) context.getBean("dao");
		dao.startSession();
		
		List<Fixture<String>> fixtures = dao.getFixtures();
		
		for (Fixture<String> fixture : fixtures) {
			SeasonDivision<String,String> seasonDivision = dao.addSeasonDivision(fixture.getSeason(), fixture.getDivision(), 0);
			dao.addSeasonDivisionTeam(seasonDivision, fixture.getHomeTeam());
			dao.addSeasonDivisionTeam(seasonDivision, fixture.getAwayTeam());
		}
		
		dao.closeSession();
	}

}
