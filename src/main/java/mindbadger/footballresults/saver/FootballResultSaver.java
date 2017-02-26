package mindbadger.footballresults.saver;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.football.domain.Division;
import mindbadger.football.domain.DomainObjectFactory;
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
public class FootballResultSaver {
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
	
	public Season createSeasonIfNotExisting(Integer seasonNumber) {
		Season season = seasonRepository.findOne(seasonNumber);
		if (season == null) {
			season = domainObjectFactory.createSeason(seasonNumber);
			season = seasonRepository.save(season);
		}
		
		return season;
	}

	public Division createDivisionIfNotExisting(String divisionId, String divisionName) {
		Division division = divisionRepository.findOne(divisionId);
		if (division == null) {
			division = domainObjectFactory.createDivision(divisionName);
			division = divisionRepository.save(division);
		}
		return division;
	}

	public Team createTeamIfNotExisting(String teamId, String teamName) {
		Team team = teamRepository.findOne(teamId);
		if (team == null) {
			team = domainObjectFactory.createTeam(teamName);
			team = teamRepository.save(team);
		}
		return team;
	}

	public void createSeasonDivisionTeamsIfNotExisting(Season season, Division division, int indexOfDivision, Team homeTeam,
			Team awayTeam) {
		boolean changed = false;
		
		SeasonDivision seasonDivision = seasonRepository.getSeasonDivision(season, division);

		if (seasonDivision == null) {
			seasonDivision = domainObjectFactory.createSeasonDivision(season, division, indexOfDivision);
			season.getSeasonDivisions().add(seasonDivision);
			changed = true;
		}
		
		SeasonDivisionTeam homeSeasonDivisionTeam = seasonRepository.getSeasonDivisionTeam(seasonDivision, homeTeam);
		if (homeSeasonDivisionTeam == null) {
			homeSeasonDivisionTeam = domainObjectFactory.createSeasonDivisionTeam(seasonDivision, homeTeam);
			seasonDivision.getSeasonDivisionTeams().add(homeSeasonDivisionTeam);
			changed = true;
		}

		SeasonDivisionTeam awaySeasonDivisionTeam = seasonRepository.getSeasonDivisionTeam(seasonDivision, awayTeam);
		if (awaySeasonDivisionTeam == null) {
			awaySeasonDivisionTeam = domainObjectFactory.createSeasonDivisionTeam(seasonDivision, awayTeam);
			seasonDivision.getSeasonDivisionTeams().add(awaySeasonDivisionTeam);
			changed = true;
		}

		if (changed) {
			seasonRepository.save(season);
		}
	}

	public void createFixture(Season season, Division division, Team homeTeam, Team awayTeam, Calendar fixtureDate,
			Integer homeGoals, Integer awayGoals) {
		Fixture fixture = fixtureRepository.getExistingFixture(season, homeTeam, awayTeam);
		
		if (fixture == null) {
			fixture = domainObjectFactory.createFixture(season, homeTeam, awayTeam);
		}
		
		fixture.setDivision(division);
		fixture.setFixtureDate(fixtureDate);
		fixture.setHomeGoals(homeGoals);
		fixture.setAwayGoals(awayGoals);
		
		fixtureRepository.createOrUpdate(fixture);
	}

	public void setDivisionRepository(DivisionRepository divisionRepository) {
		this.divisionRepository = divisionRepository;
	}

	public void setTeamRepository(TeamRepository teamRepository) {
		this.teamRepository = teamRepository;
	}

	public void setSeasonRepository(SeasonRepository seasonRepository) {
		this.seasonRepository = seasonRepository;
	}

	public void setFixtureRepository(FixtureRepository fixtureRepository) {
		this.fixtureRepository = fixtureRepository;
	}

	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
	}
	
	
}
