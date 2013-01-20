package uk.co.mindbadger.footballresults.reader;

import java.util.List;

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
		
		System.out.println("HELLO!");
		
		return null;
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

