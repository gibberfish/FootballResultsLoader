package uk.co.mindbadger.footballresults.reader.web;

import java.util.List;

import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;

public class SoccerbaseWebPageReader implements FootballResultsReader {
	private String dialect;
	private SoccerbaseTeamPageParser teamPageParser;
	private SoccerbaseDatePageParser datePageParser;
	
	@Override
	public List<ParsedFixture> readFixturesForSeason(int season) {
		String boxingDay = season + "-12-26";
		List<ParsedFixture> boxingDayFixtures = datePageParser.parseFixturesForDate(boxingDay);
		
		for (ParsedFixture fixture : boxingDayFixtures) {
			teamPageParser.parseFixturesForTeam(season, fixture.getHomeTeamId());
			teamPageParser.parseFixturesForTeam(season, fixture.getAwayTeamId());
		}
		
		return null;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public void setTeamPageParser(SoccerbaseTeamPageParser teamPageParser) {
		this.teamPageParser = teamPageParser;
	}

	public void setDatePageParser(SoccerbaseDatePageParser datePageParser) {
		this.datePageParser = datePageParser;
	}
}
