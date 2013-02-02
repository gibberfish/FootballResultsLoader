package uk.co.mindbadger.footballresults.reader.web;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

public class SoccerbaseWebPageReaderTest {

	private static final String DIALECT = "soccerbase";
	private SoccerbaseWebPageReader objectUnderTest;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new SoccerbaseWebPageReader();
		objectUnderTest.setDialect(DIALECT);
	}

}
