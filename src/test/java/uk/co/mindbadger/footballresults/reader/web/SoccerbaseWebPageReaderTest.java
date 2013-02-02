package uk.co.mindbadger.footballresults.reader.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.mockito.*;

import uk.co.mindbadger.footballresults.reader.ParsedFixture;

public class SoccerbaseWebPageReaderTest {
	private static final String BOXING_DAY_2000 = "2000-12-26";
	private static final String DIALECT = "soccerbase";
	private static final int SEASON_NUMBER = 2000;
	
	private SoccerbaseWebPageReader objectUnderTest;
	
	@Mock	private SoccerbaseTeamPageParser mockTeamPageParser;
	@Mock private SoccerbaseDatePageParser mockDatePageParser;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new SoccerbaseWebPageReader();
		objectUnderTest.setDialect(DIALECT);
		objectUnderTest.setTeamPageParser(mockTeamPageParser);
		objectUnderTest.setDatePageParser(mockDatePageParser);
	}
	
	@Test
	public void shouldReadTheFixturesOnBoxingDayToGetTheInitialDivisionsAndTeams () {
		// Given
		
		// When
		List<ParsedFixture> parsedFixtures = objectUnderTest.readFixturesForSeason(SEASON_NUMBER);
		
		// Then
		verify(mockDatePageParser).parseFixturesForDate(BOXING_DAY_2000);
	}
	
	@Test
	public void shouldReadTheTeamPagesForEachTeamPlayingOnBoxingDay () {
		// Given
		Calendar fixtureDate = Calendar.getInstance();

		ParsedFixture fixture1 = createParsedFixture(1000, 2000, 1, "Premier", 100, "Portsmouth", 101, "Leeds", fixtureDate , 3, 0);
		ParsedFixture fixture2 = createParsedFixture(1001, 2000, 2, "Championship", 102, "Southampton", 103, "Grimsby", fixtureDate , 0, 7);
		ParsedFixture fixture3 = createParsedFixture(1000, 2000, 1, "Premier", 100, "Portsmouth", 104, "Liverpool", fixtureDate , 1, 1);
		ParsedFixture fixture4 = createParsedFixture(1000, 2000, 1, "Premier", 105, "Everton", 100, "Portsmouth", fixtureDate , 1, 4);
		ParsedFixture fixture5 = createParsedFixture(1001, 2000, 2, "Championship", 102, "Southampton", 103, "Charlton", fixtureDate , 1, 2);
		ParsedFixture fixture6 = createParsedFixture(1001, 2000, 2, "Championship", 102, "Southampton", 103, "QPR", fixtureDate , 0, 5);

		List<ParsedFixture> fixturesOnBoxingDay = new ArrayList<ParsedFixture> ();
		fixturesOnBoxingDay.add(fixture1);
		fixturesOnBoxingDay.add(fixture2);

		when(mockDatePageParser.parseFixturesForDate(BOXING_DAY_2000)).thenReturn(fixturesOnBoxingDay);
		
		List<ParsedFixture> fixturesForPortsmouth = new ArrayList<ParsedFixture> ();
		fixturesForPortsmouth.add(fixture1);
		fixturesForPortsmouth.add(fixture3);
		fixturesForPortsmouth.add(fixture4);
		
		when(mockTeamPageParser.parseFixturesForTeam(SEASON_NUMBER, 100)).thenReturn(fixturesForPortsmouth);
		
		List<ParsedFixture> fixturesForLeeds = new ArrayList<ParsedFixture> ();
		fixturesForLeeds.add(fixture1);
		
		when(mockTeamPageParser.parseFixturesForTeam(SEASON_NUMBER, 101)).thenReturn(fixturesForLeeds);
		
		List<ParsedFixture> fixturesForSouthampton = new ArrayList<ParsedFixture> ();
		fixturesForSouthampton.add(fixture2);
		fixturesForSouthampton.add(fixture5);
		fixturesForSouthampton.add(fixture6);
		
		when(mockTeamPageParser.parseFixturesForTeam(SEASON_NUMBER, 102)).thenReturn(fixturesForSouthampton);
		
		List<ParsedFixture> fixturesForGrimsby = new ArrayList<ParsedFixture> ();
		fixturesForGrimsby.add(fixture2);
		
		when(mockTeamPageParser.parseFixturesForTeam(SEASON_NUMBER, 103)).thenReturn(fixturesForGrimsby);
		
		// When
		List<ParsedFixture> parsedFixtures = objectUnderTest.readFixturesForSeason(SEASON_NUMBER);
		
		// Then
		verify(mockTeamPageParser).parseFixturesForTeam(SEASON_NUMBER, 100);
		verify(mockTeamPageParser).parseFixturesForTeam(SEASON_NUMBER, 101);
		verify(mockTeamPageParser).parseFixturesForTeam(SEASON_NUMBER, 102);
		verify(mockTeamPageParser).parseFixturesForTeam(SEASON_NUMBER, 103);
		
		assertEquals (6, parsedFixtures.size());
		assertTrue (parsedFixtures.contains(fixture1));
		assertTrue (parsedFixtures.contains(fixture2));
		assertTrue (parsedFixtures.contains(fixture3));
		assertTrue (parsedFixtures.contains(fixture4));
		assertTrue (parsedFixtures.contains(fixture5));
		assertTrue (parsedFixtures.contains(fixture6));
	}

	
	
	
	
	
	
	private ParsedFixture createParsedFixture (Integer fixtureId, Integer season, Integer divisionId, String divisionName, Integer homeTeamId, String homeTeamName, Integer awayTeamId, String awayTeamName, Calendar fixtureDate, Integer homeGoals, Integer awayGoals) {
		ParsedFixture parsedFixture = new ParsedFixture ();
		parsedFixture.setFixtureId(fixtureId);
		parsedFixture.setSeasonId(season);
		parsedFixture.setDivisionId(divisionId);
		parsedFixture.setDivisionName(divisionName);
		parsedFixture.setHomeTeamId(homeTeamId);
		parsedFixture.setHomeTeamName(homeTeamName);
		parsedFixture.setAwayTeamId(awayTeamId);
		parsedFixture.setAwayTeamName(awayTeamName);
		parsedFixture.setFixtureDate(fixtureDate);
		parsedFixture.setHomeGoals(homeGoals);
		parsedFixture.setAwayGoals(awayGoals);
		return parsedFixture;
	}
}
