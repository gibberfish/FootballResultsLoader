package mindbadger.footballresults.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mindbadger.football.domain.Fixture;
import mindbadger.football.repository.FixtureRepository;

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
