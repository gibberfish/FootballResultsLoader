package mindbadger.footballresults.loader;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresults.reader.FootballResultsReader;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.football.domain.Fixture;
import mindbadger.football.repository.FixtureRepository;
import mindbadger.util.StringToCalendarConverter;

@Component
public class FootballResultsLoader {
	Logger logger = Logger.getLogger(FootballResultsLoader.class);
	
	@Autowired
	private FootballResultsReader reader;
	@Autowired
	private ParsedResultsSaver saver;
	@Autowired
	private FixtureRepository fixtureRepository;
	
	public void loadResultsForSeason(int seasonNum) {
		List<ParsedFixture> fixturesRead = reader.readFixturesForSeason(seasonNum);
		
		logger.info("Ready to save " + fixturesRead.size() + " fixtures");
		
		saver.saveFixtures(fixturesRead);
	}
	
	public void loadResultsForRecentlyPlayedFixtures() {
		logger.debug("Starting loadResultsForRecentlyPlayedFixtures");
		
		List<Fixture> unplayedFixtures = fixtureRepository.getUnplayedFixturesBeforeToday();
		List<Fixture> fixturesWithoutDates = fixtureRepository.getFixturesWithNoFixtureDate();
		
		logger.debug("loadResultsForRecentlyPlayedFixtures has found " + fixturesWithoutDates.size() + " fixtures without dates");
		
		Map<Calendar, Calendar> uniqueDates = new HashMap<Calendar, Calendar> ();
		for (Fixture fixture : unplayedFixtures) {
			uniqueDates.put(fixture.getFixtureDate(), fixture.getFixtureDate());
		}
		logger.debug("getUnplayedFixturesBeforeToday has found " + uniqueDates.size() + " fixture dates that need updating");
		
		for (Calendar fixtureDate : uniqueDates.keySet()) {
			List<ParsedFixture> parsedFixtures = reader.readFixturesForDate(fixtureDate);
			logger.debug("...got " + parsedFixtures.size() + " fixtures for date " + StringToCalendarConverter.convertCalendarToDateString(fixtureDate));
			saver.saveFixtures(parsedFixtures);
		}
		
		for (Fixture fixture : fixturesWithoutDates) {
			Integer seasonNumber = fixture.getSeason().getSeasonNumber();
			String teamId = fixture.getHomeTeam().getTeamId();
			List<ParsedFixture> parsedFixtures = reader.readFixturesForTeamInSeason(seasonNumber, teamId);
			logger.debug("...got " + parsedFixtures.size() + " fixtures for team " + teamId + " in season " + seasonNumber);
			saver.saveFixtures(parsedFixtures);
		}
	}
	
	public FootballResultsReader getReader() {
		return reader;
	}
	public void setReader(FootballResultsReader reader) {
		this.reader = reader;
	}
	public void setSaver(ParsedResultsSaver saver) {
		this.saver = saver;
	}

	public FixtureRepository getFixtureRepository() {
		return fixtureRepository;
	}

	public void setFixtureRepository(FixtureRepository fixtureRepository) {
		this.fixtureRepository = fixtureRepository;
	}
}
