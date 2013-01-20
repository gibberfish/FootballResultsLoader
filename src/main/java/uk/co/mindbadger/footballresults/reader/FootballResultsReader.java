package uk.co.mindbadger.footballresults.reader;

import java.util.List;

import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;

public interface FootballResultsReader {
	public String getDialect();
	
	public List<Fixture> readFixturesForSeason (int season);
}
