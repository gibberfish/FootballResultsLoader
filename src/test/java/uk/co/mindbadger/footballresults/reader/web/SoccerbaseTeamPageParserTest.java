package uk.co.mindbadger.footballresults.reader.web;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.mindbadger.footballresults.reader.xml.SoccerbaseXMLReader;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseTeamPageParserTest {
	private static final String URL = "http://www.soccerbase.com/teams/results.sd?season_id={seasonNum}&team_id={teamId}&teamTabs=results";

	private SoccerbaseTeamPageParser objectUnderTest;
	
	@Mock private WebPageReader mockWebPageReader;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new SoccerbaseTeamPageParser();
		objectUnderTest.setWebPageReader(mockWebPageReader);
		objectUnderTest.setUrl(URL);
	}
}
