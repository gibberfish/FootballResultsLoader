package mindbadger.footballresults.saver;

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

import mindbadger.footballresults.loader.FootballResultsLoaderMapping;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.footballresults.saver.FootballResultSaver;
import mindbadger.footballresults.saver.ParsedResultsSaver;
import mindbadger.football.domain.Division;
import mindbadger.football.domain.Season;
import mindbadger.football.domain.Team;

public class ParsedResultsSaverTest {
	private static final int SEASON = 2000;
	private static final String DIALECT = "soccerbase";
	private static final String READ_FIX_ID_1 = "100";

	private static final String READ_DIV_ID_1 = "1";
	private static final String READ_DIV_NAME_1 = "Premier";
	private static final String MAPPED_DIV_ID_1 = "10";
	private static final String MAPPED_DIV_ID_2 = "11";

	private static final String READ_DIV_ID_2 = "2";

	private static final String READ_TEAM_ID_1 = "500";
	private static final String READ_TEAM_NAME_1 = "Portsmouth";
	private static final String MAPPED_TEAM_ID_1 = "600";
	private static final String READ_TEAM_ID_2 = "501";
	private static final String READ_TEAM_NAME_2 = "Hull";
	private static final String MAPPED_TEAM_ID_2 = "601";

	private ParsedResultsSaver objectUnderTest;

	@Mock
	private FootballResultSaver footballResultSaver;	
	@Mock
	private FootballResultsLoaderMapping mockMapping;

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

	private List<ParsedFixture> fixturesReadFromReader;

	private List<String> includedDivisions;
	private Map<String, String> mappedDivisions;
	private Map<String, String> mappedTeams;
	private List<String> orderedListOfDivisions;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		createObjectToTestAndInjectDependencies();

		createDatesUsedInTheTests();

		initialiseAllListsAsEmpty();

		when(mockMapping.getIncludedDivisions(DIALECT)).thenReturn(includedDivisions);
		when(mockMapping.getDivisionMappings(DIALECT)).thenReturn(mappedDivisions);
		when(mockMapping.getTeamMappings(DIALECT)).thenReturn(mappedTeams);
		when(mockMapping.getOrderedListOfDivisions(DIALECT)).thenReturn(orderedListOfDivisions);
	}

	// ----------------------------------------------------------------------------------------------

	@Test
	public void shouldDoNothingForAnEmptyListOfParsedFixtures () {
		// Given
		
		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);
		
		// Then
		verify(mockMapping, never()).getIncludedDivisions(DIALECT);
		verify(mockMapping, never()).getDivisionMappings(DIALECT);
		verify(mockMapping, never()).getTeamMappings(DIALECT);
		verify(mockMapping, never()).getOrderedListOfDivisions(DIALECT);
	}
	
	@Test
	public void shouldGetTheMappingsWithAListOfParsedFixtures () {
		// Given
		fixturesReadFromReader.add(createParsedFixture1());
		
		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);
		
		// Then
		verify(mockMapping, times(1)).getIncludedDivisions(DIALECT);
		verify(mockMapping, times(1)).getDivisionMappings(DIALECT);
		verify(mockMapping, times(1)).getTeamMappings(DIALECT);
		verify(mockMapping, times(1)).getOrderedListOfDivisions(DIALECT);
	}
	
	@Test
	public void shouldNotSaveAnythingForParsedFixturesInANonTrackedDivision () {
		// Given
		includedDivisions.add(READ_DIV_ID_2);
		
		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);
		
		// Then
		verify(footballResultSaver, never()).createSeasonIfNotExisting(SEASON);
		verify(footballResultSaver, never()).createDivisionIfNotExisting(MAPPED_DIV_ID_1, READ_DIV_NAME_1);
		verify(footballResultSaver, never()).createTeamIfNotExisting(MAPPED_TEAM_ID_1, READ_TEAM_NAME_1);
		verify(footballResultSaver, never()).createTeamIfNotExisting(MAPPED_TEAM_ID_2, READ_TEAM_NAME_2);
		verify(footballResultSaver, never()).createSeasonDivisionTeamsIfNotExisting(mockSeason, mockDivision, 1, mockTeam1, mockTeam2);
		verify(footballResultSaver, never()).createFixture(mockSeason, mockDivision, mockTeam1, mockTeam2, date1, 4, 5);
	}

	@Test
	public void shouldSaveFixtureDetailsForParsedFixturesInATrackedDivision () {
		// Given
		includedDivisions.add(READ_DIV_ID_1);
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
		orderedListOfDivisions.add(MAPPED_DIV_ID_1);
		
		fixturesReadFromReader.add(createParsedFixture1());
		when(footballResultSaver.createSeasonIfNotExisting(SEASON)).thenReturn(mockSeason);
		when(footballResultSaver.createDivisionIfNotExisting(MAPPED_DIV_ID_1, READ_DIV_NAME_1)).thenReturn(mockDivision);
		when(footballResultSaver.createTeamIfNotExisting(MAPPED_TEAM_ID_1, READ_TEAM_NAME_1)).thenReturn(mockTeam1);
		when(footballResultSaver.createTeamIfNotExisting(MAPPED_TEAM_ID_2, READ_TEAM_NAME_2)).thenReturn(mockTeam2);
		
		when(mockDivision.getDivisionId()).thenReturn(MAPPED_DIV_ID_1);
		when(mockTeam1.getTeamId()).thenReturn(MAPPED_TEAM_ID_1);
		when(mockTeam2.getTeamId()).thenReturn(MAPPED_TEAM_ID_2);
		
		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);
		
		// Then
		verify(footballResultSaver, times(1)).createSeasonIfNotExisting(SEASON);
		verify(footballResultSaver, times(1)).createDivisionIfNotExisting(MAPPED_DIV_ID_1, READ_DIV_NAME_1);
		verify(footballResultSaver, times(1)).createTeamIfNotExisting(MAPPED_TEAM_ID_1, READ_TEAM_NAME_1);
		verify(footballResultSaver, times(1)).createTeamIfNotExisting(MAPPED_TEAM_ID_2, READ_TEAM_NAME_2);
		verify(footballResultSaver, times(1)).createSeasonDivisionTeamsIfNotExisting(mockSeason, mockDivision, 1, mockTeam1, mockTeam2);
		verify(footballResultSaver, times(1)).createFixture(mockSeason, mockDivision, mockTeam1, mockTeam2, date1, 4, 5);
	}

	@Test
	public void shouldUseTheCorrectDivisionIndexWhenCreatingTheSeasonDivision () {
		// Given
		includedDivisions.add(READ_DIV_ID_1); 
		
		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
		orderedListOfDivisions.add(MAPPED_DIV_ID_2);
		orderedListOfDivisions.add(MAPPED_DIV_ID_1); // This time the division for our fixture is the second one
		
		fixturesReadFromReader.add(createParsedFixture1());
		when(footballResultSaver.createSeasonIfNotExisting(SEASON)).thenReturn(mockSeason);
		when(footballResultSaver.createDivisionIfNotExisting(MAPPED_DIV_ID_1, READ_DIV_NAME_1)).thenReturn(mockDivision);
		when(footballResultSaver.createTeamIfNotExisting(MAPPED_TEAM_ID_1, READ_TEAM_NAME_1)).thenReturn(mockTeam1);
		when(footballResultSaver.createTeamIfNotExisting(MAPPED_TEAM_ID_2, READ_TEAM_NAME_2)).thenReturn(mockTeam2);
		
		when(mockDivision.getDivisionId()).thenReturn(MAPPED_DIV_ID_1);
		when(mockTeam1.getTeamId()).thenReturn(MAPPED_TEAM_ID_1);
		when(mockTeam2.getTeamId()).thenReturn(MAPPED_TEAM_ID_2);
		
		// When
		objectUnderTest.saveFixtures(fixturesReadFromReader);
		
		// Then
		verify(footballResultSaver, times(1)).createSeasonIfNotExisting(SEASON);
		verify(footballResultSaver, times(1)).createDivisionIfNotExisting(MAPPED_DIV_ID_1, READ_DIV_NAME_1);
		verify(footballResultSaver, times(1)).createTeamIfNotExisting(MAPPED_TEAM_ID_1, READ_TEAM_NAME_1);
		verify(footballResultSaver, times(1)).createTeamIfNotExisting(MAPPED_TEAM_ID_2, READ_TEAM_NAME_2);
		verify(footballResultSaver, times(1)).createSeasonDivisionTeamsIfNotExisting(mockSeason, mockDivision, 2, mockTeam1, mockTeam2);
		verify(footballResultSaver, times(1)).createFixture(mockSeason, mockDivision, mockTeam1, mockTeam2, date1, 4, 5);
	}

	// ----------------------------------------------------------------------------------------------------------

	private void initialiseAllListsAsEmpty() {
		fixturesReadFromReader = new ArrayList<ParsedFixture>();
		includedDivisions = new ArrayList<String>();
		mappedDivisions = new HashMap<String, String>();
		mappedTeams = new HashMap<String, String>();
		orderedListOfDivisions = new ArrayList<String> ();
	}

	private void createObjectToTestAndInjectDependencies() {
		objectUnderTest = new ParsedResultsSaver();
		objectUnderTest.setFootballResultSaver(footballResultSaver);
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
