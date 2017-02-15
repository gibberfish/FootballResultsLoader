package mindbadger.footballresults.reader.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import mindbadger.footballresults.reader.FootballResultsReader;
import mindbadger.footballresults.reader.FootballResultsReaderException;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.util.StringToCalendarConverter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SoccerbaseWebPageReader implements FootballResultsReader {
	Logger logger = Logger.getLogger(SoccerbaseWebPageReader.class);
	
	//@Autowired
	private SoccerbaseTeamPageParser teamPageParser;
	//@Autowired
	private SoccerbaseDatePageParser datePageParser;
	//@Autowired
	private FootballResultsMapping mapping;
	
	@Override
	public List<ParsedFixture> readFixturesForSeason(int season) {
		
		logger.info("About to read fixtures for season " + season + " using boxing day's results");
		
		List<String> includedDivisions = mapping.getIncludedDivisions("soccerbase");
		
		Map <ParsedFixture, ParsedFixture> fixturesForSeason = new HashMap<ParsedFixture, ParsedFixture> ();
		Set <String> boxingDayTeams = new HashSet <String> ();
		Set <String> otherTeams = new HashSet <String> ();
		
		String boxingDay = season + "-12-26";
		List<ParsedFixture> boxingDayFixtures = datePageParser.parseFixturesForDate(boxingDay);
		
		for (ParsedFixture fixture : boxingDayFixtures) {
			if (includedDivisions.contains(fixture.getDivisionId())) {
				boxingDayTeams.add(fixture.getHomeTeamId());
				boxingDayTeams.add(fixture.getAwayTeamId());
			}
		}
		
		for (String teamId : boxingDayTeams) {
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
		
		for (String teamId : otherTeams) {
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

	@Override
	public List<ParsedFixture> readFixturesForDate(Calendar date) {
		return readFixturesForDate(date, 0);
	}

	private List<ParsedFixture> readFixturesForDate(Calendar date, int retries) {
		retries++;
		
		String dateString = StringToCalendarConverter.convertCalendarToDateString(date);
		
		logger.info("About to read fixtures for date " + dateString + ": attempt #" + retries);
		
		try {
			List<ParsedFixture> parsedFixtures = datePageParser.parseFixturesForDate(dateString);
			return parsedFixtures;
		} catch (FootballResultsReaderException e) {
			if (retries < 4) {
				return readFixturesForDate(date);
			} else {
				throw (e);
			}
		}
	}
	
	@Override
	public List<ParsedFixture> readFixturesForTeamInSeason(int season, String teamId) {
		Map<String, String> teamMappings = mapping.getTeamMappings("soccerbase");
		String matchingSoccerbaseId = null;
		for (String soccerbaseId : teamMappings.keySet()) {
			
			if (teamMappings.get(soccerbaseId).equals(teamId)) {
				matchingSoccerbaseId = soccerbaseId;
				break;
			}
		}
		
		return teamPageParser.parseFixturesForTeam(season, matchingSoccerbaseId);
	}
}
