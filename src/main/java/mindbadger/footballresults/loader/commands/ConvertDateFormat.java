package mindbadger.footballresults.loader.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.footballresultsanalyser.domain.Fixture;
import mindbadger.footballresultsanalyser.repository.FixtureRepository;

@Component
public class ConvertDateFormat implements Command {

	@Autowired
	private FixtureRepository fixtureRepository;
	
	@Override
	public void run(String[] args) throws Exception {
		for (Fixture fixture : fixtureRepository.findAll()) {
			fixtureRepository.save(fixture);
		}
	}
}
