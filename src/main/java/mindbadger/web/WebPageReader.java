package mindbadger.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class WebPageReader {
	Logger logger = Logger.getLogger(WebPageReader.class);
	
	private String httpAgent;
	
	public List<String> readWebPage(String pURL) throws FileNotFoundException, IOException {
		
		logger.info("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
		logger.info("About to load web page: " + pURL + "...");
		logger.info("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
		
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
		reader.setHttpAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");
		//List<String> output = reader.readWebPage("http://www.soccerbase.com/teams/team.sd?team_id=2802&teamTabs=results");
		List<String> output = reader.readWebPage("http://www.soccerbase.com/matches/results.sd?date=1998-12-26");
		for (String line : output) {
			System.out.println(line);
		}
	}

	public String getHttpAgent() {
		return httpAgent;
	}

	public void setHttpAgent(String httpAgent) {
		this.httpAgent = httpAgent;
		System.setProperty("http.agent", httpAgent);
	}
}
