package mindbadger.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Component
public class XMLFileReader {
	Logger logger = Logger.getLogger(XMLFileReader.class);
	
	private String rootDirectory;
	
	public Document readXMLFile (String fullyQualifiedFileName) throws ParserConfigurationException, SAXException, IOException{
		File fXmlFile = new File(fullyQualifiedFileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document document = dBuilder.parse(fXmlFile);
		document.getDocumentElement().normalize();
		return document;
	}

	public List<String> getFilesInDirectory(String directory) {
		logger.debug("getFilesInDirectory: " + directory);
		File root = new File (directory);
		File[] list = root.listFiles();

		List<String> fullyQualifiedFileNames = new ArrayList<String> ();
      for ( File file : list ) {
      	fullyQualifiedFileNames.add(file.getAbsolutePath());
      }
		
		return fullyQualifiedFileNames;
	}

	public String getRootDirectory() {
		return rootDirectory;
	}

	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
//	public static void main(String[] args) throws Exception {
//		XMLFileReader reader = new XMLFileReader ();
//		reader.setRootDirectory("C:/checkout/FootballResultsLoader/testdata/");
//		
//		List<String> files = reader.getFilesForSeason(2000);
//		
//		System.out.println("Files: " + files.size());
//		System.out.println("First: " + files.get(0));
//		
//		Document doc = reader.readXMLFile("C:/checkout/FootballResultsLoader/testdata/2000/soccerbase_2000-11-18.xml");
//		
//		Element rootElement = doc.getDocumentElement();
//		String fixtureDate = rootElement.getAttribute("date");
//		System.out.println("Date = " + fixtureDate);
//		
//		NodeList competitions = rootElement.getElementsByTagName("Competition");
//		
//		for (int i=0; i<competitions.getLength();i++) {
//			Element competition = (Element) competitions.item(i);
//			System.out.println("competitionId: " + competition.getAttribute("competitionId"));
//			System.out.println("competitionName: " + competition.getAttribute("competitionName"));
//			System.out.println("seasonId: " + competition.getAttribute("seasonId"));
//			
//			NodeList games = competition.getElementsByTagName("Game");
//			
//			for (int j=0; j<games.getLength();j++) {
//				Element game = (Element) games.item(j);
//				System.out.println("gameId" + game.getAttribute("gameId"));
//				System.out.println("homeTeamId" + game.getAttribute("homeTeamId"));
//				System.out.println("homeTeamName" + game.getAttribute("homeTeamName"));
//				System.out.println("awayTeamId" + game.getAttribute("awayTeamId"));
//				System.out.println("awayTeamName" + game.getAttribute("awayTeamName"));
//				
//				NodeList scores = game.getElementsByTagName("Score");
//				Element score = (Element) scores.item(0);
//				if (score!=null){
//					System.out.println("Score: " + score.getAttribute("homeGoals") + "-" + score.getAttribute("awayGoals"));
//				} else {
//					System.out.println("!!!! NO SCORE !!!!");
//				}
//			}
//		}
//	}
}
