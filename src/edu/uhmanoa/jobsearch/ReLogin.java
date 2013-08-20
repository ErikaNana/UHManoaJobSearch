package edu.uhmanoa.jobsearch;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**This class logins, gets the cookie, and goes back to the class that called it*/
public class ReLogin extends Dialog implements OnClickListener{
	String mNewCookieValue;
	Button mLogin;
	Context mContext;
	String mLoginResponse;
	String mSearchResponse;
	TextView mErrorText;
	EditText mUserNameWindow;
	EditText mPasswordWindow;
	String mUserName;
	String mPassword;
	int mCallingClass;
	
	public static final int SEARCH_RESULT_CLASS = 1;
	public static final int MAIN_STUDENT_MENU_CLASS = 2;
	
	public ReLogin(Context context, int callingClass, String searchResponse) {
		super(context);
		mContext = context;
		mCallingClass = callingClass;
		setTitle("ReLogin");
		setContentView(R.layout.login);
		
		mErrorText = (TextView) findViewById(R.id.errorDisplay);
		mLogin = (Button) findViewById(R.id.loginButton);
		mUserNameWindow = (EditText) findViewById(R.id.inputUserName);
		mPasswordWindow = (EditText) findViewById(R.id.inputPassword);
		mLogin.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.loginButton) {
			mUserName = mUserNameWindow.getText().toString();
			mPassword = mPasswordWindow.getText().toString();
	        connectToWebsite connect = new connectToWebsite();
			connect.execute(new String[] {Login.POST_LOGIN_URL, mUserName, mPassword});
		}
	}
	private class connectToWebsite extends AsyncTask <String, Void, String>{
		
		@Override
		protected String doInBackground(String... urls) {
			Log.w("Login", "connecting");
			Document doc = null;
			try {
				//post to the login form
				Connection.Response res = Jsoup.connect(urls[0])
					    .data("module", "student")
					    .timeout(3000)
/*		    			.data("userName", urls[1])
		    			.data("userPassword", urls[2])*/
					    .data("userName", "enana")
		    			.data("userPassword", "fUcktbs!1!")
					    .method(Method.POST)
					    .execute();

				 	doc = res.parse();
				 	mLoginResponse = doc.toString();
				 	
				 	//get the cookie
					mNewCookieValue = res.cookie(Login.COOKIE_TYPE);
					Log.w("search", "response:  " + mNewCookieValue);
					
			} 
			catch (Exception e) { //catch all exceptions
				e.printStackTrace();
			}
			return mLoginResponse;
		}
	    @Override
	    protected void onPostExecute(String response) {
	    	if(response != null) {
	    		if (mErrorText.getVisibility() == View.VISIBLE) {
	    			mErrorText.setText("");
	    			mErrorText.setVisibility(View.GONE);
	    			//go back to the activity that called this error
	    			Intent intent = null;
	    			switch(mCallingClass) {
		    			case SEARCH_RESULT_CLASS:{
		    				intent = new Intent(mContext, SearchResults.class);
			    			intent.putExtra(Login.COOKIE_VALUE, mNewCookieValue);
			    			intent.putExtra(MainStudentMenu.SEARCH_RESPONSE_STRING, mSearchResponse);
			    			intent.putExtra(Login.LOGIN_RESPONSE_STRING, mLoginResponse);
			    			break;
		    			}
		    			case MAIN_STUDENT_MENU_CLASS:{
		    				intent = new Intent(mContext,MainStudentMenu.class);
		    				intent.putExtra(Login.COOKIE_VALUE, mNewCookieValue);
		    				intent.putExtra(Login.LOGIN_RESPONSE_STRING, mLoginResponse);
		    				break;
		    			}
		    			
	    			}
	    			mContext.startActivity(intent);
	    		}
	    	}
	    	else { //error
	    		mErrorText.setVisibility(View.VISIBLE);
	    		mErrorText.setText("Problem with logging in. Please try again.");
	    	}
	    }
	}
}
