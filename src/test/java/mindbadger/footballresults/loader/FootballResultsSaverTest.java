package mindbadger.footballresults.loader;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mindbadger.footballresults.loader.FootballResultsSaver;
import mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import mindbadger.footballresults.reader.FootballResultsReader;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.Division;
import mindbadger.footballresultsanalyser.domain.Season;
import mindbadger.footballresultsanalyser.domain.SeasonDivision;
import mindbadger.footballresultsanalyser.domain.Team;

public class FootballResultsSaverTest {
	private static final int SEASON = 2000;
	private static final String DIALECT = "soccerbase";
	private static final String READ_FIX_ID_1 = "100";

	private static final String READ_DIV_ID_1 = "1";
	private static final String READ_DIV_NAME_1 = "Premier";
	private static final String MAPPED_DIV_ID_1 = "10";

	private static final String READ_DIV_ID_2 = "2";

	private static final String READ_TEAM_ID_1 = "500";
	private static final String READ_TEAM_NAME_1 = "Portsmouth";
	private static final String MAPPED_TEAM_ID_1 = "600";
	private static final String READ_TEAM_ID_2 = "501";
	private static final String READ_TEAM_NAME_2 = "Hull";
	private static final String MAPPED_TEAM_ID_2 = "601";

	private FootballResultsSaver objectUnderTest;

	@Mock
	private FootballResultsAnalyserDAO mockDao;
	@Mock
	private FootballResultsReader mockReader;
	@Mock
	private FootballResultsMapping mockMapping;
	@Mock
	private Season mockSeason;
	@Mock
	private Division mockDivision;
	@Mock
	private Team mockTeam1;
	@Mock
	private Team mockTeam2;
	
	private Calendar date1;
	private Calendar date2;

	private Map<String, Division> divisionsFromDatabase;
	private Map<String, Team> teamsFromDatabase;
	private List<String> includedDivisions;
	private List<ParsedFixture> fixturesReadFromReader;
	private Map<String, String> mappedDivisions;
	private Map<String, String> mappedTeams;
	private List<String> orderedListOfDivisions;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		createObjectToTestAndInjectDependencies();

		createDatesUsedInTheTests();

		initialiseAllListsAsEmpty();

		when(mockDao.getAllDivisions()).thenReturn(divisionsFromDatabase);
		when(mockDao.getAllTeams()).thenReturn(teamsFromDatabase);
		when(mockReader.readFixturesForSeason(SEASON)).thenReturn(fixturesReadFromReader);
		when(mockMapping.getIncludedDivisions(DIALECT)).thenReturn(includedDivisions);
		when(mockMapping.getDivisionMappings(DIALECT)).thenReturn(mappedDivisions);
		when(mockMapping.getTeamMappings(DIALECT)).thenReturn(mappedTeams);
		when(mockMapping.getOrderedListOfDivisions(DIALECT)).thenReturn(orderedListOfDivisions);
		when(mockDivision.getDivisionId()).thenReturn(MAPPED_DIV_ID_1);
		when(mockTeam1.getTeamId()).thenReturn(MAPPED_TEAM_ID_1);
		when(mockTeam2.getTeamId()).thenReturn(MAPPED_TEAM_ID_2);
	}

	// ----------------------------------------------------------------------------------------------

	@Ignore ("Need to remember why this is no longer done as part of the saver")
	@Test
	public void shouldOpenAndCloseASession() {
		// Given

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao).startSession();
		verify(mockDao).closeSession();
	}

	@Test
	public void shouldReadAllDivisionsAndSeasonsDuringSaveResults() {
		// Given

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao).getAllDivisions();
		verify(mockDao).getAllTeams();
	}

	@Test
	public void shouldWriteTheMappingFileDuringSaveResults() {
		// Given

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockMapping).saveMappings();
	}

	@Test
	public void shouldTakeNoFurtherActionIfNoResultsLoaded() {
		// Given

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, never()).getSeason(SEASON);
		verify(mockDao, never()).addSeason(SEASON);
		verify(mockDao, never()).addDivision((String) any());
		verify(mockDao, never()).addTeam((String) any());
		verify(mockDao, never()).addFixture((Season) any(), (Calendar) any(), (Division) any(), (Team) any(), (Team) any(), (Integer) any(), (Integer) any());
	}

	@Test
	public void shouldNotCreateNewSeasonIfExistsInDatabase() {
		// Given
		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);
		fixturesReadFromReader.add(createParsedFixture1());
		includedDivisions.add(READ_DIV_ID_1);
		
		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
		orderedListOfDivisions.add(MAPPED_DIV_ID_1);
		
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao).getSeason(SEASON);
		verify(mockDao, never()).addSeason(SEASON);
	}

	@Test
	public void shouldCreateNewSeasonIfNotExistsInDatabase() {
		// Given
		when(mockDao.getSeason(SEASON)).thenReturn(null);
		fixturesReadFromReader.add(createParsedFixture1());
		includedDivisions.add(READ_DIV_ID_1);
		
		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
		
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		
		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao).getSeason(SEASON);
		verify(mockDao).addSeason(SEASON);
	}

	@Test
	public void shouldTakeNoActionForFixtureWhosDivisionIsNotInTheIncludedList() {
		// Given
		includedDivisions.add(READ_DIV_ID_2);
		fixturesReadFromReader.add(createParsedFixture1());

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, never()).addDivision((String) any());
		verify(mockDao, never()).addTeam((String) any());
		verify(mockDao, never()).addFixture((Season) any(), (Calendar) any(), (Division) any(), (Team) any(), (Team) any(), (Integer) any(), (Integer) any());
	}

	@Test
	public void shouldNotCreateNewDivisionIfExistsInListReadFromDatabase() {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, never()).addDivision((String) any());
	}

	@Test
	public void shouldCreateNewDivisionIfNotExistsInListReadFromDatabase() {
		// Given
		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);

		when(mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);

		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao).addDivision(READ_DIV_NAME_1);
	}

	@Test
	public void shouldNotCreateNewHomeTeamIfExistsInListReadFromDatabase() {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, never()).addTeam(READ_TEAM_NAME_1);
	}

	@Test
	public void shouldCreateNewHomeTeamIfNotExistsInListReadFromDatabase() {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		when(mockDao.addTeam(READ_TEAM_NAME_1)).thenReturn(mockTeam1);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao).addTeam(READ_TEAM_NAME_1);
	}

	@Test
	public void shouldNotCreateNewAwayTeamIfExistsInListReadFromDatabase() {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);

		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);

		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, never()).addTeam((String) any());
	}

	@Test
	public void shouldCreateNewAwayTeamIfNotExistsInListReadFromDatabase() {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		when(mockDao.addTeam(READ_TEAM_NAME_2)).thenReturn(mockTeam2);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao).addTeam(READ_TEAM_NAME_2);
	}

	@Test
	public void shouldReuseAPreviouslyAddedDivision() {
		// Given
		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);

		when(mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);

		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		fixturesReadFromReader.add(createParsedFixture1());
		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, times(1)).addDivision(READ_DIV_NAME_1);
	}

	@Test
	public void shouldReuseAPreviouslyAddedHomeTeam() {
		// Given
		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);

		when(mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		when(mockDao.addTeam(READ_TEAM_NAME_1)).thenReturn(mockTeam1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);

		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		fixturesReadFromReader.add(createParsedFixture1());
		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, times(1)).addTeam(READ_TEAM_NAME_1);
	}

	@Test
	public void shouldReuseAPreviouslyAddedAwayTeam() {
		// Given
		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);

		when(mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		when(mockDao.addTeam(READ_TEAM_NAME_2)).thenReturn(mockTeam2);

		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);

		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		fixturesReadFromReader.add(createParsedFixture1());
		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, times(1)).addTeam(READ_TEAM_NAME_2);
	}

	@Test
	public void shouldCreateFixture() {
		// Given
		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, times(1)).addFixture(mockSeason, date1, mockDivision, mockTeam1, mockTeam2, 4, 5);
	}

	@Test
	public void shouldCreateSeasonDivision () {
		// Given
		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
		orderedListOfDivisions.add(MAPPED_DIV_ID_1);
		
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, times(1)).addSeasonDivision(mockSeason, mockDivision, 0);
		// The DAO will take care of if there is an existing season division already or not
	}

	@Test
	public void shouldCreateSeasonDivisionTeams () {
		// Given
		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);

		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		fixturesReadFromReader.add(createParsedFixture1());

		includedDivisions.add(READ_DIV_ID_1);

		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);

		// Then
		verify(mockDao, times(1)).addSeasonDivisionTeam((SeasonDivision)any(), eq(mockTeam1));
		verify(mockDao, times(1)).addSeasonDivisionTeam((SeasonDivision)any(), eq(mockTeam2));
		// The DAO will take care of if there is an existing season division already or not
	}


	// ----------------------------------------------------------------------------------------------------------

	private void initialiseAllListsAsEmpty() {
		divisionsFromDatabase = new HashMap<String, Division>();
		teamsFromDatabase = new HashMap<String, Team>();
		fixturesReadFromReader = new ArrayList<ParsedFixture>();
		includedDivisions = new ArrayList<String>();
		mappedDivisions = new HashMap<String, String>();
		mappedTeams = new HashMap<String, String>();
		orderedListOfDivisions = new ArrayList<String> ();
	}

	private void createObjectToTestAndInjectDependencies() {
		objectUnderTest = new FootballResultsSaver();
		objectUnderTest.setDao(mockDao);
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
