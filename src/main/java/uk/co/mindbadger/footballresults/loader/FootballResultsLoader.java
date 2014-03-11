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

public class FootballResultsLoader {
	Logger logger = Logger.getLogger(FootballResultsLoader.class);
	
	private FootballResultsAnalyserDAO dao;
	private FootballResultsReader reader;
	private FootballResultsSaver saver;

	public void loadResultsForSeason(int seasonNum) {
		List<ParsedFixture> fixturesRead = reader.readFixturesForSeason(seasonNum);

		saver.saveFixtures(fixturesRead);
	}
	
	public void loadResultsForRecentlyPlayedFixtures() {
		System.out.println("I'm here!!!");
		logger.debug("Starting loadResultsForRecentlyPlayedFixtures");
		
		dao.startSession();
		List<Fixture> unplayedFixtures = dao.getUnplayedFixturesBeforeToday();
		List<Fixture> fixturesWithoutDates = dao.getFixturesWithNoFixtureDate();
		dao.closeSession();
		
		System.out.println("Got fixtures!!!");
		
		logger.debug("loadResultsForRecentlyPlayedFixtures has found " + fixturesWithoutDates.size() + " fixtures without dates");
		
		Map<Calendar, Calendar> uniqueDates = new HashMap<Calendar, Calendar> ();
		for (Fixture fixture : unplayedFixtures) {
			uniqueDates.put(fixture.getFixtureDate(), fixture.getFixtureDate());
		}
		logger.debug("loadResultsForRecentlyPlayedFixtures has found " + uniqueDates.size() + " fixture dates that need updating");
		
		for (Calendar fixtureDate : uniqueDates.keySet()) {
			List<ParsedFixture> parsedFixtures = reader.readFixturesForDate(fixtureDate);
			logger.debug("...got " + parsedFixtures.size() + " fixtures for date " + StringToCalendarConverter.convertCalendarToDateString(fixtureDate));
			saver.saveFixtures(parsedFixtures);
		}
		
		for (Fixture fixture : fixturesWithoutDates) {
			Integer seasonNumber = fixture.getSeason().getSeasonNumber();
			Integer teamId = fixture.getHomeTeam().getTeamId();
			List<ParsedFixture> parsedFixtures = reader.readFixturesForTeamInSeason(seasonNumber, teamId);
			logger.debug("...got " + parsedFixtures.size() + " fixtures for team " + teamId + " in season " + seasonNumber);
			saver.saveFixtures(parsedFixtures);
		}
	}
	
	public FootballResultsAnalyserDAO getDao() {
		return dao;
	}
	public void setDao(FootballResultsAnalyserDAO dao) {
		this.dao = dao;
	}
	public FootballResultsReader getReader() {
		return reader;
	}
	public void setReader(FootballResultsReader reader) {
		this.reader = reader;
	}
	public void setSaver(FootballResultsSaver saver) {
		this.saver = saver;
	}
}
