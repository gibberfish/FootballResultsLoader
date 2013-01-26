package uk.co.mindbadger.footballresults.loader.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoaderException;
import uk.co.mindbadger.xml.XMLFileReader;

public class FootballResultsMapping {
	private String mappingFile;
	private XMLFileReader xmlFileReader;

	private Map<String, Dialect> dialects = new HashMap<String, Dialect>();

	public FootballResultsMapping(String mappingFile, XMLFileReader xmlFileReader) throws FootballResultsLoaderException {
		this.mappingFile = mappingFile;
		this.xmlFileReader = xmlFileReader;

		try {
			Document doc = xmlFileReader.readXMLFile(mappingFile);
			Element rootElement = doc.getDocumentElement();

			NodeList sourceList = rootElement.getElementsByTagName("Source");
			for (int i = 0; i < sourceList.getLength(); i++) {
				Element source = (Element) sourceList.item(i);

				String dialectName = source.getAttribute("dialect");
				Dialect dialect = new Dialect(dialectName);
				dialects.put(dialectName, dialect);
				
				NodeList includedDivisionsContainer = source.getElementsByTagName("IncludedDivisions");
				if (includedDivisionsContainer.getLength() != 1) {
					throw new FootballResultsLoaderException("There are no IncludedDivisions in your mapping file for dialect " + dialectName);
				}
				
				Element includedDivisionsContainerElement = (Element) includedDivisionsContainer.item(0);
				NodeList includedDivisions = includedDivisionsContainerElement.getElementsByTagName("Div");
				
				for (int j = 0; j < includedDivisions.getLength(); j++) {
					Element includedDivision = (Element) includedDivisions.item(j);
					Integer includedDivisionId = Integer.parseInt(includedDivision.getAttribute("sourceId"));
					dialect.getIncludedDivisions().add(includedDivisionId);
				}
			}

		} catch (ParserConfigurationException e) {
			throw new FootballResultsLoaderException(e);
		} catch (SAXException e) {
			throw new FootballResultsLoaderException(e);
		} catch (IOException e) {
			throw new FootballResultsLoaderException(e);
		}
	}

	public List<Integer> getIncludedDivisions(String dialectName) {
		Dialect dialect = dialects.get(dialectName);
		if (dialect == null) {
			throw new FootballResultsLoaderException("There is no configuration for " + dialectName + " in your mapping file");
		}
		
		return dialects.get(dialectName).getIncludedDivisions();
	}

	private class Dialect {
		private String name;
		private List<Integer> includedDivisions = new ArrayList<Integer>();
		private Map<Integer, Integer> divisionMappings = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> teamMappings = new HashMap<Integer, Integer>();

		public Dialect(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public List<Integer> getIncludedDivisions() {
			return includedDivisions;
		}

		public void setIncludedDivisions(List<Integer> includedDivisions) {
			this.includedDivisions = includedDivisions;
		}

		public Map<Integer, Integer> getDivisionMappings() {
			return divisionMappings;
		}

		public void setDivisionMappings(Map<Integer, Integer> divisionMappings) {
			this.divisionMappings = divisionMappings;
		}

		public Map<Integer, Integer> getTeamMappings() {
			return teamMappings;
		}

		public void setTeamMappings(Map<Integer, Integer> teamMappings) {
			this.teamMappings = teamMappings;
		}
	}
}
