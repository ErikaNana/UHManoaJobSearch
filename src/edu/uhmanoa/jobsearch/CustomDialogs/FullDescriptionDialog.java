package edu.uhmanoa.jobsearch.CustomDialogs;

import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uhmanoa.jobsearch.R;
import edu.uhmanoa.jobsearch.CustomComponents.FullDescriptionRowView;
import edu.uhmanoa.jobsearch.R.id;
import edu.uhmanoa.jobsearch.R.layout;
import edu.uhmanoa.jobsearch.R.string;
import edu.uhmanoa.jobsearch.UI.Login;
import edu.uhmanoa.jobsearch.UI.MainStudentMenu;
import edu.uhmanoa.jobsearch.UI.SearchResults;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FullDescriptionDialog extends Dialog implements OnClickListener{
	Context mContext;
	TextView mCategoryView;
	TextView mDetailView;
	Button mAddJob;
	Button mReturnToSearch;
	int mJobID;
	int mPayID;
	String mCookie;
	String mJobCartResponse;
	ProgressDialog pd;
	String mLoginResponse;
	String mSearchResponse;
	
	public static final String JOB_CART_POST_URL = "https://sece.its.hawaii.edu/sece/addRefToCart.do";
	
	public FullDescriptionDialog(Context context, LinkedHashMap<String,String> details, int jobID, 
								int payID, String cookie, String loginResponse, String searchResponse) {
		super(context);
		mContext = context;
		mJobID = jobID;
		mPayID = payID;
		mCookie = cookie;
		mSearchResponse = searchResponse;
		mLoginResponse = loginResponse;
		
		setTitle("Detailed Listing");
		setContentView(R.layout.full_description_view);
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.fullDescriptionWindow);
		mAddJob = (Button) findViewById(R.id.addToJobCartButton);
		mReturnToSearch = (Button) findViewById(R.id.returnToSearchButton);
		
		mAddJob.setOnClickListener(this);
		mReturnToSearch.setOnClickListener(this);
		
		//iterate over the keys
		for (String key: details.keySet()) {
			FullDescriptionRowView row = new FullDescriptionRowView(mContext, key, details.get(key));
			mainLayout.addView(row);
		}
	}

	@Override
	public void onClick(View view) {
		this.dismiss();
		pd = new ProgressDialog(mContext, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
		pd.setTitle("Connecting...");
        //make this a random fact later.  haha.
        pd.setMessage("Please wait.");
        pd.setIndeterminate(true);
        pd.show();
		switch(view.getId()) {
			case R.id.addToJobCartButton:{

				Log.w("FDD", "jobID:  " + mJobID);
				Log.w("FDD", "payID:  " + mPayID);
				//get the response
				AddToJobCart jobCart = new AddToJobCart();
				jobCart.execute(new String[] {JOB_CART_POST_URL});
				//start the job cart view
				break;
			}
			case R.id.returnToSearchButton:{
				this.dismiss();
				break;
			}
		}
	}
	private class AddToJobCart extends AsyncTask <String, Void, String>{
		
		@Override
		protected String doInBackground(String... html) {
			Document doc = null;
			try {
				String jobId = String.valueOf(mJobID);
				String payId = String.valueOf(mPayID);
				doc = Jsoup.connect(html[0])
							   .timeout(5000)
							   .cookie(Login.COOKIE_TYPE, mCookie)
							   .data("jobId", jobId)
							   .data("payId", payId)
							   .data("singleResult", "")
							   //faking chrome
							   .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
							   .post();
				Log.w("FDD", doc.text());
				Elements rawr = doc.getElementsByClass("plaintitle");
				for (Element element: rawr) {
					Log.w("FDD", "element:  " + element.toString());
				}
				mJobCartResponse = doc.toString();
				return mJobCartResponse;
				
			} catch (Exception e) { 
				Log.e("MSM", e.getMessage());
				showErrorDialog();
			}
		return null;
		}
		
	    @Override
	    protected void onPostExecute(String response) {
		    	if (response != null) {
		    		//check if cookie error
		    		if (response.contains("inactivity")) {
		    			ReLoginDialog reLoginDialog = new ReLoginDialog(mContext, ReLoginDialog.SEARCH_RESULT_CLASS,
		    									null);
		    			reLoginDialog.show();
		    		}
					//go back to search
/*					Intent intent = new Intent(mContext, SearchResults.class);
					intent.putExtra(Login.COOKIE_VALUE, mCookie);
					intent.putExtra(Login.LOGIN_RESPONSE_STRING, mLoginResponse);
					intent.putExtra(MainStudentMenu.SEARCH_RESPONSE_STRING, mSearchResponse);
				    mContext.startActivity(intent);*/
				    pd.dismiss();
		    	}
		    	else { //exception thrown
		    		showErrorDialog();
		    	}
	    }
	}


	public void showErrorDialog() {
		pd.dismiss();
		AlertDialog.Builder builder=  new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
									      .setTitle(R.string.app_name);
		
				builder.setMessage("An error has occured");
				builder.setPositiveButton("Go back to search", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//go back to search
						Intent intent = new Intent(mContext, SearchResults.class);
						intent.putExtra(Login.COOKIE_VALUE, mCookie);
						intent.putExtra(Login.LOGIN_RESPONSE_STRING, mLoginResponse);
						intent.putExtra(MainStudentMenu.SEARCH_RESPONSE_STRING, mSearchResponse);
					    mContext.startActivity(intent);
					}
				});
	
		AlertDialog dialog = builder.create();
		//so dialog doesn't get closed when touched outside of it
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
}
