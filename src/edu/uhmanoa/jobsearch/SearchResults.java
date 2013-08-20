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
	Document doc;
	
	public static final String DOMAIN_NAME = "https://sece.its.hawaii.edu";
	public static final int GENERAL_ERROR = 1;
	public static final int NO_RESULT_FOUND_ERROR = 2;
	public static final int EXPIRED_COOKIE_ERROR = 3;
	
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
		doc = Jsoup.parse(mSearchResponse);
		Elements header = doc.getElementsByAttributeValue("class", "pagebanner");
		Elements numbers = header.select("font");
			
		if (numbers.isEmpty()) {
			//check if in detailed listing
			if(mSearchResponse.contains("Detailed")) {
				Log.w("SR", "DETAILED LISTING!!!!");
				//show full description
				showFullDescription(mSearchResponse);
			}
			if (mSearchResponse.contains("inactivity")) {
				Log.w("SR", "inactivity" + mSearchResponse);
				showErrorDialog(EXPIRED_COOKIE_ERROR);
			}
			//show error dialog
			if (mSearchResponse.contains("Nothing")) {
				showErrorDialog(NO_RESULT_FOUND_ERROR);
			}
			else {
				showErrorDialog(GENERAL_ERROR);
			}
		}
	
		else {
			//set number of jobs and how many displaying
			mNumberOfJobsFound = numbers.get(0).text();
			mNumberOfJobsDisplaying = Integer.valueOf(numbers.get(2).text());
			
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
			
			//get the jobs in this initial page view
			getJobs(mSearchResponse);
			
			//get the rest of the jobs...do this more elegantly later
			if (checkNextLink(mSearchResponse)) {
				//remember this runs in the background
				ClickLink getDescription = new ClickLink();
				getDescription.execute(new String[] {mNextLink, "WOOFWOOF"});
			}
			//set the adapter
			mAdapter = new JobAdapter(this, R.id.listOfJobs, mListOfJobs);
			mListOfJobsListView.setAdapter(mAdapter);
			Log.w("SR", "list number:  " + mListOfJobsListView.getCount());
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
		Log.w("SR", "mAdapter:  " + mAdapter);
		for (Element job: groupOfJobs) {
			Job newJob = createJob(job);
			mListOfJobs.add(newJob);
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	public void launchGetDescription(Job job) {
		showConnectingDialog();
		ClickLink getDescription = new ClickLink();
		getDescription.execute(new String[] {job.mFullDescripLink});
	}
	private class ClickLink extends AsyncTask <String, Void, String>{
		@Override
		protected String doInBackground(String... html) {
				Document doc = null;
				try {
					doc = Jsoup.connect(html[0])
							   .timeout(5000)
							   .cookie(Login.COOKIE_TYPE, mCookie)
							   .get();
					mSearchResponse = doc.toString();
					if (html.length == 2) {
						mSearchResponse = mSearchResponse + "WOOFWOOF";
					}
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
	    	if (response != null) {
	    		//adding jobs to the list
	    		if (mSearchResponse.contains("WOOFWOOF")) {
	    			getJobs(mSearchResponse);
	    			if (checkNextLink(response)) {
	    				ClickLink getDescription = new ClickLink();
	    				getDescription.execute(new String[] {mNextLink, "WOOFWOOF"});
	    			}
	    		}
	    		else {
	    			//getting the full description of a job
	    	    	pd.dismiss();
		    		showFullDescription(response);
	    		}
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
			case EXPIRED_COOKIE_ERROR:{
				builder.setMessage("Session has timed out");
				builder.setPositiveButton("Login again", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ReLogin reLoginDialog = new ReLogin(getBaseContext(), ReLogin.SEARCH_RESULT_CLASS, mSearchResponse);
						reLoginDialog.show();
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
			    	for (int i = 0; i < skillMatches.size(); i++) {
			    		//odd number is skill
			    		if ((i %2) == 0) {
			    			skillName = skillMatches.get(i).text();
			    		}
			    		if ((i % 2) == 1) {
			    			//check what kind of picture it is
			    			String url = skillMatches.get(i).select("img").attr("src");
			    			if (url.contains("on")) {
			    				detailString = detailString + skillName + " [ X ] " + "\n";
			    			}
			    			else {
				    			detailString = detailString + skillName + " [    ]" + "\n";			    				
			    			}
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
		Log.w("SR", "inactivity (showFull)" + response);
		if (response.contains("inactivity")) {
			showErrorDialog(EXPIRED_COOKIE_ERROR);
		}
		else {
			Dialog fullDescription = new FullDescriptionDialog(this,
					getDetails(response));
			fullDescription.show();
		}

	} 
	public boolean checkNextLink(String response) {
		Document doc = Jsoup.parse(response);
		String currentPage = doc.getElementsByTag("strong").get(1).text();
		System.out.println(currentPage);
		//get page links
		Elements pageLinks = doc.getElementsByAttributeValue("class", "pagelinks");
		Elements links = pageLinks.select("a[href]");
		int next = Integer.parseInt(currentPage) + 1;
		boolean matchValue = false;
		for (Element link: links) {
			String linkNumber = link.text();
			try {
				if (Integer.parseInt(linkNumber) == next) {
					matchValue = true;
					//get the link
					mNextLink = DOMAIN_NAME + link.getElementsByAttribute("href")
											  .get(0).attr("href");
					break;
				}
			}
			catch(Exception exception){
				continue;
			}
			
		}
		return matchValue;
	}
	public void showConnectingDialog() {
		pd = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        pd.setTitle("Connecting...");
        //make this a random fact later.  haha.
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
	}
}
