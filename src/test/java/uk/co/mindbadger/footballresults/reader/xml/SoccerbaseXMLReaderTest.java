package uk.co.mindbadger.footballresults.reader.xml;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.*;
import org.mockito.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.co.mindbadger.footballresults.reader.FootballResultsReaderException;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.footballresults.reader.xml.SoccerbaseXMLReader;
import uk.co.mindbadger.xml.XMLFileReader;

public class SoccerbaseXMLReaderTest {
	private static final Integer SEASON = 2000;
	private static final String SEASON_ID = "130";
	private static final String NOT_MATCHING_SEASON_ID = "131";

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

	private static final String NO_FIXTURE_DATE = "";
	
	private static final String ROOT_DIRECTORY = "C:\\results";
	private static final String FILE3 = "C:\\results\\2000\\FILE3.xml";
	private static final String FILE2 = "C:\\results\\2000\\FILE2.xml";
	private static final String FILE1 = "C:\\results\\2000\\FILE1.xml";

	private SoccerbaseXMLReader objectUnderTest;

	@Mock
	private XMLFileReader mockXmlFileReader;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		objectUnderTest = new SoccerbaseXMLReader();
		objectUnderTest.setXmlFileReader(mockXmlFileReader);
		objectUnderTest.setRootDirectory(ROOT_DIRECTORY);
	}

	@Test
	public void shouldReadFixturesForSeason() throws Exception {
		// Given
		List<String> listOfFiles = new ArrayList<String>();
		listOfFiles.add(FILE1);
		listOfFiles.add(FILE2);
		listOfFiles.add(FILE3);
		when(mockXmlFileReader.getFilesInDirectory(ROOT_DIRECTORY + "\\" + SEASON)).thenReturn(listOfFiles);

		when(mockXmlFileReader.readXMLFile(FILE1)).thenReturn(getDocumentForDate1());
		when(mockXmlFileReader.readXMLFile(FILE2)).thenReturn(getDocumentForDate2());
		when(mockXmlFileReader.readXMLFile(FILE3)).thenReturn(getDocumentForDate3());

		// When
		List<ParsedFixture> fixtures = objectUnderTest.readFixturesForSeason(SEASON);

		// Then
		verify(mockXmlFileReader).getFilesInDirectory(ROOT_DIRECTORY + "\\" + SEASON);
		verify(mockXmlFileReader).readXMLFile(FILE1);
		verify(mockXmlFileReader).readXMLFile(FILE2);
		verify(mockXmlFileReader).readXMLFile(FILE3);

		assertEquals(8, fixtures.size());

		ParsedFixture fixture1 = fixtures.get(0);
		assertEquals(new Integer(FIXTURE_1_ID), fixture1.getFixtureId());
		assertEquals(SEASON, fixture1.getSeasonId());
		assertEquals(new Integer(COMPETITION_1_ID), fixture1.getDivisionId());
		assertEquals(COMPETITION_1_NAME, fixture1.getDivisionName());
		assertEquals(FIXTURE_DATE_1, convertCalendarToString(fixture1.getFixtureDate()));
		assertEquals(new Integer(TEAM_1_ID), fixture1.getHomeTeamId());
		assertEquals(TEAM_1_NAME, fixture1.getHomeTeamName());
		assertEquals(new Integer(TEAM_2_ID), fixture1.getAwayTeamId());
		assertEquals(TEAM_2_NAME, fixture1.getAwayTeamName());
		assertEquals(new Integer(FIXTURE_1_HOME_GOALS), fixture1.getHomeGoals());
		assertEquals(new Integer(FIXTURE_1_AWAY_GOALS), fixture1.getAwayGoals());

		ParsedFixture fixture2 = fixtures.get(1);
		assertEquals(new Integer(FIXTURE_2_ID), fixture2.getFixtureId());
		assertEquals(SEASON, fixture2.getSeasonId());
		assertEquals(new Integer(COMPETITION_1_ID), fixture2.getDivisionId());
		assertEquals(COMPETITION_1_NAME, fixture2.getDivisionName());
		assertEquals(FIXTURE_DATE_1, convertCalendarToString(fixture2.getFixtureDate()));
		assertEquals(new Integer(TEAM_3_ID), fixture2.getHomeTeamId());
		assertEquals(TEAM_3_NAME, fixture2.getHomeTeamName());
		assertEquals(new Integer(TEAM_4_ID), fixture2.getAwayTeamId());
		assertEquals(TEAM_4_NAME, fixture2.getAwayTeamName());
		assertEquals(new Integer(FIXTURE_2_HOME_GOALS), fixture2.getHomeGoals());
		assertEquals(new Integer(FIXTURE_2_AWAY_GOALS), fixture2.getAwayGoals());

		ParsedFixture fixture3 = fixtures.get(2);
		assertEquals(new Integer(FIXTURE_3_ID), fixture3.getFixtureId());
		assertEquals(SEASON, fixture3.getSeasonId());
		assertEquals(new Integer(COMPETITION_2_ID), fixture3.getDivisionId());
		assertEquals(COMPETITION_2_NAME, fixture3.getDivisionName());
		assertEquals(FIXTURE_DATE_1, convertCalendarToString(fixture3.getFixtureDate()));
		assertEquals(new Integer(TEAM_5_ID), fixture3.getHomeTeamId());
		assertEquals(TEAM_5_NAME, fixture3.getHomeTeamName());
		assertEquals(new Integer(TEAM_6_ID), fixture3.getAwayTeamId());
		assertEquals(TEAM_6_NAME, fixture3.getAwayTeamName());
		assertEquals(new Integer(FIXTURE_3_HOME_GOALS), fixture3.getHomeGoals());
		assertEquals(new Integer(FIXTURE_3_AWAY_GOALS), fixture3.getAwayGoals());

		ParsedFixture fixture4 = fixtures.get(3);
		assertEquals(new Integer(FIXTURE_4_ID), fixture4.getFixtureId());
		assertEquals(SEASON, fixture4.getSeasonId());
		assertEquals(new Integer(COMPETITION_2_ID), fixture4.getDivisionId());
		assertEquals(COMPETITION_2_NAME, fixture4.getDivisionName());
		assertEquals(FIXTURE_DATE_1, convertCalendarToString(fixture4.getFixtureDate()));
		assertEquals(new Integer(TEAM_7_ID), fixture4.getHomeTeamId());
		assertEquals(TEAM_7_NAME, fixture4.getHomeTeamName());
		assertEquals(new Integer(TEAM_8_ID), fixture4.getAwayTeamId());
		assertEquals(TEAM_8_NAME, fixture4.getAwayTeamName());
		assertEquals(new Integer(FIXTURE_4_HOME_GOALS), fixture4.getHomeGoals());
		assertEquals(new Integer(FIXTURE_4_AWAY_GOALS), fixture4.getAwayGoals());

		ParsedFixture fixture5 = fixtures.get(4);
		assertEquals(new Integer(FIXTURE_5_ID), fixture5.getFixtureId());
		assertEquals(SEASON, fixture5.getSeasonId());
		assertEquals(new Integer(COMPETITION_1_ID), fixture5.getDivisionId());
		assertEquals(COMPETITION_1_NAME, fixture5.getDivisionName());
		assertEquals(FIXTURE_DATE_2, convertCalendarToString(fixture5.getFixtureDate()));
		assertEquals(new Integer(TEAM_2_ID), fixture5.getHomeTeamId());
		assertEquals(TEAM_2_NAME, fixture5.getHomeTeamName());
		assertEquals(new Integer(TEAM_1_ID), fixture5.getAwayTeamId());
		assertEquals(TEAM_1_NAME, fixture5.getAwayTeamName());
		assertEquals(new Integer(FIXTURE_5_HOME_GOALS), fixture5.getHomeGoals());
		assertEquals(new Integer(FIXTURE_5_AWAY_GOALS), fixture5.getAwayGoals());

		ParsedFixture fixture6 = fixtures.get(5);
		assertEquals(new Integer(FIXTURE_6_ID), fixture6.getFixtureId());
		assertEquals(SEASON, fixture6.getSeasonId());
		assertEquals(new Integer(COMPETITION_1_ID), fixture6.getDivisionId());
		assertEquals(COMPETITION_1_NAME, fixture6.getDivisionName());
		assertEquals(FIXTURE_DATE_3, convertCalendarToString(fixture6.getFixtureDate()));
		assertEquals(new Integer(TEAM_4_ID), fixture6.getHomeTeamId());
		assertEquals(TEAM_4_NAME, fixture6.getHomeTeamName());
		assertEquals(new Integer(TEAM_3_ID), fixture6.getAwayTeamId());
		assertEquals(TEAM_3_NAME, fixture6.getAwayTeamName());
		assertEquals(new Integer(FIXTURE_6_HOME_GOALS), fixture6.getHomeGoals());
		assertEquals(new Integer(FIXTURE_6_AWAY_GOALS), fixture6.getAwayGoals());

		ParsedFixture fixture7 = fixtures.get(6);
		assertEquals(new Integer(FIXTURE_7_ID), fixture7.getFixtureId());
		assertEquals(SEASON, fixture7.getSeasonId());
		assertEquals(new Integer(COMPETITION_2_ID), fixture7.getDivisionId());
		assertEquals(COMPETITION_2_NAME, fixture7.getDivisionName());
		assertEquals(FIXTURE_DATE_3, convertCalendarToString(fixture7.getFixtureDate()));
		assertEquals(new Integer(TEAM_6_ID), fixture7.getHomeTeamId());
		assertEquals(TEAM_6_NAME, fixture7.getHomeTeamName());
		assertEquals(new Integer(TEAM_5_ID), fixture7.getAwayTeamId());
		assertEquals(TEAM_5_NAME, fixture7.getAwayTeamName());
		assertEquals(new Integer(FIXTURE_7_HOME_GOALS), fixture7.getHomeGoals());
		assertEquals(new Integer(FIXTURE_7_AWAY_GOALS), fixture7.getAwayGoals());

		ParsedFixture fixture8 = fixtures.get(7);
		assertEquals(new Integer(FIXTURE_8_ID), fixture8.getFixtureId());
		assertEquals(SEASON, fixture8.getSeasonId());
		assertEquals(new Integer(COMPETITION_2_ID), fixture8.getDivisionId());
		assertEquals(COMPETITION_2_NAME, fixture8.getDivisionName());
		assertEquals(FIXTURE_DATE_3, convertCalendarToString(fixture8.getFixtureDate()));
		assertEquals(new Integer(TEAM_8_ID), fixture8.getHomeTeamId());
		assertEquals(TEAM_8_NAME, fixture8.getHomeTeamName());
		assertEquals(new Integer(TEAM_7_ID), fixture8.getAwayTeamId());
		assertEquals(TEAM_7_NAME, fixture8.getAwayTeamName());
		assertNull(fixture8.getHomeGoals());
		assertNull(fixture8.getAwayGoals());
	}

	@Test
	public void shouldThrowExceptionIfTheSeasonDoesntMatchTheFolder() throws Exception {
		// Given
		List<String> listOfFiles = new ArrayList<String>();
		listOfFiles.add(FILE1);
		when(mockXmlFileReader.getFilesInDirectory(ROOT_DIRECTORY + "\\" + SEASON)).thenReturn(listOfFiles);
		when(mockXmlFileReader.readXMLFile(FILE1)).thenReturn(getDocumentForWithNotMatchingFixtureDate());

		// When
		try {
			objectUnderTest.readFixturesForSeason(SEASON);
			fail("Should thrown an exception when the seasons don't match");
		} catch (FootballResultsReaderException e) {
			assertEquals("uk.co.mindbadger.footballresults.reader.FootballResultsReaderException: Your xml file contains a season that is not in the correct folder", e.getMessage());
		}
	}

	@Test
	public void shouldReadFixturesWithNoDateInFile() throws Exception {
		// Given
		List<String> listOfFiles = new ArrayList<String>();
		listOfFiles.add(FILE1);
		when(mockXmlFileReader.getFilesInDirectory(ROOT_DIRECTORY + "\\" + SEASON)).thenReturn(listOfFiles);

		when(mockXmlFileReader.readXMLFile(FILE1)).thenReturn(getDocumentWithNoFixtureDate());

		// When
		List<ParsedFixture> fixtures = objectUnderTest.readFixturesForSeason(SEASON);

		// Then
		verify(mockXmlFileReader).getFilesInDirectory(ROOT_DIRECTORY + "\\" + SEASON);
		verify(mockXmlFileReader).readXMLFile(FILE1);

		assertEquals(1, fixtures.size());

		ParsedFixture fixture1 = fixtures.get(0);
		assertEquals(new Integer(FIXTURE_5_ID), fixture1.getFixtureId());
		assertEquals(SEASON, fixture1.getSeasonId());
		assertEquals(new Integer(COMPETITION_1_ID), fixture1.getDivisionId());
		assertEquals(COMPETITION_1_NAME, fixture1.getDivisionName());
		assertNull(fixture1.getFixtureDate());
		assertEquals(new Integer(TEAM_2_ID), fixture1.getHomeTeamId());
		assertEquals(TEAM_2_NAME, fixture1.getHomeTeamName());
		assertEquals(new Integer(TEAM_1_ID), fixture1.getAwayTeamId());
		assertEquals(TEAM_1_NAME, fixture1.getAwayTeamName());
		assertEquals(new Integer(FIXTURE_5_HOME_GOALS), fixture1.getHomeGoals());
		assertEquals(new Integer(FIXTURE_5_AWAY_GOALS), fixture1.getAwayGoals());
	}

	@Test
	public void shouldThrowExceptionWhenReadFixturesForDate () {
		// Given
		Calendar date = Calendar.getInstance();
		
		// When
		try {
			objectUnderTest.readFixturesForDate(date);
			fail("Should throw a FootballResultsReaderException");
		} catch (FootballResultsReaderException e) {
			// Then
			assertEquals ("This method is not applicable for the XML reader", e.getMessage());
		}
	}
	
	private Document getDocumentForDate1() throws ParserConfigurationException {
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

	private Document getDocumentForDate2() throws ParserConfigurationException {
		Document doc = createNewDocument();
		Element root = createFixtureDateElement(doc, FIXTURE_DATE_2);

		Element div1 = createCompetitionElement(doc, root, COMPETITION_1_ID, COMPETITION_1_NAME, SEASON_ID);
		Element game1 = createGameElement(doc, div1, FIXTURE_5_ID, TEAM_2_ID, TEAM_2_NAME, TEAM_1_ID, TEAM_1_NAME);
		createScoreElement(doc, game1, FIXTURE_5_HOME_GOALS, FIXTURE_5_AWAY_GOALS);

		return doc;
	}

	private Document getDocumentForDate3() throws ParserConfigurationException {
		Document doc = createNewDocument();
		Element root = createFixtureDateElement(doc, FIXTURE_DATE_3);

		Element div1 = createCompetitionElement(doc, root, COMPETITION_1_ID, COMPETITION_1_NAME, SEASON_ID);
		Element game2 = createGameElement(doc, div1, FIXTURE_6_ID, TEAM_4_ID, TEAM_4_NAME, TEAM_3_ID, TEAM_3_NAME);
		createScoreElement(doc, game2, FIXTURE_6_HOME_GOALS, FIXTURE_6_AWAY_GOALS);

		Element div2 = createCompetitionElement(doc, root, COMPETITION_2_ID, COMPETITION_2_NAME, SEASON_ID);
		Element game3 = createGameElement(doc, div2, FIXTURE_7_ID, TEAM_6_ID, TEAM_6_NAME, TEAM_5_ID, TEAM_5_NAME);
		createScoreElement(doc, game3, FIXTURE_7_HOME_GOALS, FIXTURE_7_AWAY_GOALS);
		createGameElement(doc, div2, FIXTURE_8_ID, TEAM_8_ID, TEAM_8_NAME, TEAM_7_ID, TEAM_7_NAME);

		return doc;
	}

	private Document getDocumentForWithNotMatchingFixtureDate() throws ParserConfigurationException {
		Document doc = createNewDocument();
		Element root = createFixtureDateElement(doc, FIXTURE_DATE_1);
		createCompetitionElement(doc, root, COMPETITION_1_ID, COMPETITION_1_NAME, NOT_MATCHING_SEASON_ID);

		return doc;
	}

	private Document getDocumentWithNoFixtureDate() throws ParserConfigurationException {
		Document doc = createNewDocument();
		Element root = createFixtureDateElement(doc, NO_FIXTURE_DATE);

		Element div1 = createCompetitionElement(doc, root, COMPETITION_1_ID, COMPETITION_1_NAME, SEASON_ID);
		Element game1 = createGameElement(doc, div1, FIXTURE_5_ID, TEAM_2_ID, TEAM_2_NAME, TEAM_1_ID, TEAM_1_NAME);
		createScoreElement(doc, game1, FIXTURE_5_HOME_GOALS, FIXTURE_5_AWAY_GOALS);

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
