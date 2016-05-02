package uk.co.mindbadger.footballresults.loader.apps;

import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivision;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;

public class PrintSeasonShape {

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-xml-reader.xml");

		//FootballResultsLoader<String,String,String> loader = (FootballResultsLoader<String,String,String>) context.getBean("loader");
		
		FootballResultsAnalyserDAO dao = (FootballResultsAnalyserDAO) context.getBean("dao");
		dao.startSession();

		List<Season> seasons = dao.getSeasons();
		
		for (Season season : seasons) {
			System.out.println("Season: " + season.getSeasonNumber());
			
			Set<SeasonDivision> seasonDivisions = dao.getDivisionsForSeason(season);
			
			System.out.println("(" + seasonDivisions.size() + " divisions:");
			
			for (SeasonDivision seasonDivision : seasonDivisions) {
				Division division = dao.getDivision(seasonDivision.getDivision().getDivisionId());
				System.out.println("  Division: " + division.getDivisionName());
				
				Set<SeasonDivisionTeam> seasonDivisionTeams = dao.getTeamsForDivisionInSeason(seasonDivision);
				
				System.out.println("  (" + seasonDivisionTeams.size() + " teams:");
				
				for (SeasonDivisionTeam seasonDivisionTeam : seasonDivisionTeams) {
					Team team = dao.getTeam(seasonDivisionTeam.getTeam().getTeamIdAsString());
					System.out.println("    Team: " + team.getTeamName());
				}
			}
		}
		
		dao.closeSession();
	}

}
