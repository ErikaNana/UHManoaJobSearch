package edu.uhmanoa.jobsearch;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class JobSearch extends Activity implements OnClickListener{
	TextView view;
	Button button;
	String cookie;
	public static final String LOGIN_URL = "https://sece.its.hawaii.edu/sece/stdLogin.do";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_search);
		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.job_search, menu);
		return true;
	}
	
	private class connectToWebsite extends AsyncTask <String, Void, String>{

		@Override
		protected String doInBackground(String... urls) {
			String response = null;
			for (String url : urls) {
				Document doc = null;
			    try {
			    	doc = Jsoup.connect(url)
			    			  .data("module", "student")
			    			  .data("userName", "enana")
			    			  .data("userPassword", "fUcktbs!1!")
			    			  .post();
				    response = doc.toString();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return response;
		}
		
	    @Override
	    protected void onPostExecute(String result) {
	      view = (TextView) findViewById(R.id.textView);
	      view.setMovementMethod(new ScrollingMovementMethod());
	      view.setText(result);
	    }
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.button:{
				connectToWebsite connect = new connectToWebsite();
				connect.execute(new String[] {LOGIN_URL});
			}
		}
	}
	
	

}
