package uk.co.mindbadger.footballresults.reader.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

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
import uk.co.mindbadger.util.StringToCalendarConverter;
import uk.co.mindbadger.web.WebPageReader;

public class SoccerbaseTeamPageParserTest {
	private static final Integer SEASON = 2012;
	private static final Integer SOCCERBASE_SEASON_2012 = 142;
	
	private static final Integer DIV_1_ID = 1;
	private static final String DIV_1_NAME = "DIV 1";
	private static final Integer DIV_2_ID = 2;
	private static final String DIV_2_NAME = "CUP 1";
	private static final String FIXTURE_DATE_1 = "2012-08-10";
	private static final String FIXTURE_DATE_2 = "2012-09-10";
	private static final String FIXTURE_DATE_3 = "2013-01-09";
	private static final String FIXTURE_DATE_4 = "2013-03-12";
	private static final Integer TEAM_1_ID = 100;
	private static final String TEAM_1_NAME = "TEAM1";
	private static final Integer TEAM_2_ID = 101;
	private static final String TEAM_2_NAME = "TEAM2";
	private static final Integer TEAM_3_ID = 102;
	private static final String TEAM_3_NAME = "TEAM3";
	private static final Integer TEAM_4_ID = 103;
	private static final String TEAM_4_NAME = "TEAM4";
	private static final Integer TEAM_5_ID = 104;
	private static final String TEAM_5_NAME = "TEAM5";

	private static final String URL = "http://www.soccerbase.com/teams/results.sd?season_id={seasonNum}&team_id={teamId}&teamTabs=results";
	private static final String URL_FOR_TEAM = "http://www.soccerbase.com/teams/results.sd?season_id=" + SOCCERBASE_SEASON_2012 + "&team_id=" + TEAM_1_ID + "&teamTabs=results";

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
	public void shouldParseATeamPage () throws Exception {
		// Given
		when(mockWebPageReader.readWebPage(URL_FOR_TEAM)).thenReturn(getTeamPage());
		
		// When
		List<ParsedFixture> parsedFixtures = objectUnderTest.parseFixturesForTeam(SEASON, TEAM_1_ID);
		
		// Then
		assertEquals(4, parsedFixtures.size());
		
		ParsedFixture fixture1 = parsedFixtures.get(0);
		assertEquals(SEASON, fixture1.getSeasonId());
		assertEquals(DIV_1_ID, fixture1.getDivisionId());
		assertEquals(DIV_1_NAME, fixture1.getDivisionName());
		assertEquals(TEAM_1_ID, fixture1.getHomeTeamId());
		assertEquals(TEAM_1_NAME, fixture1.getHomeTeamName());
		assertEquals(TEAM_2_ID, fixture1.getAwayTeamId());
		assertEquals(TEAM_2_NAME, fixture1.getAwayTeamName());
		assertEquals(FIXTURE_DATE_1, StringToCalendarConverter.convertCalendarToDateString(fixture1.getFixtureDate()));
		assertEquals(new Integer(5), fixture1.getHomeGoals());
		assertEquals(new Integer(2), fixture1.getAwayGoals());
		assertNull(fixture1.getFixtureId());

		ParsedFixture fixture2 = parsedFixtures.get(1);
		assertEquals(SEASON, fixture2.getSeasonId());
		assertEquals(DIV_1_ID, fixture2.getDivisionId());
		assertEquals(DIV_1_NAME, fixture2.getDivisionName());
		assertEquals(TEAM_3_ID, fixture2.getHomeTeamId());
		assertEquals(TEAM_3_NAME, fixture2.getHomeTeamName());
		assertEquals(TEAM_1_ID, fixture2.getAwayTeamId());
		assertEquals(TEAM_1_NAME, fixture2.getAwayTeamName());
		assertEquals(FIXTURE_DATE_2, StringToCalendarConverter.convertCalendarToDateString(fixture2.getFixtureDate()));
		assertEquals(new Integer(3), fixture2.getHomeGoals());
		assertEquals(new Integer(0), fixture2.getAwayGoals());
		assertNull(fixture2.getFixtureId());

		ParsedFixture fixture3 = parsedFixtures.get(2);
		assertEquals(SEASON, fixture3.getSeasonId());
		assertEquals(DIV_2_ID, fixture3.getDivisionId());
		assertEquals(DIV_2_NAME, fixture3.getDivisionName());
		assertEquals(TEAM_1_ID, fixture3.getHomeTeamId());
		assertEquals(TEAM_1_NAME, fixture3.getHomeTeamName());
		assertEquals(TEAM_4_ID, fixture3.getAwayTeamId());
		assertEquals(TEAM_4_NAME, fixture3.getAwayTeamName());
		assertEquals(FIXTURE_DATE_3, StringToCalendarConverter.convertCalendarToDateString(fixture3.getFixtureDate()));
		assertEquals(new Integer(1), fixture3.getHomeGoals());
		assertEquals(new Integer(4), fixture3.getAwayGoals());
		assertNull(fixture3.getFixtureId());

		ParsedFixture fixture4 = parsedFixtures.get(3);
		assertEquals(SEASON, fixture4.getSeasonId());
		assertEquals(DIV_1_ID, fixture4.getDivisionId());
		assertEquals(DIV_1_NAME, fixture4.getDivisionName());
		assertEquals(TEAM_1_ID, fixture4.getHomeTeamId());
		assertEquals(TEAM_1_NAME, fixture4.getHomeTeamName());
		assertEquals(TEAM_5_ID, fixture4.getAwayTeamId());
		assertEquals(TEAM_5_NAME, fixture4.getAwayTeamName());
		assertEquals(FIXTURE_DATE_4, StringToCalendarConverter.convertCalendarToDateString(fixture4.getFixtureDate()));
		assertNull(fixture4.getHomeGoals());
		assertNull(fixture4.getAwayGoals());
		assertNull(fixture4.getFixtureId());

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
			objectUnderTest.parseFixturesForTeam(SEASON, TEAM_1_ID);
			fail("Should throw a FootballResultsReaderException");
		} catch (FootballResultsReaderException e) {
			// Then
			assertEquals(e.getMessage(), "No page found for team ID 100");
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
			objectUnderTest.parseFixturesForTeam(SEASON, TEAM_1_ID);
			fail("Should throw a FootballResultsReaderException");
		} catch (FootballResultsReaderException e) {
			// Then
			assertEquals(e.getMessage(), "Cannot load page for team ID 100");
		}
	}
	
	private List<String> getTeamPage() {
		List<String> page = new ArrayList<String>();

		page.add("<html>");
		page.add("<head>");
		page.add("");
		page.add("");

		addFixtureWithScore(page, SOCCERBASE_SEASON_2012, DIV_1_ID, DIV_1_NAME, TEAM_1_ID, TEAM_1_NAME, TEAM_2_ID, TEAM_2_NAME, FIXTURE_DATE_1, 5, 2);
		addFixtureWithScore(page, SOCCERBASE_SEASON_2012, DIV_1_ID, DIV_1_NAME, TEAM_3_ID, TEAM_3_NAME, TEAM_1_ID, TEAM_1_NAME, FIXTURE_DATE_2, 3, 0);
		addFixtureWithScore(page, SOCCERBASE_SEASON_2012, DIV_2_ID, DIV_2_NAME, TEAM_1_ID, TEAM_1_NAME, TEAM_4_ID, TEAM_4_NAME, FIXTURE_DATE_3, 1, 4);
		addFixtureWithoutScore(page, SOCCERBASE_SEASON_2012, DIV_1_ID, DIV_1_NAME, TEAM_1_ID, TEAM_1_NAME, TEAM_5_ID, TEAM_5_NAME, FIXTURE_DATE_4);
		
		page.add("</script>                ");
		page.add("</body>");
		page.add("");
		page.add("</html>");
		return page;
	}

	private void addFixtureWithScore(List<String> page, Integer season, Integer divisionId, String divisionName, Integer homeTeamId, String homeTeamName, Integer awayTeamId, String awayTeamName, String fixtureDate, int homeGoals, int awayGoals) {
		page.add("<tr class=\"match\" id=\"tgc663960\">");
		page.add("<a href=\"/tournaments/tournament.sd?comp_id=" + divisionId + "\" title=\"Go to " + divisionName + " competition page\">" + divisionName + "</a> <span class=\"hide\">");
		page.add("<a href=\"/matches/results.sd?date=" + fixtureDate + "\" title=\"Tu 28Aug 2012\">Tu 28Aug 2012</a>");
		page.add("<td class=\"team homeTeam\">");
		page.add("<a href=\"/teams/team.sd?team_id=" + homeTeamId + "&amp;season_id=" + season +"&amp;teamTabs=results\" title=\"Go to " + homeTeamName + " team page\">" + homeTeamName + "</a> <span class=\"hide\">");
		page.add("<td class=\"score\">");
		page.add("<a href=\"#\" class=\"vs\" title=\"View Match info\"><em>" + homeGoals + "</em>&nbsp;-&nbsp;<em>" + awayGoals + "</em></a> </td>");
		page.add("<td class=\"team awayTeam inactive\">");
		page.add("<a href=\"/teams/team.sd?team_id=" + awayTeamId + "&amp;season_id=" + season +"&amp;teamTabs=results\" title=\"Go to " + awayTeamName + " team page\">" + awayTeamName + "</a> <span class=\"hide\">");		page.add("");
		page.add("</td>");
		page.add("</tr>");
	}

	private void addFixtureWithoutScore(List<String> page, Integer season, Integer divisionId, String divisionName, Integer homeTeamId, String homeTeamName, Integer awayTeamId, String awayTeamName, String fixtureDate) {
		page.add("<tr class=\"match\" id=\"tgc663960\">");
		page.add("<a href=\"/tournaments/tournament.sd?comp_id=" + divisionId + "\" title=\"Go to " + divisionName + " competition page\">" + divisionName + "</a> <span class=\"hide\">");
		page.add("<a href=\"/matches/results.sd?date=" + fixtureDate + "\" title=\"Tu 28Aug 2012\">Tu 28Aug 2012</a>");
		page.add("<td class=\"team homeTeam\">");
		page.add("<a href=\"/teams/team.sd?team_id=" + homeTeamId + "&amp;season_id=" + season +"&amp;teamTabs=results\" title=\"Go to " + homeTeamName + " team page\">" + homeTeamName + "</a> <span class=\"hide\">");
		page.add("<td class=\"score\">");
		page.add("<span class=\"vs\">v</span>");
		page.add("<td class=\"team awayTeam inactive\">");
		page.add("<a href=\"/teams/team.sd?team_id=" + awayTeamId + "&amp;season_id=" + season +"&amp;teamTabs=results\" title=\"Go to " + awayTeamName + " team page\">" + awayTeamName + "</a> <span class=\"hide\">");		page.add("");
		page.add("</td>");
		page.add("</tr>");
	}
}
