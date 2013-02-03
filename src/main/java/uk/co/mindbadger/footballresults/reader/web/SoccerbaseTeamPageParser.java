package uk.co.mindbadger.footballresults.reader.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.mockito.internal.matchers.StartsWith;

import uk.co.mindbadger.footballresults.reader.FootballResultsReaderException;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.util.StringToCalendarConverter;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseTeamPageParser {
	private static final String END_OF_TEAM_NAME = "</a> <span";
	private static final String START_OF_TEAM_NAME = " team page\">";
	private static final String END_OF_TEAM_ID = "&amp;season_id=";
	private static final String START_OF_TEAM_ID = "<a href=\"/teams/team.sd?team_id=";
	private static final String END_OF_FIXTURE_DATE = "\" title=\"";
	private static final String START_OF_FIXTURE_DATE = "<a href=\"/matches/results.sd?date=";
	private static final String END_OF_DIVISION_NAME = "</a> <span ";
	private static final String START_OF_DIVISION_NAME = " competition page\">";
	private static final String END_OF_DIVISION_ID = "\" title=\"Go to ";
	private static final String START_OF_DIVISION_LINE = "<a href=\"/tournaments/tournament.sd?comp_id=";
	private static final String START_OF_MATCH = "<tr class=\"match\"";
	private static final String START_OF_HOME_TEAM_SECTION = "<td class=\"team homeTeam";
	private static final String START_OF_AWAY_TEAM_SECTION = "<td class=\"team awayTeam";
	private static final String START_OF_SCORE_SECTION = "<td class=\"score\">";
	private static final String END_OF_HOME_GOALS_LOCATION = "</em>&nbsp;-&nbsp;<em>";
	private static final String END_OF_AWAY_GOALS_LOCATION = "</em></a>";
	private static final String START_OF_HOME_GOALS_LOCATION = "<a href=\"#\" class=\"vs\" title=\"View Match info\"><em>";


	private String url;
	private WebPageReader webPageReader;
	
	public List<ParsedFixture> parseFixturesForTeam(Integer seasonNumber, Integer teamId) {
		Integer soccerbaseSeasonNumber = seasonNumber - 1870;
		
		String url = this.url.replace("{seasonNum}", soccerbaseSeasonNumber.toString());
		url = url.replace("{teamId}", teamId.toString());
		
		try {
			System.out.println(url);
			List<String> page = webPageReader.readWebPage(url);
			
			return parsePage(page);
		} catch (FileNotFoundException e) {
			throw new FootballResultsReaderException("No page found for team ID " + teamId);
		} catch (IOException e) {
			throw new FootballResultsReaderException("Cannot load page for team ID " + teamId);
		}

	}
	
	private List<ParsedFixture> parsePage(List<String> page) {
		List<ParsedFixture> parsedFixtures = new ArrayList<ParsedFixture> ();
		
		Integer divisionId = null;
		String divisionName = null;
		String dateString = null;
		Integer homeTeamId = null;
		Integer awayTeamId = null;
		String homeTeamName = null;
		String awayTeamName = null;
		Integer homeGoals = null;
		Integer awayGoals = null;
		Calendar fixtureDate = null;
		Integer season = null;

		boolean lookingForAwayTeam = false;

		for (String line : page) {
			if (line.startsWith(START_OF_MATCH)) {
				divisionId = null;
				divisionName = null;
				dateString = null;
				homeTeamId = null;
				awayTeamId = null;
				homeTeamName = null;
				awayTeamName = null;
				homeGoals = null;
				awayGoals = null;
				fixtureDate = null;
				season = null;
			}
			
			if (line.startsWith(START_OF_DIVISION_LINE)) {
				int divisionIdStartPos = line.indexOf(START_OF_DIVISION_LINE) + START_OF_DIVISION_LINE.length();
				int divisionIdEndPos = line.indexOf(END_OF_DIVISION_ID,divisionIdStartPos);
				divisionId = Integer.parseInt(line.substring(divisionIdStartPos, divisionIdEndPos));
				System.out.println("...divisionIdString: " + divisionId);
				
				int divisionNameStartPos = line.indexOf(START_OF_DIVISION_NAME, divisionIdEndPos) + START_OF_DIVISION_NAME.length();
				int divisionNameEndPos = line.indexOf(END_OF_DIVISION_NAME, divisionIdStartPos);
				divisionName = line.substring(divisionNameStartPos, divisionNameEndPos);
				System.out.println("divisionName: " + divisionName);
			}
			
			if (line.startsWith(START_OF_FIXTURE_DATE)) {
				int fixtureDateStartPos = line.indexOf(START_OF_FIXTURE_DATE) + START_OF_FIXTURE_DATE.length();
				int fixtureDateEndPos = line.indexOf(END_OF_FIXTURE_DATE,fixtureDateStartPos);
				String fixtureDateString = line.substring(fixtureDateStartPos, fixtureDateEndPos);
				System.out.println("...fixtureDate: " + fixtureDateString);
				
				fixtureDate = StringToCalendarConverter.convertDateStringToCalendar(fixtureDateString);
			}
			
			if (line.startsWith(START_OF_HOME_TEAM_SECTION)) {
				lookingForAwayTeam = false;
			}
			
			if (line.startsWith(START_OF_AWAY_TEAM_SECTION)) {
				lookingForAwayTeam = true;
			}

			if (line.startsWith(START_OF_TEAM_ID)) {
				int teamIdStartPos = line.indexOf(START_OF_TEAM_ID) + START_OF_TEAM_ID.length();
				int teamIdEndPos = line.indexOf(END_OF_TEAM_ID,teamIdStartPos);
				if (lookingForAwayTeam) {
					awayTeamId = Integer.parseInt(line.substring(teamIdStartPos, teamIdEndPos));
					System.out.println("awayTeamId: " + awayTeamId);
				} else {
					homeTeamId = Integer.parseInt(line.substring(teamIdStartPos, teamIdEndPos));
					System.out.println("homeTeamId: " + homeTeamId);
				}
				
				int seasonStartPos = line.indexOf(END_OF_TEAM_ID, teamIdEndPos) + END_OF_TEAM_ID.length();
				int seasonEndPos = line.indexOf("&amp;teamTabs=results\" title", seasonStartPos);
				season = Integer.parseInt(line.substring(seasonStartPos, seasonEndPos)) + 1870;
				System.out.println("... season: " + season);
				
				int teamNameStartPos = line.indexOf(START_OF_TEAM_NAME, seasonEndPos) + START_OF_TEAM_NAME.length();
				int teamNameEndPos = line.indexOf(END_OF_TEAM_NAME, teamNameStartPos);
				if (lookingForAwayTeam) {
					awayTeamName = line.substring(teamNameStartPos, teamNameEndPos);
					System.out.println("awayTeamName: " + awayTeamName);
					
					parsedFixtures.add(createFixture(divisionId, divisionName, homeTeamId, awayTeamId, homeTeamName, awayTeamName, homeGoals, awayGoals, fixtureDate, season));
				} else {
					homeTeamName = line.substring(teamNameStartPos, teamNameEndPos);
					System.out.println("homeTeamName: " + homeTeamName);
				}
			}
			
			if (line.startsWith(START_OF_SCORE_SECTION)) {
				homeGoals = null;
				awayGoals = null;
			}

			if (line.startsWith(START_OF_HOME_GOALS_LOCATION)) {
				int homeGoalsStartPos = line.indexOf(START_OF_HOME_GOALS_LOCATION) + START_OF_HOME_GOALS_LOCATION.length();
				int homeGoalsEndPos = line.indexOf(END_OF_HOME_GOALS_LOCATION,homeGoalsStartPos);
				homeGoals = Integer.parseInt(line.substring(homeGoalsStartPos, homeGoalsEndPos));
				System.out.println("homeGoals: " + homeGoals);
				
				int awayGoalsStartPos = homeGoalsEndPos + END_OF_HOME_GOALS_LOCATION.length();
				int awayGoalsEndPos = line.indexOf(END_OF_AWAY_GOALS_LOCATION, awayGoalsStartPos);
				awayGoals = Integer.parseInt(line.substring(awayGoalsStartPos, awayGoalsEndPos));
				System.out.println("awayGoals: " + awayGoals);
			}

		}		
		
		return parsedFixtures;
	}

	private ParsedFixture createFixture(Integer divisionId, String divisionName, Integer homeTeamId, Integer awayTeamId, String homeTeamName, String awayTeamName, Integer homeGoals, Integer awayGoals, Calendar fixtureDate,
			Integer season) {
		ParsedFixture parsedFixture = new ParsedFixture();
		parsedFixture.setSeasonId(season);
		parsedFixture.setFixtureDate(fixtureDate);
		parsedFixture.setDivisionId(divisionId);
		parsedFixture.setDivisionName(divisionName);
		parsedFixture.setHomeTeamId(homeTeamId);
		parsedFixture.setHomeTeamName(homeTeamName);
		parsedFixture.setAwayTeamId(awayTeamId);
		parsedFixture.setAwayTeamName(awayTeamName);
		parsedFixture.setHomeGoals(homeGoals);
		parsedFixture.setAwayGoals(awayGoals);
		
		return parsedFixture;
	}
	
	public void setWebPageReader(WebPageReader webPageReader) {
		this.webPageReader = webPageReader;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
	
	
	
	
}
