package uk.co.mindbadger.footballresults.loader;

import java.util.List;
import java.util.Map;

import uk.co.mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import uk.co.mindbadger.footballresults.reader.FootballResultsReader;
import uk.co.mindbadger.footballresults.reader.ParsedFixture;
import uk.co.mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import uk.co.mindbadger.footballresultsanalyser.domain.Division;
import uk.co.mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import uk.co.mindbadger.footballresultsanalyser.domain.Season;
import uk.co.mindbadger.footballresultsanalyser.domain.Team;

public class FootballResultsLoader {
	private String dialect;
	private DomainObjectFactory domainObjectFactory;
	private FootballResultsAnalyserDAO dao;
	private FootballResultsReader reader;
	private FootballResultsMapping mapping;

	public void loadResultsForSeason(int seasonNum) {
		Map<Integer, Division> divisionsInDatabase = dao.getAllDivisions();
		Map<Integer, Team> teamsInDatabase = dao.getAllTeams();
		
		List<ParsedFixture> fixturesRead = reader.readFixturesForSeason(seasonNum);
		
		if (fixturesRead.size() > 0) {
			Season season = dao.getSeason(seasonNum);
			if (season == null) {
				season = dao.addSeason(seasonNum);
			}

			List<Integer> includedDivisions = mapping.getIncludedDivisions(dialect);
			Map<Integer, Integer> divisionMappings = mapping.getDivisionMappings(dialect);
			Map<Integer, Integer> teamMappings = mapping.getTeamMappings(dialect);
			
			for (ParsedFixture parsedFixture : fixturesRead) {
				Division division = null;
				Team homeTeam = null;
				Team awayTeam = null;

				Integer readDivisionId = parsedFixture.getDivisionId();
				Integer readHomeTeamId = parsedFixture.getHomeTeamId();
				Integer readAwayTeamId = parsedFixture.getAwayTeamId();
				
				if (includedDivisions.contains(readDivisionId)) {
					
					Integer fraDivisionId = divisionMappings.get(readDivisionId);
					division = divisionsInDatabase.get(fraDivisionId);
					if (division == null) {
						division = dao.addDivision(parsedFixture.getDivisionName());
						divisionsInDatabase.put(division.getDivisionId(), division);
						divisionMappings.put(readDivisionId, division.getDivisionId());
					}

					Integer fraHomeTeamId = teamMappings.get(readHomeTeamId);
					homeTeam = teamsInDatabase.get(fraHomeTeamId);
					if (homeTeam == null) {
						homeTeam = dao.addTeam(parsedFixture.getHomeTeamName());
						teamsInDatabase.put(homeTeam.getTeamId(), homeTeam);
						teamMappings.put(readHomeTeamId, homeTeam.getTeamId());
					}

					Integer fraAwayTeamId = teamMappings.get(readAwayTeamId);
					awayTeam = teamsInDatabase.get(fraAwayTeamId);
					if (awayTeam == null) {
						awayTeam = dao.addTeam(parsedFixture.getAwayTeamName());
						teamsInDatabase.put(awayTeam.getTeamId(), awayTeam);
						teamMappings.put(readAwayTeamId, awayTeam.getTeamId());
					}
					
					dao.addFixture(season, parsedFixture.getFixtureDate(), division, homeTeam, awayTeam, parsedFixture.getHomeGoals(), parsedFixture.getAwayGoals());
				}
			}
		}
	}
	
	public DomainObjectFactory getDomainObjectFactory() {
		return domainObjectFactory;
	}
	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
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
	public FootballResultsReader getReader() {
		return reader;
	}
	public void setReader(FootballResultsReader reader) {
		this.reader = reader;
	}
	public FootballResultsMapping getMapping() {
		return mapping;
	}
	public void setMapping(FootballResultsMapping mapping) {
		this.mapping = mapping;
	}
}
