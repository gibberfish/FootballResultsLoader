package mindbadger.footballresults.loader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import mindbadger.football.domain.DivisionMapping;
import mindbadger.football.domain.DomainObjectFactory;
import mindbadger.football.domain.TeamMapping;
import mindbadger.football.domain.TrackedDivision;
import mindbadger.football.repository.DivisionMappingRepository;
import mindbadger.football.repository.TeamMappingRepository;
import mindbadger.football.repository.TrackedDivisionRepository;

public class FootballResultsLoaderMappingDatabase extends AbstractFootballResultsLoaderMapping {
	
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
		trackDivisions = trackedDivisionRepository.findAll();
		divisionMappings = divisionMappingRepository.findAll();
		teamMappings = teamMappingRepository.findAll();
		
		String lastDialectName = "";
		Dialect dialect = null;
		
		for (DivisionMapping divisionMapping : divisionMappings) {
			String dialectName = divisionMapping.getDialect();
			
			// Get the dialect if we don't already have it
			if (!dialectName.equals(lastDialectName)) {
				lastDialectName = dialectName;
				if (dialects.containsKey(dialectName)) {
					dialect = dialects.get(dialectName);
				} else {
					dialect = new Dialect(dialectName);
					dialects.put(dialectName, dialect);
				}
				
				// Store the tracked divisions for this dialect
				for (TrackedDivision trackedDivision : trackDivisions) {
					if (dialectName.equals(trackedDivision.getDialect())) {
						dialect.getIncludedDivisions().add(trackedDivision.getSourceId().toString());
					}
				}
				
				if (dialect.getIncludedDivisions().size() == 0) {
					throw new FootballResultsLoaderException("There are no IncludedDivisions in your mapping file for dialect " + dialectName);
				}
			}
			
			dialect.getDivisionMappings().put(divisionMapping.getSourceId().toString(), divisionMapping.getFraId().toString());
			dialect.getOrderedListOfDivisions().add(divisionMapping.getFraId().toString());
			
			if (dialect.getDivisionMappings().size() == 0) {
				throw new FootballResultsLoaderException("There are no DivisionMappings in your mapping file for dialect " + dialectName);
			}

			// Now find the team mappings
			lastDialectName = "";
			dialect = null;
			for (TeamMapping teamMapping : teamMappings) {
				// Get the dialect if we don't already have it
				if (!dialectName.equals(lastDialectName)) {
					lastDialectName = dialectName;
					if (dialects.containsKey(dialectName)) {
						dialect = dialects.get(dialectName);
					} else {
						dialect = new Dialect(dialectName);
						dialects.put(dialectName, dialect);
					}
				}

				dialect.getTeamMappings().put(teamMapping.getSourceId().toString(), teamMapping.getFraId().toString());
			}
			
			if (dialect.getTeamMappings().size() == 0) {
				throw new FootballResultsLoaderException("There are no TeamMappings in your mapping file for dialect " + dialectName);
			}
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
			TeamMapping teamMapping = domainObjectFactory.createTeamMapping(dialect.getName(), Integer.parseInt(sourceId), Integer.parseInt(fraId));
			TeamMapping teamMappingInDb = teamMappingRepository.findOne(teamMapping);
			if (teamMappingInDb == null) {
				teamMappingRepository.save(teamMapping);
			}
		}
	}

	private void saveMappedDivisions(Dialect dialect) {
		for (String sourceId : dialect.getDivisionMappings().keySet()) {
			String fraId = dialect.getDivisionMappings().get(sourceId);
			DivisionMapping divisionMapping = domainObjectFactory.createDivisionMapping(dialect.getName(), Integer.parseInt(sourceId), Integer.parseInt(fraId));
			DivisionMapping divisionMappingInDb = divisionMappingRepository.findOne(divisionMapping);
			if (divisionMappingInDb == null) {
				divisionMappingRepository.save(divisionMapping);
			}			
		}
	}

	private void saveTrackedDivisions(Dialect dialect) {
		for (String includedDivisionId : dialect.getIncludedDivisions()) {
			TrackedDivision trackedDivision = domainObjectFactory.createTrackedDivision(dialect.getName(), Integer.parseInt(includedDivisionId));
			TrackedDivision trackedDivisionInDb = trackedDivisionRepository.findOne(trackedDivision);
			if (trackedDivisionInDb == null) {
				trackedDivisionRepository.save(trackedDivision);
			}
		}
	}
	
}
