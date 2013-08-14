package edu.uhmanoa.jobsearch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainStudentMenu extends Activity {
	String mWelcomeText;
	String mResponse;
	String mCookie;
	String mUserName;
	TextView mWelcomeUser;
	TextView mResponseViewer;
	
	Spinner mJobProgramSpinner;
	Spinner mLocationsSpinner;
	Spinner mCampusSpinner;
	Spinner mCategorySpinner;
	Spinner mClassificationSpinner;
	Spinner mPostingsSpinner;
	Spinner mEligibilitySpinner;
	
	public static final String COOKIE_TYPE = "JSESSIONID";
	public static final String JOB_SEARCH_POST_URL = "https://sece.its.hawaii.edu/sece/stdJobSearchAction.do";
	
	public static final int JOB_SPINNER = 1;
	public static final int ISLAND_SPINNER = 2;
	public static final int CAMPUS_SPINNER = 3;
	public static final int CATEGORY_SPINNER = 4;
	public static final int CLASSIFICATION_SPINNER = 5;
	public static final int POSTINGS_SPINNER = 6;
	public static final int ELIGIBILITY_SPINNER = 7;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_student_menu);
		
		Intent thisIntent = this.getIntent();
		//get the cookie for this session
		mCookie = thisIntent.getStringExtra(Login.COOKIE_VALUE);
		mResponse = thisIntent.getStringExtra(Login.RESPONSE_STRING);
		
		mUserName = "";
		Log.w("search", "cookie value:  " + mCookie);
		mWelcomeUser = (TextView) findViewById(R.id.welcomeUser);
		
		//get the name of the person
		ParseHtml parseHtml = new ParseHtml();
		parseHtml.execute(new String[] {mResponse});
		
		//Set up the spinners
		mJobProgramSpinner = (Spinner) findViewById(R.id.jobProgramSpinner);
		mLocationsSpinner = (Spinner) findViewById(R.id.islandLocationSpinner);
		mCampusSpinner = (Spinner) findViewById(R.id.campusLocationSpinner);
		mCategorySpinner = (Spinner) findViewById(R.id.categorySpinner);
		mClassificationSpinner = (Spinner) findViewById(R.id.classificationSpinner);
		mPostingsSpinner = (Spinner) findViewById(R.id.postingsSpinner);
		mEligibilitySpinner = (Spinner) findViewById(R.id.eligibilitySpinner);
		
		setUpSpinner(JOB_SPINNER, mJobProgramSpinner);
		setUpSpinner(ISLAND_SPINNER, mLocationsSpinner);
		setUpSpinner(CAMPUS_SPINNER, mCampusSpinner);
		setUpSpinner(CATEGORY_SPINNER, mCategorySpinner);
		setUpSpinner(CLASSIFICATION_SPINNER, mClassificationSpinner);
		setUpSpinner(POSTINGS_SPINNER, mPostingsSpinner);
		setUpSpinner(ELIGIBILITY_SPINNER, mEligibilitySpinner);
	}	
	
	private class ParseHtml extends AsyncTask <String, Void, String>{
		
		@Override
		protected String doInBackground(String... html) {
			//if no userName, get it
			if (mUserName.isEmpty()) {
				Document doc = Jsoup.parse(html[0]);
				Element text = doc.body();
				//since the name is bolded, get the elements that are bolded
				Elements strongElements = text.select("strong");
				//name is the first element
				Element nameElement = strongElements.first();
				String nameHTML = nameElement.toString();
				//parse the HTML
				mUserName = Jsoup.parse(nameHTML).text();
				return mUserName;
			}
			return null;
		}
	    @Override
	    protected void onPostExecute(String response) {
	    	if (response != null) {
		    	Log.w("MSM", "text:  " + response);
				mWelcomeUser.setText(mUserName);
	    	}
	    }
		
	}
	
	public void setUpSpinner(int type, Spinner spinner) {
		int spinnerArray = 0;
		
		switch (type){
			case JOB_SPINNER:{
				spinnerArray = R.array.jobProgramArray;
				break;
			}
			case ISLAND_SPINNER:{
				spinnerArray = R.array.islandLocationArray;
				break;
			}
			case CAMPUS_SPINNER:{
				spinnerArray = R.array.campusLocationArray;
				break;
			}
			case CATEGORY_SPINNER:{
				spinnerArray = R.array.categoryArray;
				break;
			}
			case CLASSIFICATION_SPINNER:{
				spinnerArray = R.array.classifcationsArray;
				break;
			}
			case POSTINGS_SPINNER:{
				spinnerArray = R.array.postingsArray;
				break;
			}
			case ELIGIBILITY_SPINNER:{
				spinnerArray = R.array.eligibilityArray;
				break;
			}
		}
		// Create an ArrayAdapter using the string array and spinner item layout that
		// centers the text
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        spinnerArray, R.layout.spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}
}
