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
		if (args.length < 2)
			throw new IllegalArgumentException("Please supply a season");
		
		Integer season = Integer.parseInt(args[1]);
		loader.loadResultsForSeason(season);
	}
}
