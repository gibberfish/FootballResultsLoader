package mindbadger.footballresults.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.football.domain.Division;
import mindbadger.football.domain.DivisionImpl;
import mindbadger.football.domain.DomainObjectFactory;
import mindbadger.football.domain.Fixture;
import mindbadger.football.domain.FixtureImpl;
import mindbadger.football.domain.Season;
import mindbadger.football.domain.SeasonDivision;
import mindbadger.football.domain.SeasonDivisionTeam;
import mindbadger.football.domain.SeasonImpl;
import mindbadger.football.domain.Team;
import mindbadger.football.domain.TeamImpl;
import mindbadger.football.repository.DivisionRepository;
import mindbadger.football.repository.FixtureRepository;
import mindbadger.football.repository.SeasonRepository;
import mindbadger.football.repository.TeamRepository;

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
	DomainObjectFactory domainObjectFactory;
	
	public static final String FILE_NAME = "E:\\_temp\\export_from_couchbase_no_zero_ids.json";
	
	public void readFile () throws IOException, ParseException {
		String content = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
		
		JSONObject obj = new JSONObject(content);
		
		JSONArray divisions = obj.getJSONArray("divisions");
		for (int i=0; i<divisions.length(); i++) {
			JSONObject divisionObj = divisions.getJSONObject(i);
			String name = divisionObj.getString("name");
			String id = divisionObj.getString("id");
			
			Division division = domainObjectFactory.createDivision(name);
			division.setDivisionId(id);
			
			DivisionImpl divisionImpl = (DivisionImpl) division;
			
			divisionRepository.save(divisionImpl);
		}
		
		JSONArray teams = obj.getJSONArray("teams");
		for (int i=0; i<teams.length(); i++) {
			JSONObject teamObj = teams.getJSONObject(i);
			String name = teamObj.getString("name");
			String id = teamObj.getString("id");
			
			Team team = domainObjectFactory.createTeam(name);
			team.setTeamId(id);
			
			TeamImpl teamImpl = (TeamImpl) team;
			
			teamRepository.save(teamImpl);
		}
		
		JSONArray seasons = obj.getJSONArray("seasons");
		for (int i=0; i<seasons.length(); i++) {
			JSONObject seasonObj = seasons.getJSONObject(i);
			Integer ssnNum = seasonObj.getInt("seasonNumber");
			Season season = domainObjectFactory.createSeason(ssnNum);
			
			JSONArray seasonDivisions = seasonObj.getJSONArray("divisionsInSeason");
			
			for (int j=0; j<seasonDivisions.length(); j++) {
				JSONObject seasonDivisionObj = seasonDivisions.getJSONObject(j);
				
				String divisionId = seasonDivisionObj.getString("division");
				Integer position = seasonDivisionObj.getInt("position");
				Division division = divisionRepository.findOne(divisionId);
				
				SeasonDivision seasonDivision = domainObjectFactory.createSeasonDivision(season, division, position);
				season.getSeasonDivisions().add(seasonDivision);
				
				JSONArray seasonDivisionTeams = seasonDivisionObj.getJSONArray("teams");
				
				for (int k=0; k<seasonDivisionTeams.length(); k++) {
					Integer teamId = seasonDivisionTeams.getInt(k);
					String teamIdString = teamId.toString();
					
					Team team = teamRepository.findOne(teamIdString);
					
					SeasonDivisionTeam seasonDivisionTeam = domainObjectFactory.createSeasonDivisionTeam(seasonDivision, team);
					seasonDivision.getSeasonDivisionTeams().add(seasonDivisionTeam);
				}
			}
			
			seasonRepository.save((SeasonImpl)season);
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		JSONArray fixtures = obj.getJSONArray("fixtures");
		for (int l=0; l<fixtures.length(); l++) {
			JSONObject fixtureObj = fixtures.getJSONObject(l);
			
			String seasonNumber = fixtureObj.getString("season");
			String fixtureDateString = fixtureObj.getString("fixtureDate");
			String divisionId = fixtureObj.getString("division");
			String homeTeamId = fixtureObj.getString("homeTeam");
			String awayTeamId = fixtureObj.getString("awayTeam");

			String homeGoals = null;
			if (fixtureObj.has("homeGoals")) {
				homeGoals = fixtureObj.getString("homeGoals");	
			}
			
			String awayGoals = null;
			if (fixtureObj.has("awayGoals")) {
				awayGoals = fixtureObj.getString("awayGoals");	
			}

			Date date = dateFormat.parse(fixtureDateString);
			Calendar fixtureDateCalendar = Calendar.getInstance();
			fixtureDateCalendar.setTime(date);
			
			Division division = divisionRepository.findOne(divisionId);
			Team homeTeam = teamRepository.findOne(homeTeamId);
			Team awayTeam = teamRepository.findOne(awayTeamId);
			Season season = seasonRepository.findOne(Integer.parseInt(seasonNumber));
			
			Fixture fixture = domainObjectFactory.createFixture(season, homeTeam, awayTeam);
			fixture.setDivision(division);
			fixture.setFixtureDate(fixtureDateCalendar);
			if (homeGoals != null) {
				fixture.setHomeGoals(Integer.parseInt(homeGoals));
			}
			if (awayGoals != null) {
				fixture.setAwayGoals(Integer.parseInt(awayGoals));
			}
			
			fixtureRepository.save((FixtureImpl) fixture);
		}
	}

	@Override
	public void run(String[] args) throws Exception {
		readFile();		
	}
}
