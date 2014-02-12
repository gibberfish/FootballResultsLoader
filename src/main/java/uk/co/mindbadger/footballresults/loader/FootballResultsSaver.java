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

public class FootballResultsSaver {
	Logger logger = Logger.getLogger(FootballResultsSaver.class);
	
	private String dialect;
	private FootballResultsAnalyserDAO dao;
	private FootballResultsMapping mapping;

	public void saveFixtures(List<ParsedFixture> fixturesRead) {
		try {
			logger.debug("About to saveFixtures: " + fixturesRead.size());
			
			dao.startSession();

			Map<Integer, Division> divisionsInDatabase = dao.getAllDivisions();
			Map<Integer, Team> teamsInDatabase = dao.getAllTeams();
			
			if (fixturesRead.size() > 0) {
	
				logger.debug("Getting mappings");
				List<Integer> includedDivisions = mapping.getIncludedDivisions(dialect);
				Map<Integer, Integer> divisionMappings = mapping.getDivisionMappings(dialect);
				Map<Integer, Integer> teamMappings = mapping.getTeamMappings(dialect);
				
				for (ParsedFixture parsedFixture : fixturesRead) {
					logger.debug("Saving fixture: " + parsedFixture);
					
					
					Division division = null;
					Team homeTeam = null;
					Team awayTeam = null;
	
					Integer readDivisionId = parsedFixture.getDivisionId();
					Integer readHomeTeamId = parsedFixture.getHomeTeamId();
					Integer readAwayTeamId = parsedFixture.getAwayTeamId();
					
					if (includedDivisions.contains(readDivisionId)) {
						logger.debug("This is a division we want to track: " + readDivisionId);

						Integer seasonNumberForFixture = parsedFixture.getSeasonId();
						
						logger.debug("Get the season from the database");
						Season season = dao.getSeason(seasonNumberForFixture);
						if (season == null) {
							season = dao.addSeason(seasonNumberForFixture);
						}
						logger.debug("..got " + season);
						
						Integer fraDivisionId = divisionMappings.get(readDivisionId);
						division = divisionsInDatabase.get(fraDivisionId);
						if (division == null) {
							logger.debug("We don't have this division, so add it");
							division = dao.addDivision(parsedFixture.getDivisionName());
							divisionsInDatabase.put(division.getDivisionId(), division);
							divisionMappings.put(readDivisionId, division.getDivisionId());
						}
	
						Integer fraHomeTeamId = teamMappings.get(readHomeTeamId);
						homeTeam = teamsInDatabase.get(fraHomeTeamId);
						if (homeTeam == null) {
							logger.debug("We don't have this home team, so add it");
							homeTeam = dao.addTeam(parsedFixture.getHomeTeamName());
							teamsInDatabase.put(homeTeam.getTeamId(), homeTeam);
							teamMappings.put(readHomeTeamId, homeTeam.getTeamId());
						}
	
						Integer fraAwayTeamId = teamMappings.get(readAwayTeamId);
						awayTeam = teamsInDatabase.get(fraAwayTeamId);
						if (awayTeam == null) {
							logger.debug("We don't have this away team, so add it");
							awayTeam = dao.addTeam(parsedFixture.getAwayTeamName());
							teamsInDatabase.put(awayTeam.getTeamId(), awayTeam);
							teamMappings.put(readAwayTeamId, awayTeam.getTeamId());
						}
						
						String dateString = (new SimpleDateFormat("yyyy-MM-dd")).format(parsedFixture.getFixtureDate().getTime());
						
						logger.debug("Adding fixture: ssn:" + season + ",dt:"+dateString+",div:"+division.getDivisionName()+",hm:"+homeTeam.getTeamName()+",aw:"+awayTeam.getTeamName()+",scr:"+parsedFixture.getHomeGoals()+"-"+parsedFixture.getAwayGoals());
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
	public FootballResultsAnalyserDAO getDao() {
		return dao;
	}
	public void setDao(FootballResultsAnalyserDAO dao) {
		this.dao = dao;
	}
	public FootballResultsMapping getMapping() {
		return mapping;
	}
	public void setMapping(FootballResultsMapping mapping) {
		this.mapping = mapping;
	}
}
