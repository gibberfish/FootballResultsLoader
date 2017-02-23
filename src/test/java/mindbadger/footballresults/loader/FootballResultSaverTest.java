package mindbadger.footballresults.loader;

import static org.junit.Assert.*;
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
import org.springframework.beans.factory.annotation.Autowired;

import mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.footballresultsanalyser.domain.Division;
import mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import mindbadger.footballresultsanalyser.domain.Season;
import mindbadger.footballresultsanalyser.domain.Team;
import mindbadger.footballresultsanalyser.repository.DivisionRepository;
import mindbadger.footballresultsanalyser.repository.FixtureRepository;
import mindbadger.footballresultsanalyser.repository.SeasonRepository;
import mindbadger.footballresultsanalyser.repository.TeamRepository;

public class FootballResultSaverTest {
	private static final int SEASON = 2000;
	private static final String DIALECT = "soccerbase";
	private static final String READ_FIX_ID_1 = "100";

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
	private Team mockTeam1;
	@Mock
	private Team mockTeam2;
	
	private Calendar date1;
	private Calendar date2;

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

	// ----------------------------------------------------------------------------------------------------------

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
}
