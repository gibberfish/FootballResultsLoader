package uk.co.mindbadger.footballresults.loader;

import java.util.List;

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
		dao.startSession();
		List<Fixture> fixtures = dao.getUnplayedFixturesBeforeToday();
		dao.closeSession();
		
		logger.debug("loadResultsForRecentlyPlayedFixtures has found " + fixtures.size() + " fixture dates that need updating");
		
		for (Fixture fixture : fixtures) {
			List<ParsedFixture> parsedFixtures = reader.readFixturesForDate(fixture.getFixtureDate());
			logger.debug("...got " + parsedFixtures.size() + " fixtures for date " + StringToCalendarConverter.convertCalendarToDateString(fixture.getFixtureDate()));
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
