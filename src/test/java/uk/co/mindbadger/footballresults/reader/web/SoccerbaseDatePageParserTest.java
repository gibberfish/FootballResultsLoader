package uk.co.mindbadger.footballresults.reader.web;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.mindbadger.footballresults.reader.FootballResultsReaderException;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseDatePageParserTest {
	private static final String URL = "http://www.soccerbase.com/matches/results.sd?date={fixtureDate}";
	private static final String URL_FOR_BOXING_DAY = "http://www.soccerbase.com/matches/results.sd?date=2008-12-26";
	
	private static final Integer SEASON = new Integer (2008);
	private static final Integer DIV_1_ID = new Integer (1);
	private static final String DIV_1_NAME = "DIV1";
	private static final Integer DIV_2_ID = new Integer (2);
	private static final String DIV_2_NAME = "DIV2";
	private static final Integer TEAM_1_ID = new Integer (100);
	private static final String TEAM_1_NAME = "TEAM1";
	private static final Integer TEAM_2_ID = new Integer (101);
	private static final String TEAM_2_NAME = "TEAM2";
	private static final Integer TEAM_3_ID = new Integer (102);
	private static final String TEAM_3_NAME = "TEAM3";
	private static final Integer TEAM_4_ID = new Integer (103);
	private static final String TEAM_4_NAME = "TEAM4";
	private static final Integer TEAM_5_ID = new Integer (104);
	private static final String TEAM_5_NAME = "TEAM5";
	private static final Integer TEAM_6_ID = new Integer (105);
	private static final String TEAM_6_NAME = "TEAM6";
	private static final Integer TEAM_7_ID = new Integer (106);
	private static final String TEAM_7_NAME = "TEAM7";
	private static final Integer TEAM_8_ID = new Integer (107);
	private static final String TEAM_8_NAME = "TEAM8";

	private SoccerbaseDatePageParser objectUnderTest;
	
	@Mock private WebPageReader mockWebPageReader;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new SoccerbaseDatePageParser();
		objectUnderTest.setWebPageReader(mockWebPageReader);
		objectUnderTest.setUrl(URL);
	}

	@Test
	public void shouldThrowRuntimeExceptionIfFileNotFoundExceptionThrownDuringReadOfWebPage () throws Exception {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		fixtureDate.set(Calendar.YEAR, 2008);
		fixtureDate.set(Calendar.MONTH, 12);
		fixtureDate.set(Calendar.DAY_OF_MONTH, 26);
		
		when(mockWebPageReader.readWebPage(URL_FOR_BOXING_DAY)).thenThrow(new FileNotFoundException ());
		
		// When
		try {
			objectUnderTest.parseFixturesForDate("2008-12-26");
			fail ("Should throw a FootballResultsReaderException");
		} catch (FootballResultsReaderException e) {
			// Then
			assertEquals (e.getMessage(), "No page found for 2008-12-26");
		}
	}

	@Test
	public void shouldThrowRuntimeExceptionIfIOExceptionThrownDuringReadOfWebPage () throws Exception {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		fixtureDate.set(Calendar.YEAR, 2008);
		fixtureDate.set(Calendar.MONTH, 12);
		fixtureDate.set(Calendar.DAY_OF_MONTH, 26);
		
		when(mockWebPageReader.readWebPage(URL_FOR_BOXING_DAY)).thenThrow(new IOException ());
		
		// When
		try {
			objectUnderTest.parseFixturesForDate("2008-12-26");
			fail ("Should throw a FootballResultsReaderException");
		} catch (FootballResultsReaderException e) {
			// Then
			assertEquals (e.getMessage(), "Cannot load page for 2008-12-26");
		}
	}

	@Test
	public void shouldParseADatePage () throws Exception {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		fixtureDate.set(Calendar.YEAR, 2008);
		fixtureDate.set(Calendar.MONTH, 12);
		fixtureDate.set(Calendar.DAY_OF_MONTH, 26);
		
		when(mockWebPageReader.readWebPage(URL_FOR_BOXING_DAY)).thenReturn(get2008BoxingDayPage());
		
		// When
		List<ParsedFixture> parsedFixtures = objectUnderTest.parseFixturesForDate("2008-12-26");
		
		// Then
		assertEquals (4, parsedFixtures.size());
		ParsedFixture fixture1 = parsedFixtures.get(0);
		assertEquals (SEASON, fixture1.getSeasonId());
		assertEquals (DIV_1_ID, fixture1.getDivisionId());
		assertEquals (DIV_1_NAME, fixture1.getDivisionName());
		assertEquals (TEAM_1_ID, fixture1.getHomeTeamId());
		assertEquals (TEAM_1_NAME, fixture1.getHomeTeamName());
		assertEquals (TEAM_2_ID, fixture1.getAwayTeamId());
		assertEquals (TEAM_2_NAME, fixture1.getAwayTeamName());
		assertEquals (fixtureDate, fixture1.getFixtureDate());
		assertEquals (new Integer(5), fixture1.getHomeGoals());
		assertEquals (new Integer(2), fixture1.getAwayGoals());
		assertNotNull (fixture1.getFixtureId());
		
		ParsedFixture fixture2 = parsedFixtures.get(1);
		assertEquals (SEASON, fixture2.getSeasonId());
		assertEquals (DIV_1_ID, fixture2.getDivisionId());
		assertEquals (DIV_1_NAME, fixture2.getDivisionName());
		assertEquals (TEAM_3_ID, fixture2.getHomeTeamId());
		assertEquals (TEAM_3_NAME, fixture2.getHomeTeamName());
		assertEquals (TEAM_4_ID, fixture2.getAwayTeamId());
		assertEquals (TEAM_4_NAME, fixture2.getAwayTeamName());
		assertEquals (fixtureDate, fixture2.getFixtureDate());
		assertEquals (new Integer(3), fixture2.getHomeGoals());
		assertEquals (new Integer(0), fixture2.getAwayGoals());
		assertNotNull (fixture2.getFixtureId());

		ParsedFixture fixture3 = parsedFixtures.get(2);
		assertEquals (SEASON, fixture3.getSeasonId());
		assertEquals (DIV_2_ID, fixture3.getDivisionId());
		assertEquals (DIV_2_NAME, fixture3.getDivisionName());
		assertEquals (TEAM_5_ID, fixture3.getHomeTeamId());
		assertEquals (TEAM_5_NAME, fixture3.getHomeTeamName());
		assertEquals (TEAM_6_ID, fixture3.getAwayTeamId());
		assertEquals (TEAM_6_NAME, fixture3.getAwayTeamName());
		assertEquals (fixtureDate, fixture3.getFixtureDate());
		assertEquals (new Integer(1), fixture3.getHomeGoals());
		assertEquals (new Integer(4), fixture3.getAwayGoals());
		assertNotNull (fixture3.getFixtureId());

		ParsedFixture fixture4 = parsedFixtures.get(3);
		assertEquals (SEASON, fixture4.getSeasonId());
		assertEquals (DIV_2_ID, fixture4.getDivisionId());
		assertEquals (DIV_2_NAME, fixture4.getDivisionName());
		assertEquals (TEAM_7_ID, fixture4.getHomeTeamId());
		assertEquals (TEAM_7_NAME, fixture4.getHomeTeamName());
		assertEquals (TEAM_8_ID, fixture4.getAwayTeamId());
		assertEquals (TEAM_8_NAME, fixture4.getAwayTeamName());
		assertEquals (fixtureDate, fixture4.getFixtureDate());
		assertEquals (new Integer(2), fixture4.getHomeGoals());
		assertEquals (new Integer(3), fixture4.getAwayGoals());
		assertNotNull (fixture4.getFixtureId());
	}
	
	
	  private List <String> get2008BoxingDayPage ()
	  {
	    List<String> pageLines = new ArrayList<String> ();

	    pageLines.add ("<html>");
	    pageLines.add ("<head>");
	    pageLines.add ("");
	    pageLines.add ("");
	    pageLines.add ("<b>Results/fixtures - 26-12-2008</b><p>");
	    pageLines.add ("<b>Other results/fixturesfrom 2008/2009</b></td></tr>");
	    pageLines.add ("");
	    pageLines.add ("<tr>");
	    pageLines.add ("<td colspan=3>");
	    pageLines.add ("<form name=date>");
	    pageLines.add ("<select name=date>");
	    pageLines.add ("<option value='2008-12-01 00:00:00'>");
	    pageLines.add ("");
	    pageLines.add ("<option value='2008-12-02 00:00:00'>");
	    pageLines.add ("");
	    pageLines.add ("<option value='2008-12-03 00:00:00'>");
	    pageLines.add ("");
	    pageLines.add ("<option value='2008-12-04 00:00:00'>");
	    pageLines.add ("</option>");
	    pageLines.add ("</select>");
	    pageLines.add ("</script>                ");
	    pageLines.add ("</body>");
	    pageLines.add ("");
	    pageLines.add ("</html>");

	    return pageLines;
	  }
}
