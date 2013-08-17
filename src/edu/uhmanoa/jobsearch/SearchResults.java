package edu.uhmanoa.jobsearch;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResults extends Activity {
	String mCookie;
	String mResponse;
	ListView mListOfJobsListView;
	TextView mNumberOfJobs;
	int mNumberOfJobsDisplaying;
	String mNumberOfJobsFound;
	ArrayList<Job> mListOfJobs;
	JobAdapter mAdapter;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		
		mListOfJobsListView = (ListView) findViewById(R.id.listOfJobs);
		mNumberOfJobs = (TextView) findViewById(R.id.numberOfResults);
		mListOfJobs = new ArrayList<Job>();
		
		//get the response and the cookie
		Intent thisIntent = this.getIntent();
		mCookie = thisIntent.getStringExtra(Login.COOKIE_VALUE);
		mResponse = thisIntent.getStringExtra(MainStudentMenu.SEARCH_RESPONSE_STRING);
		
		//set the text for the window
		Document doc = Jsoup.parse(mResponse);
		Elements body = doc.getElementsByTag("tbody");
		Elements header = doc.getElementsByAttributeValue("class", "pagebanner");
		Elements numbers = header.select("font");
		
		//set number of jobs and how many displaying
		mNumberOfJobsFound = numbers.get(0).text();
		mNumberOfJobsDisplaying = Integer.valueOf(numbers.get(2).text());
		mNumberOfJobs.setText(mNumberOfJobsFound + " jobs found"); 
		
		//get all of the jobs in this page view
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
		
		mAdapter = new JobAdapter(this, R.id.listOfJobs, mListOfJobs);
		mListOfJobsListView.setAdapter(mAdapter);
		
		//listen for click event
		mListOfJobsListView.setOnItemClickListener(new OnItemClickListener() {
 
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Job job = (Job) mListOfJobsListView.getItemAtPosition(position);
				String title = job.mTitle;
				Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
			}		
		});
	}
	public Job createJob(Element job) {
		Elements attributes = job.getElementsByTag("td"); //7 attributes
		//get the job title
		String jobTitle = job.select("a[href]").get(0).text();
		//get the job description preview
		job.select("a[href]").remove(); //remove the links
		String jobDescrip = attributes.get(0).text();
		jobDescrip = jobDescrip.replace("[]","");
/*		Log.w("SR", jobDescrip);*/

		//get the rest of the attributes
		String jobProgram = attributes.get(1).text();
		String jobPay = attributes.get(2).text();
		String jobCategory = attributes.get(3).text();
		String jobLocation = attributes.get(4).text();
		String jobRefNumber = attributes.get(5).text();
		String jobSkillMatch = attributes.get(6).text();
		return new Job(jobTitle, jobDescrip, jobProgram, jobPay, jobCategory, 
				jobLocation, jobRefNumber, jobSkillMatch); 
	}
}
