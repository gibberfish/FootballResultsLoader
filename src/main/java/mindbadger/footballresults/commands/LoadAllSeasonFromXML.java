package mindbadger.footballresults.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresults.loader.FootballResultsLoader;

//TODO Used "spring-xml-reader.xml" - this will need configuring
@Component
public class LoadAllSeasonFromXML implements Command {

	@Autowired
	FootballResultsLoader loader;
	
	@Override
	public void run(String[] args) throws Exception {
		Integer season = 1949;
		while (season > 1943) {
			System.out.println("+++++++++++++++++++++LOADING SEASON: " + season);
			loader.loadResultsForSeason(season);
			season--;
		}		
	}
}
