package uk.co.mindbadger.footballresults.loader;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;
import uk.co.mindbadger.util.StringToCalendarConverter;

public class FootballResultsLoader<K,L,M> {
	Logger logger = Logger.getLogger(FootballResultsLoader.class);
	
	private FootballResultsAnalyserDAO<K,L,M> dao;
	private FootballResultsReader reader;
	private FootballResultsSaver<K,L,M> saver;

	public void loadResultsForSeason(int seasonNum) {
		List<ParsedFixture> fixturesRead = reader.readFixturesForSeason(seasonNum);
		
		logger.info("Ready to save " + fixturesRead.size() + " fixtures");
		
		saver.saveFixtures(fixturesRead);
	}
	
	public void loadResultsForRecentlyPlayedFixtures() {
		logger.debug("Starting loadResultsForRecentlyPlayedFixtures");
		
		dao.startSession();
		List<Fixture<K>> unplayedFixtures = dao.getUnplayedFixturesBeforeToday();
		List<Fixture<K>> fixturesWithoutDates = dao.getFixturesWithNoFixtureDate();
		
		logger.debug("loadResultsForRecentlyPlayedFixtures has found " + fixturesWithoutDates.size() + " fixtures without dates");
		
		Map<Calendar, Calendar> uniqueDates = new HashMap<Calendar, Calendar> ();
		for (Fixture<K> fixture : unplayedFixtures) {
			uniqueDates.put(fixture.getFixtureDate(), fixture.getFixtureDate());
		}
		logger.debug("getUnplayedFixturesBeforeToday has found " + uniqueDates.size() + " fixture dates that need updating");
		
		for (Calendar fixtureDate : uniqueDates.keySet()) {
			List<ParsedFixture> parsedFixtures = reader.readFixturesForDate(fixtureDate);
			logger.debug("...got " + parsedFixtures.size() + " fixtures for date " + StringToCalendarConverter.convertCalendarToDateString(fixtureDate));
			saver.saveFixtures(parsedFixtures);
		}
		
		for (Fixture<K> fixture : fixturesWithoutDates) {
			Integer seasonNumber = fixture.getSeason().getSeasonNumber();
			String teamId = fixture.getHomeTeam().getTeamIdAsString();
			List<ParsedFixture> parsedFixtures = reader.readFixturesForTeamInSeason(seasonNumber, teamId);
			logger.debug("...got " + parsedFixtures.size() + " fixtures for team " + teamId + " in season " + seasonNumber);
			saver.saveFixtures(parsedFixtures);
		}
		
		dao.closeSession();
	}
	
	public FootballResultsAnalyserDAO<K,L,M> getDao() {
		return dao;
	}
	public void setDao(FootballResultsAnalyserDAO<K,L,M> dao) {
		this.dao = dao;
	}
	public FootballResultsReader getReader() {
		return reader;
	}
	public void setReader(FootballResultsReader reader) {
		this.reader = reader;
	}
	public void setSaver(FootballResultsSaver<K,L,M> saver) {
		this.saver = saver;
	}
}
