package uk.co.mindbadger.footballresults.reader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.DivisionImpl;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Fixture;
import uk.co.mindbadger.footballresultsanalyser.domain.FixtureImpl;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.SeasonImpl;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;
import uk.co.mindbadger.footballresultsanalyser.domain.TeamImpl;

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

    // @Mock private Fixture mockFixture1;
    // @Mock private Fixture mockFixture2;
    // @Mock private Fixture mockFixture3;
    // @Mock private Fixture mockFixture4;
    // @Mock private Fixture mockFixture5;
    // @Mock private Fixture mockFixture6;
    // @Mock private Fixture mockFixture7;
    // @Mock private Fixture mockFixture8;

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

	Season season = new SeasonImpl();
	when(mockDomainObjectFactory.createSeason(Integer.parseInt(SEASON_ID))).thenReturn(season);

	Division division1 = new DivisionImpl();
	Division division2 = new DivisionImpl();
	when(mockDomainObjectFactory.createDivision(Integer.parseInt(COMPETITION_1_ID), COMPETITION_1_NAME)).thenReturn(division1).thenReturn(division2);

	Team team1 = new TeamImpl();
	Team team2 = new TeamImpl();
	Team team3 = new TeamImpl();
	Team team4 = new TeamImpl();
	Team team5 = new TeamImpl();
	Team team6 = new TeamImpl();
	Team team7 = new TeamImpl();
	Team team8 = new TeamImpl();
	when(mockDomainObjectFactory.createTeam(Integer.parseInt(TEAM_1_ID), TEAM_1_NAME))
		.thenReturn(team1)
		.thenReturn(team2)
		.thenReturn(team3)
		.thenReturn(team4)
		.thenReturn(team5)
		.thenReturn(team6)
		.thenReturn(team7)
		.thenReturn(team8);

	Fixture fixture1 = new FixtureImpl ();
	when(mockDomainObjectFactory.createFixture(Integer.parseInt(FIXTURE_1_ID), season, team1, team2)).thenReturn(fixture1);

	Fixture fixture2 = new FixtureImpl ();
	when(mockDomainObjectFactory.createFixture(Integer.parseInt(FIXTURE_2_ID), season, team3, team4)).thenReturn(fixture2);

	Fixture fixture3 = new FixtureImpl ();
	when(mockDomainObjectFactory.createFixture(Integer.parseInt(FIXTURE_3_ID), season, team5, team6)).thenReturn(fixture3);

	Fixture fixture4 = new FixtureImpl ();
	when(mockDomainObjectFactory.createFixture(Integer.parseInt(FIXTURE_4_ID), season, team7, team8)).thenReturn(fixture4);

	Fixture fixture5 = new FixtureImpl ();
	when(mockDomainObjectFactory.createFixture(Integer.parseInt(FIXTURE_5_ID), season, team2, team1)).thenReturn(fixture5);

	Fixture fixture6 = new FixtureImpl ();
	when(mockDomainObjectFactory.createFixture(Integer.parseInt(FIXTURE_6_ID), season, team4, team3)).thenReturn(fixture6);

	Fixture fixture7 = new FixtureImpl ();
	when(mockDomainObjectFactory.createFixture(Integer.parseInt(FIXTURE_7_ID), season, team6, team5)).thenReturn(fixture7);

	Fixture fixture8 = new FixtureImpl ();
	when(mockDomainObjectFactory.createFixture(Integer.parseInt(FIXTURE_8_ID), season, team8, team7)).thenReturn(fixture8);

	// When
	List<Fixture> fixtures = objectUnderTest.readFixturesForSeason(SEASON);

	// Then
	verify(mockXmlFileReader).getFilesForSeason(SEASON);
	verify(mockXmlFileReader).readXMLFile(FILE1);
	verify(mockXmlFileReader).readXMLFile(FILE2);
	verify(mockXmlFileReader).readXMLFile(FILE3);

	assertEquals(8, fixtures.size());

	assertEquals (fixture1, fixtures.get(0));
	assertEquals(FIXTURE_DATE_1, convertCalendarToString(fixture1.getFixtureDate()));
	assertEquals(FIXTURE_1_ID, fixture1.getFixtureId());
	assertEquals(season, fixture1.getSeason());
	assertEquals(division1, fixture1.getDivision());
	assertEquals(team1, fixture1.getHomeTeam());
	assertEquals(team2, fixture1.getAwayTeam());
	assertEquals(FIXTURE_1_HOME_GOALS, fixture1.getHomeGoals());
	assertEquals(FIXTURE_1_AWAY_GOALS, fixture1.getAwayGoals());
	
	assertEquals (fixture2, fixtures.get(1));
	assertEquals(FIXTURE_DATE_1, convertCalendarToString(fixture2.getFixtureDate()));
	assertEquals(FIXTURE_2_ID, fixture2.getFixtureId());
	assertEquals(season, fixture2.getSeason());
	assertEquals(division1, fixture2.getDivision());
	assertEquals(team3, fixture2.getHomeTeam());
	assertEquals(team4, fixture2.getAwayTeam());
	assertEquals(FIXTURE_2_HOME_GOALS, fixture2.getHomeGoals());
	assertEquals(FIXTURE_2_AWAY_GOALS, fixture2.getAwayGoals());
	
	assertEquals (fixture3, fixtures.get(2));
	assertEquals(FIXTURE_DATE_1, convertCalendarToString(fixture3.getFixtureDate()));
	assertEquals(FIXTURE_3_ID, fixture3.getFixtureId());
	assertEquals(season, fixture3.getSeason());
	assertEquals(division2, fixture3.getDivision());
	assertEquals(team5, fixture3.getHomeTeam());
	assertEquals(team6, fixture3.getAwayTeam());
	assertEquals(FIXTURE_3_HOME_GOALS, fixture3.getHomeGoals());
	assertEquals(FIXTURE_3_AWAY_GOALS, fixture3.getAwayGoals());
	
	assertEquals (fixture4, fixtures.get(3));
	assertEquals(FIXTURE_DATE_1, convertCalendarToString(fixture4.getFixtureDate()));
	assertEquals(FIXTURE_4_ID, fixture4.getFixtureId());
	assertEquals(season, fixture4.getSeason());
	assertEquals(division2, fixture4.getDivision());
	assertEquals(team7, fixture4.getHomeTeam());
	assertEquals(team8, fixture4.getAwayTeam());
	assertEquals(FIXTURE_4_HOME_GOALS, fixture4.getHomeGoals());
	assertEquals(FIXTURE_4_AWAY_GOALS, fixture4.getAwayGoals());

	assertEquals (fixture5, fixtures.get(4));
	assertEquals(FIXTURE_DATE_2, convertCalendarToString(fixture5.getFixtureDate()));
	assertEquals(FIXTURE_5_ID, fixture5.getFixtureId());
	assertEquals(season, fixture5.getSeason());
	assertEquals(division1, fixture5.getDivision());
	assertEquals(team2, fixture5.getHomeTeam());
	assertEquals(team1, fixture5.getAwayTeam());
	assertEquals(FIXTURE_5_HOME_GOALS, fixture5.getHomeGoals());
	assertEquals(FIXTURE_5_AWAY_GOALS, fixture5.getAwayGoals());

	assertEquals (fixture6, fixtures.get(5));
	assertEquals(FIXTURE_DATE_3, convertCalendarToString(fixture6.getFixtureDate()));
	assertEquals(FIXTURE_6_ID, fixture6.getFixtureId());
	assertEquals(season, fixture6.getSeason());
	assertEquals(division1, fixture6.getDivision());
	assertEquals(team4, fixture6.getHomeTeam());
	assertEquals(team3, fixture6.getAwayTeam());
	assertEquals(FIXTURE_6_HOME_GOALS, fixture6.getHomeGoals());
	assertEquals(FIXTURE_6_AWAY_GOALS, fixture6.getAwayGoals());

	assertEquals (fixture7, fixtures.get(6));
	assertEquals(FIXTURE_DATE_3, convertCalendarToString(fixture7.getFixtureDate()));
	assertEquals(FIXTURE_7_ID, fixture7.getFixtureId());
	assertEquals(season, fixture7.getSeason());
	assertEquals(division2, fixture7.getDivision());
	assertEquals(team6, fixture7.getHomeTeam());
	assertEquals(team5, fixture7.getAwayTeam());
	assertEquals(FIXTURE_7_HOME_GOALS, fixture7.getHomeGoals());
	assertEquals(FIXTURE_7_AWAY_GOALS, fixture7.getAwayGoals());
	
	assertEquals (fixture8, fixtures.get(7));
	assertEquals(FIXTURE_DATE_3, convertCalendarToString(fixture8.getFixtureDate()));
	assertEquals(FIXTURE_8_ID, fixture8.getFixtureId());
	assertEquals(season, fixture8.getSeason());
	assertEquals(division2, fixture8.getDivision());
	assertEquals(team8, fixture8.getHomeTeam());
	assertEquals(team7, fixture8.getAwayTeam());
	assertNull(fixture8.getHomeGoals());
	assertNull(fixture8.getAwayGoals());
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
	Element root = createFixtureDateElement(doc, FIXTURE_DATE_1);

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
	Element root = createFixtureDateElement(doc, FIXTURE_DATE_2);

	Element div1 = createCompetitionElement(doc, root, COMPETITION_1_ID, COMPETITION_1_NAME, SEASON_ID);
	Element game1 = createGameElement(doc, div1, FIXTURE_5_ID, TEAM_2_ID, TEAM_2_NAME, TEAM_1_ID, TEAM_1_NAME);
	createScoreElement(doc, game1, FIXTURE_5_HOME_GOALS, FIXTURE_5_AWAY_GOALS);

	return doc;
    }

    private Document getDocument3() throws ParserConfigurationException {
	Document doc = createNewDocument();
	Element root = createFixtureDateElement(doc, FIXTURE_DATE_3);

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

    private Element createFixtureDateElement(Document doc, String date) {
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

    private String convertCalendarToString(Calendar calendar) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	return sdf.format(calendar.getTime());
    }
}
