package uk.co.mindbadger.footballresults.reader.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.mindbadger.footballresults.reader.FootballResultsReaderException;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseTeamPageParser {
	private static final String START_OF_DIVISION_LINE = "<a href=\"/tournaments/tournament.sd?comp_id=";
	private static final String START_OF_MATCH = "<tr class=\"match\"";
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
			if (line.startsWith(START_OF_MATCH)) {
			
			}
			
			if (line.startsWith(START_OF_DIVISION_LINE)) {
				
			}
		}		
		
		return parsedFixtures;
	}

	public void setWebPageReader(WebPageReader webPageReader) {
		this.webPageReader = webPageReader;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
