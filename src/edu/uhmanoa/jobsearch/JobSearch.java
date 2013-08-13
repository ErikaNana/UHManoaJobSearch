package edu.uhmanoa.jobsearch;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JobSearch extends Activity implements OnClickListener{
	
	Button mLoginButton;
	EditText mUserName;
	EditText mPassword;
	
	public static final String LOGIN_URL = "https://sece.its.hawaii.edu/sece/stdLogin.do";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_search);
		mLoginButton = (Button) findViewById(R.id.loginButton);
		mLoginButton.setOnClickListener(this);
		
		mUserName = (EditText) findViewById(R.id.inputUserName);
		mPassword = (EditText) findViewById(R.id.inputPassword);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.job_search, menu);
		return true;
	}
	
	private class connectToWebsite extends AsyncTask <String, Integer, String>{

		@Override
		protected String doInBackground(String... urls) {
			String response = null;
			Document doc = null;
			try {
			    doc = Jsoup.connect(urls[0])
			    			.data("module", "student")
			    			.data("userName", urls[1])
			    			.data("userPassword", urls[2])
			    			.post();
				response = doc.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return response;
		}
		
	    @Override
	    protected void onPostExecute(String result) {
		    if (result.contains("Welcome")) {
		    	Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
		    }
		    else {
		    	Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
		    }
	    }
		
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.loginButton:{
				String userName = mUserName.getText().toString();
				String password = mPassword.getText().toString();
				Log.w("js", "userName:  " + userName);
				Log.w("password", "password:  " + password);
				connectToWebsite connect = new connectToWebsite();
				connect.execute(new String[] {LOGIN_URL, userName, password});
			}
		}
	}
	
	

}
