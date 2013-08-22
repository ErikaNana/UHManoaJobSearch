package edu.uhmanoa.jobsearch.CustomDialogs;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.uhmanoa.jobsearch.R;
import edu.uhmanoa.jobsearch.UI.Login;
import edu.uhmanoa.jobsearch.UI.SearchForm;
import edu.uhmanoa.jobsearch.UI.SearchResults;

/**This class logins, gets the cookie, and goes back to the class that called it*/
public class ReLoginDialog extends Dialog implements OnClickListener{
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
	ProgressDialog pd;
	int mCallingClass;
	
	public static final int SEARCH_RESULT_CLASS = 1;
	public static final int SEARCH_FORM_CLASS = 2;
	
	public ReLoginDialog(Context context, int callingClass, String searchResponse) {
		super(context);
		mContext = context;
		mCallingClass = callingClass;
		mSearchResponse = searchResponse;
		
		setTitle("Session has expired.  Please login.");
		setContentView(R.layout.relogin);
		
		mErrorText = (TextView) findViewById(R.id.errorDisplay);
		mLogin = (Button) findViewById(R.id.reloginButton);
		mUserNameWindow = (EditText) findViewById(R.id.inputUserName);
		mPasswordWindow = (EditText) findViewById(R.id.inputPassword);

		mLogin.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.reloginButton) {
			mUserName = mUserNameWindow.getText().toString();
			mPassword = mPasswordWindow.getText().toString();

			if (mUserName.isEmpty() || mPassword.isEmpty()) {
				mErrorText.setVisibility(View.VISIBLE);
				mErrorText.setText("Please enter both a username and password to proceed");
				return;
			}
			else {
				mErrorText.setText("");
				mErrorText.setVisibility(View.GONE);
				login();
			}
		}
	}
	private class connectToWebsite extends AsyncTask <String, Void, String>{
		
		@Override
		protected String doInBackground(String... urls) {
			Document doc = null;
			try {
				//post to the login form
				Connection.Response res = Jsoup.connect(urls[0])
					    .data("module", "student")
					    .timeout(3000)
		    			.data("userName", urls[1])
		    			.data("userPassword", urls[2])
					    .method(Method.POST)
					    .execute();

				 	doc = res.parse();
				 	mLoginResponse = doc.toString();
				 	
				 	//get the cookie
					mNewCookieValue = res.cookie(Login.COOKIE_TYPE);
/*					Log.w("RL", "response:  " + mSearchResponse);*/
					return mLoginResponse;
					
			} 
			catch (Exception e) { //catch all exceptions
				e.printStackTrace();
			}
			return mLoginResponse;
		}
	    @Override
	    protected void onPostExecute(String response) {
	    	pd.dismiss();
	    	if(response != null) {
	    		if (mErrorText.getVisibility() == View.VISIBLE) {
	    			mErrorText.setText("");
	    			mErrorText.setVisibility(View.GONE);
    			}
	    		//go back to the activity that called this error
	    		if (response.contains("All Programs")) {
		    		Intent intent = null;
		    		switch(mCallingClass) {
			    		case SEARCH_RESULT_CLASS:{
			    			Log.w("RL", "launching search result");
			    			intent = new Intent(mContext, SearchResults.class);
				    		intent.putExtra(Login.COOKIE_VALUE, mNewCookieValue);
				    		intent.putExtra(Login.LOGIN_RESPONSE_STRING, response);
				    		intent.putExtra(SearchForm.SEARCH_RESPONSE_STRING, mSearchResponse);
				    		break;
			    		}
			    		case SEARCH_FORM_CLASS:{
			    			Log.w("RL", "launching MSM");
			    			intent = new Intent(mContext,SearchForm.class);
			    			intent.putExtra(Login.COOKIE_VALUE, mNewCookieValue);
			    			intent.putExtra(Login.LOGIN_RESPONSE_STRING, response);
			    		break;
			    		}	
		    		}
		    		mContext.startActivity(intent);
		    	}
	    		else {
	    			mErrorText.setVisibility(View.VISIBLE);
	    			mErrorText.setText("Wrong login.  Please try again");
	    		}
	    	}
	    	else { //error
	    		mErrorText.setVisibility(View.VISIBLE);
	    		mErrorText.setText("Problem with logging in. Please try again.");
	    	}
	    }
	}
	public void login() {
		pd = new ProgressDialog(mContext, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        pd.setTitle("Connecting...");
        //make this a random fact later.  haha.
        pd.setMessage("Please wait.");
        pd.setIndeterminate(true);
        pd.show();
        connectToWebsite connect = new connectToWebsite();
		connect.execute(new String[] {Login.POST_LOGIN_URL, mUserName, mPassword});	
	}
}
