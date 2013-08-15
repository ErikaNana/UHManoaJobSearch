package edu.uhmanoa.jobsearch;

import java.io.IOException;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener{
	
	Button mLoginButton;
	String mCookieValue;
	String mLoginResponse;
	EditText mUserName;
	EditText mPassword;
	Context mContext;
	ProgressDialog pd;
	
	public static final String POST_LOGIN_URL = "https://sece.its.hawaii.edu/sece/stdLogin.do";
	public static final String COOKIE_TYPE = "JSESSIONID";
	
	/**Values for data passed into the intent*/
	public static final String COOKIE_VALUE = "cookie value";
	public static final String RESPONSE_STRING = "response string";
	
	/**Error codes for checking login information*/
	public static final int NO_INPUT_ERROR = 1;
	public static final int WRONG_INPUT_ERROR = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mLoginButton = (Button) findViewById(R.id.loginButton);
		mLoginButton.setOnClickListener(this);
		
		mUserName = (EditText) findViewById(R.id.inputUserName);
		mPassword = (EditText) findViewById(R.id.inputPassword);
	}

	private class connectToWebsite extends AsyncTask <String, Void, String>{
		
		@Override
		protected String doInBackground(String... urls) {
			Document doc = null;
			try {
				//post to the login form
				Connection.Response res = Jsoup.connect(urls[0])
					    .data("module", "student")
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
					
			} catch (IOException e) {
				e.printStackTrace();
			}
			return mLoginResponse;
		}
	    @Override
	    protected void onPostExecute(String response) {
	    	pd.dismiss();
		    if (response.contains("Welcome")) {
		    	Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
		    	launchMainStudentMenu();
		    }
		    else {
		    	showErrorDialog(WRONG_INPUT_ERROR);
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
				//just for debugging
/*				if (userName.isEmpty() || password.isEmpty()) {
					showErrorDialog(NO_INPUT_ERROR);
					return;
				}*/
				connectToWebsite connect = new connectToWebsite();
				pd = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
                pd.setTitle("Connecting...");
                //make this a random fact later.  haha.
                pd.setMessage("Please wait.");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
				connect.execute(new String[] {POST_LOGIN_URL, userName, password});
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
						mUserName.setText("");
						mPassword.setText("");
						//shift focus back to user name field
						mUserName.requestFocus();
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
		}

		AlertDialog dialog = builder.create();
		//so dialog doesn't get closed when touched outside of it
		dialog.setCanceledOnTouchOutside(false);
		//so dialog doesn't get dismissed by back button
		dialog.setCancelable(false);
		dialog.show();
	}
	
	public void launchMainStudentMenu() {
		Intent launchStudentMenu = new Intent(this,MainStudentMenu.class);
		launchStudentMenu.putExtra(COOKIE_VALUE, mCookieValue);
		launchStudentMenu.putExtra(RESPONSE_STRING, mLoginResponse);
    	startActivity(launchStudentMenu);
	}
}
