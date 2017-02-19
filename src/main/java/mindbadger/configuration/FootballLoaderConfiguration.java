package mindbadger.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import mindbadger.footballresults.loader.FootballResultsLoader;
import mindbadger.footballresults.loader.FootballResultsSaver;
import mindbadger.footballresults.loader.mapping.FootballResultsMapping;
import mindbadger.footballresults.reader.web.SoccerbaseDatePageParser;
import mindbadger.footballresults.reader.web.SoccerbaseTeamPageParser;
import mindbadger.footballresults.reader.web.SoccerbaseWebPageReader;
import mindbadger.footballresultsanalyser.dao.FootballResultsAnalyserDAO;
import mindbadger.footballresultsanalyser.domain.DomainObjectFactory;
import mindbadger.footballresultsanalyser.domain.DomainObjectFactoryImpl;
import mindbadger.util.Pauser;
import mindbadger.web.WebPageReader;
import mindbadger.xml.XMLFileReader;
import mindbadger.xml.XMLFileWriter;

@Configuration
@ComponentScan ("mindbadger")
@EnableAutoConfiguration
public class FootballLoaderConfiguration {
	
	private XMLFileReader xmlFileReader;
	private XMLFileWriter xmlFileWriter;
	private FootballResultsMapping mapping;
	private DomainObjectFactory domainObjectFactory;
	private FootballResultsSaver saver;
	private FootballResultsLoader loader;
	private WebPageReader webPageReader;
	private Pauser pauser;
	private SoccerbaseTeamPageParser teamPageParser;
	private SoccerbaseDatePageParser datePageParser;
	private SoccerbaseWebPageReader reader;
	
	@Autowired
	private FootballResultsAnalyserDAO dao;

	public FootballLoaderConfiguration () {

		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		
		xmlFileReader = new XMLFileReader();
		xmlFileWriter = new XMLFileWriter();
		
		mapping = new FootballResultsMapping(
				"E:\\Mark\\appConfig\\fra_mapping_cb.xml", xmlFileReader, xmlFileWriter);
		
		domainObjectFactory = new DomainObjectFactoryImpl ();
		
		pauser = new Pauser();
		
		webPageReader = new WebPageReader ();
		//webPageReader.setHttpAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");
		webPageReader.setHttpAgent("Googlebot/2.1 (+http://www.googlebot.com/bot.html)");

		teamPageParser = new SoccerbaseTeamPageParser();
		teamPageParser.setWebPageReader(webPageReader);
		teamPageParser.setUrl("http://www.soccerbase.com/teams/team.sd?season_id={seasonNum}&amp;team_id={teamId}&amp;teamTabs=results");
		teamPageParser.setPauser(pauser);
		
		datePageParser = new SoccerbaseDatePageParser ();
		datePageParser.setDialect("soccerbase");
		datePageParser.setMapping(mapping);
		datePageParser.setUrl("http://www.soccerbase.com/matches/results.sd?date={fixtureDate}");
		datePageParser.setPauser(pauser);
		datePageParser.setWebPageReader(webPageReader);
		
		saver = new FootballResultsSaver();
		saver.setMapping(mapping);
		saver.setDialect("soccerbase");
		
		reader = new SoccerbaseWebPageReader ();
		reader.setTeamPageParser(teamPageParser);
		reader.setDatePageParser(datePageParser);
		reader.setMapping(mapping);
		
		loader = new FootballResultsLoader ();
		loader.setReader(reader);
		loader.setSaver(saver);
	}
	
	
//	@Bean
//	public PropertyPlaceholderConfigurer props() {
//		PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
//		
//		Resource resource = new FileSystemResource("file:///E:/Mark/appConfig/mongo.properties");
//		propertyPlaceholderConfigurer.setLocations(resource);
//		
//		return propertyPlaceholderConfigurer;
//	}
	
	@Bean
	public XMLFileReader xmlFileReader () {
		return xmlFileReader;
	}
	
	@Bean
	public XMLFileWriter xmlFileWriter () {
		return xmlFileWriter;
	}
	
	@Bean
	public FootballResultsMapping mapping() {
		return mapping;
	}
	
	@Bean
	public DomainObjectFactory domainObjectFactory () {
		return domainObjectFactory;
	}
	
	@Bean
	public FootballResultsSaver saver () {
		return saver;
	}
	
	@Bean
	public FootballResultsLoader loader () {
		return loader;
	}
	
	@Bean
	public WebPageReader webPageReader() {
		return webPageReader;
	}
	
	@Bean
	public Pauser pauser() {
		return pauser;
	}
	
	@Bean
	public SoccerbaseTeamPageParser teamPageParser() {
		return teamPageParser;
	}
	
	@Bean
	public SoccerbaseDatePageParser datePageParser() {
		return datePageParser; 
	}
	
	@Bean
	public SoccerbaseWebPageReader reader () {
		return reader;
	}
}
