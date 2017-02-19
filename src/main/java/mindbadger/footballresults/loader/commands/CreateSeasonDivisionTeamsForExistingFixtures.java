package mindbadger.footballresults.loader.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import mindbadger.footballresultsanalyser.domain.Fixture;
import mindbadger.footballresultsanalyser.domain.Season;
import mindbadger.footballresultsanalyser.domain.SeasonDivision;
import mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import mindbadger.footballresultsanalyser.repository.FixtureRepository;
import mindbadger.footballresultsanalyser.repository.SeasonRepository;

@Component
public class CreateSeasonDivisionTeamsForExistingFixtures implements Command {

	@Autowired
	private FixtureRepository fixtureRepository;
	
	@Autowired
	private SeasonRepository seasonRepository;
	
	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@Override
	public void run(String[] args) throws Exception {
		for (Fixture fixture : fixtureRepository.findAll()) {
			Season season = seasonRepository.findMatching(fixture.getSeason());

			SeasonDivision seasonDivision = seasonRepository.getSeasonDivision(fixture.getSeason(), fixture.getDivision());
			if (seasonDivision == null) {
				seasonDivision = domainObjectFactory.createSeasonDivision(season, fixture.getDivision(), 0);
				season.getSeasonDivisions().add(seasonDivision);
			}
			
			SeasonDivisionTeam seasonDivisionTeam = seasonRepository.getSeasonDivisionTeam(seasonDivision, fixture.getHomeTeam());
			if (seasonDivisionTeam == null) {
				seasonDivisionTeam = domainObjectFactory.createSeasonDivisionTeam(seasonDivision, fixture.getHomeTeam());
				seasonDivision.getSeasonDivisionTeams().add(seasonDivisionTeam);
			}

			seasonDivisionTeam = seasonRepository.getSeasonDivisionTeam(seasonDivision, fixture.getAwayTeam());
			if (seasonDivisionTeam == null) {
				seasonDivisionTeam = domainObjectFactory.createSeasonDivisionTeam(seasonDivision, fixture.getAwayTeam());
				seasonDivision.getSeasonDivisionTeams().add(seasonDivisionTeam);
			}

			seasonRepository.save(season);
		}
	}
}
