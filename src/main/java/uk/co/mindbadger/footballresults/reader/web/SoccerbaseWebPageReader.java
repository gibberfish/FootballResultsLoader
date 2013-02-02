package uk.co.mindbadger.footballresults.reader.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;

public class SoccerbaseWebPageReader implements FootballResultsReader {
	private SoccerbaseTeamPageParser teamPageParser;
	private SoccerbaseDatePageParser datePageParser;
	
	@Override
	public List<ParsedFixture> readFixturesForSeason(int season) {
		
		Map <ParsedFixture, ParsedFixture> fixturesForSeason = new HashMap<ParsedFixture, ParsedFixture> ();
		Set <Integer> boxingDayTeams = new HashSet <Integer> ();
		Set <Integer> otherTeams = new HashSet <Integer> ();
		
		String boxingDay = season + "-12-26";
		List<ParsedFixture> boxingDayFixtures = datePageParser.parseFixturesForDate(boxingDay);
		
		for (ParsedFixture fixture : boxingDayFixtures) {
			boxingDayTeams.add(fixture.getHomeTeamId());
			boxingDayTeams.add(fixture.getAwayTeamId());
		}
		
		for (Integer teamId : boxingDayTeams) {
			List<ParsedFixture> fixtures = teamPageParser.parseFixturesForTeam(season, teamId);
			for (ParsedFixture fixture : fixtures) {
				fixturesForSeason.put(fixture, fixture);

				if (!boxingDayTeams.contains(fixture.getHomeTeamId())) {
					otherTeams.add(fixture.getHomeTeamId());
				}
				
				if (!boxingDayTeams.contains(fixture.getAwayTeamId())) {
					otherTeams.add(fixture.getAwayTeamId());
				}
			}
		}
		
		for (Integer teamId : otherTeams) {
			List<ParsedFixture> fixtures = teamPageParser.parseFixturesForTeam(season, teamId);
			for (ParsedFixture fixture : fixtures) {
				fixturesForSeason.put(fixture, fixture);
			}
		}
		
		return new ArrayList<ParsedFixture> (fixturesForSeason.values());
	}

	public void setTeamPageParser(SoccerbaseTeamPageParser teamPageParser) {
		this.teamPageParser = teamPageParser;
	}

	public void setDatePageParser(SoccerbaseDatePageParser datePageParser) {
		this.datePageParser = datePageParser;
	}
}
