package mindbadger.footballresults.commands;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.football.domain.Division;
import mindbadger.football.domain.Fixture;
import mindbadger.football.domain.Season;
import mindbadger.football.domain.SeasonDivision;
import mindbadger.football.domain.SeasonDivisionTeam;
import mindbadger.football.domain.Team;
import mindbadger.football.repository.DivisionRepository;
import mindbadger.football.repository.FixtureRepository;
import mindbadger.football.repository.SeasonRepository;
import mindbadger.football.repository.TeamRepository;

@Component
public class ExportDataFromCouchbaseApplication implements Command {

	public static final String FILE_NAME = "E:\\_temp\\export_from_couchbase.json";

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private FixtureRepository fixtureRepository;

	/*
	 * Exports data into a simple JSON format...
	 */
	@Override
	public void run(String[] args) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Path path = Paths.get(FILE_NAME);

		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			
			writer.write("{");
			writer.newLine();
			writer.write(" divisions: [");
			writer.newLine();
			
			Iterable<Division> divisions = divisionRepository.findAll();
			Iterator<Division> divisionIterator = divisions.iterator();
			while (divisionIterator.hasNext()) {
				Division division = divisionIterator.next();
				
				writer.write("{ type: 'division', ");
				
				writer.write("  id: '" + division.getDivisionId() + "', ");
				writer.write("  name: '" + division.getDivisionName() + "'");
				
				writer.write("}");

				if (divisionIterator.hasNext()) {
					writer.write(",");
				}

				writer.newLine();
				writer.flush();			
			}

			writer.write("],");
			writer.newLine();
			writer.write(" teams: [");
			writer.newLine();

			Iterable<Team> teams = teamRepository.findAll();
			Iterator<Team> teamIterator = teams.iterator();
			while (teamIterator.hasNext()) {
				Team team = teamIterator.next();
				
				writer.write("{ type: 'team', ");
				
				writer.write("  id: '" + team.getTeamId() + "', ");
				writer.write("  name: '" + team.getTeamName() + "'");
				
				writer.write("}");

				if (teamIterator.hasNext()) {
					writer.write(",");
				}

				writer.newLine();
				writer.flush();			
			}

			writer.write("],");
			writer.newLine();
			writer.write(" seasons: [");
			writer.newLine();

			Iterable<Season> seasons = seasonRepository.findAll();
			Iterator<Season> seasonIterator = seasons.iterator();
			while (seasonIterator.hasNext()) {
				Season season = seasonIterator.next();
				
				writer.write("{ type: 'season', ");
				
				writer.write("  seasonNumber: '" + season.getSeasonNumber() + "',");

				writer.write("  divisionsInSeason: [");
				writer.newLine();
				
				Set<SeasonDivision> seasonDivisions = season.getSeasonDivisions();
				Iterator<SeasonDivision> seasonDivisionIterator = seasonDivisions.iterator();
				while (seasonDivisionIterator.hasNext()) {
					SeasonDivision seasonDivision = seasonDivisionIterator.next();
					
					writer.write("{ division: '" + seasonDivision.getDivision().getDivisionId() + "', ");
					writer.write("position: '" + seasonDivision.getDivisionPosition() + "', ");
					writer.write("teams: [");
					
					Set<SeasonDivisionTeam> seasonDivisionTeams = seasonDivision.getSeasonDivisionTeams();
					Iterator<SeasonDivisionTeam> seasonDivisionTeamIterator = seasonDivisionTeams.iterator();
					while (seasonDivisionTeamIterator.hasNext()) {
						SeasonDivisionTeam seasonDivisionTeam = seasonDivisionTeamIterator.next();
						writer.write(seasonDivisionTeam.getTeam().getTeamId());
						if (seasonDivisionTeamIterator.hasNext()) {
							writer.write(", ");
						}						
					}
					
					writer.write("]}");
					if (seasonDivisionIterator.hasNext()) {
						writer.write(",");
					}
					
					writer.newLine();
				}
				
				writer.write("  ]");
				writer.newLine();

				writer.write("}");

				if (seasonIterator.hasNext()) {
					writer.write(",");
				}

				writer.newLine();

				
				writer.flush();
			}
			
			writer.write("],");
			writer.newLine();
			writer.write(" fixtures: [");
			writer.newLine();

			Iterable<Fixture> fixtures = fixtureRepository.findAll();
			Iterator<Fixture> fixtureIterator = fixtures.iterator();
			while (fixtureIterator.hasNext()) {
				Fixture fixture = fixtureIterator.next();
				
				writer.write("{ type: 'fixture', ");
				writer.write("  season: '" + fixture.getSeason().getSeasonNumber() + "', ");
				
				if (fixture.getFixtureDate() != null) {
					writer.write("  fixtureDate: '" + dateFormat.format(fixture.getFixtureDate().getTime()) + "', ");
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
				
				if (fixtureIterator.hasNext()) {
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
	}
}
