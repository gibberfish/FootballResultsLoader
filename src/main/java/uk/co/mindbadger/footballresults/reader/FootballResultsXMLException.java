package uk.co.mindbadger.footballresults.reader;

public class FootballResultsXMLException extends RuntimeException {
    private static final long serialVersionUID = 6763661656254528431L;

    public FootballResultsXMLException(Exception e) {
	super (e);
    }

    public FootballResultsXMLException(String string) {
	super (string);
    }
}
