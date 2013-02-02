package uk.co.mindbadger.footballresults.reader.web;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseDatePageParserTest {
	private static final String URL = "http://www.soccerbase.com/matches/results.sd?date={fixtureDate}";

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
	public void shouldParseADatePage () {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		fixtureDate.set(Calendar.YEAR, 2008);
		//TODO Set the date
		
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
