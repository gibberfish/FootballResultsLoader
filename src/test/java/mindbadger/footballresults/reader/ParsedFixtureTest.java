package mindbadger.footballresults.reader;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import mindbadger.footballresults.reader.ParsedFixture;

import org.junit.Before;
import org.junit.Test;

public class ParsedFixtureTest {
	private static final String TEAM_ID_2 = "1001";
	private static final String TEAM_ID_1 = "1000";
	private static final String DIV_ID = "1";
	private static final int SEASON = 2000;
	private Calendar fixtureDate2000_06_06;
	private Calendar fixtureDate2001_06_06;
	private Calendar fixtureDate2001_06_04;
	private Calendar fixtureDate2001_05_20;

	@Before
	public void setup() {
		fixtureDate2000_06_06 = Calendar.getInstance();
		fixtureDate2000_06_06.set(Calendar.YEAR, 2000);
		fixtureDate2000_06_06.set(Calendar.MONTH, 6);
		fixtureDate2000_06_06.set(Calendar.DAY_OF_MONTH, 6);
		
		fixtureDate2001_06_06 = Calendar.getInstance();
		fixtureDate2001_06_06.set(Calendar.YEAR, 2001);
		fixtureDate2001_06_06.set(Calendar.MONTH, 6);
		fixtureDate2001_06_06.set(Calendar.DAY_OF_MONTH, 6);
		
		fixtureDate2001_06_04 = Calendar.getInstance();
		fixtureDate2001_06_04.set(Calendar.YEAR, 2001);
		fixtureDate2001_06_04.set(Calendar.MONTH, 6);
		fixtureDate2001_06_04.set(Calendar.DAY_OF_MONTH, 4);

		fixtureDate2001_05_20 = Calendar.getInstance();
		fixtureDate2001_05_20.set(Calendar.YEAR, 2001);
		fixtureDate2001_05_20.set(Calendar.MONTH, 5);
		fixtureDate2001_05_20.set(Calendar.DAY_OF_MONTH, 20);

	}
	
	@Test
	public void shouldNotBeEqualIfDifferentSeason () {
		// Given
		ParsedFixture fixture1 = new ParsedFixture();
		fixture1.setSeasonId(SEASON);
		fixture1.setDivisionId(DIV_ID);
		fixture1.setHomeTeamId(TEAM_ID_1);
		fixture1.setAwayTeamId(TEAM_ID_2);
		fixture1.setFixtureDate(fixtureDate2000_06_06);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId("2");
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate2000_06_06);

		// When
		boolean equals = fixture1.equals(fixture2);
		
		// Then
		assertFalse(equals);
	}

	@Test
	public void shouldNotBeEqualIfDifferentDivision () {
		// Given
		ParsedFixture fixture1 = new ParsedFixture();
		fixture1.setSeasonId(SEASON);
		fixture1.setDivisionId(DIV_ID);
		fixture1.setHomeTeamId(TEAM_ID_1);
		fixture1.setAwayTeamId(TEAM_ID_2);
		fixture1.setFixtureDate(fixtureDate2000_06_06);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId("9999");
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate2000_06_06);

		// When
		boolean equals = fixture1.equals(fixture2);
		
		// Then
		assertFalse(equals);
	}

	@Test
	public void shouldNotBeEqualIfDifferentHomeTeam () {
		// Given
		ParsedFixture fixture1 = new ParsedFixture();
		fixture1.setSeasonId(SEASON);
		fixture1.setDivisionId(DIV_ID);
		fixture1.setHomeTeamId(TEAM_ID_1);
		fixture1.setAwayTeamId(TEAM_ID_2);
		fixture1.setFixtureDate(fixtureDate2000_06_06);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId("9999");
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate2000_06_06);

		// When
		boolean equals = fixture1.equals(fixture2);
		
		// Then
		assertFalse(equals);
	}

	@Test
	public void shouldNotBeEqualIfDifferentAwayTeam () {
		// Given
		ParsedFixture fixture1 = new ParsedFixture();
		fixture1.setSeasonId(SEASON);
		fixture1.setDivisionId(DIV_ID);
		fixture1.setHomeTeamId(TEAM_ID_1);
		fixture1.setAwayTeamId(TEAM_ID_2);
		fixture1.setFixtureDate(fixtureDate2000_06_06);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId("9999");
		fixture2.setFixtureDate(fixtureDate2000_06_06);

		// When
		boolean equals = fixture1.equals(fixture2);
		
		// Then
		assertFalse(equals);
	}

	@Test
	public void shouldNotBeEqualIfDifferentDate () {
		// Given
		ParsedFixture fixture1 = new ParsedFixture();
		fixture1.setSeasonId(SEASON);
		fixture1.setDivisionId(DIV_ID);
		fixture1.setHomeTeamId(TEAM_ID_1);
		fixture1.setAwayTeamId(TEAM_ID_2);
		fixture1.setFixtureDate(fixtureDate2000_06_06);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate2001_06_06);

		// When
		boolean equals = fixture1.equals(fixture2);
		
		// Then
		assertFalse(equals);
	}

	@Test
	public void shouldBeEqualIfSeasonDivTeamsAndDateAreIdentical () {
		// Given
		ParsedFixture fixture1 = new ParsedFixture();
		fixture1.setSeasonId(SEASON);
		fixture1.setDivisionId(DIV_ID);
		fixture1.setHomeTeamId(TEAM_ID_1);
		fixture1.setAwayTeamId(TEAM_ID_2);
		fixture1.setFixtureDate(fixtureDate2000_06_06);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate2000_06_06);

		// When
		boolean equals = fixture1.equals(fixture2);
		
		// Then
		assertTrue(equals);
	}

	@Test
	public void shouldStillBeEqualIfSeasonDivTeamsAndDateAreIdenticalButNamesAndScoreAreDifferent () {
		// Given
		ParsedFixture fixture1 = new ParsedFixture();
		fixture1.setSeasonId(SEASON);
		fixture1.setDivisionId(DIV_ID);
		fixture1.setHomeTeamId(TEAM_ID_1);
		fixture1.setAwayTeamId(TEAM_ID_2);
		fixture1.setFixtureDate(fixtureDate2000_06_06);
		fixture1.setDivisionName("DIV1");
		fixture1.setHomeTeamName("TEAM1");
		fixture1.setAwayTeamName("TEAM2");
		fixture1.setHomeGoals(1);
		fixture1.setAwayGoals(2);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate2000_06_06);
		fixture2.setDivisionName("XXDIV1");
		fixture2.setHomeTeamName("XXTEAM1");
		fixture2.setAwayTeamName("XXTEAM2");
		fixture2.setHomeGoals(3);
		fixture2.setAwayGoals(4);

		// When
		boolean equals = fixture1.equals(fixture2);
		
		// Then
		assertTrue(equals);
	}

	@Test
	public void shouldSortFixtures () {
		// Given
		ParsedFixture fixture1 = new ParsedFixture();
		fixture1.setSeasonId(2001);
		fixture1.setDivisionId(DIV_ID);
		fixture1.setHomeTeamId(TEAM_ID_1);
		fixture1.setAwayTeamId(TEAM_ID_2);
		fixture1.setFixtureDate(fixtureDate2001_06_04);
		fixture1.setDivisionName("DIV1");
		fixture1.setHomeTeamName("Z_TEAM");
		fixture1.setAwayTeamName("A_TEAM");
		fixture1.setHomeGoals(3);
		fixture1.setAwayGoals(4);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(2001);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate2001_06_04);
		fixture2.setDivisionName("DIV1");
		fixture2.setHomeTeamName("C_TEAM");
		fixture2.setAwayTeamName("D_TEAM");
		fixture2.setHomeGoals(3);
		fixture2.setAwayGoals(4);

		ParsedFixture fixture3 = new ParsedFixture();
		fixture3.setSeasonId(2000);
		fixture3.setDivisionId(DIV_ID);
		fixture3.setHomeTeamId(TEAM_ID_1);
		fixture3.setAwayTeamId(TEAM_ID_2);
		fixture3.setFixtureDate(fixtureDate2000_06_06);
		fixture3.setDivisionName("DIV1");
		fixture3.setHomeTeamName("A_TEAM");
		fixture3.setAwayTeamName("B_TEAM");
		fixture3.setHomeGoals(1);
		fixture3.setAwayGoals(2);

		ParsedFixture fixture4 = new ParsedFixture();
		fixture4.setSeasonId(2001);
		fixture4.setDivisionId(DIV_ID);
		fixture4.setHomeTeamId(TEAM_ID_1);
		fixture4.setAwayTeamId(TEAM_ID_2);
		fixture4.setFixtureDate(fixtureDate2001_05_20);
		fixture4.setDivisionName("DIV1");
		fixture4.setHomeTeamName("A_TEAM");
		fixture4.setAwayTeamName("B_TEAM");
		fixture4.setHomeGoals(3);
		fixture4.setAwayGoals(4);

		ParsedFixture fixture5 = new ParsedFixture();
		fixture5.setSeasonId(2001);
		fixture5.setDivisionId(DIV_ID);
		fixture5.setHomeTeamId(TEAM_ID_1);
		fixture5.setAwayTeamId(TEAM_ID_2);
		fixture5.setFixtureDate(fixtureDate2001_06_06);
		fixture5.setDivisionName("DIV1");
		fixture5.setHomeTeamName("A_TEAM");
		fixture5.setAwayTeamName("B_TEAM");
		fixture5.setHomeGoals(3);
		fixture5.setAwayGoals(4);

		ParsedFixture fixture6 = new ParsedFixture();
		fixture6.setSeasonId(2001);
		fixture6.setDivisionId(DIV_ID);
		fixture6.setHomeTeamId(TEAM_ID_1);
		fixture6.setAwayTeamId(TEAM_ID_2);
		fixture6.setFixtureDate(null);
		fixture6.setDivisionName("DIV1");
		fixture6.setHomeTeamName("A_TEAM");
		fixture6.setAwayTeamName("B_TEAM");
		fixture6.setHomeGoals(3);
		fixture6.setAwayGoals(4);

		// When
		List<ParsedFixture> list = new ArrayList<ParsedFixture> ();
		list.add(fixture1);
		list.add(fixture2);
		list.add(fixture3);
		list.add(fixture4);
		list.add(fixture5);
		list.add(fixture6);
		
		Collections.sort(list);
		
		// Then
		assertEquals (fixture3, list.get(0));
		assertEquals (fixture6, list.get(1));
		assertEquals (fixture4, list.get(2));
		assertEquals (fixture2, list.get(3));
		assertEquals (fixture1, list.get(4));
		assertEquals (fixture5, list.get(5));
		
	}
	
}
