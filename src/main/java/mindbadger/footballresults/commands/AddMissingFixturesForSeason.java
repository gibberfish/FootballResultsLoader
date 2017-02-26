package mindbadger.footballresults.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AddMissingFixturesForSeason implements Command {
	private static final Logger log = LoggerFactory.getLogger(AddMissingFixturesForSeason.class);
	
	@Autowired
	private SeasonRepository seasonRepository;
	@Autowired
	private FixtureRepository fixtureRepository;
	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@Override
	public void run(String[] args) throws Exception {
		
		if (args.length < 2)
			throw new IllegalArgumentException("Please supply a season");
		
		Integer seasonNumber = Integer.parseInt(args[1]);
		
		Season season = seasonRepository.findOne(seasonNumber);
		log.info("Checking for missing fixtures for season: " + season.getSeasonNumber());
		
		for (SeasonDivision seasonDivision : season.getSeasonDivisions()) {
			log.info("Checking for missing fixtures in division: " + seasonDivision.getDivision());
			for (SeasonDivisionTeam seasonDivisionHomeTeam : seasonDivision.getSeasonDivisionTeams()) {
				for (SeasonDivisionTeam seasonDivisionAwayTeam : seasonDivision.getSeasonDivisionTeams()) {
					if (seasonDivisionHomeTeam != seasonDivisionAwayTeam) {
						Fixture fixture = domainObjectFactory.createFixture(season, seasonDivisionHomeTeam.getTeam(), seasonDivisionAwayTeam.getTeam());
						Fixture fixtureFound = fixtureRepository.findMatching(fixture);
						if (fixtureFound == null) {
							fixture.setDivision(seasonDivision.getDivision());
							fixtureRepository.save(fixture);
							log.info("Adding missing fixture: " + fixture);
						}
					}
				}
			}
		}		
	}
}
