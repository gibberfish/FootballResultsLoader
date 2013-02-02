package uk.co.mindbadger.footballresults.reader.web;

import java.util.List;

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
		return null;
	}

}
