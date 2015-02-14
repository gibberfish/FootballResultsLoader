package uk.co.mindbadger.footballresults.loader.mapping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mindbadger.footballresults.loader.FootballResultsLoaderException;
import uk.co.mindbadger.xml.XMLFileReader;
import uk.co.mindbadger.xml.XMLFileWriter;

public class FootballResultsMapping<K> {
	private XMLFileWriter xmlFileWriter;
	private String mappingFile;
	
	private Map<String, Dialect> dialects = new HashMap<String, Dialect>();

	public FootballResultsMapping(String mappingFile, XMLFileReader xmlFileReader, XMLFileWriter xmlFileWriter) throws FootballResultsLoaderException {
		this.xmlFileWriter = xmlFileWriter;
		this.mappingFile = mappingFile;
		
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
				
				NodeList divisionMappingsContainer = source.getElementsByTagName("DivisionMappings");
				if (divisionMappingsContainer.getLength() != 1) {
					throw new FootballResultsLoaderException("There are no DivisionMappings in your mapping file for dialect " + dialectName);
				}

				Element divisionMappingsContainerElement = (Element) divisionMappingsContainer.item(0);
				NodeList divisionMappings = divisionMappingsContainerElement.getElementsByTagName("Div");

				for (int k = 0; k < divisionMappings.getLength(); k++) {
					Element divisionMapping = (Element) divisionMappings.item(k);
					Integer sourceDivisionId = Integer.parseInt(divisionMapping.getAttribute("sourceId"));
					Integer fraDivisionId = Integer.parseInt(divisionMapping.getAttribute("fraId"));
					
					dialect.getDivisionMappings().put(sourceDivisionId, fraDivisionId);
				}

				NodeList teamMappingsContainer = source.getElementsByTagName("TeamMappings");
				Element teamMappingsContainerElement = (Element) teamMappingsContainer.item(0);
				NodeList teamMappings = teamMappingsContainerElement.getElementsByTagName("Team");

				for (int l = 0; l < teamMappings.getLength(); l++) {
					Element teamMapping = (Element) teamMappings.item(l);
					Integer sourceTeamId = Integer.parseInt(teamMapping.getAttribute("sourceId"));
					Integer fraTeamId = Integer.parseInt(teamMapping.getAttribute("fraId"));
					
					dialect.getTeamMappings().put(sourceTeamId, fraTeamId);
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
	
	public void addDivisionMapping(String dialectName, Integer sourceDivId, Integer fraDivId) {
		dialects.get(dialectName).getDivisionMappings().put(sourceDivId, fraDivId);
	}

	public Map<Integer, K> getDivisionMappings(String dialectName) {
		return dialects.get(dialectName).getDivisionMappings();
	}
	
	public Map<Integer, K> getTeamMappings(String dialectName) {
		return dialects.get(dialectName).getTeamMappings();
	}
	
	public void addTeamMapping(String dialectName, Integer sourceTeamId, Integer fraTeamId) {
		dialects.get(dialectName).getTeamMappings().put(sourceTeamId, fraTeamId);
	}
	
	public void saveMappings() throws FootballResultsLoaderException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Mapping");
			doc.appendChild(rootElement);
			
			for (Dialect dialect : dialects.values()) {
				Element source = doc.createElement("Source");
				rootElement.appendChild(source);
				Attr attr = doc.createAttribute("dialect");
				attr.setValue(dialect.getName());
				source.setAttributeNode(attr);

				Element includedDivisions = doc.createElement("IncludedDivisions");
				source.appendChild(includedDivisions);

				for (Integer includedDivision : dialect.getIncludedDivisions()) {
					Element div = doc.createElement("Div");
					includedDivisions.appendChild(div);
					Attr attr2 = doc.createAttribute("sourceId");
					attr2.setValue(includedDivision.toString());
					div.setAttributeNode(attr2);
				}

				Element divisionMappings = doc.createElement("DivisionMappings");
				source.appendChild(divisionMappings);

				for (Integer divisionMappingSourceId : dialect.getDivisionMappings().keySet()) {
					Element div = doc.createElement("Div");
					divisionMappings.appendChild(div);
					Attr attr2 = doc.createAttribute("sourceId");
					attr2.setValue(divisionMappingSourceId.toString());
					div.setAttributeNode(attr2);
					Attr attr3 = doc.createAttribute("fraId");
					attr3.setValue(dialect.getDivisionMappings().get(divisionMappingSourceId).toString());
					div.setAttributeNode(attr3);
				}

				Element teamMappings = doc.createElement("TeamMappings");
				source.appendChild(teamMappings);

				for (Integer teamMappingSourceId : dialect.getTeamMappings().keySet()) {
					Element team = doc.createElement("Team");
					teamMappings.appendChild(team);
					Attr attr2 = doc.createAttribute("sourceId");
					attr2.setValue(teamMappingSourceId.toString());
					team.setAttributeNode(attr2);
					Attr attr3 = doc.createAttribute("fraId");
					attr3.setValue(dialect.getTeamMappings().get(teamMappingSourceId).toString());
					team.setAttributeNode(attr3);
				}
			}
			
			xmlFileWriter.writeXMLFile(mappingFile, doc);
		} catch (ParserConfigurationException | FileNotFoundException | TransformerException e) {
			throw new FootballResultsLoaderException(e);
		}
	}
	
	private class Dialect<K> {
		private String name;
		private List<Integer> includedDivisions = new ArrayList<Integer>();
		private Map<Integer, K> divisionMappings = new HashMap<Integer, K>();
		private Map<Integer, K> teamMappings = new HashMap<Integer, K>();

		public Dialect(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public List<Integer> getIncludedDivisions() {
			return includedDivisions;
		}

		public Map<Integer, K> getDivisionMappings() {
			return divisionMappings;
		}

		public Map<Integer, K> getTeamMappings() {
			return teamMappings;
		}
	}
}
