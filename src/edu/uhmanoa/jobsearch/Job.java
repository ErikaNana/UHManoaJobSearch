package edu.uhmanoa.jobsearch;

public class Job {
	String mTitle;
	String mDescription;
	String mProgram;
	String mPay;
	String mCategory;
	String mLocation;
	String mRefNumber;
	String mSkillMatches;
	
	public static final int UHM = 1;
	public static final int UHH = 2;
	public static final int UHWO = 3;
	public static final int UHMC = 4;
	public static final int HCC = 5;
	public static final int KAP_CC = 6;
	public static final int KA_CC = 7;
	public static final int LCC = 8;
	public static final int WCC = 9;
	public static final int OTHER = 10;
	
	public Job(String title, String description, String program, String pay, String category, String location, 
			String refNumber, String skillMatches) {
		mTitle = title;
		mDescription = description;
		mProgram = program;
		mPay = pay;
		mCategory = category;
		mLocation = location;
		mRefNumber = refNumber;
		mSkillMatches = skillMatches;
	}
	
	public int whichIcon() {
		if (mLocation.equals("UH Manoa")) {
			return UHM;
		}
		if (mLocation.equals("UH Hilo")) {
			return UHH;
		}
		if (mLocation.equals("UH West Oahu")) {
			return UHWO;
		}
		if (mLocation.equals("UH Maui College")) {
			return UHMC;
		}
		if (mLocation.equals("Honolulu CC")) {
			return HCC;
		}
		if (mLocation.equals("Kapiolani CC")) {
			return KAP_CC;
		}
		if (mLocation.equals("Kauai CC")) {
			return KA_CC;
		}
		if (mLocation.equals("Leeward CC")) {
			return LCC;
		}
		if (mLocation.equals("Windward CC")) {
			return WCC;
		}
		else {
			return OTHER;
		}
	}
	
	public boolean jobOnCampus() {
		if (whichIcon() == OTHER) {
			return false;
		}
		else {
			return true;
		}
	}
}
