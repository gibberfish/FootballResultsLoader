package uk.co.mindbadger.footballresults.reader;

import java.util.List;

import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;

public interface FootballResultsReader {
	public String getDialect();
	
	public DomainObjectFactory getDomainObjectFactory ();
	public void setDomainObjectFactory (DomainObjectFactory domainObjectFactory);
	
	public FootballResultsAnalyserDAO getDAO ();
	public void setDAO (FootballResultsAnalyserDAO dao);
	
	public List<Fixture> readFixturesForSeason (int season);
}
