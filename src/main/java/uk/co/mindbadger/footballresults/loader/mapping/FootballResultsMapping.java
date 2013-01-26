package uk.co.mindbadger.footballresults.loader.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoaderException;
import uk.co.mindbadger.xml.XMLFileReader;

public class FootballResultsMapping {
	private String mappingFile;
	private XMLFileReader xmlFileReader;

	private Map<String, Dialect> dialects = new HashMap<String, Dialect>();

	private List<Integer> includedDivisions = new ArrayList<Integer>();

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
			}

		} catch (Exception e) {
			throw new FootballResultsLoaderException(e);
		}
	}

	public List<Integer> getIncludedDivisions(String dialectName) {
		Dialect dialect = dialects.get(dialectName);
		if (dialect == null) {
			throw new FootballResultsLoaderException("There is no configuration for " + dialectName + " in your mapping file");
		}
		
		return includedDivisions;
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
