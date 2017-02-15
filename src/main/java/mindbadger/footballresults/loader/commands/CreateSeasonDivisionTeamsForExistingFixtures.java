package mindbadger.footballresults.loader.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.Fixture;
import mindbadger.footballresultsanalyser.domain.SeasonDivision;

@Component
public class CreateSeasonDivisionTeamsForExistingFixtures implements Command {

	@Autowired
	private FootballResultsAnalyserDAO dao;
	
	@Override
	public void run(String[] args) throws Exception {
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
