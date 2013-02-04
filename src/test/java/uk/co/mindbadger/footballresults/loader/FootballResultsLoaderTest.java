package uk.co.mindbadger.footballresults.loader;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;
import uk.co.mindbadger.footballresultsanalyser.domain.FixtureImpl;

public class FootballResultsLoaderTest {
	private static final int SEASON = 2000;
	
	private static final Integer READ_FIX_ID_1 = 100;

	private static final Integer READ_DIV_ID_1 = 1;
	private static final String READ_DIV_NAME_1 = "Premier";

	private static final Integer READ_TEAM_ID_1 = 500;
	private static final String READ_TEAM_NAME_1 = "Portsmouth";
	private static final Integer READ_TEAM_ID_2 = 501;
	private static final String READ_TEAM_NAME_2 = "Hull";

	private FootballResultsLoader objectUnderTest;

	@Mock
	private FootballResultsAnalyserDAO mockDao;
	@Mock
	private FootballResultsReader mockReader;
	@Mock
	private FootballResultsSaver mockSaver;

	private Calendar date1;
	private Calendar date2;

	private List<ParsedFixture> fixturesReadFromReader;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		createObjectToTestAndInjectDependencies();

		createDatesUsedInTheTests();

		initialiseAllListsAsEmpty();

		when(mockReader.readFixturesForSeason(SEASON)).thenReturn(fixturesReadFromReader);
	}

	// ----------------------------------------------------------------------------------------------

	@Test
	public void shouldReadFixturesForSeason() {
		// Given

		// When
		objectUnderTest.loadResultsForSeason(SEASON);

		// Then
		verify(mockReader).readFixturesForSeason(SEASON);
		verify(mockSaver).saveFixtures(fixturesReadFromReader);
	}

	@Test
	public void shouldLoadMissingFixtureDates () {
		// Given
		Fixture fixture1 = new FixtureImpl();
		fixture1.setFixtureDate(date1);
		
		Fixture fixture2 = new FixtureImpl();
		fixture2.setFixtureDate(date2);
		
		List<Fixture> fixtures = new ArrayList<Fixture> ();
		fixtures.add(fixture1);
		fixtures.add(fixture2);
		when(mockDao.getUnplayedFixturesBeforeToday()).thenReturn(fixtures);
		
		List<ParsedFixture> parsedFixturesForDate1 = new ArrayList<ParsedFixture> ();
		parsedFixturesForDate1.add(createParsedFixture1());
		
		List<ParsedFixture> parsedFixturesForDate2 = new ArrayList<ParsedFixture> ();
		
		when (mockReader.readFixturesForDate(date1)).thenReturn(parsedFixturesForDate1);
		when (mockReader.readFixturesForDate(date2)).thenReturn(parsedFixturesForDate2);
		
		// When
		objectUnderTest.loadResultsForRecentlyPlayedFixtures();
		
		// Then
		verify(mockDao).getUnplayedFixturesBeforeToday();
		verify(mockReader).readFixturesForDate(date1);
		verify(mockReader).readFixturesForDate(date2);
		
		verify(mockSaver).saveFixtures(parsedFixturesForDate1);
		verify(mockSaver).saveFixtures(parsedFixturesForDate2);
	}
	
	// ----------------------------------------------------------------------------------------------------------

	private void initialiseAllListsAsEmpty() {
		fixturesReadFromReader = new ArrayList<ParsedFixture>();
	}

	private void createObjectToTestAndInjectDependencies() {
		objectUnderTest = new FootballResultsLoader();
		objectUnderTest.setDao(mockDao);
		objectUnderTest.setReader(mockReader);
		objectUnderTest.setSaver(mockSaver);
	}

	private void createDatesUsedInTheTests() {
		date1 = Calendar.getInstance();
		date1.set(Calendar.DAY_OF_MONTH, 20);
		date1.set(Calendar.MONTH, 9);
		date1.set(Calendar.YEAR, 2000);

		date2 = Calendar.getInstance();
		date2.set(Calendar.DAY_OF_MONTH, 28);
		date2.set(Calendar.MONTH, 10);
		date2.set(Calendar.YEAR, 2000);
	}

	private ParsedFixture createParsedFixture1() {
		ParsedFixture parsedFixture1 = new ParsedFixture();
		parsedFixture1.setFixtureId(READ_FIX_ID_1);
		parsedFixture1.setSeasonId(SEASON);
		parsedFixture1.setDivisionId(READ_DIV_ID_1);
		parsedFixture1.setDivisionName(READ_DIV_NAME_1);
		parsedFixture1.setHomeTeamId(READ_TEAM_ID_1);
		parsedFixture1.setHomeTeamName(READ_TEAM_NAME_1);
		parsedFixture1.setAwayTeamId(READ_TEAM_ID_2);
		parsedFixture1.setAwayTeamName(READ_TEAM_NAME_2);
		parsedFixture1.setFixtureDate(date1);
		parsedFixture1.setHomeGoals(4);
		parsedFixture1.setAwayGoals(5);
		return parsedFixture1;
	}
}
