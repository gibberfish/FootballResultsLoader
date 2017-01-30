package mindbadger.footballresults.loader.apps;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
			
			writer.write("{");
			writer.newLine();
			writer.write(" divisions: [");
			writer.newLine();
			
			Map<String, Division> divisions = dao.getAllDivisions();
			List<Division> divisionList = new ArrayList<Division>(divisions.values());
			Division lastDivision = divisionList.get(divisionList.size()-1);
			for (Division division : divisionList) {
				writer.write("{ type: 'division', ");
				
				writer.write("  id: '" + division.getDivisionId() + "', ");
				writer.write("  name: '" + division.getDivisionName() + "'");
				
				writer.write("}");

				if (division != lastDivision) {
					writer.write(",");
				}

				writer.newLine();
				writer.flush();
			}

			writer.write("],");
			writer.newLine();
			writer.write(" teams: [");
			writer.newLine();

			Map<String, Team> teams = dao.getAllTeams();
			List<Team> teamList = new ArrayList<Team>(teams.values());
			Team lastTeam = teamList.get(teamList.size()-1);
			for (Team team : teamList) {
				writer.write("{ type: 'team', ");
				
				writer.write("  id: '" + team.getTeamId() + "', ");
				writer.write("  name: '" + team.getTeamName() + "'");
				
				writer.write("}");

				if (team != lastTeam) {
					writer.write(",");
				}

				writer.newLine();
				writer.flush();
			}

			writer.write("],");
			writer.newLine();
			writer.write(" seasons: [");
			writer.newLine();

			List<Season> seasons = dao.getSeasons();
			Season lastSeason = seasons.get(seasons.size()-1);
			
			for (Season season : seasons) {
				writer.write("{ type: 'season', ");
				
				writer.write("  seasonNumber: '" + season.getSeasonNumber() + "',");

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

				if (season != lastSeason) {
					writer.write(",");
				}

				writer.newLine();

				
				writer.flush();
			}

			writer.write("],");
			writer.newLine();
			writer.write(" fixtures: [");
			writer.newLine();

			List<Fixture> fixtures = dao.getFixtures();
			Fixture lastFixture = fixtures.get(fixtures.size()-1);
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
				
				if (fixture != lastFixture) {
					writer.write(",");
				}
				
				writer.newLine();
				writer.flush();
			}
			
			writer.write("]");
			writer.newLine();
			writer.write("}");
			writer.newLine();
		}
		
		dao.closeSession();
		((ConfigurableApplicationContext) context).close();
	}
}
