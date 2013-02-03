package uk.co.mindbadger.footballresults.reader.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.mindbadger.footballresults.reader.FootballResultsReaderException;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.util.StringToCalendarConverter;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseDatePageParser {
	private static final String END_OF_HOME_GOALS_LOCATION = "</em>&nbsp;-&nbsp;<em>";
	private static final String END_OF_AWAY_GOALS_LOCATION = "</em></a>";
	private static final String START_OF_HOME_GOALS_LOCATION = "<a href=\"#\" class=\"vs\" title=\"View Match info\"><em>";
	private static final String START_OF_SCORE_SECTION = "<td class=\"score\">";
	private static final String END_OF_TEAM_NAME_LOCATION = "</a>";
	private static final String START_OF_TEAM_NAME_LOCATION = "team page\">";
	private static final String END_OF_TEAM_ID_LOCATION = "\" title=";
	private static final String START_OF_TEAM_ID_LOCATION = "<a href=\"/teams/team.sd?team_id=";
	private static final String START_OF_AWAY_TEAM_SECTION = "<td class=\"team awayTeam";
	private static final String START_OF_HOME_TEAM_SECTION = "<td class=\"team homeTeam";
	private static final String END_OF_DATE_LOCATION = END_OF_TEAM_ID_LOCATION;
	private static final String START_OF_DATE_LOCATION = "<a href=\"/matches/results.sd?date=";
	private static final String START_OF_DIVISION_NAME_LOCATION = "page\">";
	private static final String END_OF_DIVISION_ID_LOCATION = "\" title";
	private static final String START_OF_DIVISION_ID_LOCATION = "<a href=\"/tournaments/tournament.sd?comp_id=";
	private WebPageReader webPageReader;
	private String url;
	
	public void setWebPageReader(WebPageReader webPageReader) {
		this.webPageReader = webPageReader;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<ParsedFixture> parseFixturesForDate(String dateString) {
		String url = this.url.replace("{fixtureDate}", dateString);
		
		try {
			System.out.println(url);
			List<String> page = webPageReader.readWebPage(url);
			
			return parsePage(page);
		} catch (FileNotFoundException e) {
			throw new FootballResultsReaderException("No page found for " + dateString);
		} catch (IOException e) {
			throw new FootballResultsReaderException("Cannot load page for " + dateString);
		}
	}

	protected List<ParsedFixture> parsePage(List<String> page) {
		
		List<ParsedFixture> parsedFixtures = new ArrayList<ParsedFixture> ();
		
		Integer divisionId = null;
		String divisionName = null;
		String dateString = null;
		boolean lookingForAwayTeam = false;
		Integer homeTeamId = null;
		Integer awayTeamId = null;
		String homeTeamName = null;
		String awayTeamName = null;
		Integer homeGoals = null;
		Integer awayGoals = null;
		Calendar fixtureDate = null;
		Integer season = null;
		
		for (String line : page) {
			if (line.startsWith(START_OF_DIVISION_ID_LOCATION)) {
				int divisionIdStartPos = line.indexOf(START_OF_DIVISION_ID_LOCATION) + START_OF_DIVISION_ID_LOCATION.length();
				int divisionIdEndPos = line.indexOf(END_OF_DIVISION_ID_LOCATION,divisionIdStartPos);
				divisionId = Integer.parseInt(line.substring(divisionIdStartPos, divisionIdEndPos));
				System.out.println("divisionIdString: " + line.substring(divisionIdStartPos, divisionIdEndPos));
				
				int divisionNameStartPos = line.indexOf(START_OF_DIVISION_NAME_LOCATION, divisionIdEndPos) + START_OF_DIVISION_NAME_LOCATION.length();
				int divisionNameEndPos = line.indexOf(END_OF_TEAM_NAME_LOCATION, divisionIdStartPos);
				divisionName = line.substring(divisionNameStartPos, divisionNameEndPos);
				System.out.println("divisionName: " + divisionName);
			}
			
			if (line.startsWith(START_OF_DATE_LOCATION)) {
				int dateStartPos = line.indexOf(START_OF_DATE_LOCATION) + START_OF_DATE_LOCATION.length();
				int dateEndPos = line.indexOf(END_OF_DATE_LOCATION, dateStartPos);
				dateString = line.substring(dateStartPos, dateEndPos);
				System.out.println("dateString: " + dateString);
				fixtureDate = StringToCalendarConverter.convertDateStringToCalendar(dateString);
				
				if (season == null) {
					season = getSeasonFromFixturedate (fixtureDate);
				}
			}
			
			if (line.startsWith(START_OF_HOME_TEAM_SECTION)) {
				lookingForAwayTeam = false;
			}
			
			if (line.startsWith(START_OF_AWAY_TEAM_SECTION)) {
				lookingForAwayTeam = true;
			}

			if (line.startsWith(START_OF_TEAM_ID_LOCATION)) {
				int teamIdStartPos = line.indexOf(START_OF_TEAM_ID_LOCATION) + START_OF_TEAM_ID_LOCATION.length();
				int teamIdEndPos = line.indexOf(END_OF_TEAM_ID_LOCATION,teamIdStartPos);
				if (lookingForAwayTeam) {
					awayTeamId = Integer.parseInt(line.substring(teamIdStartPos, teamIdEndPos));
					System.out.println("awayTeamId: " + awayTeamId);
				} else {
					homeTeamId = Integer.parseInt(line.substring(teamIdStartPos, teamIdEndPos));
					System.out.println("homeTeamId: " + homeTeamId);
				}
				
				int teamNameStartPos = line.indexOf(START_OF_TEAM_NAME_LOCATION, teamIdEndPos) + START_OF_TEAM_NAME_LOCATION.length();
				int teamNameEndPos = line.indexOf(END_OF_TEAM_NAME_LOCATION, teamIdStartPos);
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

	
	private Integer getSeasonFromFixturedate(Calendar fixtureDate) {
		if (fixtureDate.get(Calendar.MONTH) < 7) {
			return (fixtureDate.get(Calendar.YEAR) -1);
		} else {
			return fixtureDate.get(Calendar.YEAR);
		}
	}
}
