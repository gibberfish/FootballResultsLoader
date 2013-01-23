package uk.co.mindbadger.footballresults.reader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;

public class SoccerbaseXMLReaderTest {
    private static final int SEASON = 2000;
    private static final String SEASON_ID = "130";

    private static final String COMPETITION_1_ID = "100";
    private static final String COMPETITION_1_NAME = "English Premier";
    private static final String COMPETITION_2_ID = "101";
    private static final String COMPETITION_2_NAME = "English Championship";

    private static final String TEAM_1_ID = "1000";
    private static final String TEAM_1_NAME = "Chelsea";
    private static final String TEAM_2_ID = "1001";
    private static final String TEAM_2_NAME = "Newcastle";
    private static final String TEAM_3_ID = "1002";
    private static final String TEAM_3_NAME = "Arsenal";
    private static final String TEAM_4_ID = "1003";
    private static final String TEAM_4_NAME = "Man Utd";
    private static final String TEAM_5_ID = "1004";
    private static final String TEAM_5_NAME = "Ipswich";
    private static final String TEAM_6_ID = "1005";
    private static final String TEAM_6_NAME = "Sheff Utd";
    private static final String TEAM_7_ID = "1006";
    private static final String TEAM_7_NAME = "Middlesbrough";
    private static final String TEAM_8_ID = "1007";
    private static final String TEAM_8_NAME = "Cardiff";
    
    private static final String FIXTURE_1_ID = "9990";
    private static final String FIXTURE_DATE_1 = "2000-11-18";
    private static final String FIXTURE_1_HOME_GOALS = "0";
    private static final String FIXTURE_1_AWAY_GOALS = "1";
    private static final String FIXTURE_2_ID = "9991";
    private static final String FIXTURE_2_HOME_GOALS = "2";
    private static final String FIXTURE_2_AWAY_GOALS = "3";
    private static final String FIXTURE_3_ID = "9992";
    private static final String FIXTURE_3_HOME_GOALS = "4";
    private static final String FIXTURE_3_AWAY_GOALS = "5";
    private static final String FIXTURE_4_ID = "9993";
    private static final String FIXTURE_4_HOME_GOALS = "6";
    private static final String FIXTURE_4_AWAY_GOALS = "7";
    private static final String FIXTURE_DATE_2 = "2000-11-29";
    private static final String FIXTURE_5_ID = "9994";
    private static final String FIXTURE_5_HOME_GOALS = "8";
    private static final String FIXTURE_5_AWAY_GOALS = "9";
    private static final String FIXTURE_DATE_3 = "2000-12-20";
    private static final String FIXTURE_6_ID = "9995";
    private static final String FIXTURE_6_HOME_GOALS = "10";
    private static final String FIXTURE_6_AWAY_GOALS = "11";
    private static final String FIXTURE_7_ID = "9996";
    private static final String FIXTURE_7_HOME_GOALS = "12";
    private static final String FIXTURE_7_AWAY_GOALS = "13";
    private static final String FIXTURE_8_ID = "9997";
    
    private static final String DIALECT = "soccerbase";
    private static final String FILE3 = "/location/FILE3.xml";
    private static final String FILE2 = "/location/FILE2.xml";
    private static final String FILE1 = "/location/FILE1.xml";

    private SoccerbaseXMLReader objectUnderTest;

    @Mock
    private DomainObjectFactory mockDomainObjectFactory;
    @Mock
    private FootballResultsAnalyserDAO mockDao;
    @Mock
    private XMLFileReader mockXmlFileReader;

    @Before
    public void setup() {
	MockitoAnnotations.initMocks(this);

	objectUnderTest = new SoccerbaseXMLReader();
	objectUnderTest.setDAO(mockDao);
	objectUnderTest.setDialect(DIALECT);
	objectUnderTest.setDomainObjectFactory(mockDomainObjectFactory);
	objectUnderTest.setXmlFileReader(mockXmlFileReader);
    }

    @Test
    public void testReadFixturesForSeason() throws Exception {
	// Given
	weHaveThreeFilesInOurDirectory();

	when(mockXmlFileReader.readXMLFile(FILE1)).thenReturn(getDocument1());
	when(mockXmlFileReader.readXMLFile(FILE2)).thenReturn(getDocument2());
	when(mockXmlFileReader.readXMLFile(FILE3)).thenReturn(getDocument3());

	// When
	List<Fixture> fixtures = objectUnderTest.readFixturesForSeason(SEASON);

	// Then
	verify(mockXmlFileReader).getFilesForSeason(SEASON);
	verify(mockXmlFileReader).readXMLFile(FILE1);
	verify(mockXmlFileReader).readXMLFile(FILE2);
	verify(mockXmlFileReader).readXMLFile(FILE3);
	
	assertEquals(8, fixtures.size());
    }

    private void weHaveThreeFilesInOurDirectory() {
	List<String> listOfFiles = new ArrayList<String>();
	listOfFiles.add(FILE1);
	listOfFiles.add(FILE2);
	listOfFiles.add(FILE3);
	when(mockXmlFileReader.getFilesForSeason(SEASON)).thenReturn(listOfFiles);
    }

    private Document getDocument1() throws ParserConfigurationException {
	Document doc = createNewDocument();
	Element root = createFixtureDateElement (doc, FIXTURE_DATE_1);

	Element div1 = createCompetitionElement(doc, root, COMPETITION_1_ID, COMPETITION_1_NAME, SEASON_ID);
	Element game1 = createGameElement(doc, div1, FIXTURE_1_ID, TEAM_1_ID, TEAM_1_NAME, TEAM_2_ID, TEAM_2_NAME);
	createScoreElement(doc, game1, FIXTURE_1_HOME_GOALS, FIXTURE_1_AWAY_GOALS);
	Element game2 = createGameElement(doc, div1, FIXTURE_2_ID, TEAM_3_ID, TEAM_3_NAME, TEAM_4_ID, TEAM_4_NAME);
	createScoreElement(doc, game2, FIXTURE_2_HOME_GOALS, FIXTURE_2_AWAY_GOALS);

	Element div2 = createCompetitionElement(doc, root, COMPETITION_2_ID, COMPETITION_2_NAME, SEASON_ID);
	Element game3 = createGameElement(doc, div2, FIXTURE_3_ID, TEAM_5_ID, TEAM_5_NAME, TEAM_6_ID, TEAM_6_NAME);
	createScoreElement(doc, game3, FIXTURE_3_HOME_GOALS, FIXTURE_3_AWAY_GOALS);
	Element game4 = createGameElement(doc, div2, FIXTURE_4_ID, TEAM_7_ID, TEAM_7_NAME, TEAM_8_ID, TEAM_8_NAME);
	createScoreElement(doc, game4, FIXTURE_4_HOME_GOALS, FIXTURE_4_AWAY_GOALS);

	return doc;
    }
    
    private Document getDocument2() throws ParserConfigurationException {
	Document doc = createNewDocument();
	Element root = createFixtureDateElement (doc, FIXTURE_DATE_2);

	Element div1 = createCompetitionElement(doc, root, COMPETITION_1_ID, COMPETITION_1_NAME, SEASON_ID);
	Element game1 = createGameElement(doc, div1, FIXTURE_5_ID, TEAM_2_ID, TEAM_2_NAME, TEAM_1_ID, TEAM_1_NAME);
	createScoreElement(doc, game1, FIXTURE_5_HOME_GOALS, FIXTURE_5_AWAY_GOALS);

	return doc;
    }

    private Document getDocument3() throws ParserConfigurationException {
	Document doc = createNewDocument();
	Element root = createFixtureDateElement (doc, FIXTURE_DATE_3);

	Element div1 = createCompetitionElement(doc, root, COMPETITION_1_ID, COMPETITION_1_NAME, SEASON_ID);
	Element game2 = createGameElement(doc, div1, FIXTURE_6_ID, TEAM_4_ID, TEAM_4_NAME, TEAM_3_ID, TEAM_3_NAME);
	createScoreElement(doc, game2, FIXTURE_6_HOME_GOALS, FIXTURE_6_AWAY_GOALS);

	Element div2 = createCompetitionElement(doc, root, COMPETITION_2_ID, COMPETITION_2_NAME, SEASON_ID);
	Element game3 = createGameElement(doc, div2, FIXTURE_7_ID, TEAM_6_ID, TEAM_6_NAME, TEAM_5_ID, TEAM_5_NAME);
	createScoreElement(doc, game3, FIXTURE_7_HOME_GOALS, FIXTURE_7_AWAY_GOALS);
	Element game4 = createGameElement(doc, div2, FIXTURE_8_ID, TEAM_8_ID, TEAM_8_NAME, TEAM_7_ID, TEAM_7_NAME);

	return doc;
    }
    
    private Document createNewDocument() throws ParserConfigurationException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = factory.newDocumentBuilder();
	Document doc = builder.newDocument();
	return doc;
    }
    
    private Element createFixtureDateElement (Document doc, String date) {
	Element element = doc.createElement("FixtureDate");
	element.setAttribute("date", date);
	doc.appendChild(element);
	return element;
    }
    
    private Element createCompetitionElement(Document doc, Element rootElement, String competitionId, String competitionName, String seasonId) {
	Element div = doc.createElement("Competition");
	rootElement.appendChild(div);
	div.setAttribute("competitionId", competitionId);
	div.setAttribute("competitionName", competitionName);
	div.setAttribute("seasonId", seasonId);
	return div;
    }
    
    private Element createGameElement(Document doc, Element competitionElement, String gameId, String homeTeamId, String homeTeamName, String awayTeamId, String awayTeamName) {
	Element game = doc.createElement("Game");
	competitionElement.appendChild(game);
	game.setAttribute("awayTeamId", awayTeamId);
	game.setAttribute("awayTeamName", awayTeamName);
	game.setAttribute("gameId", gameId);
	game.setAttribute("homeTeamId", homeTeamId);
	game.setAttribute("homeTeamName", homeTeamName);
	return game;
    }
    
    private void createScoreElement(Document doc, Element game1, String homeGoals, String awayGoals) {
	Element score1 = doc.createElement("Score");
	game1.appendChild(score1);
	score1.setAttribute("awayGoals", awayGoals);
	score1.setAttribute("homeGoals", homeGoals);
    }
}
