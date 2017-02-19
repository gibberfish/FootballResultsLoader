package mindbadger.footballresults.loader.commands;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresultsanalyser.domain.Division;
import mindbadger.footballresultsanalyser.domain.Season;
import mindbadger.footballresultsanalyser.domain.SeasonDivision;
import mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import mindbadger.footballresultsanalyser.domain.Team;
import mindbadger.footballresultsanalyser.repository.SeasonRepository;

@Component
public class PrintSeasonShape implements Command {

	@Autowired
	private SeasonRepository seasonRepository;

	@Override
	public void run(String[] args) throws Exception {
		Iterable<Season> seasons = seasonRepository.findAll();
		
		for (Season season : seasons) {
			System.out.println("Season: " + season.getSeasonNumber());
			
			Set<SeasonDivision> seasonDivisions = season.getSeasonDivisions();
			
			System.out.println("(" + seasonDivisions.size() + " divisions:");
			
			for (SeasonDivision seasonDivision : seasonDivisions) {
				Division division = seasonDivision.getDivision();
				System.out.println("  Division: " + division.getDivisionName());
				
				Set<SeasonDivisionTeam> seasonDivisionTeams = seasonDivision.getSeasonDivisionTeams();
				
				System.out.println("  (" + seasonDivisionTeams.size() + " teams:");
				
				for (SeasonDivisionTeam seasonDivisionTeam : seasonDivisionTeams) {
					Team team = seasonDivisionTeam.getTeam();
					System.out.println("    Team: " + team.getTeamName());
				}
			}
		}
	}
}
