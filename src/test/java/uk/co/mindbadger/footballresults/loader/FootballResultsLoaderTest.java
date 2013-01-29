package uk.co.mindbadger.footballresults.loader;

import static org.mockito.Matchers.any;
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
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.DivisionImpl;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonImpl;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;
import uk.co.mindbadger.footballresultsanalyser.domain.TeamImpl;

public class FootballResultsLoaderTest {
	private static final int SEASON = 2000;
	private static final String DIALECT = "soccerbase";
	private static final Integer DIVISION_NOT_INCLUDED_IN_MAPPING = 5;
	private static final Integer READ_FIX_ID_1 = 100;

	private static final Integer READ_DIV_ID_1 = 1;
	private static final String READ_DIV_NAME_1 = "Premier";
	private static final Integer MAPPED_DIV_ID_1 = 10;

	private static final Integer READ_DIV_ID_2 = 2;
	private static final String READ_DIV_NAME_2 = "Championship";
	private static final Integer MAPPED_DIV_ID_2 = 11;

	private static final Integer READ_TEAM_ID_1 = 500;
	private static final String READ_TEAM_NAME_1 = "Portsmouth";
	private static final Integer MAPPED_TEAM_ID_1 = 600;
	private static final Integer READ_TEAM_ID_2 = 501;
	private static final String READ_TEAM_NAME_2 = "Hull";
	private static final Integer MAPPED_TEAM_ID_2 = 601;

	private FootballResultsLoader objectUnderTest;
	
	@Mock private FootballResultsAnalyserDAO mockDao;
	@Mock private FootballResultsReader mockReader;
	@Mock private FootballResultsMapping mockMapping;

	private Calendar date1;
	private Calendar date2;
	
	private Map<Integer, Division> divisionsFromDatabase;
	private Map<Integer, Team> teamsFromDatabase;
	private List<Integer> includedDivisions;
	private List<ParsedFixture> fixturesReadFromReader;
	private Map<Integer,Integer> mappedDivisions;
	private Map<Integer,Integer> mappedTeams;
	
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
		when (mockMapping.getDivisionMappings(DIALECT)).thenReturn(mappedDivisions);
		when (mockMapping.getTeamMappings(DIALECT)).thenReturn(mappedTeams);
	}
	
	// ----------------------------------------------------------------------------------------------

	@Test
	public void shouldOpenAndCloseASession () {
		// Given
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao).startSession();
		verify(mockDao).closeSession();
	}

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
	public void shouldWriteTheMappingFileDuringLoadResults () {
		// Given
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockMapping).saveMappings();
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
		verify(mockDao, never()).addDivision((String)any());
		verify(mockDao, never()).addTeam((String)any());
		verify(mockDao, never()).addFixture((Season)any(), (Calendar)any(), (Division)any(), (Team)any(), (Team)any(), (Integer)any(), (Integer)any());
	}

	@Test
	public void shouldNotCreateNewSeasonIfExistsInDatabase () {
		// Given
		Season season = new SeasonImpl();
		when (mockDao.getSeason(SEASON)).thenReturn(season);
		fixturesReadFromReader.add(createParsedFixture1());
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao).getSeason(SEASON);
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

	@Test
	public void shouldTakeNoActionForFixtureWhosDivisionIsNotInTheIncludedList () {
		// Given
		includedDivisions.add(READ_DIV_ID_2);
		fixturesReadFromReader.add(createParsedFixture1());
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao, never()).addDivision((String)any());
		verify(mockDao, never()).addTeam((String)any());
		verify(mockDao, never()).addFixture((Season)any(), (Calendar)any(), (Division)any(), (Team)any(), (Team)any(), (Integer)any(), (Integer)any());
	}
	
	@Test
	public void shouldNotCreateNewDivisionIfExistsInListReadFromDatabase () {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
		
		Division division1 = new DivisionImpl();
		division1.setDivisionId(MAPPED_DIV_ID_1);
		divisionsFromDatabase.put(MAPPED_DIV_ID_1, division1);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
		
		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, team1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		teamsFromDatabase.put(MAPPED_TEAM_ID_2, team2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao, never()).addDivision((String)any());
	}

	@Test
	public void shouldCreateNewDivisionIfNotExistsInListReadFromDatabase () {
		// Given
		Season season = new SeasonImpl();
		when (mockDao.getSeason(SEASON)).thenReturn(season);
		
		Division division = new DivisionImpl ();
		division.setDivisionId(MAPPED_DIV_ID_1);
		when (mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(division );
		
		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, team1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		teamsFromDatabase.put(MAPPED_TEAM_ID_2, team2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
		
		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
				
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao).addDivision(READ_DIV_NAME_1);
	}

	@Test
	public void shouldNotCreateNewHomeTeamIfExistsInListReadFromDatabase () {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
		
		Division division1 = new DivisionImpl();
		division1.setDivisionId(MAPPED_DIV_ID_1);
		divisionsFromDatabase.put(MAPPED_DIV_ID_1, division1);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
		
		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, team1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
		
		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		teamsFromDatabase.put(MAPPED_TEAM_ID_2, team2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao, never()).addTeam(READ_TEAM_NAME_1);
	}
	
	@Test
	public void shouldCreateNewHomeTeamIfNotExistsInListReadFromDatabase () {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
		
		Division division1 = new DivisionImpl();
		division1.setDivisionId(MAPPED_DIV_ID_1);
		divisionsFromDatabase.put(MAPPED_DIV_ID_1, division1);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
				
		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		teamsFromDatabase.put(MAPPED_TEAM_ID_2, team2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		when (mockDao.addTeam(READ_TEAM_NAME_1)).thenReturn(team1);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao).addTeam(READ_TEAM_NAME_1);
	}
	
	@Test
	public void shouldNotCreateNewAwayTeamIfExistsInListReadFromDatabase () {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
		
		Division division1 = new DivisionImpl();
		division1.setDivisionId(MAPPED_DIV_ID_1);
		divisionsFromDatabase.put(MAPPED_DIV_ID_1, division1);
		
		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, team1);
		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		teamsFromDatabase.put(MAPPED_TEAM_ID_2, team2);
		
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao, never()).addTeam((String)any());
	}
	
	@Test
	public void shouldCreateNewAwayTeamIfNotExistsInListReadFromDatabase () {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
		
		Division division1 = new DivisionImpl();
		division1.setDivisionId(MAPPED_DIV_ID_1);
		divisionsFromDatabase.put(MAPPED_DIV_ID_1, division1);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, team1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		when (mockDao.addTeam(READ_TEAM_NAME_2)).thenReturn(team2);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao).addTeam(READ_TEAM_NAME_2);
	}
	
	@Test
	public void shouldReuseAPreviouslyAddedDivision () {
		// Given
		Season season = new SeasonImpl();
		when (mockDao.getSeason(SEASON)).thenReturn(season);

		Division division = new DivisionImpl ();
		division.setDivisionId(MAPPED_DIV_ID_1);
		when (mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(division );

		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, team1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		teamsFromDatabase.put(MAPPED_TEAM_ID_2, team2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		fixturesReadFromReader.add(createParsedFixture1());
		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);

		// Then
		verify(mockDao,times(1)).addDivision(READ_DIV_NAME_1);
	}
	
	@Test
	public void shouldReuseAPreviouslyAddedHomeTeam () {
		// Given
		Season season = new SeasonImpl();
		when (mockDao.getSeason(SEASON)).thenReturn(season);

		Division division = new DivisionImpl ();
		division.setDivisionId(MAPPED_DIV_ID_1);
		when (mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(division );
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		when (mockDao.addTeam(READ_TEAM_NAME_1)).thenReturn(team1);

		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		teamsFromDatabase.put(MAPPED_TEAM_ID_2, team2);
		
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		fixturesReadFromReader.add(createParsedFixture1());
		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);

		// Then
		verify(mockDao,times(1)).addTeam(READ_TEAM_NAME_1);
	}
	
	@Test
	public void shouldReuseAPreviouslyAddedAwayTeam () {
		// Given
		Season season = new SeasonImpl();
		when (mockDao.getSeason(SEASON)).thenReturn(season);

		Division division = new DivisionImpl ();
		division.setDivisionId(MAPPED_DIV_ID_1);
		when (mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(division );
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		when (mockDao.addTeam(READ_TEAM_NAME_2)).thenReturn(team2);

		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, team1);
		
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);

		fixturesReadFromReader.add(createParsedFixture1());
		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);

		// Then
		verify(mockDao,times(1)).addTeam(READ_TEAM_NAME_2);
	}
	
	@Test
	public void shouldCreateFixture () {
		// Given
		Season season = new SeasonImpl();
		when (mockDao.getSeason(SEASON)).thenReturn(season);

		Division division = new DivisionImpl ();
		division.setDivisionId(MAPPED_DIV_ID_1);
		divisionsFromDatabase.put(MAPPED_DIV_ID_1, division);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);

		Team team1 = new TeamImpl();
		team1.setTeamId(MAPPED_TEAM_ID_1);
		teamsFromDatabase.put(MAPPED_TEAM_ID_1, team1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
		
		Team team2 = new TeamImpl();
		team2.setTeamId(MAPPED_TEAM_ID_2);
		teamsFromDatabase.put(MAPPED_TEAM_ID_2, team2);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);

		fixturesReadFromReader.add(createParsedFixture1());
		
		includedDivisions.add(READ_DIV_ID_1);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);

		// Then
		verify(mockDao,times(1)).addFixture(season, date1, division, team1, team2, 4, 5);
	}

	// ----------------------------------------------------------------------------------------------------------
	
	private void initialiseAllListsAsEmpty() {
		divisionsFromDatabase = new HashMap<Integer, Division> ();
		teamsFromDatabase = new HashMap<Integer, Team> ();
		fixturesReadFromReader = new ArrayList<ParsedFixture> ();
		includedDivisions = new ArrayList<Integer> ();
		mappedDivisions = new HashMap<Integer, Integer> ();
		mappedTeams = new HashMap<Integer, Integer> ();
	}
	
	private void createObjectToTestAndInjectDependencies() {
		objectUnderTest = new FootballResultsLoader();
		objectUnderTest.setDao(mockDao);
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
		parsedFixture1.setHomeGoals(4);
		parsedFixture1.setAwayGoals(5);
		return parsedFixture1;
	}
}
