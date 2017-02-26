package mindbadger.footballresults.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresults.loader.FootballResultsLoader;

@Component
public class LoadSeasonFromInternet implements Command {

	@Autowired
	FootballResultsLoader loader;
	
	@Override
	public void run(String[] args) throws Exception {
		Integer season = Integer.parseInt(args[0]);
		loader.loadResultsForSeason(season);
	}
}
