package uk.co.mindbadger.footballresults.reader.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.mindbadger.footballresults.reader.FootballResultsReaderException;
import uk.co.mindbadger.footballresults.reader.xml.SoccerbaseXMLReader;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseTeamPageParserTest {
	private static final String URL = "http://www.soccerbase.com/teams/results.sd?season_id={seasonNum}&team_id={teamId}&teamTabs=results";
	
	private static final String URL_FOR_TEAM = "http://www.soccerbase.com/teams/results.sd?season_id=142&team_id=123&teamTabs=results";

	private SoccerbaseTeamPageParser objectUnderTest;
	
	@Mock private WebPageReader mockWebPageReader;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new SoccerbaseTeamPageParser();
		objectUnderTest.setWebPageReader(mockWebPageReader);
		objectUnderTest.setUrl(URL);
	}
	
	@Test
	public void shouldThrowRuntimeExceptionIfFileNotFoundExceptionThrownDuringReadOfWebPage() throws Exception {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		fixtureDate.set(Calendar.YEAR, 2008);
		fixtureDate.set(Calendar.MONTH, 12);
		fixtureDate.set(Calendar.DAY_OF_MONTH, 26);

		when(mockWebPageReader.readWebPage(URL_FOR_TEAM)).thenThrow(new FileNotFoundException());

		// When
		try {
			objectUnderTest.parseFixturesForTeam(2012, 123);
			fail("Should throw a FootballResultsReaderException");
		} catch (FootballResultsReaderException e) {
			// Then
			assertEquals(e.getMessage(), "No page found for team ID 123");
		}
	}

	@Test
	public void shouldThrowRuntimeExceptionIfIOExceptionThrownDuringReadOfWebPage() throws Exception {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		fixtureDate.set(Calendar.YEAR, 2008);
		fixtureDate.set(Calendar.MONTH, 12);
		fixtureDate.set(Calendar.DAY_OF_MONTH, 26);

		when(mockWebPageReader.readWebPage(URL_FOR_TEAM)).thenThrow(new IOException());

		// When
		try {
			objectUnderTest.parseFixturesForTeam(2012, 123);
			fail("Should throw a FootballResultsReaderException");
		} catch (FootballResultsReaderException e) {
			// Then
			assertEquals(e.getMessage(), "Cannot load page for team ID 123");
		}
	}

}
