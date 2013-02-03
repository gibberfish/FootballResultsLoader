package uk.co.mindbadger.util;

import java.util.Date;

import org.apache.log4j.Logger;

public class Pauser {
	Logger logger = Logger.getLogger(Pauser.class);
	
	public void pause(int minimumPauseInSeconds, int maximumPauseInSeconds) {
		long range = (maximumPauseInSeconds - minimumPauseInSeconds) * 1000L;

		long randomPointBetweenTheRange = ((new Date()).getTime() % range);

		long forRandomTime = randomPointBetweenTheRange + (minimumPauseInSeconds * 1000L);

		try {
			logger.debug("Pausing for " + forRandomTime + " milliseconds");
			Thread.sleep(forRandomTime);
		} catch (InterruptedException e) {
			logger.error("ERROR: Random Pause Interrupted:");
			e.printStackTrace();
		}
	}

	public void pause () {
		this.pause(5, 15);
	}
}
