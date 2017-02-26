package mindbadger.footballresults.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractFootballResultsLoaderMapping {
	
	Map<String, Dialect> dialects = new HashMap<String, Dialect>();

	/*
	 *  ******* PLEASE ENSURE THE CONSTRUCTOR OF THE CONCRETE CLASS IS RESPONSIBLE FOR INITIAL POPULATION OF THE DIALECTS MAP
	 */
	
	public List<String> getIncludedDivisions(String dialectName) {
		Dialect dialect = dialects.get(dialectName);
		if (dialect == null) {
			throw new FootballResultsLoaderException("There is no configuration for " + dialectName + " in your mapping file");
		}
		
		return dialects.get(dialectName).getIncludedDivisions();
	}
	
	public void addDivisionMapping(String dialectName, String sourceDivId, String fraDivId) {
		dialects.get(dialectName).getDivisionMappings().put(sourceDivId, fraDivId);
		dialects.get(dialectName).getOrderedListOfDivisions().add(fraDivId);
		for (String s : dialects.get(dialectName).getOrderedListOfDivisions()) {
			System.out.println("ADD " + fraDivId + " : " + s);
		}
	}

	public int getIndexForDivision(String dialectName, String fraDivId) {
		for (String s : dialects.get(dialectName).getOrderedListOfDivisions()) {
			System.out.println("GET " + fraDivId + " : " + s);
		}
		
		return dialects.get(dialectName).getOrderedListOfDivisions().indexOf(fraDivId);
	}
	
	public List<String> getOrderedListOfDivisions(String dialectName) {
		return dialects.get(dialectName).getOrderedListOfDivisions();
	}
	
	public Map<String, String> getDivisionMappings(String dialectName) {
		return dialects.get(dialectName).getDivisionMappings();
	}
	
	public Map<String, String> getTeamMappings(String dialectName) {
		return dialects.get(dialectName).getTeamMappings();
	}
	
	public void addTeamMapping(String dialectName, String sourceTeamId, String fraTeamId) {
		dialects.get(dialectName).getTeamMappings().put(sourceTeamId, fraTeamId);
	}
	
	public abstract void saveMappings() throws FootballResultsLoaderException;
	
	class Dialect {
		private String name;
		private List<String> includedDivisions = new ArrayList<String>();
		private Map<String, String> divisionMappings = new HashMap<String, String>();
		private Map<String, String> teamMappings = new HashMap<String, String>();
		private List<String> orderedListOfDivisions = new ArrayList<String> ();

		public Dialect(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public List<String> getIncludedDivisions() {
			return includedDivisions;
		}

		public Map<String, String> getDivisionMappings() {
			return divisionMappings;
		}

		public Map<String, String> getTeamMappings() {
			return teamMappings;
		}

		public List<String> getOrderedListOfDivisions() {
			return orderedListOfDivisions;
		}
	}
}
