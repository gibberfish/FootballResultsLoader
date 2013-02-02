package uk.co.mindbadger.footballresults.reader.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.mindbadger.footballresults.reader.FootballResultsReaderException;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseDatePageParser {
	private WebPageReader webPageReader;
	private String url;
	
	public void setWebPageReader(WebPageReader webPageReader) {
		this.webPageReader = webPageReader;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<ParsedFixture> parseFixturesForDate(String dateString) {
		List<ParsedFixture> parsedFixtures = new ArrayList<ParsedFixture> ();
		
		String url = this.url.replace("{fixtureDate}", dateString);
		
		try {
			System.out.println(url);
			List<String> page = webPageReader.readWebPage(url);
			
			for (String line : page) {
				
			}
		} catch (FileNotFoundException e) {
			throw new FootballResultsReaderException("No page found for " + dateString);
		} catch (IOException e) {
			throw new FootballResultsReaderException("Cannot load page for " + dateString);
		}
		
		return parsedFixtures;
	}

}
