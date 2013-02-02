package uk.co.mindbadger.footballresults.reader.web;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseDatePageParserTest {
	private static final String URL = "http://www.soccerbase.com/matches/results.sd?date={fixtureDate}";

	private SoccerbaseDatePageParser objectUnderTest;
	
	@Mock private WebPageReader mockWebPageReader;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new SoccerbaseDatePageParser();
		objectUnderTest.setWebPageReader(mockWebPageReader);
		objectUnderTest.setUrl(URL);
	}
}
