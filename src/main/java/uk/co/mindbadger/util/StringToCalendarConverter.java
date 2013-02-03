package uk.co.mindbadger.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.co.mindbadger.footballresults.reader.FootballResultsReaderException;

public class StringToCalendarConverter {
	public static Calendar convertDateStringToCalendar(String dateString) {
		
		if (dateString == null || "".equals(dateString)) {
			return null;
		}
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			cal.setTime(sdf.parse(dateString));
			return cal;
		} catch (ParseException e) {
			// TODO Need to add a test to deal with being unable to parse the
			// date
			throw new FootballResultsReaderException(e);
		}
	}
}
