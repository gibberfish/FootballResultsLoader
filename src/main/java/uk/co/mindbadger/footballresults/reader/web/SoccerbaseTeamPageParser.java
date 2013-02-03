package uk.co.mindbadger.footballresults.reader.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import uk.co.mindbadger.footballresults.reader.FootballResultsReaderException;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseTeamPageParser {
	private String url;
	private WebPageReader webPageReader;
	
	public List<ParsedFixture> parseFixturesForTeam(Integer seasonNumber, Integer teamId) {
		Integer soccerbaseSeasonNumber = seasonNumber - 1870;
		
		String url = this.url.replace("{seasonNum}", soccerbaseSeasonNumber.toString());
		url = url.replace("{teamId}", teamId.toString());
		
		try {
			System.out.println(url);
			List<String> page = webPageReader.readWebPage(url);
			
			return null;
//			return parsePage(page);
		} catch (FileNotFoundException e) {
			throw new FootballResultsReaderException("No page found for team ID " + teamId);
		} catch (IOException e) {
			throw new FootballResultsReaderException("Cannot load page for team ID " + teamId);
		}

	}
	
	public void setWebPageReader(WebPageReader webPageReader) {
		this.webPageReader = webPageReader;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
