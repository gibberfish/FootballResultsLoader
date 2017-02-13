package mindbadger.footballresults.loader.apps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import mindbadger.football.dao.FootballResultsAnalyserJPADAO;
import mindbadger.football.repository.DivisionRepository;
import mindbadger.football.repository.FixtureRepository;
import mindbadger.football.repository.SeasonRepository;
import mindbadger.football.repository.TeamRepository;
import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.Division;
import mindbadger.footballresultsanalyser.domain.DivisionImpl;
import mindbadger.footballresultsanalyser.domain.DomainObjectFactory;

@Component
public class ImportDataFromJSONToDB implements Command {

	@Autowired
	DivisionRepository divisionRepository;
	
	@Autowired
	SeasonRepository seasonRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Autowired
	FixtureRepository fixtureRepository;
	
	@Autowired
	FootballResultsAnalyserDAO dao;

	@Autowired
	DomainObjectFactory domainObjectFactory;
	
	public static final String FILE_NAME = "E:\\_temp\\export_from_couchbase.json";
	
	public void readFile () throws IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-web-reader.xml");
		
		String content = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
		
		JSONObject obj = new JSONObject(content);
		
		JSONArray divisions = obj.getJSONArray("divisions");
		for (int i=0; i<divisions.length(); i++) {
			JSONObject divisionObj = divisions.getJSONObject(i);
			String name = divisionObj.getString("name");
			String id = divisionObj.getString("id");
			
			System.out.println("YYY: " + domainObjectFactory);
			System.out.println("XXX: " + divisionRepository);
			
			Division division = domainObjectFactory.createDivision(name);
			division.setDivisionId(id);
			
			DivisionImpl divisionImpl = (DivisionImpl) division;
			
			divisionRepository.save(divisionImpl);
		}
	}

	@Override
	public void run(String[] args) throws IOException {
		ImportDataFromJSONToDB app = new ImportDataFromJSONToDB ();
		app.readFile();		
	}
}
