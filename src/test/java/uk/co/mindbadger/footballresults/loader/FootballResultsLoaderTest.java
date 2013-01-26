package uk.co.mindbadger.footballresults.loader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.mockito.*;

import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;

public class FootballResultsLoaderTest {
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
	}
	
	@Test
	public void shouldThrowAnExceptionWhenUnableToLoadMappingFile () {
		// Given
		
		// When
		
		// Then
		
	}
}
