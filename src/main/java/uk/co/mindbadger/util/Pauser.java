package uk.co.mindbadger.util;

import java.util.Date;

public class Pauser {
	public void pause(int minimumPauseInSeconds, int maximumPauseInSeconds) {
		long range = (maximumPauseInSeconds - minimumPauseInSeconds) * 1000L;

		long randomPointBetweenTheRange = ((new Date()).getTime() % range);

		long forRandomTime = randomPointBetweenTheRange + (minimumPauseInSeconds * 1000L);

		try {
			System.out.println("Pausing for " + forRandomTime + " milliseconds");
			Thread.sleep(forRandomTime);
		} catch (InterruptedException e) {
			System.err.println("ERROR: Random Pause Interrupted:");
			e.printStackTrace();
		}
	}

	public void pause () {
		this.pause(5, 15);
	}
}
