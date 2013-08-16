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
}
