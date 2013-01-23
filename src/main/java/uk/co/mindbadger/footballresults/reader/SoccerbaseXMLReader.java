package uk.co.mindbadger.footballresults.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;

public class SoccerbaseXMLReader implements FootballResultsReader {
    private String dialect;
    private DomainObjectFactory domainObjectFactory;
    private FootballResultsAnalyserDAO dao;
    private XMLFileReader xmlFileReader;

    @Override
    public List<Fixture> readFixturesForSeason(int season) {
	List<Fixture> fixtures = new ArrayList<Fixture> ();
	
	List<String> fullyQualifiedSeasonFileNames = xmlFileReader.getFilesForSeason(season);
	
	for (String fileName : fullyQualifiedSeasonFileNames) {
	    try {
		Document doc = xmlFileReader.readXMLFile(fileName);
		
		Element rootElement = doc.getDocumentElement();
		
		NodeList competitions = rootElement.getElementsByTagName("Competition");
		
		for (int i=0; i<competitions.getLength();i++) {
		    Element competition = (Element) competitions.item(i);
		    
		    NodeList games = competition.getElementsByTagName("Game");
		    for (int j=0; j<games.getLength();j++) {
			fixtures.add(domainObjectFactory.createFixture(null, null, null, null));
		    }
		}
	    } catch (Exception e) {
		throw new FootballResultsXMLException(e);
	    }
	}
	
	return fixtures;
    }

    @Override
    public String getDialect() {
	return dialect;
    }

    public void setDialect(String dialect) {
	this.dialect = dialect;
    }

    @Override
    public DomainObjectFactory getDomainObjectFactory() {
	return domainObjectFactory;
    }

    @Override
    public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
	this.domainObjectFactory = domainObjectFactory;
    }

    @Override
    public FootballResultsAnalyserDAO getDAO() {
	return dao;
    }

    @Override
    public void setDAO(FootballResultsAnalyserDAO dao) {
	this.dao = dao;
    }

    public XMLFileReader getXmlFileReader() {
	return xmlFileReader;
    }

    public void setXmlFileReader(XMLFileReader xmlFileReader) {
	this.xmlFileReader = xmlFileReader;
    }
}
