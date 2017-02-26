package mindbadger.footballresults.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresults.loader.FootballResultsLoader;

@Component
public class LoadSeasonFromXML implements Command {

	@Autowired
	FootballResultsLoader loader;
	
	//TODO Used "spring-xml-reader.xml" - this will need configuring
	@Override
	public void run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Integer season = Integer.parseInt(args[0]);
		loader.loadResultsForSeason(season);		
	}
}
