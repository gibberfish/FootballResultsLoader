package mindbadger.footballresults.loader;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import mindbadger.footballresults.loader.ParsedResultsSaver;
import mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import mindbadger.footballresults.reader.FootballResultsReader;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.Division;
import mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import mindbadger.footballresultsanalyser.domain.Fixture;
import mindbadger.footballresultsanalyser.domain.Season;
import mindbadger.footballresultsanalyser.domain.SeasonDivision;
import mindbadger.footballresultsanalyser.domain.Team;
import mindbadger.footballresultsanalyser.repository.DivisionRepository;
import mindbadger.footballresultsanalyser.repository.FixtureRepository;
import mindbadger.footballresultsanalyser.repository.SeasonRepository;
import mindbadger.footballresultsanalyser.repository.TeamRepository;

public class ParsedResultsSaverTest {
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

	private ParsedResultsSaver objectUnderTest;

	@Mock
	private FootballResultSaver footballResultSaver;	
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
		fail("Test not yet implemented");
	}
	
	@Test
	public void shouldGetTheMappingsWithAListOfParsedFixtures () {
		fail("Test not yet implemented");
	}
	
	@Test
	public void shouldNotSaveAnythingForParsedFixturesInANonTrackedDivision () {
		fail("Test not yet implemented");
	}

	@Test
	public void shouldSaveFixtureDetailsForParsedFixturesInATrackedDivision () {
		fail("Test not yet implemented");
	}

//
//	@Test
//	public void shouldNotRetrieveAnythingWhenThereAreNoFixturesToSave () {
//		// Given
//		
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//		
//		// Then
//		verify(mockDivisionRepository, never()).findAll();
//		verify(mockTeamRepository, never()).findAll();
//	}
//
////	@Test
////	public void shouldRetrieveAllDivisionsTeamsAndMappingsWhenThereAreSomeFixturesToSave () {
////		// Given
////		fixturesReadFromReader.add(createParsedFixture1());
////		
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////		
////		// Then
////		verify(mockDivisionRepository, times(1)).findAll();
////		verify(mockTeamRepository, times(1)).findAll();
////		verify(mockMapping, times(1)).getIncludedDivisions(DIALECT);
////		verify(mockMapping, times(1)).getDivisionMappings(DIALECT);
////		verify(mockMapping, times(1)).getTeamMappings(DIALECT);
////		verify(mockMapping, times(1)).getOrderedListOfDivisions(DIALECT);
////	}
//	
//	@Test
//	public void shouldSaveASeasonForAFixtureIfItDoesntAlreadyExist () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(null);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(mockDivision);
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		when (mockDomainObjectFactory.createSeason(SEASON)).thenReturn(mockSeason);
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(mockTeam1);
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(mockTeam2);
//		when (mockSeasonRepository.getSeasonDivision(mockSeason, mockDivision)).thenReturn(mockSeasonDivision);
//		verify(mockSeasonRepository, times(1)).getSeasonDivision(mockSeason, mockDivision);
//		
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//		
//		// Then
//		verify(mockSeasonRepository, times(1)).findOne(SEASON);
//		verify(mockDomainObjectFactory, times(1)).createSeason(SEASON);
//		verify(mockSeasonRepository, times(1)).save(mockSeason);
//	}
//	
//	@Test
//	public void shouldNotSaveASeasonForAFixtureIfItAlreadyExists () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(mockDivision);
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(mockTeam1);
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(mockTeam2);
//
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//		
//		// Then
//		verify(mockSeasonRepository, times(1)).findOne(SEASON);
//		verify(mockDomainObjectFactory, never()).createSeason(SEASON);
//		verify(mockSeasonRepository, never()).save(mockSeason);
//	}
//	
//	@Test
//	public void shouldSaveADivisionForAFixtureIfItDoesntAlreadyExist () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(mockTeam1);
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(mockTeam2);
//
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(null);
//		when (mockDomainObjectFactory.createDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);
//		
//		when (mockDivision.getDivisionId()).thenReturn(MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.save(mockDivision)).thenReturn(mockDivision);
//		
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//		
//		// Then
//		verify(mockDivisionRepository, times(1)).findOne(MAPPED_DIV_ID_1);
//		verify(mockDomainObjectFactory, times(1)).createDivision(READ_DIV_NAME_1);
//		verify(mockDivisionRepository, times(1)).save(mockDivision);
//	}
//	
//	@Test
//	public void shouldNotSaveADivisionForAFixtureIfItAlreadyExists () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(mockTeam1);
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(mockTeam2);
//
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(mockDivision);
//		
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//		
//		// Then
//		verify(mockDivisionRepository, times(1)).findOne(MAPPED_DIV_ID_1);
//		verify(mockDomainObjectFactory, never()).createDivision(MAPPED_DIV_ID_1);
//		verify(mockDivisionRepository, never()).save(mockDivision);
//	}
//
//	@Test
//	public void shouldSaveAHomeTeamForAFixtureIfItDoesntAlreadyExist () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
//
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(null);
//		when (mockDomainObjectFactory.createDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);
//		
//		when (mockDivision.getDivisionId()).thenReturn(MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.save(mockDivision)).thenReturn(mockDivision);
//		
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(mockTeam2);
//
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(null);
//		when (mockDomainObjectFactory.createTeam(READ_TEAM_NAME_1)).thenReturn(mockTeam1);
//		
//		when (mockTeam1.getTeamId()).thenReturn(READ_TEAM_NAME_1);
//		when (mockTeamRepository.save(mockTeam1)).thenReturn(mockTeam1);
//		
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//		
//		// Then
//		verify(mockTeamRepository, times(1)).findOne(MAPPED_TEAM_ID_1);
//		verify(mockDomainObjectFactory, times(1)).createTeam(READ_TEAM_NAME_1);
//		verify(mockTeamRepository, times(1)).save(mockTeam1);
//	}
//	
//	@Test
//	public void shouldNotSaveAHomeTeamForAFixtureIfItAlreadyExists () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
//
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(mockDivision);
//
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(mockTeam2);
//
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(mockTeam1);
//		
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//		
//		// Then
//		verify(mockTeamRepository, times(1)).findOne(MAPPED_TEAM_ID_1);
//		verify(mockDomainObjectFactory, never()).createTeam(READ_TEAM_NAME_1);
//		verify(mockTeamRepository, never()).save(mockTeam1);
//	}
//	
//	@Test
//	public void shouldSaveAnAwayTeamForAFixtureIfItDoesntAlreadyExist () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
//
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(null);
//		when (mockDomainObjectFactory.createDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);
//		
//		when (mockDivision.getDivisionId()).thenReturn(MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.save(mockDivision)).thenReturn(mockDivision);
//		
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(mockTeam1);
//	
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(null);
//		when (mockDomainObjectFactory.createTeam(READ_TEAM_NAME_2)).thenReturn(mockTeam2);
//		
//		when (mockTeam2.getTeamId()).thenReturn(READ_TEAM_NAME_2);
//		when (mockTeamRepository.save(mockTeam2)).thenReturn(mockTeam2);
//	
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//		
//		// Then
//		verify(mockTeamRepository, times(1)).findOne(MAPPED_TEAM_ID_2);
//		verify(mockDomainObjectFactory, times(1)).createTeam(READ_TEAM_NAME_2);
//		verify(mockTeamRepository, times(1)).save(mockTeam2);
//	}
//	
//	@Test
//	public void shouldNotSaveAnAwayTeamForAFixtureIfItAlreadyExists () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
//
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(mockDivision);
//
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(mockTeam1);
//
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(mockTeam2);
//
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//		
//		// Then
//		verify(mockTeamRepository, times(1)).findOne(MAPPED_TEAM_ID_2);
//		verify(mockDomainObjectFactory, never()).createTeam(READ_TEAM_NAME_2);
//		verify(mockTeamRepository, never()).save(mockTeam2);
//	}
//	
//	@Test
//	public void shouldNotCreateANewSeasonDivisionForFixtureIfOneAlreadyExists () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(mockDivision);
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(mockTeam1);
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(mockTeam2);
//
//		when (mockSeasonRepository.getSeasonDivision(mockSeason, mockDivision)).thenReturn(mockSeasonDivision);
//		when (mockSeason.getSeasonDivisions()).thenReturn(mockSetOfSeasonDivisions);
//		
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//				
//		// Then
//		verify (mockSeasonRepository, times(1)).getSeasonDivision(mockSeason, mockDivision);
//		verify (mockSeason, never()).getSeasonDivisions();
//		verify (mockSetOfSeasonDivisions, never()).add(mockSeasonDivision);
//		verify (mockSeasonRepository, never()).save(mockSeason);
//	}
//	
//	@Test
//	public void shouldCreateANewSeasonDivisionForFixtureIfOneDoesntExist () {
//		// Given
//		fixturesReadFromReader.add(createParsedFixture1());
//
//		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
//		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
//		when (mockDivisionRepository.findOne(MAPPED_DIV_ID_1)).thenReturn(mockDivision);
//		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_1)).thenReturn(mockTeam1);
//		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
//		when (mockTeamRepository.findOne(MAPPED_TEAM_ID_2)).thenReturn(mockTeam2);
//
//		when (mockSeasonRepository.getSeasonDivision(mockSeason, mockDivision)).thenReturn(null);
//		when (mockSeason.getSeasonDivisions()).thenReturn(mockSetOfSeasonDivisions);
//		when (mockDomainObjectFactory.createSeasonDivision(mockSeason, mockDivision, 0)).thenReturn(mockSeasonDivision);
//		
//		// When
//		objectUnderTest.saveFixtures(fixturesReadFromReader);
//				
//		// Then
//		verify (mockSeasonRepository, times(1)).getSeasonDivision(mockSeason, mockDivision);
//		verify (mockDomainObjectFactory, times(1)).createSeasonDivision(mockSeason, mockDivision, 0);
//		verify (mockSeason, times(1)).getSeasonDivisions();
//		verify (mockSetOfSeasonDivisions, times(1)).add(mockSeasonDivision);
//		verify (mockSeasonRepository, times(1)).save(mockSeason);
//	}
//	
//	
//	
//	
//	//TODO Fix this test
//	@Test
//	public void shouldCreateFixture() {
////		// Given
////		when(mockDivisionRepository.findAll()).thenReturn(divisionsFromDatabase);
////		when(mockDomainObjectFactory.createFixture(mockSeason, mockTeam1, mockTeam2)).thenReturn(mockFixture);
////		
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////		
////		// Then
////		verify(mockFixtureRepository, times(1)).save(mockFixture);
////		
////		
////		// Given
////		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, times(1)).addFixture(mockSeason, date1, mockDivision, mockTeam1, mockTeam2, 4, 5);
//	}
//	
//	//TODO Fix this test
//	@Test
//	public void shouldReadAllDivisionsAndSeasonsDuringSaveResults() {
////		// Given
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDivisionRepository).findAll();
////		verify(mockTeamRepository).findAll();
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldWriteTheMappingFileDuringSaveResults() {
////		// Given
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockMapping).saveMappings();
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldTakeNoFurtherActionIfNoResultsLoaded() {
////		// Given
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, never()).getSeason(SEASON);
////		verify(mockDao, never()).addSeason(SEASON);
////		verify(mockDao, never()).addDivision((String) any());
////		verify(mockDao, never()).addTeam((String) any());
////		verify(mockDao, never()).addFixture((Season) any(), (Calendar) any(), (Division) any(), (Team) any(), (Team) any(), (Integer) any(), (Integer) any());
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldNotCreateNewSeasonIfExistsInDatabase() {
////		// Given
////		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);
////		fixturesReadFromReader.add(createParsedFixture1());
////		includedDivisions.add(READ_DIV_ID_1);
////		
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////		orderedListOfDivisions.add(MAPPED_DIV_ID_1);
////		
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao).getSeason(SEASON);
////		verify(mockDao, never()).addSeason(SEASON);
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldCreateNewSeasonIfNotExistsInDatabase() {
////		// Given
////		when(mockDao.getSeason(SEASON)).thenReturn(null);
////		fixturesReadFromReader.add(createParsedFixture1());
////		includedDivisions.add(READ_DIV_ID_1);
////		
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////		
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao).getSeason(SEASON);
////		verify(mockDao).addSeason(SEASON);
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldTakeNoActionForFixtureWhosDivisionIsNotInTheIncludedList() {
////		// Given
////		includedDivisions.add(READ_DIV_ID_2);
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, never()).addDivision((String) any());
////		verify(mockDao, never()).addTeam((String) any());
////		verify(mockDao, never()).addFixture((Season) any(), (Calendar) any(), (Division) any(), (Team) any(), (Team) any(), (Integer) any(), (Integer) any());
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldNotCreateNewDivisionIfExistsInListReadFromDatabase() {
////		// Given
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, never()).addDivision((String) any());
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldCreateNewDivisionIfNotExistsInListReadFromDatabase() {
////		// Given
////		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);
////
////		when(mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao).addDivision(READ_DIV_NAME_1);
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldNotCreateNewHomeTeamIfExistsInListReadFromDatabase() {
////		// Given
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, never()).addTeam(READ_TEAM_NAME_1);
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldCreateNewHomeTeamIfNotExistsInListReadFromDatabase() {
////		// Given
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		when(mockDao.addTeam(READ_TEAM_NAME_1)).thenReturn(mockTeam1);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao).addTeam(READ_TEAM_NAME_1);
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldNotCreateNewAwayTeamIfExistsInListReadFromDatabase() {
////		// Given
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, never()).addTeam((String) any());
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldCreateNewAwayTeamIfNotExistsInListReadFromDatabase() {
////		// Given
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		when(mockDao.addTeam(READ_TEAM_NAME_2)).thenReturn(mockTeam2);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao).addTeam(READ_TEAM_NAME_2);
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldReuseAPreviouslyAddedDivision() {
////		// Given
////		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);
////
////		when(mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		fixturesReadFromReader.add(createParsedFixture1());
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, times(1)).addDivision(READ_DIV_NAME_1);
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldReuseAPreviouslyAddedHomeTeam() {
////		// Given
////		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);
////
////		when(mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////
////		when(mockDao.addTeam(READ_TEAM_NAME_1)).thenReturn(mockTeam1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		fixturesReadFromReader.add(createParsedFixture1());
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, times(1)).addTeam(READ_TEAM_NAME_1);
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldReuseAPreviouslyAddedAwayTeam() {
////		// Given
////		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);
////
////		when(mockDao.addDivision(READ_DIV_NAME_1)).thenReturn(mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////
////		when(mockDao.addTeam(READ_TEAM_NAME_2)).thenReturn(mockTeam2);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		fixturesReadFromReader.add(createParsedFixture1());
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, times(1)).addTeam(READ_TEAM_NAME_2);
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldCreateSeasonDivision () {
////		// Given
////		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////		orderedListOfDivisions.add(MAPPED_DIV_ID_1);
////		
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, times(1)).addSeasonDivision(mockSeason, mockDivision, 0);
////		// The DAO will take care of if there is an existing season division already or not
//	}
//
//	//TODO Fix this test
//	@Test
//	public void shouldCreateSeasonDivisionTeams () {
////		// Given
////		when(mockDao.getSeason(SEASON)).thenReturn(mockSeason);
////
////		divisionsFromDatabase.put(MAPPED_DIV_ID_1, mockDivision);
////		mappedDivisions.put(READ_DIV_ID_1, MAPPED_DIV_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_1, mockTeam1);
////		mappedTeams.put(READ_TEAM_ID_1, MAPPED_TEAM_ID_1);
////
////		teamsFromDatabase.put(MAPPED_TEAM_ID_2, mockTeam2);
////		mappedTeams.put(READ_TEAM_ID_2, MAPPED_TEAM_ID_2);
////
////		fixturesReadFromReader.add(createParsedFixture1());
////
////		includedDivisions.add(READ_DIV_ID_1);
////
////		// When
////		objectUnderTest.saveFixtures(fixturesReadFromReader);
////
////		// Then
////		verify(mockDao, times(1)).addSeasonDivisionTeam((SeasonDivision)any(), eq(mockTeam1));
////		verify(mockDao, times(1)).addSeasonDivisionTeam((SeasonDivision)any(), eq(mockTeam2));
////		// The DAO will take care of if there is an existing season division already or not
//	}
//
//
//	// ----------------------------------------------------------------------------------------------------------

	private void initialiseAllListsAsEmpty() {
//		divisionsFromDatabase = new HashSet<Division>();
//		teamsFromDatabase = new HashSet<Team>();
//		fixturesReadFromReader = new ArrayList<ParsedFixture>();
//		includedDivisions = new ArrayList<String>();
//		mappedDivisions = new HashMap<String, String>();
//		mappedTeams = new HashMap<String, String>();
//		orderedListOfDivisions = new ArrayList<String> ();
	}

	private void createObjectToTestAndInjectDependencies() {
//		objectUnderTest = new ParsedResultsSaver();
//		objectUnderTest.setDivisionRepository(mockDivisionRepository);
//		objectUnderTest.setTeamRepository(mockTeamRepository);
//		objectUnderTest.setSeasonRepository(mockSeasonRepository);
//		objectUnderTest.setFixtureRepository(mockFixtureRepository);
//		objectUnderTest.setDomainObjectFactory(mockDomainObjectFactory);
//		objectUnderTest.setMapping(mockMapping);
//		objectUnderTest.setDialect(DIALECT);
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
