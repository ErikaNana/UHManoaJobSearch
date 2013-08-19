package edu.uhmanoa.jobsearch;

import java.util.LinkedHashMap;

import android.app.Dialog;
import android.content.Context;
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
	
	public FullDescriptionDialog(Context context, LinkedHashMap<String,String> details) {
		super(context);
		mContext = context;
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
		switch(view.getId()) {
			case R.id.addToJobCartButton:{
				//just return for now 
				this.dismiss();
				break;
			}
			case R.id.returnToSearchButton:{
				this.dismiss();
				break;
			}
		}
	}
}
