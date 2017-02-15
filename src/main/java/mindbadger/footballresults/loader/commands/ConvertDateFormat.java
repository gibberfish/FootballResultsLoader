package mindbadger.footballresults.loader.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.Fixture;

@Component
public class ConvertDateFormat implements Command {

	@Autowired
	private FootballResultsAnalyserDAO dao;
	
	@Override
	public void run(String[] args) throws Exception {
		dao.startSession();
		
		List<Fixture> fixtures = dao.getFixtures();
		for (Fixture fixture : fixtures) {
			
			Fixture fixtureAdded = dao.addFixture(fixture.getSeason(), fixture.getFixtureDate(), fixture.getDivision(),
					fixture.getHomeTeam(), fixture.getAwayTeam(), fixture.getHomeGoals(), fixture.getAwayGoals());
		}
		
		dao.closeSession();
	}
}
