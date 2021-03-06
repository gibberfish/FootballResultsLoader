package mindbadger.footballresults.reader.xml;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mindbadger.footballresults.reader.FootballResultsReader;
import mindbadger.footballresults.reader.FootballResultsReaderException;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.util.StringToCalendarConverter;
import mindbadger.xml.XMLFileReader;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SoccerbaseXMLReader implements FootballResultsReader {
	Logger logger = Logger.getLogger(SoccerbaseXMLReader.class);
	
	private XMLFileReader xmlFileReader;
	private String rootDirectory;

	@Override
	public List<ParsedFixture> readFixturesForSeason(int seasonNumber) {
		List<ParsedFixture> fixtures = new ArrayList<ParsedFixture>();

		List<String> fullyQualifiedSeasonFileNames = xmlFileReader.getFilesInDirectory(rootDirectory + "\\" + seasonNumber);

		for (String fileName : fullyQualifiedSeasonFileNames) {
			try {
				logger.debug("READING FILE: " + fileName);
				Document doc = xmlFileReader.readXMLFile(fileName);

				Element rootElement = doc.getDocumentElement();

				Calendar fixtureDate = StringToCalendarConverter.convertDateStringToCalendar(rootElement.getAttribute("date"));

				NodeList competitions = rootElement.getElementsByTagName("Competition");

				for (int i = 0; i < competitions.getLength(); i++) {
					Element competition = (Element) competitions.item(i);

					Integer seasonId = Integer.parseInt(competition.getAttribute("seasonId")) + 1870;
					if (seasonId != seasonNumber) {
						throw new FootballResultsReaderException("Your xml file contains a season that is not in the correct folder");
					}

					String competitionId = competition.getAttribute("competitionId");
					String competitionName = competition.getAttribute("competitionName");

					logger.debug("  COMPETITION: " + competitionId + ", " + competitionName);

					NodeList games = competition.getElementsByTagName("Game");
					for (int j = 0; j < games.getLength(); j++) {
						Element game = (Element) games.item(j);

						String fixtureIdString = game.getAttribute("gameId");
						String fixtureId = null;
						if (fixtureIdString != null && !"".equals(fixtureIdString)) {
							fixtureId = game.getAttribute("gameId");
						}
						String homeTeamId = game.getAttribute("homeTeamId");
						String awayTeamId = game.getAttribute("awayTeamId");
						String homeTeamName = game.getAttribute("homeTeamName");
						String awayTeamName = game.getAttribute("awayTeamName");

						logger.debug("    GAME: " + homeTeamName + " vs " + awayTeamName + " on " + fixtureDate);
						
						ParsedFixture fixture = new ParsedFixture();
						fixture.setFixtureId(fixtureId);
						fixture.setSeasonId(seasonId);
						fixture.setDivisionId(competitionId);
						fixture.setDivisionName(competitionName);
						fixture.setFixtureDate(fixtureDate);
						fixture.setHomeTeamId(homeTeamId);
						fixture.setHomeTeamName(homeTeamName);
						fixture.setAwayTeamId(awayTeamId);
						fixture.setAwayTeamName(awayTeamName);

						fixtures.add(fixture);

						NodeList scores = game.getElementsByTagName("Score");
						Element score = (Element) scores.item(0);
						if (score != null) {
							fixture.setHomeGoals(Integer.parseInt(score.getAttribute("homeGoals")));
							fixture.setAwayGoals(Integer.parseInt(score.getAttribute("awayGoals")));
						}
					}
				}
			} catch (Exception e) {
				logger.error("IGNORING FILE: " + e.getMessage());
				//e.printStackTrace();
				//throw new FootballResultsReaderException(e);
			}
		}

		logger.debug("WE HAVE PARSED " + fixtures.size()  + " FIXTURES");
		
		return fixtures;
	}

	public XMLFileReader getXmlFileReader() {
		return xmlFileReader;
	}

	public void setXmlFileReader(XMLFileReader xmlFileReader) {
		this.xmlFileReader = xmlFileReader;
	}

	public String getRootDirectory() {
		return rootDirectory;
	}

	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	@Override
	public List<ParsedFixture> readFixturesForDate(Calendar date) {
		throw new FootballResultsReaderException ("This method is not applicable for the XML reader");
	}

	@Override
	public List<ParsedFixture> readFixturesForTeamInSeason(int season, String teamId) {
		throw new FootballResultsReaderException ("This method is not applicable for the XML reader");
	}

}
