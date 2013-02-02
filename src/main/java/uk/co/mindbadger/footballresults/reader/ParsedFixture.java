package uk.co.mindbadger.footballresults.reader;

import java.util.Calendar;

public class ParsedFixture {
	private Integer fixtureId;
	private Integer seasonId;
	private Calendar fixtureDate;
	private Integer divisionId;
	private String divisionName;
	private Integer homeTeamId;
	private String homeTeamName;
	private Integer awayTeamId;
	private String awayTeamName;
	private Integer homeGoals;
	private Integer awayGoals;
	public Integer getFixtureId() {
		return fixtureId;
	}
	public void setFixtureId(Integer fixtureId) {
		this.fixtureId = fixtureId;
	}
	public Integer getSeasonId() {
		return seasonId;
	}
	public void setSeasonId(Integer seasonId) {
		this.seasonId = seasonId;
	}
	public Calendar getFixtureDate() {
		return fixtureDate;
	}
	public void setFixtureDate(Calendar fixtureDate) {
		this.fixtureDate = fixtureDate;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public String getDivisionName() {
		return divisionName;
	}
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}
	public Integer getHomeTeamId() {
		return homeTeamId;
	}
	public void setHomeTeamId(Integer homeTeamId) {
		this.homeTeamId = homeTeamId;
	}
	public String getHomeTeamName() {
		return homeTeamName;
	}
	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}
	public Integer getAwayTeamId() {
		return awayTeamId;
	}
	public void setAwayTeamId(Integer awayTeamId) {
		this.awayTeamId = awayTeamId;
	}
	public String getAwayTeamName() {
		return awayTeamName;
	}
	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
	}
	public Integer getHomeGoals() {
		return homeGoals;
	}
	public void setHomeGoals(Integer homeGoals) {
		this.homeGoals = homeGoals;
	}
	public Integer getAwayGoals() {
		return awayGoals;
	}
	public void setAwayGoals(Integer awayGoals) {
		this.awayGoals = awayGoals;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ParsedFixture) {
			ParsedFixture parsedFixture = (ParsedFixture) obj;
			
			boolean equals = parsedFixture.getSeasonId().equals(this.seasonId);
			equals = equals && ((parsedFixture.getFixtureDate() == null && this.fixtureDate == null) || (parsedFixture.getFixtureDate().equals(this.fixtureDate)));
			equals = equals && parsedFixture.getHomeTeamId().equals(this.homeTeamId);
			equals = equals && parsedFixture.getAwayTeamId().equals(this.awayTeamId);
			equals = equals && parsedFixture.getDivisionId().equals(this.divisionId);
			
			return equals;
		} else {
			return false;
		}
	}

	
	
}
