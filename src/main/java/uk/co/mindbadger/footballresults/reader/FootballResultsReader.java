package uk.co.mindbadger.footballresults.reader;

import java.util.List;

public interface FootballResultsReader {
	public List<ParsedFixture> readFixturesForSeason (int season);
}
