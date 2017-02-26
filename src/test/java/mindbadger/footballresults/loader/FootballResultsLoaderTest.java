package mindbadger.footballresults.loader;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mindbadger.footballresults.loader.FootballResultsLoader;
import mindbadger.footballresults.reader.FootballResultsReader;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.footballresults.saver.ParsedResultsSaver;
import mindbadger.football.domain.Fixture;
import mindbadger.football.domain.FixtureImpl;
import mindbadger.football.domain.Season;
import mindbadger.football.domain.SeasonImpl;
import mindbadger.football.domain.Team;
import mindbadger.football.domain.TeamImpl;
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

		Fixture fixture3 = new FixtureImpl();
		fixture3.setFixtureDate(date1);

		List<Fixture> unplayedFixtures = new ArrayList<Fixture> ();
		unplayedFixtures.add(fixture1);
		unplayedFixtures.add(fixture2);
		unplayedFixtures.add(fixture3);
		when(mockFixtureRepository.getUnplayedFixturesBeforeToday()).thenReturn(unplayedFixtures);

		Fixture fixture4 = new FixtureImpl();
		fixture4.setFixtureDate(null);
		Season season = new SeasonImpl ();
		season.setSeasonNumber(2000);
		fixture4.setSeason(season );
		Team homeTeam = new TeamImpl();
		homeTeam.setTeamId("100");
		fixture4.setHomeTeam(homeTeam);
		
		List<Fixture> fixturesWithoutDates = new ArrayList<Fixture> ();
		fixturesWithoutDates.add(fixture4);
		when(mockFixtureRepository.getFixturesWithNoFixtureDate()).thenReturn(fixturesWithoutDates);

		
		List<ParsedFixture> parsedFixturesForDate1 = new ArrayList<ParsedFixture> ();
		parsedFixturesForDate1.add(createParsedFixture1());
		
		List<ParsedFixture> parsedFixturesForDate2 = new ArrayList<ParsedFixture> ();
		
		List<ParsedFixture> parsedFixturesForTeam = new  ArrayList<ParsedFixture> ();
		parsedFixturesForTeam.add(createParsedFixture2());
		
		when (mockReader.readFixturesForDate(date1)).thenReturn(parsedFixturesForDate1);
		when (mockReader.readFixturesForDate(date2)).thenReturn(parsedFixturesForDate2);
		
		when (mockReader.readFixturesForTeamInSeason(2000, "100")).thenReturn(parsedFixturesForTeam);
		
		// When
		objectUnderTest.loadResultsForRecentlyPlayedFixtures();
		
		// Then
		verify(mockFixtureRepository).getUnplayedFixturesBeforeToday();
		verify(mockReader,times(1)).readFixturesForDate(date1);
		verify(mockReader,times(1)).readFixturesForDate(date2);
		
		verify(mockSaver).saveFixtures(parsedFixturesForDate1);
		verify(mockSaver).saveFixtures(parsedFixturesForDate2);
		
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
