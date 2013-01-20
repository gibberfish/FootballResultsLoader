package uk.co.mindbadger.footballresults.reader;

import java.util.List;

import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;

public class SoccerbasePageReader implements FootballResultsReader {
	private String dialect;

	@Override
	public String getDialect() {
		return dialect;
	}
	
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	@Override
	public List<Fixture> readFixturesForSeason(int season) {
		throw new RuntimeException ("Method not implemented");
	}
}
