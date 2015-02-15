package uk.co.mindbadger.footballresults.loader;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.co.mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;

public class FootballResultsSaver<K> {
	Logger logger = Logger.getLogger(FootballResultsSaver.class);
	
	private String dialect;
	private FootballResultsAnalyserDAO<K> dao;
	private FootballResultsMapping mapping;

	public void saveFixtures(List<ParsedFixture> fixturesRead) {
		try {
			logger.debug("About to saveFixtures: " + fixturesRead.size());
			
			dao.startSession();

			Map<K, Division<K>> divisionsInDatabase = dao.getAllDivisions();
			Map<K, Team<K>> teamsInDatabase = dao.getAllTeams();
			
			if (fixturesRead.size() > 0) {
	
				logger.debug("Getting mappings");
				List<String> includedDivisions = mapping.getIncludedDivisions(dialect);
				Map<String, String> divisionMappings = mapping.getDivisionMappings(dialect);
				Map<String, String> teamMappings = mapping.getTeamMappings(dialect);
				
				for (ParsedFixture parsedFixture : fixturesRead) {
					logger.debug("Saving fixture: " + parsedFixture);
					
					
					Division<K> division = null;
					Team<K> homeTeam = null;
					Team<K> awayTeam = null;
	
					String readDivisionId = parsedFixture.getDivisionId();
					String readHomeTeamId = parsedFixture.getHomeTeamId();
					String readAwayTeamId = parsedFixture.getAwayTeamId();
					
					if (includedDivisions.contains(readDivisionId)) {
						logger.debug("This is a division we want to track: " + readDivisionId);

						Integer seasonNumberForFixture = parsedFixture.getSeasonId();
						
						logger.debug("Get the season from the database");
						Season<K> season = dao.getSeason(seasonNumberForFixture);
						if (season == null) {
							season = dao.addSeason(seasonNumberForFixture);
						}
						logger.debug("..got " + season);
						
						String fraDivisionId = divisionMappings.get(readDivisionId);
						division = divisionsInDatabase.get(fraDivisionId);
						if (division == null) {
							logger.debug("We don't have this division, so add it");
							division = dao.addDivision(parsedFixture.getDivisionName());
							divisionsInDatabase.put(division.getDivisionId(), division);
							divisionMappings.put(readDivisionId, division.getDivisionIdAsString());
						}
	
						String fraHomeTeamId = teamMappings.get(readHomeTeamId);
						homeTeam = teamsInDatabase.get(fraHomeTeamId);
						if (homeTeam == null) {
							logger.debug("We don't have this home team, so add it");
							homeTeam = dao.addTeam(parsedFixture.getHomeTeamName());
							teamsInDatabase.put(homeTeam.getTeamId(), homeTeam);
							teamMappings.put(readHomeTeamId, homeTeam.getTeamIdAsString());
						}
	
						String fraAwayTeamId = teamMappings.get(readAwayTeamId);
						awayTeam = teamsInDatabase.get(fraAwayTeamId);
						if (awayTeam == null) {
							logger.debug("We don't have this away team, so add it");
							awayTeam = dao.addTeam(parsedFixture.getAwayTeamName());
							teamsInDatabase.put(awayTeam.getTeamId(), awayTeam);
							teamMappings.put(readAwayTeamId, awayTeam.getTeamIdAsString());
						}
						
						String dateString = (new SimpleDateFormat("yyyy-MM-dd")).format(parsedFixture.getFixtureDate().getTime());
						
						logger.info("Adding fixture: ssn:"+
								seasonNumberForFixture+
								",dt:"+dateString+
								",div:"+parsedFixture.getDivisionName()+
								",hm:"+parsedFixture.getHomeTeamName()+
								",aw:"+parsedFixture.getAwayTeamName()+
								",scr:"+parsedFixture.getHomeGoals()+"-"+parsedFixture.getAwayGoals());
						dao.addFixture(season, parsedFixture.getFixtureDate(), division, homeTeam, awayTeam, parsedFixture.getHomeGoals(), parsedFixture.getAwayGoals());
					}
				}
			}
			logger.debug("Saving mappings");
			mapping.saveMappings();
		} finally {
			dao.closeSession();
		}
	}
		
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	public FootballResultsAnalyserDAO<K> getDao() {
		return dao;
	}
	public void setDao(FootballResultsAnalyserDAO<K> dao) {
		this.dao = dao;
	}
	public FootballResultsMapping getMapping() {
		return mapping;
	}
	public void setMapping(FootballResultsMapping mapping) {
		this.mapping = mapping;
	}
}
