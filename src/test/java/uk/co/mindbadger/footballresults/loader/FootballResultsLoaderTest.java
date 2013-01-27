package uk.co.mindbadger.footballresults.loader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.*;
import org.mockito.*;

import uk.co.mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonImpl;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;

public class FootballResultsLoaderTest {
	private static final int SEASON = 2000;
	private static final String DIALECT = "soccerbase";
	private static final Integer DIVISION_NOT_INCLUDED_IN_MAPPING = 5;
	private static final Integer READ_FIX_ID_1 = 100;
	private static final Integer READ_DIV_ID_1 = 1;
	private static final String READ_DIV_NAME_1 = "Premier";
	private static final Integer READ_TEAM_ID_1 = 500;
	private static final String READ_TEAM_NAME_1 = "Portsmouth";
	private static final Integer READ_TEAM_ID_2 = 501;
	private static final String READ_TEAM_NAME_2 = "Hull";

	private FootballResultsLoader objectUnderTest;
	
	@Mock private FootballResultsAnalyserDAO mockDao;
	@Mock private DomainObjectFactory mockDomainObjectFactory;
	@Mock private FootballResultsReader mockReader;
	@Mock private FootballResultsMapping mockMapping;

	private Calendar date1;
	private Calendar date2;
	
	private List<Division> divisionsFromDatabase;
	private List<Team> teamsFromDatabase;
	private List<Integer> includedDivisions;
	private List<ParsedFixture> fixturesReadFromReader;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		createObjectToTestAndInjectDependencies();
		
		createDatesUsedInTheTests();
		
		initialiseAllListsAsEmpty();
		
		when (mockDao.getAllDivisions()).thenReturn(divisionsFromDatabase);
		when (mockDao.getAllTeams()).thenReturn(teamsFromDatabase);
		when (mockReader.readFixturesForSeason(SEASON)).thenReturn(fixturesReadFromReader);
		when (mockMapping.getIncludedDivisions(DIALECT)).thenReturn(includedDivisions);
	}
	
	// ----------------------------------------------------------------------------------------------
	
	@Test
	public void shouldReadAllDivisionsAndSeasonsDuringLoadResults () {
		// Given
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao).getAllDivisions();
		verify(mockDao).getAllTeams();
	}
	
	@Test
	public void shouldReadFixturesForSeason () {
		// Given
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify (mockReader).readFixturesForSeason(SEASON);
	}

	@Test
	public void shouldTakeNoFurtherActionIfNoResultsLoaded () {
		// Given
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao, never()).getSeason(SEASON);
		verify(mockDao, never()).addSeason (SEASON);
	}

	@Test
	public void shouldCreateNewSeasonIfNotExistsInDatabase () {
		// Given
		when (mockDao.getSeason(SEASON)).thenReturn(null);
		fixturesReadFromReader.add(createParsedFixture1());
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao).getSeason(SEASON);
		verify(mockDao).addSeason (SEASON);
	}
	
	@Ignore
	@Test
	public void shouldNotCreateNewSeasonIfExistsInDatabase () {
		fail ("Need to implement this test");
	}
	
	@Test
	public void shouldNotCreateFixtureIfDivisionForFixtureReadIsNotInMappingList () {
		// Given
		ParsedFixture parsedFixture1 = createParsedFixture1();
		parsedFixture1.setDivisionId(DIVISION_NOT_INCLUDED_IN_MAPPING);
		fixturesReadFromReader.add(parsedFixture1);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify (mockReader).readFixturesForSeason(SEASON);	
		//TODO Verify never check season exists in database
		//TODO Verify never add season to databae
		//TODO Verify never add division to database
	}

	// ----------------------------------------------------------------------------------------------------------
	
	private void initialiseAllListsAsEmpty() {
		divisionsFromDatabase = new ArrayList<Division> ();
		teamsFromDatabase = new ArrayList<Team> ();
		fixturesReadFromReader = new ArrayList<ParsedFixture> ();
		includedDivisions = new ArrayList<Integer> ();
	}
	
	private void createObjectToTestAndInjectDependencies() {
		objectUnderTest = new FootballResultsLoader();
		objectUnderTest.setDao(mockDao);
		objectUnderTest.setDomainObjectFactory(mockDomainObjectFactory);
		objectUnderTest.setReader(mockReader);
		objectUnderTest.setMapping(mockMapping);
		objectUnderTest.setDialect(DIALECT);
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
	
	private ParsedFixture createParsedFixture1 () {
		ParsedFixture parsedFixture1 = new ParsedFixture ();
		parsedFixture1.setFixtureId(READ_FIX_ID_1);
		parsedFixture1.setSeasonId(SEASON);
		parsedFixture1.setDivisionId(READ_DIV_ID_1);
		parsedFixture1.setDivisionName(READ_DIV_NAME_1);
		parsedFixture1.setHomeTeamId(READ_TEAM_ID_1);
		parsedFixture1.setHomeTeamName(READ_TEAM_NAME_1);
		parsedFixture1.setAwayTeamId(READ_TEAM_ID_2);
		parsedFixture1.setAwayTeamName(READ_TEAM_NAME_2);
		parsedFixture1.setFixtureDate(date1);
		return parsedFixture1;
	}
}
