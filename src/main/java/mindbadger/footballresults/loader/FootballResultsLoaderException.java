package mindbadger.footballresults.loader;

public class FootballResultsLoaderException extends RuntimeException {
	private static final long serialVersionUID = 6763661656254528431L;

	public FootballResultsLoaderException(Exception e) {
		super(e);
	}

	public FootballResultsLoaderException(String message) {
		super(message);
	}
}
