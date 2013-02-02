package uk.co.mindbadger.footballresults.reader.web;

import java.util.ArrayList;
import java.util.List;

import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;

public class SoccerbaseWebPageReader implements FootballResultsReader {
	private String dialect;
	private SoccerbaseTeamPageParser teamPageParser;
	private SoccerbaseDatePageParser datePageParser;
	
	@Override
	public List<ParsedFixture> readFixturesForSeason(int season) {
		
		List<ParsedFixture> fixturesForSeason = new ArrayList<ParsedFixture> ();
		
		String boxingDay = season + "-12-26";
		List<ParsedFixture> boxingDayFixtures = datePageParser.parseFixturesForDate(boxingDay);
		
		for (ParsedFixture fixture : boxingDayFixtures) {
			List<ParsedFixture> homeTeamFixtures = teamPageParser.parseFixturesForTeam(season, fixture.getHomeTeamId());
			List<ParsedFixture> awayTeamFixtures = teamPageParser.parseFixturesForTeam(season, fixture.getAwayTeamId());
			
			fixturesForSeason.addAll(homeTeamFixtures);
			fixturesForSeason.addAll(awayTeamFixtures);
		}
		
		return fixturesForSeason;
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
