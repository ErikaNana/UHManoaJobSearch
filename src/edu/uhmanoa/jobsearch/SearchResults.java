package edu.uhmanoa.jobsearch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class SearchResults extends Activity {
	String mCookie;
	String mResponse;
	TextView mResponseText;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		//get the response and the cookie
		Intent thisIntent = this.getIntent();
		mCookie = thisIntent.getStringExtra(Login.COOKIE_VALUE);
		mResponse = thisIntent.getStringExtra(MainStudentMenu.SEARCH_RESPONSE_STRING);
		
		mResponseText = (TextView) findViewById(R.id.responseTextView);
		mResponseText.setMovementMethod(new ScrollingMovementMethod());

		//set the text for the window
		Document doc = Jsoup.parse(mResponse);
		Elements body = doc.getElementsByTag("tbody");
/*		Element header = body.get(0);*/
		Element listOfJobs = body.get(3);
		Elements groupOfJobs = listOfJobs.children(); //25 jobs
		String jobText = "";
		for (Element job: groupOfJobs) {
			Job newJob = createJob(job);
			jobText = jobText + newJob.mTitle + "\n" + newJob.mDescription + "\n" + 
					  newJob.mProgram+ "\n" + newJob.mPay + "\n" + 
					  newJob.mCategory + "\n" + newJob.mLocation + "\n" +
				      newJob.mRefNumber + "\n" + newJob.mSkillMatches + "\n" + "\n";
			mResponseText.setText(jobText);
		}		
	}
	public Job createJob(Element job) {
		Elements attributes = job.getElementsByTag("td"); //7 attributes
		//get the job title
		String jobTitle = job.select("a[href]").get(0).text();
		//get the job description preview
		job.select("a[href]").remove(); //remove the links
		String jobDescrip = attributes.get(0).text();
		jobDescrip = jobDescrip.replace("[]","");
		Log.w("SR", jobDescrip);

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
