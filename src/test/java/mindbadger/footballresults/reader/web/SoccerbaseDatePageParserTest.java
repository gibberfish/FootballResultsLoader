package mindbadger.footballresults.reader.web;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mindbadger.footballresults.loader.FootballResultsLoaderMapping;
import mindbadger.footballresults.reader.FootballResultsReaderException;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.footballresults.reader.web.SoccerbaseDatePageParser;
import mindbadger.util.Pauser;
import mindbadger.util.StringToCalendarConverter;
import mindbadger.web.WebPageReader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SoccerbaseDatePageParserTest {
	private static final String URL = "http://www.soccerbase.com/matches/results.sd?date={fixtureDate}";

	private static final String BOXING_DAY_DATE_STRING = "2008-12-26";

	private static final String URL_FOR_BOXING_DAY = "http://www.soccerbase.com/matches/results.sd?date=" + BOXING_DAY_DATE_STRING;

	private static final Integer SEASON = new Integer(2008);
	private static final String DIV_1_ID = "1";
	private static final String DIV_1_NAME = "DIV1";
	private static final String DIV_2_ID = "2";
	private static final String DIV_2_NAME = "DIV2";
	private static final String TEAM_1_ID = "100";
	private static final String TEAM_1_NAME = "TEAM1";
	private static final String TEAM_2_ID = "101";
	private static final String TEAM_2_NAME = "TEAM2";
	private static final String TEAM_3_ID = "102";
	private static final String TEAM_3_NAME = "TEAM3";
	private static final String TEAM_4_ID = "103";
	private static final String TEAM_4_NAME = "TEAM4";
	private static final String TEAM_5_ID = "104";
	private static final String TEAM_5_NAME = "TEAM5";
	private static final String TEAM_6_ID = "105";
	private static final String TEAM_6_NAME = "TEAM6";
	private static final String TEAM_7_ID = "106";
	private static final String TEAM_7_NAME = "TEAM7";
	private static final String TEAM_8_ID = "107";
	private static final String TEAM_8_NAME = "TEAM8";
	private String DIALECT = "soccerbase";

	private SoccerbaseDatePageParser objectUnderTest;

	@Mock private WebPageReader mockWebPageReader;
	@Mock private Pauser mockPauser;
	@Mock private FootballResultsLoaderMapping mockMapping;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new SoccerbaseDatePageParser();
		objectUnderTest.setWebPageReader(mockWebPageReader);
		objectUnderTest.setUrl(URL);
		objectUnderTest.setPauser(mockPauser);
		objectUnderTest.setMapping(mockMapping);
		objectUnderTest.setDialect(DIALECT);
	}

	@Test
	public void shouldThrowRuntimeExceptionIfFileNotFoundExceptionThrownDuringReadOfWebPage() throws Exception {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		fixtureDate.set(Calendar.YEAR, 2008);
		fixtureDate.set(Calendar.MONTH, 12);
		fixtureDate.set(Calendar.DAY_OF_MONTH, 26);

		when(mockWebPageReader.readWebPage(URL_FOR_BOXING_DAY)).thenThrow(new FileNotFoundException());

		// When
		try {
			objectUnderTest.parseFixturesForDate("2008-12-26");
			fail("Should throw a FootballResultsReaderException");
		} catch (FootballResultsReaderException e) {
			// Then
			assertEquals(e.getMessage(), "No page found for 2008-12-26");
		}
	}

	@Test
	public void shouldThrowRuntimeExceptionIfIOExceptionThrownDuringReadOfWebPage() throws Exception {
		// Given
		Calendar fixtureDate = Calendar.getInstance();
		fixtureDate.set(Calendar.YEAR, 2008);
		fixtureDate.set(Calendar.MONTH, 12);
		fixtureDate.set(Calendar.DAY_OF_MONTH, 26);

		when(mockWebPageReader.readWebPage(URL_FOR_BOXING_DAY)).thenThrow(new IOException());

		// When
		try {
			objectUnderTest.parseFixturesForDate("2008-12-26");
			fail("Should throw a FootballResultsReaderException");
		} catch (FootballResultsReaderException e) {
			// Then
			assertEquals(e.getMessage(), "Cannot load page for 2008-12-26");
		}
	}

	@Test
	public void shouldExcludeDivisionsNotInTheMapping() throws Exception {
		// Given
		when(mockWebPageReader.readWebPage(URL_FOR_BOXING_DAY)).thenReturn(get2008BoxingDayPage());
		List<String> listOfIncludedDivisions = new ArrayList<String> ();
		listOfIncludedDivisions.add(DIV_1_ID);
		when(mockMapping.getIncludedDivisions(DIALECT)).thenReturn(listOfIncludedDivisions);

		// When
		List<ParsedFixture> parsedFixtures = objectUnderTest.parseFixturesForDate("2008-12-26");

		// Then
		assertEquals(2, parsedFixtures.size());
		ParsedFixture fixture1 = parsedFixtures.get(0);
		assertEquals(SEASON, fixture1.getSeasonId());
		assertEquals(DIV_1_ID, fixture1.getDivisionId());
		assertEquals(DIV_1_NAME, fixture1.getDivisionName());
		assertEquals(TEAM_1_ID, fixture1.getHomeTeamId());
		assertEquals(TEAM_1_NAME, fixture1.getHomeTeamName());
		assertEquals(TEAM_2_ID, fixture1.getAwayTeamId());
		assertEquals(TEAM_2_NAME, fixture1.getAwayTeamName());
		assertEquals(BOXING_DAY_DATE_STRING, StringToCalendarConverter.convertCalendarToDateString(fixture1.getFixtureDate()));
		assertEquals(new Integer(5), fixture1.getHomeGoals());
		assertEquals(new Integer(2), fixture1.getAwayGoals());
		assertNull(fixture1.getFixtureId());

		ParsedFixture fixture2 = parsedFixtures.get(1);
		assertEquals(SEASON, fixture2.getSeasonId());
		assertEquals(DIV_1_ID, fixture2.getDivisionId());
		assertEquals(DIV_1_NAME, fixture2.getDivisionName());
		assertEquals(TEAM_3_ID, fixture2.getHomeTeamId());
		assertEquals(TEAM_3_NAME, fixture2.getHomeTeamName());
		assertEquals(TEAM_4_ID, fixture2.getAwayTeamId());
		assertEquals(TEAM_4_NAME, fixture2.getAwayTeamName());
		assertEquals(BOXING_DAY_DATE_STRING, StringToCalendarConverter.convertCalendarToDateString(fixture2.getFixtureDate()));
		assertEquals(new Integer(3), fixture2.getHomeGoals());
		assertEquals(new Integer(0), fixture2.getAwayGoals());
		assertNull(fixture2.getFixtureId());
		
		verify (mockPauser).pause();
	}

	@Test
	public void shouldParseADatePage() throws Exception {
		// Given
		when(mockWebPageReader.readWebPage(URL_FOR_BOXING_DAY)).thenReturn(get2008BoxingDayPage());
		List<String> listOfIncludedDivisions = new ArrayList<String> ();
		listOfIncludedDivisions.add(DIV_1_ID);
		listOfIncludedDivisions.add(DIV_2_ID);
		when(mockMapping.getIncludedDivisions(DIALECT)).thenReturn(listOfIncludedDivisions);

		// When
		List<ParsedFixture> parsedFixtures = objectUnderTest.parseFixturesForDate("2008-12-26");

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
		assertEquals(BOXING_DAY_DATE_STRING, StringToCalendarConverter.convertCalendarToDateString(fixture1.getFixtureDate()));
		assertEquals(new Integer(5), fixture1.getHomeGoals());
		assertEquals(new Integer(2), fixture1.getAwayGoals());
		assertNull(fixture1.getFixtureId());

		ParsedFixture fixture2 = parsedFixtures.get(1);
		assertEquals(SEASON, fixture2.getSeasonId());
		assertEquals(DIV_1_ID, fixture2.getDivisionId());
		assertEquals(DIV_1_NAME, fixture2.getDivisionName());
		assertEquals(TEAM_3_ID, fixture2.getHomeTeamId());
		assertEquals(TEAM_3_NAME, fixture2.getHomeTeamName());
		assertEquals(TEAM_4_ID, fixture2.getAwayTeamId());
		assertEquals(TEAM_4_NAME, fixture2.getAwayTeamName());
		assertEquals(BOXING_DAY_DATE_STRING, StringToCalendarConverter.convertCalendarToDateString(fixture2.getFixtureDate()));
		assertEquals(new Integer(3), fixture2.getHomeGoals());
		assertEquals(new Integer(0), fixture2.getAwayGoals());
		assertNull(fixture2.getFixtureId());

		ParsedFixture fixture3 = parsedFixtures.get(2);
		assertEquals(SEASON, fixture3.getSeasonId());
		assertEquals(DIV_2_ID, fixture3.getDivisionId());
		assertEquals(DIV_2_NAME, fixture3.getDivisionName());
		assertEquals(TEAM_5_ID, fixture3.getHomeTeamId());
		assertEquals(TEAM_5_NAME, fixture3.getHomeTeamName());
		assertEquals(TEAM_6_ID, fixture3.getAwayTeamId());
		assertEquals(TEAM_6_NAME, fixture3.getAwayTeamName());
		assertEquals(BOXING_DAY_DATE_STRING, StringToCalendarConverter.convertCalendarToDateString(fixture3.getFixtureDate()));
		assertEquals(new Integer(1), fixture3.getHomeGoals());
		assertEquals(new Integer(4), fixture3.getAwayGoals());
		assertNull(fixture3.getFixtureId());

		ParsedFixture fixture4 = parsedFixtures.get(3);
		assertEquals(SEASON, fixture4.getSeasonId());
		assertEquals(DIV_2_ID, fixture4.getDivisionId());
		assertEquals(DIV_2_NAME, fixture4.getDivisionName());
		assertEquals(TEAM_7_ID, fixture4.getHomeTeamId());
		assertEquals(TEAM_7_NAME, fixture4.getHomeTeamName());
		assertEquals(TEAM_8_ID, fixture4.getAwayTeamId());
		assertEquals(TEAM_8_NAME, fixture4.getAwayTeamName());
		assertEquals(BOXING_DAY_DATE_STRING, StringToCalendarConverter.convertCalendarToDateString(fixture4.getFixtureDate()));
		assertNull(fixture4.getHomeGoals());
		assertNull(fixture4.getAwayGoals());
		assertNull(fixture4.getFixtureId());
		
		verify (mockPauser).pause();
	}

	private List<String> get2008BoxingDayPage() {
		List<String> page = new ArrayList<String>();

		page.add("<html>");
		page.add("<head>");
		page.add("");
		page.add("");

		addDivisionHTML(page, DIV_1_ID, DIV_1_NAME);

		addFixtureHTMLWithScore(page, TEAM_1_ID, TEAM_1_NAME, TEAM_2_ID, TEAM_2_NAME, 5, 2);
		addFixtureHTMLWithScore(page, TEAM_3_ID, TEAM_3_NAME, TEAM_4_ID, TEAM_4_NAME, 3, 0);

		addDivisionHTML(page, DIV_2_ID, DIV_2_NAME);

		addFixtureHTMLWithScore(page, TEAM_5_ID, TEAM_5_NAME, TEAM_6_ID, TEAM_6_NAME, 1, 4);
		addFixtureHTMLWithoutScore(page, TEAM_7_ID, TEAM_7_NAME, TEAM_8_ID, TEAM_8_NAME);

		page.add("");
		page.add("<tr>");
		page.add("<td colspan=3>");
		page.add("<form name=date>");
		page.add("<select name=date>");
		page.add("<option value='2008-12-01 00:00:00'>");
		page.add("");
		page.add("<option value='2008-12-02 00:00:00'>");
		page.add("");
		page.add("<option value='2008-12-03 00:00:00'>");
		page.add("");
		page.add("<option value='2008-12-04 00:00:00'>");
		page.add("</option>");
		page.add("</select>");
		page.add("</script>                ");
		page.add("</body>");
		page.add("");
		page.add("</html>");

		return page;
	}

	private void addFixtureHTMLWithScore(List<String> page, String homeTeamId, String homeTeamName, String awayTeamId, String awayTeamName, Integer homeGoals, Integer awayGoals) {
		page.add(" <a href=\"/matches/results.sd?date=" + BOXING_DAY_DATE_STRING + "\" title=\"We 26Dec 2008\">We 26Dec 2008</a>");
		page.add(" <td class=\"team homeTeam\">");
		page.add(" <a href=\"/teams/team.sd?team_id=" + homeTeamId + "\" title=\"Go to Everton team page\">" + homeTeamName + "</a> </td>");
		page.add(" <td class=\"score\">");
		page.add(" <a href=\"#\" class=\"vs\" title=\"View Match info\"><em>" + homeGoals + "</em>&nbsp;-&nbsp;<em>" + awayGoals + "</em></a> </td>");
		page.add(" <td class=\"team awayTeam\">");
		page.add(" <a href=\"/teams/team.sd?team_id=" + awayTeamId + "\" title=\"Go to Wigan team page\">" + awayTeamName + "</a> </td>");
	}

	private void addFixtureHTMLWithoutScore(List<String> page, String homeTeamId, String homeTeamName, String awayTeamId, String awayTeamName) {
		page.add(" <a href=\"/matches/results.sd?date=" + BOXING_DAY_DATE_STRING + "\" title=\"We 26Dec 2008\">We 26Dec 2008</a>");
		page.add(" <td class=\"team homeTeam\">");
		page.add(" <a href=\"/teams/team.sd?team_id=" + homeTeamId + "\" title=\"Go to Everton team page\">" + homeTeamName + "</a> </td>");
		page.add(" <td class=\"score\">");
		page.add(" <span class=\"vs\">v</span>");
		page.add(" <td class=\"team awayTeam\">");
		page.add(" <a href=\"/teams/team.sd?team_id=" + awayTeamId + "\" title=\"Go to Wigan team page\">" + awayTeamName + "</a> </td>");
	}

	private void addDivisionHTML(List<String> page, String divisionId, String divisionName) {
		page.add(" <a href=\"/tournaments/tournament.sd?comp_id=" + divisionId + "\" title=\"Go to English Premier competition page\">" + divisionName + "</a>");
	}
}
