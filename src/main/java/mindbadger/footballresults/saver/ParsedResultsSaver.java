package mindbadger.footballresults.saver;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mindbadger.footballresults.loader.AbstractFootballResultsLoaderMapping;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.football.domain.Division;
import mindbadger.football.domain.Season;
import mindbadger.football.domain.Team;

@Component
public class ParsedResultsSaver {
	Logger logger = Logger.getLogger(ParsedResultsSaver.class);
	
	@Autowired
	@Value("${dialect}")
	private String dialect;

	@Autowired
	private FootballResultSaver footballResultSaver;
	@Autowired
	private AbstractFootballResultsLoaderMapping mapping;

	public void saveFixtures(List<ParsedFixture> fixturesRead) {
		logger.debug("About to saveFixtures: " + fixturesRead.size());
		if (fixturesRead.size() > 0) {
			logger.debug("Getting mappings");
			List<String> includedDivisions = mapping.getIncludedDivisions(dialect);
			Map<String, String> divisionMappings = mapping.getDivisionMappings(dialect);
			Map<String, String> teamMappings = mapping.getTeamMappings(dialect);
			List<String> orderedListOfDivisions = mapping.getOrderedListOfDivisions(dialect);
			
			for (ParsedFixture parsedFixture : fixturesRead) {
				logger.debug("Saving fixture: " + parsedFixture);
				
				Integer seasonNumberForFixture = parsedFixture.getSeasonId();
				String readDivisionId = parsedFixture.getDivisionId();
				String readDivisionName = parsedFixture.getDivisionName();
				String readHomeTeamId = parsedFixture.getHomeTeamId();
				String readHomeTeamName = parsedFixture.getHomeTeamName();
				String readAwayTeamId = parsedFixture.getAwayTeamId();
				String readAwayTeamName = parsedFixture.getAwayTeamName();
				Calendar fixtureDate = parsedFixture.getFixtureDate();
				Integer homeGoals = parsedFixture.getHomeGoals();
				Integer awayGoals = parsedFixture.getAwayGoals();

				if (includedDivisions.contains(readDivisionId)) {
					logger.debug("This is a division we want to track: " + readDivisionId);

					Season season = footballResultSaver.createSeasonIfNotExisting (seasonNumberForFixture);
			
					String fraDivisionId = divisionMappings.get(readDivisionId);
					Division division = footballResultSaver.createDivisionIfNotExisting(fraDivisionId, readDivisionName);
					divisionMappings.put(readDivisionId, division.getDivisionId());
					
					String fraHomeTeamId = teamMappings.get(readHomeTeamId);
					Team homeTeam = footballResultSaver.createTeamIfNotExisting(fraHomeTeamId, readHomeTeamName);
					teamMappings.put(readHomeTeamId, homeTeam.getTeamId());
					
					String fraAwayTeamId = teamMappings.get(readAwayTeamId);
					Team awayTeam = footballResultSaver.createTeamIfNotExisting(fraAwayTeamId, readAwayTeamName);
					teamMappings.put(readAwayTeamId, awayTeam.getTeamId());
					
					int indexOfDivision = orderedListOfDivisions.indexOf(division.getDivisionId());
					
					footballResultSaver.createSeasonDivisionTeamsIfNotExisting(season, division, indexOfDivision+1, homeTeam, awayTeam);
					
					footballResultSaver.createFixture(season, division, homeTeam, awayTeam, fixtureDate, homeGoals, awayGoals);
				}
			}

			logger.debug("Saving mappings");
			mapping.saveMappings();
		}
	}	
		
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	public AbstractFootballResultsLoaderMapping getMapping() {
		return mapping;
	}
	public void setMapping(AbstractFootballResultsLoaderMapping mapping) {
		this.mapping = mapping;
	}
	public FootballResultSaver getFootballResultSaver() {
		return footballResultSaver;
	}
	public void setFootballResultSaver(FootballResultSaver footballResultSaver) {
		this.footballResultSaver = footballResultSaver;
	}
}
