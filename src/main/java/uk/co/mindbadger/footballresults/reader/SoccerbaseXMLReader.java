package uk.co.mindbadger.footballresults.reader;

import java.util.List;

import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;

public class SoccerbaseXMLReader implements FootballResultsReader {
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
		
		System.out.println("HELLO!");
		
		return null;
	}
}

