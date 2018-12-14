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
public class CreateSeasonShape implements Command {
	private static final Logger log = LoggerFactory.getLogger(CreateSeasonShape.class);
	
	@Autowired
	private SeasonRepository seasonRepository;
	@Autowired
	private FixtureRepository fixtureRepository;
	@Autowired
	private DomainObjectFactory domainObjectFactory;

	//TODO Implement this method properly
	@Override
	public void run(String[] args) throws Exception {
		
		if (args.length < 2)
			throw new IllegalArgumentException("Please supply a season");
		
		Integer seasonNumber = Integer.parseInt(args[1]);
		Season season = seasonRepository.findOne(seasonNumber);
		
		Iterable<Fixture> allFixtures = fixtureRepository.findAll();
		
		for (Fixture fixture : allFixtures) {
			if (fixture.getSeasonDivision().getSeason().getSeasonNumber() == seasonNumber) {
				
			}
		}		
	}
}
