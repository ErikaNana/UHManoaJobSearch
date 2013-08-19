package edu.uhmanoa.jobsearch;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SearchResults extends Activity {
	String mCookie;
	String mSearchResponse;
	String mLoginResponse;
	ListView mListOfJobsListView;
	TextView mNumberOfJobs;
	int mNumberOfJobsDisplaying;
	String mNumberOfJobsFound;
	ArrayList<Job> mListOfJobs;
	JobAdapter mAdapter;
	String mNextLink;
	ProgressDialog pd;
	Job mJobLookingAt;
	LinearLayout mFullDescripHolder;
	
	public static final String DOMAIN_NAME = "https://sece.its.hawaii.edu";
	public static final int GENERAL_ERROR = 1;
	public static final int NO_RESULT_FOUND_ERROR = 2;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		
		mListOfJobsListView = (ListView) findViewById(R.id.listOfJobs);
		mNumberOfJobs = (TextView) findViewById(R.id.numberOfResults);
		mListOfJobs = new ArrayList<Job>();
		mFullDescripHolder = (LinearLayout) findViewById(R.id.fullDescriptionWindow);
		
		//get the response and the cookie
		Intent thisIntent = this.getIntent();
		mCookie = thisIntent.getStringExtra(Login.COOKIE_VALUE);
		mSearchResponse = thisIntent.getStringExtra(MainStudentMenu.SEARCH_RESPONSE_STRING);
		//need this in case of error
		mLoginResponse = thisIntent.getStringExtra(Login.LOGIN_RESPONSE_STRING);
		
		//parse the header
		Document doc = Jsoup.parse(mSearchResponse);
		Elements header = doc.getElementsByAttributeValue("class", "pagebanner");
		Elements numbers = header.select("font");
			
		if (numbers.isEmpty()) {
			//check if in detailed listing
			if(mSearchResponse.contains("Detailed")) {
				Log.w("SR", "DETAILED LISTING!!!!");
				//show full description
				showFullDescription(mSearchResponse);
			}
			//show error dialog
			showErrorDialog(NO_RESULT_FOUND_ERROR);
		}
	
		else {
			//set number of jobs and how many displaying
			mNumberOfJobsFound = numbers.get(0).text();
			mNumberOfJobsDisplaying = Integer.valueOf(numbers.get(2).text());
			
			//get page links
			Elements pageLinks = doc.getElementsByAttributeValue("class", "pagelinks");
			Elements links = pageLinks.select("a[href]");
			
			//check if there was any search keywords
			Elements searchForm = doc.getElementsByAttributeValue("name", "keywords");
			//get the search term
			String keyword = searchForm.attr("value");
			//set the top right text
			if (!keyword.isEmpty()) {
				mNumberOfJobs.setText(mNumberOfJobsFound + " jobs found for \"" + keyword
									 + "\"");
			}
			else {
				mNumberOfJobs.setText(mNumberOfJobsFound + " jobs found"); 
			}
			
			//get the link of the next page
			String nextLink = links.get(0).attr("href");
			mNextLink = DOMAIN_NAME + nextLink;

			//get the jobs in this initial page view
			getJobs(mSearchResponse);
			
			//set the adapter
			mAdapter = new JobAdapter(this, R.id.listOfJobs, mListOfJobs);
			mListOfJobsListView.setAdapter(mAdapter);
			
			//listen for click event
			mListOfJobsListView.setOnItemClickListener(new OnItemClickListener() {
	 
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					mJobLookingAt = (Job) mListOfJobsListView.getItemAtPosition(position);
					launchGetDescription(mJobLookingAt);
				}		
			});			
		}
	}
	public Job createJob(Element job) {
		Elements attributes = job.getElementsByTag("td"); //7 attributes
		//get the job title
		String jobTitle = job.select("a[href]").get(0).text();
		//get the full description link
		String jobFullDescripLink = DOMAIN_NAME + job.select("a[href]").get(0).attr("href");

		//get the job description preview
		job.select("a[href]").remove(); //remove the links
		String jobDescrip = attributes.get(0).text();
		jobDescrip = jobDescrip.replace("[]","");

		//get the rest of the attributes
		String jobProgram = attributes.get(1).text();
		String jobPay = attributes.get(2).text();
		String jobCategory = attributes.get(3).text();
		String jobLocation = attributes.get(4).text();
		String jobRefNumber = attributes.get(5).text();
		String jobSkillMatch = attributes.get(6).text();
		return new Job(jobTitle, jobDescrip, jobProgram, jobPay, jobCategory, 
				jobLocation, jobRefNumber, jobSkillMatch, jobFullDescripLink); 
	}
	
	public void getJobs(String response) {
		//get all of the jobs in this page view
		Document doc = Jsoup.parse(mSearchResponse);
		Elements body = doc.getElementsByTag("tbody");
		Element listOfJobs = body.get(3);
		Elements groupOfJobs = listOfJobs.children(); //25 jobs
		String jobText = "";
		for (Element job: groupOfJobs) {
			Job newJob = createJob(job);
			jobText = jobText + newJob.mTitle + "\n" + newJob.mDescription + "\n" + 
					  newJob.mProgram+ "\n" + newJob.mPay + "\n" + 
					  newJob.mCategory + "\n" + newJob.mLocation + "\n" +
				      newJob.mRefNumber + "\n" + newJob.mSkillMatches + "\n" + "\n";
			mListOfJobs.add(newJob);
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	public void launchGetDescription(Job job) {
		pd = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        pd.setTitle("Connecting...");
        //make this a random fact later.  haha.
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
		GetFullDescription getDescription = new GetFullDescription();
		getDescription.execute(new String[] {job.mFullDescripLink});
	}
	private class GetFullDescription extends AsyncTask <String, Void, String>{
		@Override
		protected String doInBackground(String... html) {
				Document doc = null;
				try {
					doc = Jsoup.connect(html[0])
							   .timeout(5000)
							   .cookie(Login.COOKIE_TYPE, mCookie)
							   .get();
					mSearchResponse = doc.toString();
					return mSearchResponse;
					/*//Log.w("MSTD", "response:  " + doc.text());*/
				} catch (Exception e) { 
					Log.e("SR", "EXCEPTION!!!!");
					Log.e("MSM", e.getMessage());
					showErrorDialog(GENERAL_ERROR);
				}
			return null;
		}
	    @Override
	    protected void onPostExecute(String response) {
	    	pd.dismiss();
	    	if (response != null) {
	    		showFullDescription(response);
	    	}
	    }
	}
	
	public void showErrorDialog(int type) {
		AlertDialog.Builder builder=  new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
									      .setTitle(R.string.app_name);
		switch(type) {
			case GENERAL_ERROR:{
				builder.setMessage("An error has occured.  Try again?");
				builder.setPositiveButton("Yes", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						launchGetDescription(mJobLookingAt);
					}
				});
				builder.setNegativeButton("No", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
				break;
			}
			case NO_RESULT_FOUND_ERROR:{
				builder.setMessage("No job matches that criteria");
				builder.setPositiveButton("Go back to search", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//go back to search
						Intent intent = new Intent(getBaseContext(), MainStudentMenu.class);
						intent.putExtra(Login.COOKIE_VALUE, mCookie);
						intent.putExtra(Login.LOGIN_RESPONSE_STRING, mLoginResponse);
					    startActivity(intent);
					}
				});
				builder.setNegativeButton("Quit", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
			}
		}

		AlertDialog dialog = builder.create();
		//so dialog doesn't get closed when touched outside of it
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();
	}
	/**Get the details for the full job listing*/
	public LinkedHashMap<String,String> getDetails(String response) {
		//Linked HashMap because still want to preserve order
		LinkedHashMap<String,String> listingDetails = new LinkedHashMap<String,String>();
		//get the details
		Document doc = Jsoup.parse(response);
		Elements rows = doc.getElementsByTag("tr");
		for (Element row: rows) {
			if (row.children().size() == 3) {
				String category = row.getElementsByAttributeValue("width", "20%").text();
				Elements detail = row.getElementsByAttributeValue("width", "80%");
				//transform <br> to new lines 
				detail.select("br").append("\\n");
				String detailString = detail.text().replaceAll("\\\\n", "\n");
				
				//fix inconsistent pay string
				if (category.equals("Pay Rate")) {
					if (!detailString.contains("$")) {
						detailString = "$" +detailString;
					}
					int decimal = detailString.indexOf(".");
					//indexing starts at 0
					if (detailString.length() < decimal + 3) {
						detailString = detailString + "0";
					}
				}
				//get the skill matches and if user has skill
				if (category.equals("Skill Matches")) {
					detailString = "";
			    	//only do this if there is a skill matches table
			    	Elements table = row.select("tbody");
			    	//get elements with static class
			    	Elements skillMatches = table.select("td");
			    	//get the skills and if user has them or not
			    	String skillName = null;
			    	Boolean hasSkill = false;
			    	for (int i = 0; i < skillMatches.size(); i++) {
			    		//odd number is skill
			    		if ((i %2) == 0) {
			    			skillName = skillMatches.get(i).text();
			    		}
			    		if ((i % 2) == 1) {
			    			//check what kind of picture it is
			    			String url = skillMatches.get(i).select("img").attr("src");
			    			if (url.contains("on")) {
			    				hasSkill = true;
			    			}
			    			detailString = detailString + skillName + ": " + "[" + hasSkill + "]" + " ";
			    			//reset
			    			hasSkill = false;
			    		}
			    	}					
				}
				//add it to HashMap
				listingDetails.put(category, detailString);
				Log.w("SR", category + ": " + detailString);
			}
		}
		return listingDetails;
	}
	public void showFullDescription(String response) {
		Dialog fullDescription = new FullDescriptionDialog(this,
				getDetails(response));
		fullDescription.show();
	}
}
