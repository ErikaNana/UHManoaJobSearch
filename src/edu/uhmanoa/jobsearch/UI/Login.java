package edu.uhmanoa.jobsearch.UI;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import edu.uhmanoa.jobsearch.R;

public class Login extends Activity implements OnClickListener{
	
	Button mLoginButton;
	String mCookieValue;
	String mLoginResponse;
	EditText mUserNameBox;
	EditText mPasswordBox;
	Context mContext;
	ProgressDialog pd;
	String mUserName;
	String mPassword;
	
	public static final String POST_LOGIN_URL = "https://sece.its.hawaii.edu/sece/stdLogin.do";
	public static final String COOKIE_TYPE = "JSESSIONID";
	
	/**Values for data passed into the intent*/
	public static final String COOKIE_VALUE = "cookie value";
	public static final String LOGIN_RESPONSE_STRING = "response string";
	
	/**Error codes for checking login information and connect*/
	public static final int NO_INPUT_ERROR = 1;
	public static final int WRONG_INPUT_ERROR = 2;
	public static final int CONNECTION_ERROR = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mLoginButton = (Button) findViewById(R.id.loginButton);
		mLoginButton.setOnClickListener(this);
		
		mUserNameBox = (EditText) findViewById(R.id.inputUserName);
		mPasswordBox = (EditText) findViewById(R.id.inputPassword);
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
					mCookieValue = res.cookie(COOKIE_TYPE);
					Log.w("search", "response:  " + mCookieValue);
					
			} 
			catch (Exception e) { //catch all exceptions
				e.printStackTrace();
			}
			return mLoginResponse;
		}
	    @Override
	    protected void onPostExecute(String response) {
	    	if (pd != null) {
		    	pd.dismiss();
	    	}
	    	if (response != null) {
			    if (response.contains("Welcome")) {
			    	launchMainStudentMenu();
			    }
			    else {
			    	showErrorDialog(WRONG_INPUT_ERROR);
			    }
	    	}
	    	else { //wasn't able to connect at all
	    		showErrorDialog(CONNECTION_ERROR);
	    	}
	    }
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.loginButton:{
				mUserName = mUserNameBox.getText().toString();
				mPassword = mPasswordBox.getText().toString();
				Log.w("js", "userName:  " + mUserName);
				Log.w("password", "password:  " + mPassword);
				//just for debugging
/*				if (userName.isEmpty() || password.isEmpty()) {
					showErrorDialog(NO_INPUT_ERROR);
					return;
				}*/
				login();
			}
		}
	}
	
	//needs to redirect to login help later
	public void showErrorDialog(int typeOfError) {
		AlertDialog.Builder builder=  new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
									      .setTitle(R.string.app_name);
		
		switch(typeOfError) {
			case WRONG_INPUT_ERROR:{
				builder.setMessage("Username and/or password is incorrect.  Please try again!");
				
				builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//reset both fields
						mUserNameBox.setText("");
						mPasswordBox.setText("");
						//shift focus back to user name field
						mUserNameBox.requestFocus();
						return;
					}
				});
				break;
			}
			case NO_INPUT_ERROR:{
				builder.setMessage("Username and/or password is empty!  Please enter a username and a password to continue");
				builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
				break;
			}
			case CONNECTION_ERROR:{
				builder.setMessage("Connection failed.  Please check your internet connection.");
				builder.setPositiveButton("Check settings", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pd.dismiss();
						Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
						startActivity(intent);
					}
				});
				builder.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pd.dismiss();
						login();
					}
				});
				break;
			}
		}

		AlertDialog dialog = builder.create();
		//so dialog doesn't get closed when touched outside of it
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	public void launchMainStudentMenu() {
		Intent launchStudentMenu = new Intent(this,MainStudentMenu.class);
		launchStudentMenu.putExtra(COOKIE_VALUE, mCookieValue);
		launchStudentMenu.putExtra(LOGIN_RESPONSE_STRING, mLoginResponse);
    	startActivity(launchStudentMenu);
	}
	
	public void login() {
		pd = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        pd.setTitle("Connecting...");
        //make this a random fact later.  haha.
        pd.setMessage("Please wait.");
        pd.setIndeterminate(true);
        pd.show();
        connectToWebsite connect = new connectToWebsite();
		connect.execute(new String[] {POST_LOGIN_URL, mUserName, mPassword});
	}
}
