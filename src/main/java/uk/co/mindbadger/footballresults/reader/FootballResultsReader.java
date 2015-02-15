package uk.co.mindbadger.footballresults.reader;

import java.util.Calendar;
import java.util.List;

public interface FootballResultsReader {
	public List<ParsedFixture> readFixturesForSeason (int season);
	
	public List<ParsedFixture> readFixturesForDate (Calendar date);
	
	public List<ParsedFixture> readFixturesForTeamInSeason (int season, String teamId);
}
