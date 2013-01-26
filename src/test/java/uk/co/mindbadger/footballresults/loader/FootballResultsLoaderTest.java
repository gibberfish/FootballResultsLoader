package uk.co.mindbadger.footballresults.loader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.mockito.*;

import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;

public class FootballResultsLoaderTest {
	private static final int SEASON = 2000;
	private static final String DIALECT = "soccerbase";

	private FootballResultsLoader objectUnderTest;
	
	@Mock private FootballResultsAnalyserDAO mockDao;
	@Mock private DomainObjectFactory mockDomainObjectFactory;
	@Mock private FootballResultsReader mockReader;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		objectUnderTest = new FootballResultsLoader();
		
		objectUnderTest.setDao(mockDao);
		objectUnderTest.setDomainObjectFactory(mockDomainObjectFactory);
		objectUnderTest.setReader(mockReader);
		objectUnderTest.setDialect(DIALECT);
	}
	
	@Test
	public void shouldReadAllDivisionsAndSeasonsDuringLoadResults () {
		// Given
		List<Division> divisionsFromDatabase = new ArrayList<Division> ();
		List<Team> teamsFromDatabase = new ArrayList<Team> ();
		
		when (mockDao.getAllDivisions()).thenReturn(divisionsFromDatabase);
		when (mockDao.getAllTeams()).thenReturn(teamsFromDatabase);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify(mockDao).getAllDivisions();
		verify(mockDao).getAllTeams();
	}
	
	@Test
	public void shouldReadFixturesForSeason () {
		// Given
		List<Division> divisionsFromDatabase = new ArrayList<Division> ();
		List<Team> teamsFromDatabase = new ArrayList<Team> ();
		
		when (mockDao.getAllDivisions()).thenReturn(divisionsFromDatabase);
		when (mockDao.getAllTeams()).thenReturn(teamsFromDatabase);
		
		List<ParsedFixture> fixturesReadFromReader = new ArrayList<ParsedFixture> ();
		
		when (mockReader.readFixturesForSeason(SEASON)).thenReturn(fixturesReadFromReader);
		
		// When
		objectUnderTest.loadResultsForSeason(SEASON);
		
		// Then
		verify (mockReader).readFixturesForSeason(SEASON);
	}
}
