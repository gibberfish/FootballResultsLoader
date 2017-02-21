package mindbadger.footballresults.loader;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import mindbadger.footballresults.reader.ParsedFixture;
import mindbadger.footballresultsanalyser.dao.ChangeScoreException;
import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.Division;
import mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import mindbadger.footballresultsanalyser.domain.Fixture;
import mindbadger.footballresultsanalyser.domain.Season;
import mindbadger.footballresultsanalyser.domain.SeasonDivision;
import mindbadger.footballresultsanalyser.domain.SeasonDivisionTeam;
import mindbadger.footballresultsanalyser.domain.Team;
import mindbadger.footballresultsanalyser.repository.DivisionRepository;
import mindbadger.footballresultsanalyser.repository.FixtureRepository;
import mindbadger.footballresultsanalyser.repository.SeasonRepository;
import mindbadger.footballresultsanalyser.repository.TeamRepository;

@Component
public class FootballResultsSaver {
	Logger logger = Logger.getLogger(FootballResultsSaver.class);
	
	private String dialect;
	
	@Autowired
	private FootballResultsMapping mapping;
	@Autowired
	private DivisionRepository divisionRepository;
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private SeasonRepository seasonRepository;
	@Autowired
	private FixtureRepository fixtureRepository;
	@Autowired
	private DomainObjectFactory domainObjectFactory;

	public void saveFixtures(List<ParsedFixture> fixturesRead) {
		logger.debug("About to saveFixtures: " + fixturesRead.size());
		if (fixturesRead.size() > 0) {
			logger.debug("Getting mappings");
			List<String> includedDivisions = mapping.getIncludedDivisions(dialect);
			Map<String, String> divisionMappings = mapping.getDivisionMappings(dialect);
			Map<String, String> teamMappings = mapping.getTeamMappings(dialect);
			List<String> orderedListOfDivisions = mapping.getOrderedListOfDivisions(dialect);
//			
//			// Ensure that we add the fixtures in chronological order so that we don't try to add playoffs before the main games
//			Collections.sort(fixturesRead);
//			
			for (ParsedFixture parsedFixture : fixturesRead) {
				logger.debug("Saving fixture: " + parsedFixture);
//				
//				
//
//				
//				if (includedDivisions.contains(readDivisionId)) {
//					logger.debug("This is a division we want to track: " + readDivisionId);

					Integer seasonNumberForFixture = parsedFixture.getSeasonId();
					
					logger.debug("Get the season from the database");
					Season season = seasonRepository.findOne(seasonNumberForFixture);
					if (season == null) {
						season = domainObjectFactory.createSeason(seasonNumberForFixture);
						season = seasonRepository.save(season);
					}
					logger.debug("..got " + season);
					
					String readDivisionId = parsedFixture.getDivisionId();
					Division division = null;
					String fraDivisionId = divisionMappings.get(readDivisionId);
					division = divisionRepository.findOne(fraDivisionId);
					if (division == null) {
						logger.debug("We don't have this division, so add it");
						division = domainObjectFactory.createDivision(parsedFixture.getDivisionName());
						division = divisionRepository.save(division);
						divisionMappings.put(readDivisionId, division.getDivisionId());
					}

					String readHomeTeamId = parsedFixture.getHomeTeamId();
					Team homeTeam = null;
					String fraHomeTeamId = teamMappings.get(readHomeTeamId);
					homeTeam = teamRepository.findOne(fraHomeTeamId);
					if (homeTeam == null) {
						logger.debug("We don't have this home team, so add it");
						homeTeam = domainObjectFactory.createTeam(parsedFixture.getHomeTeamName());
						homeTeam = teamRepository.save(homeTeam);
						teamMappings.put(readHomeTeamId, homeTeam.getTeamId());
					}

					String readAwayTeamId = parsedFixture.getAwayTeamId();
					Team awayTeam = null;
					String fraAwayTeamId = teamMappings.get(readAwayTeamId);
					awayTeam = teamRepository.findOne(fraAwayTeamId);
					if (awayTeam == null) {
						logger.debug("We don't have this away team, so add it");
						awayTeam = domainObjectFactory.createTeam(parsedFixture.getAwayTeamName());
						awayTeam = teamRepository.save(awayTeam);
						teamMappings.put(readAwayTeamId, awayTeam.getTeamId());
					}
					
//					String dateString = (new SimpleDateFormat("yyyy-MM-dd")).format(parsedFixture.getFixtureDate().getTime());
//					
//					logger.info("Adding fixture: ssn:"+
//							seasonNumberForFixture+
//							",dt:"+dateString+
//							",div:"+parsedFixture.getDivisionName()+
//							",hm:"+parsedFixture.getHomeTeamName()+
//							",aw:"+parsedFixture.getAwayTeamName()+
//							",scr:"+parsedFixture.getHomeGoals()+"-"+parsedFixture.getAwayGoals());
//					
//					try {
//						int indexOfDivision = orderedListOfDivisions.indexOf(division.getDivisionId());
//						
//						SeasonDivision seasonDivision = domainObjectFactory.createSeasonDivision(season, division, indexOfDivision);
//						season.getSeasonDivisions().add(seasonDivision);
//						
//						SeasonDivisionTeam homeSeasonDivisionTeam = domainObjectFactory.createSeasonDivisionTeam(seasonDivision, homeTeam);
//						SeasonDivisionTeam awaySeasonDivisionTeam = domainObjectFactory.createSeasonDivisionTeam(seasonDivision, awayTeam);
//						seasonDivision.getSeasonDivisionTeams().add(homeSeasonDivisionTeam);
//						seasonDivision.getSeasonDivisionTeams().add(awaySeasonDivisionTeam);
//						
//						seasonRepository.save(season);
//
//						Fixture fixture = domainObjectFactory.createFixture(season, homeTeam, awayTeam);
//						fixture.setDivision(division);
//						fixture.setFixtureDate(parsedFixture.getFixtureDate());
//						fixture.setHomeGoals(parsedFixture.getHomeGoals());
//						fixture.setAwayGoals(parsedFixture.getAwayGoals());
//						
//						fixtureRepository.save(fixture);
//					} catch (ChangeScoreException e) {
//						logger.info("Ignoring play-off result...");
//					}
//				}
			}
		}
		logger.debug("Saving mappings");
//		mapping.saveMappings();
	}
		
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	public FootballResultsMapping getMapping() {
		return mapping;
	}
	public void setMapping(FootballResultsMapping mapping) {
		this.mapping = mapping;
	}

	public DivisionRepository getDivisionRepository() {
		return divisionRepository;
	}

	public void setDivisionRepository(DivisionRepository divisionRepository) {
		this.divisionRepository = divisionRepository;
	}

	public TeamRepository getTeamRepository() {
		return teamRepository;
	}

	public void setTeamRepository(TeamRepository teamRepository) {
		this.teamRepository = teamRepository;
	}

	public SeasonRepository getSeasonRepository() {
		return seasonRepository;
	}

	public void setSeasonRepository(SeasonRepository seasonRepository) {
		this.seasonRepository = seasonRepository;
	}

	public FixtureRepository getFixtureRepository() {
		return fixtureRepository;
	}

	public void setFixtureRepository(FixtureRepository fixtureRepository) {
		this.fixtureRepository = fixtureRepository;
	}

	public DomainObjectFactory getDomainObjectFactory() {
		return domainObjectFactory;
	}

	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
	}
}
