package mindbadger.footballresults.loader.apps;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.Division;
import mindbadger.footballresultsanalyser.domain.Fixture;
import mindbadger.footballresultsanalyser.domain.Season;
import mindbadger.footballresultsanalyser.domain.SeasonDivision;
import mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import mindbadger.footballresultsanalyser.domain.Team;

public class ExportDataFromCouchbaseApplication {

	public static final String FILE_NAME = "E:\\_temp\\export_from_couchbase.json";
	
	/*
	 * Exports data into a simple JSON format...
	 */
	public static void main(String[] args) throws IOException {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-web-reader.xml");

		FootballResultsAnalyserDAO dao = (FootballResultsAnalyserDAO) context.getBean("dao");
		dao.startSession();
		
		Path path = Paths.get(FILE_NAME);

		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			Map<String, Division> divisions = dao.getAllDivisions();
			for (Division division : divisions.values()) {
				writer.write("{ type: 'division', ");
				
				writer.write("  id: '" + division.getDivisionId() + "', ");
				writer.write("  name: '" + division.getDivisionName() + "'");
				
				writer.write("}");
				
				writer.newLine();
				writer.flush();
			}
			
			Map<String, Team> teams = dao.getAllTeams();
			for (Team team : teams.values()) {
				writer.write("{ type: 'team', ");
				
				writer.write("  id: '" + team.getTeamId() + "', ");
				writer.write("  name: '" + team.getTeamName() + "'");
				
				writer.write("}");
				
				writer.newLine();
				writer.flush();
			}
			
			List<Season> seasons = dao.getSeasons();
			
			for (Season season : seasons) {
				writer.write("{ type: 'season', ");
				
				writer.write("  seasonNumber: '" + season.getSeasonNumber() + "'");

				writer.write("  divisionsInSeason: [");
				writer.newLine();
				
				List<SeasonDivision> seasonDivisions = dao.getDivisionsForSeason(season);
				SeasonDivision lastSeasonDivision = seasonDivisions.get(seasonDivisions.size()-1);
				for (SeasonDivision seasonDivision : seasonDivisions) {
					writer.write("{ division: '" + seasonDivision.getDivision().getDivisionId() + "', ");
					writer.write("position: '" + seasonDivision.getDivisionPosition() + "', ");
					writer.write("teams: [");
					
					List<SeasonDivisionTeam> seasonDivisionTeams = dao.getTeamsForDivisionInSeason(seasonDivision);
					SeasonDivisionTeam lastSeasonDivisionTeam = seasonDivisionTeams.get(seasonDivisionTeams.size()-1);
					for (SeasonDivisionTeam seasonDivisionTeam : seasonDivisionTeams) {
						writer.write(seasonDivisionTeam.getTeam().getTeamId());
						if (seasonDivisionTeam != lastSeasonDivisionTeam) {
							writer.write(", ");
						}
					}
					
					writer.write("]}");
					if (seasonDivision != lastSeasonDivision) {
						writer.write(",");
					}
					
					writer.newLine();
				}

				writer.write("  ]");
				writer.newLine();

				writer.write("}");
				
				writer.newLine();

				
				writer.flush();
			}
			

			List<Fixture> fixtures = dao.getFixtures();
			for (Fixture fixture : fixtures) {
				writer.write("{ type: 'fixture', ");
				writer.write("  season: '" + fixture.getSeason().getSeasonNumber() + "', ");
				
				if (fixture.getFixtureDate() != null) {
					writer.write("  fixtureDate: '" + format1.format(fixture.getFixtureDate().getTime()) + "', ");
				}
				
				if (fixture.getHomeGoals() != null) {
					writer.write("  homeGoals: '" + fixture.getHomeGoals() + "', ");
				}

				if (fixture.getAwayGoals() != null) {
					writer.write("  awayGoals: '" + fixture.getAwayGoals() + "', ");
				}

				writer.write("  division: '" + fixture.getDivision().getDivisionId() + "', ");
				writer.write("  homeTeam: '" + fixture.getHomeTeam().getTeamId() + "', ");
				writer.write("  awayTeam: '" + fixture.getAwayTeam().getTeamId() + "'");
				
				writer.write("}");
				
				writer.newLine();
				writer.flush();
			}
		}
		
		dao.closeSession();
		((ConfigurableApplicationContext) context).close();
	}
}
