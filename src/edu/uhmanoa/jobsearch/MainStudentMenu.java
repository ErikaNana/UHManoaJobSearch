package edu.uhmanoa.jobsearch;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainStudentMenu extends Activity implements OnClickListener, OnItemSelectedListener {
	String mWelcomeText;
	String mResponse;
	String mCookieValue;
	String mUserName;
	TextView mWelcomeUser;
	Button mSearchButton;
	ProgressDialog pd;
	
	EditText mSearchBox;
	EditText mJobNumberBox;
	
	Spinner mJobProgramSpinner;
	Spinner mLocationsSpinner;
	Spinner mCampusSpinner;
	Spinner mCategorySpinner;
	Spinner mClassificationSpinner;
	Spinner mPostingsSpinner;
	Spinner mEligibilitySpinner;
	
	/**Option that is selected from spinners that will be posted*/
	String mOptionJobProgram;
	String mOptionIslandLocation;
	String mOptionCampusLocation;
	String mOptionCategory;
	String mOptionClassification;
	String mOptionPostings;
	String mOptionsEligibility;
	
	/**Final input values before posting*/
	String mKeywords;
	String mJobNumber;
	
	
	public static final String COOKIE_TYPE = "JSESSIONID";
	public static final String JOB_SEARCH_POST_URL = "https://sece.its.hawaii.edu/sece/stdJobSearchAction.do";
	public static final String SEARCH_RESPONSE_STRING = "search response string";
	
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
		mCookieValue = thisIntent.getStringExtra(Login.COOKIE_VALUE);
		mResponse = thisIntent.getStringExtra(Login.RESPONSE_STRING);
		
		mUserName = "";
		//Log.w("search", "cookie value:  " + mCookieValue);
		mWelcomeUser = (TextView) findViewById(R.id.welcomeUser);
		mSearchButton = (Button) findViewById(R.id.searchButton);
		mSearchButton.setOnClickListener(this);
		//inflate and set default values
		mSearchBox = (EditText) findViewById(R.id.searchBox);
		mJobNumberBox = (EditText) findViewById(R.id.jobNumberInput);
		mKeywords = "";
		mJobNumber = "";
		
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
			if (mWelcomeUser.getText().toString().isEmpty()) {
				//Log.w("MSM", "User name is empty");
				Document doc = Jsoup.parse(html[0]);
				Element text = doc.body();
				//since the name is bolded, get the elements that are bolded
				Elements strongElements = text.select("strong");
				//name is the first element
				Element nameElement = strongElements.first();
				String nameHTML = nameElement.toString();
				//parse the HTML
				mUserName = Jsoup.parse(nameHTML).text();
				//Log.w("MSM", "mUserName is:  " + mUserName);
				return mUserName;
			}
			else { //post the search criteria
				Document doc = null;
				try {
					doc = Jsoup.connect(html[0])
								   .timeout(5000)
								   .cookie(COOKIE_TYPE, mCookieValue)
								   .data(getSearchMap())
								   .post();
					mResponse = doc.toString();
					/*//Log.w("MSTD", "response:  " + doc.text());*/
				} catch (IOException e) {
					Log.w("MSM", "IO EXCEPTION:  " + e.getMessage());
					e.printStackTrace();
				}
			}
			return null;
		}
	    @Override
	    protected void onPostExecute(String response) {
	    	if (mWelcomeUser.getText().toString().isEmpty()) {
		    	//Log.w("MSM", "text:  " + response);
				mWelcomeUser.setText(mUserName);
	    	}
	    	if (pd != null) {
		    	pd.dismiss();
		    	//start the search activity
		    	launchSearchResults();
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
		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		switch (parent.getId()) {
			case R.id.jobProgramSpinner:{
				//Log.w("MSM", "Job program spinner");
				String option = (String) parent.getItemAtPosition(pos);
				//Log.w("MSM", "item:  " + option);
				mOptionJobProgram = Utils.getFromProgramHashMap(option);
				//Log.w("MSM", "value:  " + mOptionJobProgram);
				break;
			}
			case R.id.islandLocationSpinner:{
				//Log.w("MSM", "island location spinner");
				String option = (String) parent.getItemAtPosition(pos);
				//Log.w("MSM", "item:  " + option);
				mOptionIslandLocation = Utils.getFromIslandOptionMap(option);
				//Log.w("MSM", "value:  " + mOptionIslandLocation);
				break;
			}
			case R.id.campusLocationSpinner:{
				//Log.w("MSM", "campus location spinner");
				String option = (String) parent.getItemAtPosition(pos);
				//Log.w("MSM", "item:  " + option);
				mOptionCampusLocation = Utils.getFromCampusOptionMap(option);
				//Log.w("MSM", "value:  " + mOptionCampusLocation);
				break;
			}
			case R.id.categorySpinner:{
				//Log.w("MSM", "category spinner");
				String option = (String) parent.getItemAtPosition(pos);
				//Log.w("MSM", "item:  " + option);
				mOptionCategory = Utils.getFromCategoryOptionMap(option);
				//Log.w("MSM", "value:  " + mOptionCategory);
				break;
			}
			case R.id.classificationSpinner:{
				//Log.w("MSM", "classification spinner");
				String option = (String) parent.getItemAtPosition(pos);
				//Log.w("MSM", "item:  " + option);
				mOptionClassification = Utils.getFromClassificationOptionMap(option);
				//Log.w("MSM", "value:  " + mOptionClassification);
				break;
			}
			case R.id.postingsSpinner:{
				//Log.w("MSM", "postings spinner");
				String option = (String) parent.getItemAtPosition(pos);
				//Log.w("MSM", "item:  " + option);
				mOptionPostings = Utils.getFromPostingsOptionMap(option);
				//Log.w("MSM", "value:  " + mOptionPostings);
				break;
			}
			case R.id.eligibilitySpinner:{
				//Log.w("MSM", "eligibility spinner");
				String option = (String) parent.getItemAtPosition(pos);
				//Log.w("MSM", "item:  " + option);
				mOptionsEligibility = Utils.getFromEligibilityOptionMap(option);
/*				Log.w("MSM", "value:  " + mOptionsEligibility);*/
				break;
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.searchButton) {
			//get the params
			mKeywords = mSearchBox.getText().toString();
			mJobNumber = mJobNumberBox.getText().toString();
			//post the params
			ParseHtml parseHtml = new ParseHtml();
			parseHtml.execute(new String[] {JOB_SEARCH_POST_URL});
			pd = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            pd.setTitle("Connecting...");
            //make this a random fact later.  haha.
            pd.setMessage("Please wait.");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
		}		
	}
	public HashMap<String,String> getSearchMap(){
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("action", "search");
		map.put("keywords", mKeywords);
		map.put("program", mOptionJobProgram);
		map.put("island", mOptionIslandLocation);
		map.put("campus", mOptionCampusLocation);
		map.put("categories", mOptionCategory);
		map.put("specialClassification", mOptionClassification);
		map.put("postSince", mOptionPostings);
		map.put("limitEligible", mOptionsEligibility);
		map.put("jobNumber", mJobNumber);
		map.put("locArea", "stdMainMenu");
		return map;
	}
	
	public void launchSearchResults() {
		Intent launchStudentMenu = new Intent(this,SearchResults.class);
		launchStudentMenu.putExtra(Login.COOKIE_VALUE, mCookieValue);
		launchStudentMenu.putExtra(SEARCH_RESPONSE_STRING, mResponse);
    	startActivity(launchStudentMenu);
	}
}
