package uk.co.mindbadger.footballresults.reader.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.co.mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;

public class SoccerbaseWebPageReader implements FootballResultsReader {
	Logger logger = Logger.getLogger(SoccerbaseWebPageReader.class);
	
	private SoccerbaseTeamPageParser teamPageParser;
	private SoccerbaseDatePageParser datePageParser;
	private FootballResultsMapping mapping;
	
	@Override
	public List<ParsedFixture> readFixturesForSeason(int season) {
		
		List<Integer> includedDivisions = mapping.getIncludedDivisions("soccerbase");
		
		Map <ParsedFixture, ParsedFixture> fixturesForSeason = new HashMap<ParsedFixture, ParsedFixture> ();
		Set <Integer> boxingDayTeams = new HashSet <Integer> ();
		Set <Integer> otherTeams = new HashSet <Integer> ();
		
		String boxingDay = season + "-12-26";
		List<ParsedFixture> boxingDayFixtures = datePageParser.parseFixturesForDate(boxingDay);
		
		for (ParsedFixture fixture : boxingDayFixtures) {
			if (includedDivisions.contains(fixture.getDivisionId())) {
				boxingDayTeams.add(fixture.getHomeTeamId());
				boxingDayTeams.add(fixture.getAwayTeamId());
			}
		}
		
		for (Integer teamId : boxingDayTeams) {
			logger.debug("#### Parse fixture for team ID " + teamId);
			List<ParsedFixture> fixtures = teamPageParser.parseFixturesForTeam(season, teamId);
			for (ParsedFixture fixture : fixtures) {
				if (includedDivisions.contains(fixture.getDivisionId())) {
					fixturesForSeason.put(fixture, fixture);
	
					if (!boxingDayTeams.contains(fixture.getHomeTeamId())) {
						otherTeams.add(fixture.getHomeTeamId());
					}
					
					if (!boxingDayTeams.contains(fixture.getAwayTeamId())) {
						otherTeams.add(fixture.getAwayTeamId());
					}
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

	public void setMapping(FootballResultsMapping mapping) {
		this.mapping = mapping;
	}
}
