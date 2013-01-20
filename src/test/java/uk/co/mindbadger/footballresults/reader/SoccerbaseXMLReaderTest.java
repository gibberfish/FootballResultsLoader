package uk.co.mindbadger.footballresults.reader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;

public class SoccerbaseXMLReaderTest {
	private static final int SEASON = 2000;
	private static final String DIALECT = "soccerbase";
	private static final String FILE3 = "/location/FILE3.xml";
	private static final String FILE2 = "/location/FILE2.xml";
	private static final String FILE1 = "/location/FILE1.xml";

	private SoccerbaseXMLReader objectUnderTest;
	
	@Mock private DomainObjectFactory mockDomainObjectFactory;
	@Mock private FootballResultsAnalyserDAO mockDao;
	@Mock private XMLFileReader mockXmlFileReader;
	@Mock private Document mockDocument1;
	@Mock private Document mockDocument2;
	@Mock private Document mockDocument3;
	
	@Before
	public void setup () {
		MockitoAnnotations.initMocks(this);
		
		objectUnderTest = new SoccerbaseXMLReader();
		objectUnderTest.setDAO(mockDao);
		objectUnderTest.setDialect(DIALECT);
		objectUnderTest.setDomainObjectFactory(mockDomainObjectFactory);
		objectUnderTest.setXmlFileReader(mockXmlFileReader);
	}

	@Test
	public void testReadFixturesForSeason () throws Exception {
		// Given
		List<String> listOfFiles = new ArrayList<String> ();
		listOfFiles.add(FILE1);
		listOfFiles.add(FILE2);
		listOfFiles.add(FILE3);
		when (mockXmlFileReader.getFilesForSeason(SEASON)).thenReturn(listOfFiles);
		
		when (mockXmlFileReader.readXMLFile(FILE1)).thenReturn(mockDocument1);
		when (mockXmlFileReader.readXMLFile(FILE2)).thenReturn(mockDocument2);
		when (mockXmlFileReader.readXMLFile(FILE3)).thenReturn(mockDocument3);
		
		// When
		List<Fixture> fixtures = objectUnderTest.readFixturesForSeason(SEASON);
		
		// Then
		verify(mockXmlFileReader).getFilesForSeason(SEASON);
		verify(mockXmlFileReader).readXMLFile(FILE1);
		verify(mockXmlFileReader).readXMLFile(FILE2);
		verify(mockXmlFileReader).readXMLFile(FILE3);
		
	}
}
