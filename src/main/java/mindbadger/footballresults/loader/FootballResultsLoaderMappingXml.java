package mindbadger.footballresults.loader;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import mindbadger.xml.XMLFileReader;
import mindbadger.xml.XMLFileWriter;

public class FootballResultsLoaderMappingXml extends AbstractFootballResultsLoaderMapping {
	private XMLFileWriter xmlFileWriter;
	private String mappingFile;
	
	public FootballResultsLoaderMappingXml(String mappingFile, XMLFileReader xmlFileReader, XMLFileWriter xmlFileWriter) throws FootballResultsLoaderException {
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
					String sourceId = includedDivision.getAttribute("sourceId");
					//Integer includedDivisionId = Integer.parseInt(sourceId);
					dialect.getIncludedDivisions().add(sourceId);
				}
				
				NodeList divisionMappingsContainer = source.getElementsByTagName("DivisionMappings");
				if (divisionMappingsContainer.getLength() != 1) {
					throw new FootballResultsLoaderException("There are no DivisionMappings in your mapping file for dialect " + dialectName);
				}

				Element divisionMappingsContainerElement = (Element) divisionMappingsContainer.item(0);
				NodeList divisionMappings = divisionMappingsContainerElement.getElementsByTagName("Div");

				for (int k = 0; k < divisionMappings.getLength(); k++) {
					Element divisionMapping = (Element) divisionMappings.item(k);
					String sourceDivisionId = divisionMapping.getAttribute("sourceId");
					String fraDivisionId = divisionMapping.getAttribute("fraId");
					dialect.getDivisionMappings().put(sourceDivisionId, fraDivisionId);
					dialect.getOrderedListOfDivisions().add(fraDivisionId);
				}

				NodeList teamMappingsContainer = source.getElementsByTagName("TeamMappings");
				Element teamMappingsContainerElement = (Element) teamMappingsContainer.item(0);
				NodeList teamMappings = teamMappingsContainerElement.getElementsByTagName("Team");

				for (int l = 0; l < teamMappings.getLength(); l++) {
					Element teamMapping = (Element) teamMappings.item(l);
					String sourceTeamId = teamMapping.getAttribute("sourceId");
					String fraTeamId = teamMapping.getAttribute("fraId");
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

	@Override
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

				for (String includedDivision : dialect.getIncludedDivisions()) {
					Element div = doc.createElement("Div");
					includedDivisions.appendChild(div);
					Attr attr2 = doc.createAttribute("sourceId");
					attr2.setValue(includedDivision);
					div.setAttributeNode(attr2);
				}

				Element divisionMappings = doc.createElement("DivisionMappings");
				source.appendChild(divisionMappings);

				for (String divisionMappingSourceId : dialect.getDivisionMappings().keySet()) {
					Element div = doc.createElement("Div");
					divisionMappings.appendChild(div);
					Attr attr2 = doc.createAttribute("sourceId");
					attr2.setValue(divisionMappingSourceId);
					div.setAttributeNode(attr2);
					Attr attr3 = doc.createAttribute("fraId");
					attr3.setValue(dialect.getDivisionMappings().get(divisionMappingSourceId));
					div.setAttributeNode(attr3);
				}

				Element teamMappings = doc.createElement("TeamMappings");
				source.appendChild(teamMappings);

				for (String teamMappingSourceId : dialect.getTeamMappings().keySet()) {
					Element team = doc.createElement("Team");
					teamMappings.appendChild(team);
					Attr attr2 = doc.createAttribute("sourceId");
					attr2.setValue(teamMappingSourceId);
					team.setAttributeNode(attr2);
					Attr attr3 = doc.createAttribute("fraId");
					attr3.setValue(dialect.getTeamMappings().get(teamMappingSourceId));
					team.setAttributeNode(attr3);
				}
			}
			
			xmlFileWriter.writeXMLFile(mappingFile, doc);
		} catch (ParserConfigurationException | FileNotFoundException | TransformerException e) {
			throw new FootballResultsLoaderException(e);
		}
	}
}
