package uk.co.mindbadger.footballresults.loader.mapping;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ietf.jgss.Oid;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoaderException;
import uk.co.mindbadger.xml.XMLFileReader;

public class FootballResultsMappingTest{
	private static final String NON_EXISTANT_DIALECT = "Non Existant Dialect";
	private static final String FRA_TEAM_ID_2 = "4";
	private static final String FRA_TEAM_ID_1 = "3";
	private static final String FRA_DIV_ID_2 = "2";
	private static final String FRA_DIV_ID_1 = "1";
	private static final String SOURCE_TEAM_ID_2 = "501";
	private static final String SOURCE_TEAM_ID_1 = "500";
	private static final String SOURCE_DIV_ID_3 = "1025";
	private static final String SOURCE_DIV_ID_2 = "1024";
	private static final String SOURCE_DIV_ID_1 = "1023";
	private static final String MAPPING_FILE = "C:\\mapping\\mapping.xml";
	private static final String DIALECT = "soccerbase";

	private FootballResultsMapping objectUnderTest;
	
	@Mock private XMLFileReader mockXmlFileReader;
	
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
			objectUnderTest = new FootballResultsMapping(MAPPING_FILE, mockXmlFileReader);
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
			objectUnderTest = new FootballResultsMapping(MAPPING_FILE, mockXmlFileReader);
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
			objectUnderTest = new FootballResultsMapping(MAPPING_FILE, mockXmlFileReader);
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
		objectUnderTest = new FootballResultsMapping(MAPPING_FILE, mockXmlFileReader);
			
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
			objectUnderTest = new FootballResultsMapping(MAPPING_FILE, mockXmlFileReader);
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
		objectUnderTest = new FootballResultsMapping(MAPPING_FILE, mockXmlFileReader);

		// When
		List<Integer> includedDivisions = objectUnderTest.getIncludedDivisions (DIALECT);
		
		// Then
		assertEquals (3, includedDivisions.size());
		assertEquals (new Integer(SOURCE_DIV_ID_1), includedDivisions.get(0));
		assertEquals (new Integer(SOURCE_DIV_ID_2), includedDivisions.get(1));
		assertEquals (new Integer(SOURCE_DIV_ID_3), includedDivisions.get(2));
	}

	@Test
	public void shouldThrowExceptionWhenNoDivisionMappingsExistsInXML () throws Exception {
		// Given
		when (mockXmlFileReader.readXMLFile(MAPPING_FILE)).thenReturn(getDocumentWithNoDivisionMappingsXML());
			
		// When
		try {
			objectUnderTest = new FootballResultsMapping(MAPPING_FILE, mockXmlFileReader);
			fail("Should thrown a FootballResultsLoaderException here");
		} catch (FootballResultsLoaderException e) {
			// Then
			assertEquals ("There are no DivisionMappings in your mapping file for dialect " + DIALECT, e.getMessage());
		}
	}

	
	// ---------------------------------------------------------------------------------------------------------------
	
	private Document getDocumentWithNoDivisionMappingsXML() throws ParserConfigurationException {
		Document doc = createNewDocument();
		Element root = createRootElement(doc);

		Element source = createSourceElement(doc, root, "soccerbase");
		Element includedDivisions = createIncludedDivisionsElement(doc, source);
		Element teamMapping = createTeamMappingsElement(doc, source);
		
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
		Element divisionMapping = createDivisionMappingsElement(doc, source);
		Element teamMapping = createTeamMappingsElement(doc, source);
		
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
