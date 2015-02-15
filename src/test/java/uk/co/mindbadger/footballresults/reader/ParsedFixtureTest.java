package uk.co.mindbadger.footballresults.reader;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

public class ParsedFixtureTest {
	private static final String TEAM_ID_2 = "1001";
	private static final String TEAM_ID_1 = "1000";
	private static final String DIV_ID = "1";
	private static final int SEASON = 2000;
	private Calendar fixtureDate1;
	private Calendar fixtureDate2;

	@Before
	public void setup() {
		fixtureDate1 = Calendar.getInstance();
		fixtureDate1.set(Calendar.YEAR, 2000);
		fixtureDate2 = Calendar.getInstance();
		fixtureDate2.set(Calendar.YEAR, 2001);
	}
	
	@Test
	public void shouldNotBeEqualIfDifferentSeason () {
		// Given
		ParsedFixture fixture1 = new ParsedFixture();
		fixture1.setSeasonId(SEASON);
		fixture1.setDivisionId(DIV_ID);
		fixture1.setHomeTeamId(TEAM_ID_1);
		fixture1.setAwayTeamId(TEAM_ID_2);
		fixture1.setFixtureDate(fixtureDate1);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId("2");
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate1);

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
		fixture1.setFixtureDate(fixtureDate1);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId("9999");
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate1);

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
		fixture1.setFixtureDate(fixtureDate1);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId("9999");
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate1);

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
		fixture1.setFixtureDate(fixtureDate1);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId("9999");
		fixture2.setFixtureDate(fixtureDate1);

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
		fixture1.setFixtureDate(fixtureDate1);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate2);

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
		fixture1.setFixtureDate(fixtureDate1);

		ParsedFixture fixture2 = new ParsedFixture();
		fixture2.setSeasonId(SEASON);
		fixture2.setDivisionId(DIV_ID);
		fixture2.setHomeTeamId(TEAM_ID_1);
		fixture2.setAwayTeamId(TEAM_ID_2);
		fixture2.setFixtureDate(fixtureDate1);

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
		fixture1.setFixtureDate(fixtureDate1);
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
		fixture2.setFixtureDate(fixtureDate1);
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

}
