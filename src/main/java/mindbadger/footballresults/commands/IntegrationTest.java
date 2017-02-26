package mindbadger.footballresults.commands;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.LoaderEntryPointApplication;
import mindbadger.football.domain.Division;
import mindbadger.football.domain.DivisionImpl;
import mindbadger.football.domain.Season;
import mindbadger.football.domain.SeasonDivision;
import mindbadger.football.domain.SeasonDivisionImpl;
import mindbadger.football.domain.SeasonDivisionTeam;
import mindbadger.football.domain.SeasonDivisionTeamImpl;
import mindbadger.football.domain.SeasonImpl;
import mindbadger.football.domain.Team;
import mindbadger.football.domain.TeamImpl;
import mindbadger.football.repository.DivisionRepository;
import mindbadger.football.repository.SeasonRepository;
import mindbadger.football.repository.TeamRepository;

@Component
public class IntegrationTest implements Command {

	@Autowired
	DivisionRepository divisionRepository;

	@Autowired
	SeasonRepository seasonRepository;

	@Autowired
	TeamRepository teamRepository;
	
	private static final Logger log = LoggerFactory.getLogger(LoaderEntryPointApplication.class);
	
	@Override
	public void run(String[] args) {
		log.error("************ STEP 1: Create new division *************");
		
		Division division1 = new DivisionImpl();
		division1.setDivisionId("1000");
		division1.setDivisionName("Premier Fish");
		log.info("Created new Division : " + division1.toString());
		
		Division savedDivision = divisionRepository.save((DivisionImpl) division1);

		log.info("division1 hashCode: " + division1.hashCode() + " / savedDivsion hashCode: " + savedDivision.hashCode());
		if (division1 == savedDivision) {
			log.info("The Division returned by save is the same as the one passed in");
		}
		if (division1.equals(savedDivision)) {
			log.info("The Division returned by save is the same as the one passed in (using equals method)");
		}

		
		
		log.error("************ STEP 2: Find saved division *************");
		
		savedDivision = divisionRepository.findOne("1000");
		
		log.info("Retrieved Division : " + savedDivision.toString());
		
		log.error("************ STEP 3: Fix division name *************");
		
		savedDivision.setDivisionName("Premier League");
		savedDivision = divisionRepository.save((DivisionImpl)savedDivision);
		
		log.info("Re-retrieved Division : " + savedDivision.toString());
		
		log.error("************ STEP 3b: Create new teams *************");
		
		Team team1 = new TeamImpl ();
		team1.setTeamId("2000");
		team1.setTeamName("Madeup United");
		
		Team team2 = new TeamImpl ();
		team2.setTeamId("2001");
		team2.setTeamName("Many Fish City");
		
		teamRepository.save((TeamImpl) team1);
		teamRepository.save((TeamImpl)team2);
		

		
		log.error("************ STEP 4: Create new season *************");
		
		Season season = new SeasonImpl ();
		season.setSeasonNumber(1800);
		log.info("Created new Season : " + season.toString());
		
		Season origSavedSeason = seasonRepository.save((SeasonImpl)season);
		log.info("Original non-persisted Season Instance : " + System.identityHashCode(season));
		log.info("Original Saved Season Instance : " + System.identityHashCode(origSavedSeason));
		
		log.error("************ STEP 5: Find saved season *************");
		
		Season retrievedSeason = seasonRepository.findOne(1800);
		
		log.info("Retrieved Season : " + retrievedSeason.toString());
		log.info("Retrieved Season Instance : " + System.identityHashCode(retrievedSeason));

		log.info("season hashCode: " + season.hashCode() + " / retrieved Season hashCode: " + retrievedSeason.hashCode() + " / saved Season hashCode: " + origSavedSeason.hashCode());
		if (season == retrievedSeason) {
			log.info("The Season returned by save is the same as the one passed in");
		}
		if (season.equals(retrievedSeason)) {
			log.info("The Season returned by save is the same as the one passed in (using equals method)");
		}

		
		log.error("************ STEP 6: Create new season division relationship *************");
		
		SeasonDivision seasonDivision = new SeasonDivisionImpl();
		seasonDivision.setSeason(origSavedSeason);
		seasonDivision.setDivision(savedDivision);
		seasonDivision.setDivisionPosition(1);
		
		Set<SeasonDivision> newSeasonDivisions = new HashSet<SeasonDivision> ();
		newSeasonDivisions.add(seasonDivision);
		//retrievedSeason.getSeasonDivisions().add(seasonDivision);
		retrievedSeason.setSeasonDivisions(newSeasonDivisions);
		
		log.info("Season with division attached : " + retrievedSeason.toString());
		log.info("Retrieved Season Instance : " + System.identityHashCode(retrievedSeason));
		
		log.error("************ STEP 7: Save the season with this new relationship *************");
		
		//seasonRepository.flush();
		seasonRepository.save((SeasonImpl)retrievedSeason);

		Division division2 = new DivisionImpl();
		division2.setDivisionId("1001");
		division2.setDivisionName("League of Fools");
		divisionRepository.save((DivisionImpl)division2);

		
		SeasonDivision seasonDivision2 = new SeasonDivisionImpl();
		seasonDivision2.setSeason(origSavedSeason);
		seasonDivision2.setDivision(division2);
		seasonDivision2.setDivisionPosition(2);
		
		retrievedSeason = seasonRepository.save((SeasonImpl)retrievedSeason);
		
		SeasonDivisionTeam seasonDivisionTeam1 = new SeasonDivisionTeamImpl();
		seasonDivisionTeam1.setSeasonDivision(seasonDivision2);
		seasonDivisionTeam1.setTeam(team1);

		seasonDivision2.getSeasonDivisionTeams().add(seasonDivisionTeam1);
		
		retrievedSeason.getSeasonDivisions().add(seasonDivision2);
		retrievedSeason = seasonRepository.save((SeasonImpl)retrievedSeason);

		
		
		
		
		log.error("************ STEP 8: Cleanup *************");
		Set<SeasonDivision> seasonDivisions = retrievedSeason.getSeasonDivisions();
		
		Iterator<SeasonDivision> iter = seasonDivisions.iterator();
		while (iter.hasNext()) {
			SeasonDivision seasonDivisionToDelete = iter.next();
			
			Set<SeasonDivisionTeam> seasonDivisionTeams = seasonDivisionToDelete.getSeasonDivisionTeams();
			
			Iterator<SeasonDivisionTeam> iter2 = seasonDivisionTeams.iterator();
			
			while (iter2.hasNext()) {
				iter2.next();
				iter2.remove();
			}
			
			iter.remove();
		}
		retrievedSeason = seasonRepository.save((SeasonImpl)retrievedSeason);
		
		
		
		
		seasonRepository.delete((SeasonImpl)retrievedSeason);
		teamRepository.delete((TeamImpl)team1);
		teamRepository.delete((TeamImpl)team2);
		divisionRepository.delete((DivisionImpl)savedDivision);
		divisionRepository.delete((DivisionImpl)division2);
	}
}
