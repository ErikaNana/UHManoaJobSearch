package edu.uhmanoa.jobsearch.CustomComponents;

import edu.uhmanoa.jobsearch.R;
import edu.uhmanoa.jobsearch.R.id;
import edu.uhmanoa.jobsearch.R.layout;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


public class FullDescriptionRowView extends LinearLayout {
	TextView mCategoryView;
	TextView mDetailView;
	
	//needed
	public FullDescriptionRowView(Context context) {
		super(context);
	}
	public FullDescriptionRowView(Context context, String category, String detail) {
		super(context);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.full_descrip_row_view, this, true);
		mCategoryView = (TextView) findViewById(R.id.categoryWindow);
		mDetailView = (TextView) findViewById(R.id.detaillWindow);
		mCategoryView.setText(category);
		mDetailView.setText(detail);
	}

}
