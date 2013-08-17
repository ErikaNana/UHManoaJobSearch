package edu.uhmanoa.jobsearch;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JobAdapter extends ArrayAdapter<Job>{
	ArrayList<Job> mListOfJobs;
	Activity mActivity;

	public JobAdapter(Activity activity, int textViewResourceId, ArrayList<Job> listOfJobs) {
        super(activity, textViewResourceId, listOfJobs);
        mActivity = activity;
        mListOfJobs = listOfJobs;
    }
	
	/**Makes ListView more efficient since Android recycles views in a ListView
	 * Code might call findViewById() frequently during the scrolling of ListView, which
	 * can slow performance. 
	 * ViewHolder stores each of the component views inside the tag field of the layout,
	 * so can immediately access them without the need to look them up repeatedly*/
	public static class ViewHolder{
		ImageView jobIcon;
		TextView jobTitle;
		TextView jobDescription;
		TextView jobProgramType;
		TextView jobSalary;
		TextView jobHour;
		TextView jobLocation;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.single_job_view, null);
			holder = new ViewHolder();
			holder.jobIcon= (ImageView) view.findViewById(R.id.jobLocationIcon);
			holder.jobTitle = (TextView) view.findViewById(R.id.jobTitle);
			holder.jobDescription = (TextView) view.findViewById(R.id.jobDescrip);
			holder.jobProgramType = (TextView) view.findViewById(R.id.jobProgramType);
			holder.jobSalary = (TextView) view.findViewById(R.id.jobSalary);
			holder.jobLocation = (TextView) view.findViewById(R.id.jobLocation);
			holder.jobHour = (TextView) view.findViewById(R.id.jobHour);
			view.setTag(holder);
		}
		else {
			//returns the object stored in this view as a tag
			holder = (ViewHolder) view.getTag();
		}
		final Job job = mListOfJobs.get(position);
		if (job != null) {
			int icon = job.whichIcon();

			//set job title
			holder.jobTitle.setText(job.mTitle);
			//set the description
			holder.jobDescription.setText(job.mDescription);
			//set the programType
			holder.jobProgramType.setText(job.mProgram);
			if (job.jobOnCampus()) {
				holder.jobHour.setVisibility(View.GONE);
				holder.jobLocation.setVisibility(View.GONE);
			}
			else {
				holder.jobLocation.setText(job.mLocation);
				holder.jobLocation.setVisibility(View.VISIBLE);
			}
			//set the appropriate icon
			Drawable jobIconPicture;
			switch (icon) {
				case Job.UHM:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.uhm);
					break;
				}
				case Job.UHH:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.uhh);
					break;				}
				case Job.UHWO:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.uhwo);
					break;
				}
					case Job.UHMC:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.uhmc);
					break;
				}
				case Job.HCC:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.hcc);
					break;
				}
				case Job.KAP_CC:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.kapcc);
					break;
				}
				case Job.KA_CC:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.kauaicc);
					break;
				}
				case Job.LCC:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.lcc);
					break;
				}
				case Job.WCC:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.wcc);
					break;
				}
				default:{
					jobIconPicture = mActivity.getResources().getDrawable(R.drawable.other);
					break;
				}
			}
			holder.jobIcon.setImageDrawable(jobIconPicture);
			//set the salary text
			if (job.mPay.length() > 6) {
				holder.jobHour.setVisibility(View.VISIBLE);
				holder.jobHour.setText("details");
				holder.jobSalary.setText("click for");
				holder.jobSalary.setTextSize(12);
			}
			else {
				holder.jobSalary.setText(job.mPay);
				holder.jobSalary.setTextSize(20);
				holder.jobHour.setVisibility(View.GONE);
			}
		}
		return view;
	}

}
