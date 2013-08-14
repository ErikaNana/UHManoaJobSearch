package edu.uhmanoa.jobsearch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class MainStudentMenu extends Activity {
	String mWelcomeText;
	String mResponse;
	String mCookie;
	String mUserName;
	TextView mWelcomeUser;
	TextView mResponseViewer;
	
	public static final String COOKIE_TYPE = "JSESSIONID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_student_menu);
		
		Intent thisIntent = this.getIntent();
		//get the cookie for this session
		mCookie = thisIntent.getStringExtra(Login.COOKIE_VALUE);
		mResponse = thisIntent.getStringExtra(Login.RESPONSE_STRING);
		
		Log.w("search", "cookie value:  " + mCookie);
		mWelcomeUser = (TextView) findViewById(R.id.welcomeUser);
		mResponseViewer = (TextView) findViewById(R.id.htmlViewer);
		
		//set the text to display
		mResponseViewer.setMovementMethod(new ScrollingMovementMethod());
		mResponseViewer.setText(mResponse);
		
		ParseHtml parseHtml = new ParseHtml();
		parseHtml.execute(new String[] {mResponse});
	}
	
	private class ParseHtml extends AsyncTask <String, Void, String>{
		
		@Override
		protected String doInBackground(String... html) {
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
	    @Override
	    protected void onPostExecute(String response) {
	    	Log.w("MSM", "text:  " + response);
			mWelcomeUser.setText(mUserName);
	    }
		
	}
}
