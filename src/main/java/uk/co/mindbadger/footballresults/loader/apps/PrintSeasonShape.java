package uk.co.mindbadger.footballresults.loader.apps;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoader;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivision;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;

public class PrintSeasonShape {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-xml-reader.xml");

		//FootballResultsLoader<String,String,String> loader = (FootballResultsLoader<String,String,String>) context.getBean("loader");
		
		FootballResultsAnalyserDAO<String,String, String> dao = (FootballResultsAnalyserDAO) context.getBean("dao");
		dao.startSession();

		List<Season<String>> seasons = dao.getSeasons();
		
		for (Season<String> season : seasons) {
			System.out.println("Season: " + season.getSeasonNumber());
			
			Set<SeasonDivision<String,String>> seasonDivisions = dao.getDivisionsForSeason(season);
			
			System.out.println("(" + seasonDivisions.size() + " divisions:");
			
			for (SeasonDivision<String,String> seasonDivision : seasonDivisions) {
				Division<String> division = dao.getDivision(seasonDivision.getDivision().getDivisionIdAsString());
				System.out.println("  Division: " + division.getDivisionName());
				
				Set<SeasonDivisionTeam<String,String,String>> seasonDivisionTeams = dao.getTeamsForDivisionInSeason(seasonDivision);
				
				System.out.println("  (" + seasonDivisionTeams.size() + " teams:");
				
				for (SeasonDivisionTeam<String,String,String> seasonDivisionTeam : seasonDivisionTeams) {
					Team<String> team = dao.getTeam(seasonDivisionTeam.getTeam().getTeamIdAsString());
					System.out.println("    Team: " + team.getTeamName());
				}
			}
		}
		
		
		
		dao.closeSession();
	}

}
