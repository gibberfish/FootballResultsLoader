package uk.co.mindbadger.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebPageReader {
	public List<String> readWebPage(String pURL) throws FileNotFoundException, IOException {
		ArrayList<String> results = new ArrayList <String>();
		BufferedReader in = null;
		InputStreamReader webReader = null;

		try {
			URL url = new URL(pURL);
			webReader = new InputStreamReader(url.openStream());
			in = new BufferedReader(webReader);

			String line;
			while ((line = in.readLine()) != null) {
				results.add(line);
			}
		} finally {
			// Force the close of the file...
			if (webReader != null)
				try {
					webReader.close();
				} catch (IOException e) {
					;
				}
		}

		return results;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		WebPageReader reader = new WebPageReader();
		List<String> output = reader.readWebPage("http://www.soccerbase.com/matches/results.sd?date=2012-12-26");
		for (String line : output) {
			System.out.println(line);
		}
	}

}
