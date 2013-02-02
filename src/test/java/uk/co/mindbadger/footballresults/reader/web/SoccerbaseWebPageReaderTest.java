package uk.co.mindbadger.footballresults.reader.web;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.mockito.*;

import uk.co.mindbadger.footballresults.reader.ParsedFixture;

public class SoccerbaseWebPageReaderTest {
	private static final String DIALECT = "soccerbase";
	private static final int SEASON_NUMBER = 2000;
	
	private SoccerbaseWebPageReader objectUnderTest;
	
	@Mock	private SoccerbaseTeamPageParser mockTeamPageParser;
	@Mock private SoccerbaseDatePageParser mockDatePageParser;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new SoccerbaseWebPageReader();
		objectUnderTest.setDialect(DIALECT);
		objectUnderTest.setTeamPageParser(mockTeamPageParser);
		objectUnderTest.setDatePageParser(mockDatePageParser);
	}

	
	@Test
	public void shouldReadTheFixturesOnBoxingDayToGetTheInitialDivisionsAndTeams () {
		// Given
		
		// When
		List<ParsedFixture> parsedFixtures = objectUnderTest.readFixturesForSeason(SEASON_NUMBER);
		
		// Then
		verify(mockDatePageParser).parseFixturesForDate("2000-12-26");
	}
}
