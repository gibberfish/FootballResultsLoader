package mindbadger.footballresults.loader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mindbadger.footballresultsanalyser.domain.Division;
import mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import mindbadger.footballresultsanalyser.domain.Fixture;
import mindbadger.footballresultsanalyser.domain.Season;
import mindbadger.footballresultsanalyser.domain.SeasonDivision;
import mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import mindbadger.footballresultsanalyser.domain.Team;
import mindbadger.footballresultsanalyser.repository.DivisionRepository;
import mindbadger.footballresultsanalyser.repository.FixtureRepository;
import mindbadger.footballresultsanalyser.repository.SeasonRepository;
import mindbadger.footballresultsanalyser.repository.TeamRepository;

public class FootballResultSaverTest {
	private static final int SEASON = 2000;
	private static final String DIV_ID_1 = "1";
	private static final String DIV_NAME_1 = "Premier";

	private static final String TEAM_ID_1 = "500";
	private static final String TEAM_NAME_1 = "Portsmouth";

	private FootballResultSaver objectUnderTest;

	@Mock
	private DivisionRepository mockDivisionRepository;
	@Mock
	private TeamRepository mockTeamRepository;
	@Mock
	private SeasonRepository mockSeasonRepository;
	@Mock
	private FixtureRepository mockFixtureRepository;
	@Mock
	private DomainObjectFactory mockDomainObjectFactory;

	@Mock
	private Season mockSeason;
	@Mock
	private Division mockDivision;
	@Mock
	private Team mockTeam;
	@Mock
	private SeasonDivision mockSeasonDivision;
	@Mock
	private Team mockTeam1;
	@Mock
	private Team mockTeam2;
	@Mock
	private SeasonDivisionTeam mockSeasonDivisionTeam1;
	@Mock
	private SeasonDivisionTeam mockSeasonDivisionTeam2;
	@Mock
	private Fixture mockFixture;
	
	private Calendar date1;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new FootballResultSaver();
		objectUnderTest.setSeasonRepository(mockSeasonRepository);
		objectUnderTest.setDivisionRepository(mockDivisionRepository);
		objectUnderTest.setTeamRepository(mockTeamRepository);
		objectUnderTest.setFixtureRepository(mockFixtureRepository);
		objectUnderTest.setDomainObjectFactory(mockDomainObjectFactory);
		
		createDatesUsedInTheTests();
	}

	// ----------------------------------------------------------------------------------------------

	@Test
	public void shouldNotSaveASeasonIfItAlreadyExists () {
		// Given
		when (mockSeasonRepository.findOne(SEASON)).thenReturn(mockSeason);
		
		// When
		Season returnedSeason = objectUnderTest.createSeasonIfNotExisting(SEASON);
		
		// Then
		assertEquals (mockSeason, returnedSeason);
		verify (mockDomainObjectFactory, never()).createSeason(SEASON);
		verify (mockSeasonRepository, never()).save(mockSeason);
	}

	@Test
	public void shouldSaveASeasonIfDoesNotExist () {
		// Given
		when (mockSeasonRepository.findOne(SEASON)).thenReturn(null);
		when (mockDomainObjectFactory.createSeason(SEASON)).thenReturn(mockSeason);
		when (mockSeasonRepository.save(mockSeason)).thenReturn(mockSeason);
		
		// When
		Season returnedSeason = objectUnderTest.createSeasonIfNotExisting(SEASON);
		
		// Then
		assertEquals (mockSeason, returnedSeason);
		verify (mockDomainObjectFactory, times(1)).createSeason(SEASON);
		verify (mockSeasonRepository, times(1)).save(mockSeason);
	}

	@Test
	public void shouldNotSaveADivisionIfItAlreadyExists () {
		// Given
		when (mockDivisionRepository.findOne(DIV_ID_1)).thenReturn(mockDivision);
		
		// When
		Division returnedDivision = objectUnderTest.createDivisionIfNotExisting(DIV_ID_1, DIV_NAME_1);
		
		// Then
		assertEquals (mockDivision, returnedDivision);
		verify (mockDomainObjectFactory, never()).createDivision(DIV_ID_1);
		verify (mockDivisionRepository, never()).save(mockDivision);
	}

	@Test
	public void shouldSaveADivisionIfDoesNotExist () {
		// Given
		when (mockDivisionRepository.findOne(DIV_ID_1)).thenReturn(null);
		when (mockDomainObjectFactory.createDivision(DIV_NAME_1)).thenReturn(mockDivision);
		when (mockDivisionRepository.save(mockDivision)).thenReturn(mockDivision);
		
		// When
		Division returnedDivision = objectUnderTest.createDivisionIfNotExisting(DIV_ID_1, DIV_NAME_1);
		
		// Then
		assertEquals (mockDivision, returnedDivision);
		verify (mockDomainObjectFactory, times(1)).createDivision(DIV_NAME_1);
		verify (mockDivisionRepository, times(1)).save(mockDivision);
	}

	@Test
	public void shouldNotSaveATeamIfItAlreadyExists () {
		// Given
		when (mockTeamRepository.findOne(TEAM_ID_1)).thenReturn(mockTeam);
		
		// When
		Team returnedTeam = objectUnderTest.createTeamIfNotExisting(TEAM_ID_1, TEAM_NAME_1);
		
		// Then
		assertEquals (mockTeam, returnedTeam);
		verify (mockDomainObjectFactory, never()).createTeam(TEAM_ID_1);
		verify (mockTeamRepository, never()).save(mockTeam);
	}

	@Test
	public void shouldSaveATeamIfDoesNotExist () {
		// Given
		when (mockTeamRepository.findOne(TEAM_ID_1)).thenReturn(null);
		when (mockDomainObjectFactory.createTeam(TEAM_NAME_1)).thenReturn(mockTeam);
		when (mockTeamRepository.save(mockTeam)).thenReturn(mockTeam);
		
		// When
		Team returnedTeam = objectUnderTest.createTeamIfNotExisting(TEAM_ID_1, TEAM_NAME_1);
		
		// Then
		assertEquals (mockTeam, returnedTeam);
		verify (mockDomainObjectFactory, times(1)).createTeam(TEAM_NAME_1);
		verify (mockTeamRepository, times(1)).save(mockTeam);
	}

	@Test
	public void shouldNotSaveAnythingIfTheSeasonDivisionAndTeamsAlreadyExists () {
		// Given
		when (mockSeasonRepository.getSeasonDivision(mockSeason, mockDivision)).thenReturn(mockSeasonDivision);
		when (mockSeasonRepository.getSeasonDivisionTeam(mockSeasonDivision, mockTeam1)).thenReturn(mockSeasonDivisionTeam1);
		when (mockSeasonRepository.getSeasonDivisionTeam(mockSeasonDivision, mockTeam2)).thenReturn(mockSeasonDivisionTeam2);
		
		// When
		objectUnderTest.createSeasonDivisionTeamsIfNotExisting(mockSeason, mockDivision, 1, mockTeam1, mockTeam2);
		
		// Then
		verify(mockSeasonRepository, times(1)).getSeasonDivision(mockSeason, mockDivision);
		verify(mockSeasonRepository, times(1)).getSeasonDivisionTeam(mockSeasonDivision, mockTeam1);
		verify(mockSeasonRepository, times(1)).getSeasonDivisionTeam(mockSeasonDivision, mockTeam2);
		
		verify(mockDomainObjectFactory, never()).createSeasonDivision(mockSeason, mockDivision, 1);
		verify(mockDomainObjectFactory, never()).createSeasonDivisionTeam(mockSeasonDivision, mockTeam1);
		verify(mockDomainObjectFactory, never()).createSeasonDivisionTeam(mockSeasonDivision, mockTeam2);
		verify(mockSeasonRepository, never()).save(mockSeason);
	}

	@Test
	public void shouldNotSaveASeasonDivisionIfItDoesNotExist () {
		// Given
		when (mockSeasonRepository.getSeasonDivision(mockSeason, mockDivision)).thenReturn(null);
		when (mockSeasonRepository.getSeasonDivisionTeam(mockSeasonDivision, mockTeam1)).thenReturn(mockSeasonDivisionTeam1);
		when (mockSeasonRepository.getSeasonDivisionTeam(mockSeasonDivision, mockTeam2)).thenReturn(mockSeasonDivisionTeam2);

		when (mockDomainObjectFactory.createSeasonDivision(mockSeason, mockDivision, 1)).thenReturn(mockSeasonDivision);
		
		// When
		objectUnderTest.createSeasonDivisionTeamsIfNotExisting(mockSeason, mockDivision, 1, mockTeam1, mockTeam2);
		
		// Then
		verify(mockSeasonRepository, times(1)).getSeasonDivision(mockSeason, mockDivision);
		verify(mockSeasonRepository, times(1)).getSeasonDivisionTeam(mockSeasonDivision, mockTeam1);
		verify(mockSeasonRepository, times(1)).getSeasonDivisionTeam(mockSeasonDivision, mockTeam2);
		
		verify(mockDomainObjectFactory, times(1)).createSeasonDivision(mockSeason, mockDivision, 1);
		verify(mockDomainObjectFactory, never()).createSeasonDivisionTeam(mockSeasonDivision, mockTeam1);
		verify(mockDomainObjectFactory, never()).createSeasonDivisionTeam(mockSeasonDivision, mockTeam2);
		verify(mockSeasonRepository, times(1)).save(mockSeason);
	}

	@Test
	public void shouldNotSaveASeasonDivisionHomeTeamIfItDoesNotExist () {
		// Given
		when (mockSeasonRepository.getSeasonDivision(mockSeason, mockDivision)).thenReturn(mockSeasonDivision);
		when (mockSeasonRepository.getSeasonDivisionTeam(mockSeasonDivision, mockTeam1)).thenReturn(null);
		when (mockSeasonRepository.getSeasonDivisionTeam(mockSeasonDivision, mockTeam2)).thenReturn(mockSeasonDivisionTeam2);

		when (mockDomainObjectFactory.createSeasonDivisionTeam(mockSeasonDivision, mockTeam1)).thenReturn(mockSeasonDivisionTeam1);
		
		// When
		objectUnderTest.createSeasonDivisionTeamsIfNotExisting(mockSeason, mockDivision, 1, mockTeam1, mockTeam2);
		
		// Then
		verify(mockSeasonRepository, times(1)).getSeasonDivision(mockSeason, mockDivision);
		verify(mockSeasonRepository, times(1)).getSeasonDivisionTeam(mockSeasonDivision, mockTeam1);
		verify(mockSeasonRepository, times(1)).getSeasonDivisionTeam(mockSeasonDivision, mockTeam2);
		
		verify(mockDomainObjectFactory, never()).createSeasonDivision(mockSeason, mockDivision, 1);
		verify(mockDomainObjectFactory, times(1)).createSeasonDivisionTeam(mockSeasonDivision, mockTeam1);
		verify(mockDomainObjectFactory, never()).createSeasonDivisionTeam(mockSeasonDivision, mockTeam2);
		verify(mockSeasonRepository, times(1)).save(mockSeason);
	}

	@Test
	public void shouldNotSaveASeasonDivisionAwayTeamIfItDoesNotExist () {
		// Given
		when (mockSeasonRepository.getSeasonDivision(mockSeason, mockDivision)).thenReturn(mockSeasonDivision);
		when (mockSeasonRepository.getSeasonDivisionTeam(mockSeasonDivision, mockTeam1)).thenReturn(mockSeasonDivisionTeam1);
		when (mockSeasonRepository.getSeasonDivisionTeam(mockSeasonDivision, mockTeam2)).thenReturn(null);

		when (mockDomainObjectFactory.createSeasonDivisionTeam(mockSeasonDivision, mockTeam2)).thenReturn(mockSeasonDivisionTeam2);
		
		// When
		objectUnderTest.createSeasonDivisionTeamsIfNotExisting(mockSeason, mockDivision, 1, mockTeam1, mockTeam2);
		
		// Then
		verify(mockSeasonRepository, times(1)).getSeasonDivision(mockSeason, mockDivision);
		verify(mockSeasonRepository, times(1)).getSeasonDivisionTeam(mockSeasonDivision, mockTeam1);
		verify(mockSeasonRepository, times(1)).getSeasonDivisionTeam(mockSeasonDivision, mockTeam2);
		
		verify(mockDomainObjectFactory, never()).createSeasonDivision(mockSeason, mockDivision, 1);
		verify(mockDomainObjectFactory, never()).createSeasonDivisionTeam(mockSeasonDivision, mockTeam1);
		verify(mockDomainObjectFactory, times(1)).createSeasonDivisionTeam(mockSeasonDivision, mockTeam2);
		verify(mockSeasonRepository, times(1)).save(mockSeason);
	}

	@Test
	public void shouldUpdateAnExistingFixture () {
		// Given
		when (mockFixtureRepository.getExistingFixture(mockSeason, mockTeam1, mockTeam2)).thenReturn(mockFixture);
		
		// When
		objectUnderTest.createFixture(mockSeason, mockDivision, mockTeam1, mockTeam2, date1, 5, 3);
		
		// Then
		verify (mockDomainObjectFactory, never()).createFixture(mockSeason, mockTeam1, mockTeam2);
		
		verify (mockFixture, times(1)).setDivision(mockDivision);
		verify (mockFixture, times(1)).setFixtureDate(date1);
		verify (mockFixture, times(1)).setHomeGoals(5);
		verify (mockFixture, times(1)).setAwayGoals(3);
		
		verify (mockFixtureRepository, times(1)).createOrUpdate(mockFixture);
	}

	@Test
	public void shouldCreateANewFixture () {
		// Given
		when (mockFixtureRepository.getExistingFixture(mockSeason, mockTeam1, mockTeam2)).thenReturn(null);
		when (mockDomainObjectFactory.createFixture(mockSeason, mockTeam1, mockTeam2)).thenReturn(mockFixture);
		
		// When
		objectUnderTest.createFixture(mockSeason, mockDivision, mockTeam1, mockTeam2, date1, 5, 3);
		
		// Then
		verify (mockDomainObjectFactory, times(1)).createFixture(mockSeason, mockTeam1, mockTeam2);
		
		verify (mockFixture, times(1)).setDivision(mockDivision);
		verify (mockFixture, times(1)).setFixtureDate(date1);
		verify (mockFixture, times(1)).setHomeGoals(5);
		verify (mockFixture, times(1)).setAwayGoals(3);
		
		verify (mockFixtureRepository, times(1)).createOrUpdate(mockFixture);
	}

	// ----------------------------------------------------------------------------------------------------------

	private void createDatesUsedInTheTests() {
		date1 = Calendar.getInstance();
		date1.set(Calendar.DAY_OF_MONTH, 20);
		date1.set(Calendar.MONTH, 9);
		date1.set(Calendar.YEAR, 2000);
	}
}
