package mindbadger.footballresults.loader.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.Division;
import mindbadger.footballresultsanalyser.domain.Season;
import mindbadger.footballresultsanalyser.domain.SeasonDivision;
import mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import mindbadger.footballresultsanalyser.domain.Team;

@Component
public class PrintSeasonShape implements Command {
	@Autowired
	private FootballResultsAnalyserDAO dao;

	@Override
	public void run(String[] args) throws Exception {
		dao.startSession();

		List<Season> seasons = dao.getSeasons();
		
		for (Season season : seasons) {
			System.out.println("Season: " + season.getSeasonNumber());
			
			List<SeasonDivision> seasonDivisions = dao.getDivisionsForSeason(season);
			
			System.out.println("(" + seasonDivisions.size() + " divisions:");
			
			for (SeasonDivision seasonDivision : seasonDivisions) {
				Division division = dao.getDivision(seasonDivision.getDivision().getDivisionId());
				System.out.println("  Division: " + division.getDivisionName());
				
				List<SeasonDivisionTeam> seasonDivisionTeams = dao.getTeamsForDivisionInSeason(seasonDivision);
				
				System.out.println("  (" + seasonDivisionTeams.size() + " teams:");
				
				for (SeasonDivisionTeam seasonDivisionTeam : seasonDivisionTeams) {
					Team team = dao.getTeam(seasonDivisionTeam.getTeam().getTeamId());
					System.out.println("    Team: " + team.getTeamName());
				}
			}
		}
		
		dao.closeSession();		
	}
}
