package uk.co.mindbadger.footballresults.reader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;

public class SoccerbaseXMLReader implements FootballResultsReader {
    private String dialect;
    private DomainObjectFactory domainObjectFactory;
    private FootballResultsAnalyserDAO dao;
    private XMLFileReader xmlFileReader;

    private Map<Integer, Division> divisions = new HashMap<Integer, Division> ();
    private Map<Integer, Team> teams = new HashMap<Integer, Team> ();
    
    @Override
    public List<Fixture> readFixturesForSeason(int seasonNumber) {
	List<Fixture> fixtures = new ArrayList<Fixture> ();

	Season season = domainObjectFactory.createSeason(seasonNumber);
	
	List<String> fullyQualifiedSeasonFileNames = xmlFileReader.getFilesForSeason(seasonNumber);
	
	for (String fileName : fullyQualifiedSeasonFileNames) {
	    try {
		Document doc = xmlFileReader.readXMLFile(fileName);
		
		Element rootElement = doc.getDocumentElement();
		
		Calendar fixtureDate = convertDateStringToCalendar(rootElement.getAttribute("date"));
		
		NodeList competitions = rootElement.getElementsByTagName("Competition");
		
		for (int i=0; i<competitions.getLength();i++) {
		    Element competition = (Element) competitions.item(i);
		    
		    Integer seasonId = Integer.parseInt(competition.getAttribute("seasonId")) + 1870;
		    if (seasonId != seasonNumber) {
			//TODO need a test for this scenario
			throw new FootballResultsXMLException("Your xml file contains a season that is not in the correct folder");
		    }
		    
		    Integer competitionId = Integer.parseInt(competition.getAttribute("competitionId"));
		    Division division = null;
		    if (divisions.containsKey(competitionId)) {
			division = divisions.get(competitionId);
		    } else {
			division = domainObjectFactory.createDivision(competitionId, competition.getAttribute("competitionName"));
		    }
		    
		    NodeList games = competition.getElementsByTagName("Game");
		    for (int j=0; j<games.getLength();j++) {
			Element game = (Element) games.item(j);
			
			Integer fixtureId = Integer.parseInt(game.getAttribute("gameId"));
			Integer homeTeamId = Integer.parseInt(game.getAttribute("homeTeamId"));
			Integer awayTeamId = Integer.parseInt(game.getAttribute("awayTeamId"));

			Team homeTeam = null;
			if (teams.containsKey(homeTeamId)) {
			    homeTeam = teams.get(homeTeamId);
			} else {
			    homeTeam = domainObjectFactory.createTeam(homeTeamId, game.getAttribute("homeTeamName"));
			}

			Team awayTeam = null;
			if (teams.containsKey(awayTeamId)) {
			    awayTeam = teams.get(awayTeamId);
			} else {
			    awayTeam = domainObjectFactory.createTeam(awayTeamId, game.getAttribute("awayTeamName"));
			}

			Fixture fixture = domainObjectFactory.createFixture(fixtureId, season, homeTeam, awayTeam);
			fixture.setFixtureDate(fixtureDate);
			fixture.setDivision(division);
			fixtures.add(fixture);

			NodeList scores = game.getElementsByTagName("Score");
			Element score = (Element) scores.item(0);
			if (score!=null){
			    fixture.setHomeGoals(Integer.parseInt(score.getAttribute("homeGoals")));
			    fixture.setAwayGoals(Integer.parseInt(score.getAttribute("awayGoals")));
			}
		    }
		}
	    } catch (Exception e) {
		throw new FootballResultsXMLException(e);
	    }
	}
	
	return fixtures;
    }

    private Calendar convertDateStringToCalendar (String dateString) {
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	try {
	    cal.setTime(sdf.parse(dateString));
	    return cal;
	} catch (ParseException e) {
	    //TODO Need to add a test to deal with being unable to parse the date
	    throw new FootballResultsXMLException(e);
	}
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
