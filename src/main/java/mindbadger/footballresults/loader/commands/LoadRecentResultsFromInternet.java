package mindbadger.footballresults.loader.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresults.loader.FootballResultsLoader;

@Component
public class LoadRecentResultsFromInternet implements Command {

	@Autowired
	private FootballResultsLoader loader;
	
	@Override
	public void run(String[] args) throws Exception {
		loader.loadResultsForRecentlyPlayedFixtures();
	}
}
