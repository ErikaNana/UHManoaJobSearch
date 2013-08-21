package edu.uhmanoa.jobsearch.Utils;

import java.util.HashMap;


public class Utils{

	public static String getFromProgramHashMap(String find) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("All Programs","0");
		map.put("Full Time (FT)","7");
		map.put("Part Time (PT)","10");
		map.put("UH","1");
		map.put("UH & FWS","2");
		map.put("FWS","3");
		map.put("NONUH","4");
		map.put("COOP","5");
		map.put("INTERN","6");
		
		return returnValue(map, find);
	}	
	
	public static String getFromIslandOptionMap(String find) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("All Locations","");
		map.put("Oahu","1");
		map.put("Hawaii","2");
		map.put("Kauai","3");
		map.put("Lanai","4");
		map.put("Maui","5");
		map.put("Molokai","6");
		map.put("Other","7");
		
		return returnValue(map, find);
	}
	
	public static String getFromCampusOptionMap(String find) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("All Campuses","");
		map.put("UH Manoa","1");
		map.put("UH Hilo","2");
		map.put("UH West Oahu","3");
		map.put("UH Maui College","9");
		map.put("Hawaii CC","4");
		map.put("Honolulu CC","5");
		map.put("Kapiolani CC","6");
		map.put("Kauai CC","7");
		map.put("Leeward CC","8");
		map.put("Windward CC","10");
		
		return returnValue(map,find);
	}
	public static String getFromCategoryOptionMap(String find) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("All Categories","");
		map.put("Accounting/Fiscal","1");
		map.put("Administrative/Clerical","2");
		map.put("Agriculture","3");
		map.put("Aquaculture","30");
		map.put("Architecture","4");
		map.put("Art","49");
		map.put("Automotive","31");
		map.put("Business","5");
		map.put("Child Care","50");
		map.put("Communication","6");
		map.put("Computer","7");
		map.put("Construction Trades","53");
		map.put("Culinary Arts","132");
		map.put("Education","8");
		map.put("Electrical","33");
		map.put("Electronics","32");
		map.put("Emergency Medical Services","133");
		map.put("Engineering","9");
		map.put("Fashion Tech","54");
		map.put("Finance","10");
		map.put("Fine Arts","152");
		map.put("Food Service","11");
		map.put("Graphic Arts","12");
		map.put("Groundskeeping/Janitorial","13");
		map.put("Health/Medical","14");
		map.put("Human Resources","134");
		map.put("Human Services","55");
		map.put("Janitorial/Custodial","94");
		map.put("Journalism","15");
		map.put("Laborer","16");
		map.put("Language","17");
		map.put("Law Enforcement/Judicial","52");
		map.put("Library","18");
		map.put("Mail Processing","93");
		map.put("Marketing","19");
		map.put("Mathematics Statistics","173");
		map.put("Media","51");
		map.put("Miscellaneous","20");
		map.put("Performing Arts","21");
		map.put("Receptionist","22");
		map.put("Research","23");
		map.put("Retail","24");
		map.put("Science","25");
		map.put("Social Services","26");
		map.put("Sports/Recreation","172");
		map.put("Student Activities","135");
		map.put("Sustainability","193");
		map.put("Switch Board Operator","92");
		map.put("Technical/Trades","27");
		map.put("Tourism/Hospitality","28");
		map.put("Tutoring","112");
		
		return returnValue(map,find);
	}
	
	public static String getFromClassificationOptionMap(String find) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("All Classifications","");
		map.put("Summer Only","S");
		map.put("Nights","N");
		map.put("Weekends","W");
		map.put("Nights & Weekends","P");
		
		return returnValue(map,find);
	}
	public static String getFromPostingsOptionMap(String find) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("All Postings","");
		map.put("Today","today");
		map.put("Yesterday","yesterday");
		map.put("Last week","lastweek");
		map.put("Last month","lastmonth");
		
		return returnValue(map,find);
	}
	public static String getFromEligibilityOptionMap(String find) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("All Jobs","");
		map.put("Jobs that I am eligible for","true");
		return returnValue(map,find);
	}
	public static String returnValue(HashMap<String,String> map, String find) {
		if (map.containsKey(find)) {
			return map.get(find);
		}
		else {
			return "";
		}
	}
}
