package mindbadger.footballresults.loader;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mindbadger.football.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mindbadger.footballresults.loader.FootballResultsLoader;
import mindbadger.footballresults.reader.FootballResultsReader;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.footballresults.saver.ParsedResultsSaver;
import mindbadger.football.repository.FixtureRepository;

public class FootballResultsLoaderTest {
	private static final int SEASON = 2000;
	
	private static final String READ_FIX_ID_1 = "100";
	private static final String READ_FIX_ID_2 = "100";

	private static final String READ_DIV_ID_1 = "1";
	private static final String READ_DIV_NAME_1 = "Premier";

	private static final String READ_TEAM_ID_1 = "500";
	private static final String READ_TEAM_NAME_1 = "Portsmouth";
	private static final String READ_TEAM_ID_2 = "501";
	private static final String READ_TEAM_NAME_2 = "Hull";

	private FootballResultsLoader objectUnderTest;

	@Mock
	private FootballResultsReader mockReader;
	@Mock
	private ParsedResultsSaver mockSaver;
	@Mock
	private FixtureRepository mockFixtureRepository;
	@Mock
	private Season mockSeason;
	@Mock
	private Division mockDivision;
	@Mock
	private SeasonDivision mockSeasonDivision;
	@Mock
	private Team mockHomeTeam;
	@Mock
	private Fixture mockFixture1;
	@Mock
	private Fixture mockFixture2;
	@Mock
	private Fixture mockFixture3_not_on_date_1_page;
	@Mock
	private Fixture mockFixture4;
	@Mock
	private Fixture mockFixture5_nodate;
	
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
		when(mockSeasonDivision.getSeason()).thenReturn(mockSeason);
		when(mockSeasonDivision.getDivision()).thenReturn(mockDivision);
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
		when (mockSeason.getSeasonNumber()).thenReturn(2000);
		when (mockHomeTeam.getTeamId()).thenReturn("100");
		
		// 3 fixtures on date 1
		when(mockFixture1.getFixtureDate()).thenReturn(date1);
		when(mockFixture3_not_on_date_1_page.getFixtureDate()).thenReturn(date1);
		when(mockFixture4.getFixtureDate()).thenReturn(date1);
		
		// 1 fixture on date 2
		when(mockFixture2.getFixtureDate()).thenReturn(date2);

		// Fixture 5 does not have a fixture date
		when(mockFixture5_nodate.getFixtureDate()).thenReturn(null);
		when(mockFixture5_nodate.getSeasonDivision()).thenReturn(mockSeasonDivision);
		when(mockFixture5_nodate.getHomeTeam()).thenReturn(mockHomeTeam);
		
		// Setup the unplayed Fixtures with dates
		List<Fixture> unplayedFixtures = new ArrayList<Fixture> ();
		unplayedFixtures.add(mockFixture1);
		unplayedFixtures.add(mockFixture2);
		unplayedFixtures.add(mockFixture3_not_on_date_1_page);
		unplayedFixtures.add(mockFixture4);
		when(mockFixtureRepository.getUnplayedFixturesBeforeToday()).thenReturn(unplayedFixtures);

		// Setup the unplayed Fixtures without dates
		List<Fixture> fixturesWithoutDates = new ArrayList<Fixture> ();
		fixturesWithoutDates.add(mockFixture5_nodate);
		when(mockFixtureRepository.getFixturesWithNoFixtureDate()).thenReturn(fixturesWithoutDates);

		
		List<ParsedFixture> parsedFixturesForDate1 = new ArrayList<ParsedFixture> ();
		parsedFixturesForDate1.add(createParsedFixture1());
		when (mockReader.readFixturesForDate(date1)).thenReturn(parsedFixturesForDate1);
		
		List<ParsedFixture> parsedFixturesForDate2 = new ArrayList<ParsedFixture> ();
		when (mockReader.readFixturesForDate(date2)).thenReturn(parsedFixturesForDate2);
		
		List<ParsedFixture> parsedFixturesForTeam = new  ArrayList<ParsedFixture> ();
		parsedFixturesForTeam.add(createParsedFixture2());		
		when (mockReader.readFixturesForTeamInSeason(2000, "100")).thenReturn(parsedFixturesForTeam);
		
		List<Fixture> unplayedFixturesOnDate1 = new ArrayList<Fixture> ();
		unplayedFixturesOnDate1.add(mockFixture3_not_on_date_1_page);
		when (mockFixtureRepository.getUnplayedFixturesOnDate(date1)).thenReturn(unplayedFixturesOnDate1);
		
		List<Fixture> unplayedFixturesOnDate2 = new ArrayList<Fixture> ();
		when (mockFixtureRepository.getUnplayedFixturesOnDate(date2)).thenReturn(unplayedFixturesOnDate2);
		
		// When
		objectUnderTest.loadResultsForRecentlyPlayedFixtures();
		
		// Then
		verify(mockFixtureRepository).getUnplayedFixturesBeforeToday();
		verify(mockReader,times(1)).readFixturesForDate(date1);
		verify(mockReader,times(1)).readFixturesForDate(date2);
		
		verify(mockSaver).saveFixtures(parsedFixturesForDate1);
		verify(mockSaver).saveFixtures(parsedFixturesForDate2);
		
		verify(mockFixtureRepository).getUnplayedFixturesOnDate(date1);
		verify(mockFixtureRepository).getUnplayedFixturesOnDate(date2);
		
		verify(mockFixture3_not_on_date_1_page, times(1)).setFixtureDate(null);
		verify(mockFixture3_not_on_date_1_page, times(1)).setHomeGoals(null);
		verify(mockFixture3_not_on_date_1_page, times(1)).setAwayGoals(null);
		verify(mockFixtureRepository, times(1)).save(mockFixture3_not_on_date_1_page);
		verify(mockFixtureRepository, never()).save(mockFixture1);
		verify(mockFixtureRepository, never()).save(mockFixture2);
		verify(mockFixtureRepository, never()).save(mockFixture4);
		verify(mockFixtureRepository, never()).save(mockFixture5_nodate);
		
		verify(mockReader).readFixturesForTeamInSeason(2000, "100");
		
		verify(mockSaver).saveFixtures(parsedFixturesForTeam);
	}
	
	// ----------------------------------------------------------------------------------------------------------

	private void initialiseAllListsAsEmpty() {
		fixturesReadFromReader = new ArrayList<ParsedFixture>();
	}

	private void createObjectToTestAndInjectDependencies() {
		objectUnderTest = new FootballResultsLoader();
		objectUnderTest.setFixtureRepository(mockFixtureRepository);
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

	private ParsedFixture createParsedFixture2() {
		ParsedFixture parsedFixture2 = new ParsedFixture();
		parsedFixture2.setFixtureId(READ_FIX_ID_2);
		parsedFixture2.setSeasonId(SEASON);
		parsedFixture2.setDivisionId(READ_DIV_ID_1);
		parsedFixture2.setDivisionName(READ_DIV_NAME_1);
		parsedFixture2.setHomeTeamId(READ_TEAM_ID_2);
		parsedFixture2.setHomeTeamName(READ_TEAM_NAME_2);
		parsedFixture2.setAwayTeamId(READ_TEAM_ID_1);
		parsedFixture2.setAwayTeamName(READ_TEAM_NAME_1);
		parsedFixture2.setFixtureDate(date2);
		parsedFixture2.setHomeGoals(2);
		parsedFixture2.setAwayGoals(2);
		return parsedFixture2;
	}
}
