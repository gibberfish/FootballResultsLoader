package mindbadger.footballresults.loader;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import mindbadger.football.domain.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.football.repository.DivisionMappingRepository;
import mindbadger.football.repository.TeamMappingRepository;
import mindbadger.football.repository.TrackedDivisionRepository;

@Component
public class FootballResultsLoaderMappingDatabase extends AbstractFootballResultsLoaderMapping {
	Logger logger = Logger.getLogger(FootballResultsLoaderMappingDatabase.class);
	
	@Autowired
	private TrackedDivisionRepository trackedDivisionRepository;

	@Autowired
	private DivisionMappingRepository divisionMappingRepository;

	@Autowired
	private TeamMappingRepository teamMappingRepository;
	
	@Autowired
	private DomainObjectFactory domainObjectFactory;
	
	private Iterable<TrackedDivision> trackDivisions;
	private Iterable<DivisionMapping> divisionMappings;
	private Iterable<TeamMapping> teamMappings;
	
	//TODO This is shitty code that needs to be refactorred
	@PostConstruct
	public void initialise() throws FootballResultsLoaderException {
		logger.info("Initialising the FootballResultsLoaderMappingDatabase");
		
		trackDivisions = trackedDivisionRepository.findAll();
		logger.info("Got " + trackDivisions.spliterator().getExactSizeIfKnown() + " divisions to track");

		divisionMappings = divisionMappingRepository.findAll();
		logger.info("Got " + divisionMappings.spliterator().getExactSizeIfKnown() + " divisions to track");

		teamMappings = teamMappingRepository.findAll();
		logger.info("Got " + teamMappings.spliterator().getExactSizeIfKnown() + " teams to track");
		
		Dialect dialect = null;

		// Store the tracked divisions for this dialect
		for (TrackedDivision trackedDivision : trackDivisions) {
			String dialectName = trackedDivision.getDialect();
			
			if (dialects.containsKey(dialectName)) {
				dialect = dialects.get(dialectName);
				logger.info("Getting dialect " + dialectName + " from the map");
			} else {
				dialect = new Dialect(dialectName);
				dialects.put(dialectName, dialect);
				logger.info("Adding dialect " + dialectName + " to the map");
			}
			
			dialect.getIncludedDivisions().add(trackedDivision.getSourceId().toString());
			logger.info("Adding " + trackedDivision.getSourceId() + "to dialect " + dialectName);
		}
		
		if (dialects.size() == 0) {
			throw new FootballResultsLoaderException("There are no IncludedDivisions in your mapping file");
		}
		
		for (DivisionMapping divisionMapping : divisionMappings) {
			String dialectName = divisionMapping.getDialect();
			dialect = dialects.get(dialectName);
			logger.info("Getting dialect " + dialectName + " from the map");
						
			dialect.getDivisionMappings().put(divisionMapping.getSourceId().toString(), divisionMapping.getFraId().toString());
			dialect.getOrderedListOfDivisions().add(divisionMapping.getFraId().toString());
			logger.info("Adding " + divisionMapping.getSourceId() + "to dialect " + dialectName);
		}
		
		for (TeamMapping teamMapping : teamMappings) {
			String dialectName = teamMapping.getDialect();
			dialect = dialects.get(dialectName);
			logger.info("Getting dialect " + dialectName + " from the map");
			
			dialect.getTeamMappings().put(teamMapping.getSourceId().toString(), teamMapping.getFraId().toString());
			logger.info("Adding " + teamMapping.getSourceId() + "to dialect " + dialectName);
		}
	}

	@Override
	public void saveMappings() throws FootballResultsLoaderException {
		for (Dialect dialect : dialects.values()) {
			
			//TODO this method never deletes items that have been removed - not urgent, but needs adding.
			
			saveTrackedDivisions(dialect);
			
			saveMappedDivisions(dialect);
			
			saveMappedTeams(dialect);
		}
	}

	private void saveMappedTeams(Dialect dialect) {
		for (String sourceId : dialect.getTeamMappings().keySet()) {
			String fraId = dialect.getTeamMappings().get(sourceId);

			MappingId teamMappingId = new MappingId();
			teamMappingId.setDialect(dialect.getName());
			teamMappingId.setSourceId(Integer.parseInt(sourceId));
			teamMappingId.setFraId(Integer.parseInt(fraId));

			TeamMapping teamMappingInDb = teamMappingRepository.findOne(teamMappingId);

			if (teamMappingInDb == null) {
				TeamMapping teamMapping = domainObjectFactory.createTeamMapping(dialect.getName(), Integer.parseInt(sourceId), Integer.parseInt(fraId));
				teamMappingRepository.save(teamMapping);
			}
		}
	}

	private void saveMappedDivisions(Dialect dialect) {
		for (String sourceId : dialect.getDivisionMappings().keySet()) {
			String fraId = dialect.getDivisionMappings().get(sourceId);

			MappingId divisionMappingId = new MappingId();
			divisionMappingId.setDialect(dialect.getName());
			divisionMappingId.setSourceId(Integer.parseInt(sourceId));
			divisionMappingId.setFraId(Integer.parseInt(fraId));

			DivisionMapping divisionMappingInDb = divisionMappingRepository.findOne(divisionMappingId);

			if (divisionMappingInDb == null) {
				DivisionMapping divisionMapping = domainObjectFactory.createDivisionMapping(dialect.getName(), Integer.parseInt(sourceId), Integer.parseInt(fraId));
				divisionMappingRepository.save(divisionMapping);
			}			
		}
	}

	private void saveTrackedDivisions(Dialect dialect) {
		for (String includedDivisionId : dialect.getIncludedDivisions()) {
			TrackedDivisionId trackedDivisionId = new TrackedDivisionId();
			trackedDivisionId.setDialect(dialect.getName());
			trackedDivisionId.setSourceId(Integer.parseInt(includedDivisionId));

			TrackedDivision trackedDivisionInDb = trackedDivisionRepository.findOne(trackedDivisionId);

			if (trackedDivisionInDb == null) {
				TrackedDivision trackedDivision = domainObjectFactory.createTrackedDivision(dialect.getName(), Integer.parseInt(includedDivisionId));
				trackedDivisionRepository.save(trackedDivision);
			}
		}
	}
	
}
