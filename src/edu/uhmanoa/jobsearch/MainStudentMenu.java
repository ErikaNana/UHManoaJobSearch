package edu.uhmanoa.jobsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class MainStudentMenu extends Activity {
	String mWelcomeText;
	String mResponse;
	String mCookie;
	
	TextView mWelcomeUser;
	TextView mResponseViewer;
	
	public static final String WELCOME_START = "Welcome, ";
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
		
		mWelcomeUser.setText(WELCOME_START);
	}
}
