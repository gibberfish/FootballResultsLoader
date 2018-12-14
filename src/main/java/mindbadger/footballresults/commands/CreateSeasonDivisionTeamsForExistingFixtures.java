package mindbadger.footballresults.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.football.domain.DomainObjectFactory;
import mindbadger.football.domain.Fixture;
import mindbadger.football.domain.Season;
import mindbadger.football.domain.SeasonDivision;
import mindbadger.football.domain.SeasonDivisionTeam;
import mindbadger.football.repository.FixtureRepository;
import mindbadger.football.repository.SeasonRepository;

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
			Season season = seasonRepository.findMatching(fixture.getSeasonDivision().getSeason());

			SeasonDivision seasonDivision = seasonRepository.getSeasonDivision(fixture.getSeasonDivision().getSeason(), fixture.getSeasonDivision().getDivision());
			if (seasonDivision == null) {
				seasonDivision = domainObjectFactory.createSeasonDivision(season, fixture.getSeasonDivision().getDivision(), 0);
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
