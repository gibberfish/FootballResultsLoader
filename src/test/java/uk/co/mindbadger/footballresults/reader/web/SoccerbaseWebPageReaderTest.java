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
		List<ParsedFixture> fixturesOnBoxingDay = new ArrayList<ParsedFixture> ();
		Calendar fixtureDate = Calendar.getInstance();
		fixturesOnBoxingDay.add(createParsedFixture(1000, 2000, 1, "Premier", 100, "Portsmouth", 101, "Leeds", fixtureDate , 3, 0));
		fixturesOnBoxingDay.add(createParsedFixture(1001, 2000, 2, "Championship", 102, "Southampton", 103, "Grimsby", fixtureDate , 0, 7));
		
		when(mockDatePageParser.parseFixturesForDate(BOXING_DAY_2000)).thenReturn(fixturesOnBoxingDay);
		
		// When
		List<ParsedFixture> parsedFixtures = objectUnderTest.readFixturesForSeason(SEASON_NUMBER);
		
		// Then
		verify(mockTeamPageParser).parseFixturesForTeam(SEASON_NUMBER, 100);
		verify(mockTeamPageParser).parseFixturesForTeam(SEASON_NUMBER, 101);
		verify(mockTeamPageParser).parseFixturesForTeam(SEASON_NUMBER, 102);
		verify(mockTeamPageParser).parseFixturesForTeam(SEASON_NUMBER, 103);
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
