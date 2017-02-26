package mindbadger.footballresults.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mindbadger.footballresults.loader.FootballResultsLoaderException;
import mindbadger.footballresults.loader.FootballResultsLoaderMapping;
import mindbadger.xml.XMLFileReader;
import mindbadger.xml.XMLFileWriter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class FootballResultsLoaderMappingTest{
	private static final String NON_EXISTANT_DIALECT = "Non Existant Dialect";
	private static final String FRA_TEAM_ID_3 = "5";
	private static final String FRA_TEAM_ID_2 = "4";
	private static final String FRA_TEAM_ID_1 = "3";
	private static final String FRA_DIV_ID_2 = "2";
	private static final String FRA_DIV_ID_1 = "1";
	private static final String FRA_DIV_ID_3 = "3";
	private static final String SOURCE_TEAM_ID_3 = "502";
	private static final String SOURCE_TEAM_ID_2 = "501";
	private static final String SOURCE_TEAM_ID_1 = "500";
	private static final String SOURCE_DIV_ID_3 = "1025";
	private static final String SOURCE_DIV_ID_2 = "1024";
	private static final String SOURCE_DIV_ID_1 = "1023";
	//TODO Need to wire an example mapping XML file into the build
	private static final String MAPPING_FILE = "C:\\mapping\\mapping.xml";
	private static final String DIALECT = "soccerbase";

	private FootballResultsLoaderMapping objectUnderTest;
	
	@Mock private XMLFileReader mockXmlFileReader;
	@Mock private XMLFileWriter mockXmlFileWriter;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldThrowExceptionWhenParserConfigurationException () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenThrow(new ParserConfigurationException("TestException"));
			
		// When
		try {
			objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
			fail("Should thrown a FootballResultsLoaderException here");
		} catch (FootballResultsLoaderException e) {
			// Then
			assertEquals ("javax.xml.parsers.ParserConfigurationException: TestException", e.getMessage());
		}
	}

	@Test
	public void shouldThrowExceptionWhenSAXException () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenThrow(new SAXException("TestException"));
			
		// When
		try {
			objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
			fail("Should thrown a FootballResultsLoaderException here");
		} catch (FootballResultsLoaderException e) {
			// Then
			assertEquals ("org.xml.sax.SAXException: TestException", e.getMessage());
		}
	}

	@Test
	public void shouldThrowExceptionWhenIOException () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenThrow(new IOException("TestException"));
			
		// When
		try {
			objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
			fail("Should thrown a FootballResultsLoaderException here");
		} catch (FootballResultsLoaderException e) {
			// Then
			assertEquals ("java.io.IOException: TestException", e.getMessage());
		}
	}

	@Test
	public void shouldThrowExceptionWhenNoSpecifiedSourceExistsInXML () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getValidDocument());
		objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
			
		// When
		try {
			objectUnderTest.getIncludedDivisions(NON_EXISTANT_DIALECT);
			fail("Should thrown a FootballResultsLoaderException here");
		} catch (FootballResultsLoaderException e) {
			// Then
			assertEquals ("There is no configuration for " + NON_EXISTANT_DIALECT + " in your mapping file", e.getMessage());
		}
	}
	
	@Test
	public void shouldThrowExceptionWhenNoIncludedDivisionsExistsInXML () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getDocumentWithNoIncludedDivisionsXML());
			
		// When
		try {
			objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
			fail("Should thrown a FootballResultsLoaderException here");
		} catch (FootballResultsLoaderException e) {
			// Then
			assertEquals ("There are no IncludedDivisions in your mapping file for dialect " + DIALECT, e.getMessage());
		}
	}


	@Test
	public void shouldReadIncludedDivisionsFromAValidMappingFile () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getValidDocument());
		objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);

		// When
		List<String> includedDivisions = objectUnderTest.getIncludedDivisions (DIALECT);
		
		// Then
		assertEquals (3, includedDivisions.size());
		assertEquals (SOURCE_DIV_ID_1, includedDivisions.get(0));
		assertEquals (SOURCE_DIV_ID_2, includedDivisions.get(1));
		assertEquals (SOURCE_DIV_ID_3, includedDivisions.get(2));
	}

	@Test
	public void shouldThrowExceptionWhenNoDivisionMappingsExistsInXML () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getDocumentWithNoDivisionMappingsXML());
			
		// When
		try {
			objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
			fail("Should thrown a FootballResultsLoaderException here");
		} catch (FootballResultsLoaderException e) {
			// Then
			assertEquals ("There are no DivisionMappings in your mapping file for dialect " + DIALECT, e.getMessage());
		}
	}

	@Test
	public void shouldReadDivisionMappingsFromAValidMappingFile () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getValidDocument());
		objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);

		// When
		Map<String,String> divisionMappings = objectUnderTest.getDivisionMappings (DIALECT);
		
		// Then
		assertEquals (2, divisionMappings.size());
		
		assertEquals (FRA_DIV_ID_1, divisionMappings.get(SOURCE_DIV_ID_1));
		assertEquals (FRA_DIV_ID_2, divisionMappings.get(SOURCE_DIV_ID_2));
	}

	@Test
	public void shouldReadTeamMappingsFromAValidMappingFile () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getValidDocument());
		objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);

		// When
		Map<String,String> teamMappings = objectUnderTest.getTeamMappings (DIALECT);
		
		// Then
		assertEquals (2, teamMappings.size());
		
		assertEquals (FRA_TEAM_ID_1, teamMappings.get(SOURCE_TEAM_ID_1));
		assertEquals (FRA_TEAM_ID_2, teamMappings.get(SOURCE_TEAM_ID_2));
	}
	
	@Test
	public void shouldAddDivisionMapping () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getValidDocument());
		objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
		
		// When
		objectUnderTest.addDivisionMapping (DIALECT, SOURCE_DIV_ID_3, FRA_DIV_ID_3);
		
		// Then
		Map<String,String> divisionMappings = objectUnderTest.getDivisionMappings (DIALECT);

		assertEquals (3, divisionMappings.size());
		assertEquals (FRA_DIV_ID_1, divisionMappings.get(SOURCE_DIV_ID_1));
		assertEquals (FRA_DIV_ID_2, divisionMappings.get(SOURCE_DIV_ID_2));
		assertEquals (FRA_DIV_ID_3, divisionMappings.get(SOURCE_DIV_ID_3));
	}

	@Test
	public void shouldGetIndexOfDivisionMapping () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getValidDocument());
		objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
		objectUnderTest.addDivisionMapping (DIALECT, SOURCE_DIV_ID_3, FRA_DIV_ID_3);
		
		// When
		int indexOfDivision1 = objectUnderTest.getIndexForDivision(DIALECT, FRA_DIV_ID_1);
		int indexOfDivision2 = objectUnderTest.getIndexForDivision(DIALECT, FRA_DIV_ID_2);
		int indexOfDivision3 = objectUnderTest.getIndexForDivision(DIALECT, FRA_DIV_ID_3);
		
		// Then
		assertEquals (0, indexOfDivision1);
		assertEquals (1, indexOfDivision2);
		assertEquals (2, indexOfDivision3);
	}

	@Test
	public void shouldAddTeamMapping () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getValidDocument());
		objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
		
		// When
		objectUnderTest.addTeamMapping (DIALECT, SOURCE_TEAM_ID_3, FRA_TEAM_ID_3);
		
		// Then
		Map<String,String> teamMappings = objectUnderTest.getTeamMappings (DIALECT);

		assertEquals (3, teamMappings.size());
		assertEquals (FRA_TEAM_ID_1, teamMappings.get(SOURCE_TEAM_ID_1));
		assertEquals (FRA_TEAM_ID_2, teamMappings.get(SOURCE_TEAM_ID_2));
		assertEquals (FRA_TEAM_ID_3, teamMappings.get(SOURCE_TEAM_ID_3));
	}
	
	@Test
	public void shouldSaveMappingFile () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getValidDocument());
		objectUnderTest = new FootballResultsLoaderMapping(MAPPING_FILE, mockXmlFileReader, mockXmlFileWriter);
		
		// When
		objectUnderTest.saveMappings ();
		
		// Then
		verify (mockXmlFileWriter).writeXMLFile(eq(MAPPING_FILE), (Document)any());
	}

	// ---------------------------------------------------------------------------------------------------------------
	
	private Document getDocumentWithNoDivisionMappingsXML() throws ParserConfigurationException {
		Document doc = createNewDocument();
		Element root = createRootElement(doc);

		Element source = createSourceElement(doc, root, "soccerbase");
		createIncludedDivisionsElement(doc, source);
		createTeamMappingsElement(doc, source);
		
		return doc;
	}


	private Document getValidDocument() throws ParserConfigurationException {
		Document doc = createNewDocument();
		Element root = createRootElement(doc);

		Element source = createSourceElement(doc, root, "soccerbase");
		Element includedDivisions = createIncludedDivisionsElement(doc, source);
		Element divisionMapping = createDivisionMappingsElement(doc, source);
		Element teamMapping = createTeamMappingsElement(doc, source);
		
		createIncludedDivElement (doc, includedDivisions, SOURCE_DIV_ID_1);
		createIncludedDivElement (doc, includedDivisions, SOURCE_DIV_ID_2);
		createIncludedDivElement (doc, includedDivisions, SOURCE_DIV_ID_3);
		
		createDivisionMappingElement (doc, divisionMapping, SOURCE_DIV_ID_1, FRA_DIV_ID_1);
		createDivisionMappingElement (doc, divisionMapping, SOURCE_DIV_ID_2, FRA_DIV_ID_2);
		
		createTeamMappingElement (doc, teamMapping, SOURCE_TEAM_ID_1, FRA_TEAM_ID_1);
		createTeamMappingElement (doc, teamMapping, SOURCE_TEAM_ID_2, FRA_TEAM_ID_2);
		
		return doc;
	}
	
	private Document getDocumentWithNoIncludedDivisionsXML() throws ParserConfigurationException {
		Document doc = createNewDocument();
		Element root = createRootElement(doc);

		Element source = createSourceElement(doc, root, "soccerbase");
		createDivisionMappingsElement(doc, source);
		createTeamMappingsElement(doc, source);
		
		return doc;
	}
	
	private void createTeamMappingElement(Document doc, Element parent, String sourceId, String fraId) {
		Element element = doc.createElement("Team");
		element.setAttribute("sourceId", sourceId);
		element.setAttribute("fraId", fraId);
		parent.appendChild(element);
	}

	private void createDivisionMappingElement(Document doc, Element parent, String sourceId, String fraId) {
		Element element = doc.createElement("Div");
		element.setAttribute("sourceId", sourceId);
		element.setAttribute("fraId", fraId);
		parent.appendChild(element);
	}

	private void createIncludedDivElement(Document doc, Element parent, String sourceId) {
		Element element = doc.createElement("Div");
		element.setAttribute("sourceId", sourceId);
		parent.appendChild(element);
	}

	private Element createTeamMappingsElement(Document doc, Element parent) {
		Element element = doc.createElement("TeamMappings");
		parent.appendChild(element);
		return element;
	}

	private Element createDivisionMappingsElement(Document doc, Element parent) {
		Element element = doc.createElement("DivisionMappings");
		parent.appendChild(element);
		return element;
	}

	private Element createIncludedDivisionsElement(Document doc, Element parent) {
		Element element = doc.createElement("IncludedDivisions");
		parent.appendChild(element);
		return element;
	}

	private Element createSourceElement(Document doc, Element parent, String dialect) {
		Element element = doc.createElement("Source");
		element.setAttribute("dialect", dialect);
		parent.appendChild(element);
		return element;
	}

	private Element createRootElement(Document doc) {
		Element element = doc.createElement("Mapping");
		doc.appendChild(element);
		return element;
	}

	private Document createNewDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		return doc;
	}
}
