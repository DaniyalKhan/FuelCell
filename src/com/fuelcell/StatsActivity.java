package com.fuelcell;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fuelcell.action.ButtonSettings;
import com.fuelcell.models.CarFrame;
import com.fuelcell.util.CarDatabase;

public class StatsActivity extends NavActivity {
	
	ListView resultList;
	List<CarFrame> searched;
	TextView hint;
	Button clearButton;
	Boolean isFavourites = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater.inflate(R.layout.activity_stats,  null, false);
		mDrawer.addView(contentView,0);
		
		getWindow().setBackgroundDrawableResource(R.drawable.background);
		boolean showClear = false;
		
		Intent intentLast = getIntent();
		if (intentLast.getStringExtra("title").equalsIgnoreCase("Results")) {
			((ImageView) findViewById(R.id.resulticon)).setImageResource(R.drawable.header_results);
			isFavourites = false;
		} else if (intentLast.getStringExtra("title").equalsIgnoreCase("Saved")){
			((ImageView) findViewById(R.id.resulticon)).setImageResource(R.drawable.header_fav);
			showClear = true;
			isFavourites = true;
		}
		
		clearButton = (Button) findViewById(R.id.clearButton);
		
		hint = (TextView) findViewById(R.id.hint);
		hint.setText(intentLast.getStringExtra("hint"));
		
		if (!showClear) {
			clearButton.setVisibility(View.GONE);
		} else {
			clearButton.setVisibility(View.VISIBLE);
			clearButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CarDatabase.obtain(StatsActivity.this).removeFavCars();
					Intent intent = getIntent();
					startActivity(intent);
				}
			});
			
		}
		
		resultList = (ListView) findViewById(R.id.searchedList);
		ButtonSettings.setHomeButton(((ImageView) findViewById(R.id.mainicon)),mDrawer);
		
	}
	
	
	
	
	//load the car data
	@Override
	protected void onStart() {
		super.onStart();
		Intent intentLast = getIntent();
		final CarFrame carFrame = CarFrame.loadCarFromIntent(intentLast);
		//TODO maybe make this an async task?
		new Thread(new Runnable() {
			@Override
/*
			public View getView(final int position, View convertView, ViewGroup parent) {
				View rowView = convertView;
				//Hide message when there are items
				hint.setVisibility(View.GONE);
				
				if (rowView == null) {
					LayoutInflater inflater = StatsActivity.this.getLayoutInflater();
					rowView = inflater.inflate(R.layout.list_item, null);
					final ViewHolder viewHolder = new ViewHolder();
					viewHolder.text = (TextView) rowView.findViewById(R.id.text);
					rowView.setTag(viewHolder);
				}
				rowView.setOnClickListener(new OnClickListener() {
*/
			public void run() {
				Intent intentLast = getIntent();
				if (intentLast.getStringExtra("title").equalsIgnoreCase("Results")) {
					searched = CarDatabase.obtain(StatsActivity.this).getCarFrames(carFrame.year, carFrame.manufacturer, carFrame.model, carFrame.vehicleClass);
				} else if (intentLast.getStringExtra("title").equalsIgnoreCase("Saved")){
					searched = CarDatabase.obtain(StatsActivity.this).getFavCarFrames();
				}
				StatsActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final List<String> resultsOutput = new ArrayList<String>();
						for (int i = 0 ; i < searched.size() ; i++) {
							resultsOutput.add(searched.get(i).year + " " + searched.get(i).manufacturer + " " + searched.get(i).model);
						}
						final ArrayAdapter<String> resultsAdapter = new ArrayAdapter<String>(StatsActivity.this, R.layout.list_item, resultsOutput) {
							@Override
							public View getView(final int position, View convertView, ViewGroup parent) {
								View row = convertView;
								//Hide message when there are items
								hint.setVisibility(View.GONE);
								if (isFavourites) {
									if (row == null) {
										LayoutInflater inflater = StatsActivity.this.getLayoutInflater();
										row = inflater.inflate(R.layout.fav_list_item, null);
										final ViewHolder viewHolder = new ViewHolder();
										viewHolder.text = (TextView) row.findViewById(R.id.text);
										viewHolder.delete = (ImageView) row.findViewById(R.id.delete);
										row.setTag(viewHolder);
										
										viewHolder.delete.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
												//Delete from favourite when clicking delete image
												CarDatabase.obtain(StatsActivity.this).removeFavCarFrames(searched.get(position));
												resultsOutput.remove(position);
												notifyDataSetChanged();
											}
										}); 
									}
								} else {
									if (row == null) {
										LayoutInflater inflater = StatsActivity.this.getLayoutInflater();
										row = inflater.inflate(R.layout.list_item, null);
										final ViewHolder viewHolder = new ViewHolder();
										viewHolder.text = (TextView) row.findViewById(R.id.text);
										row.setTag(viewHolder);
									}
								}
								row.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										//After they click on car a car, just query the first car that coems up that matches the 4 criteria for now 
										Intent intent = new Intent(StatsActivity.this, CarProfileActivity.class);
										CarFrame.saveCarToIntent(intent, Integer.toString(searched.get(position).year), searched.get(position).manufacturer, searched.get(position).model, searched.get(position).vehicleClass);
										startActivity(intent);
									}
								});
								ViewHolder holder = (ViewHolder) row.getTag();
							    String s = getItem(position);
						    	holder.text.setText(s);
								return row;
							}
							
						};
						resultList.setAdapter(resultsAdapter);
					}
				});
/*
				ViewHolder holder = (ViewHolder) rowView.getTag();
			    String s = getItem(position);
		    		holder.text.setText(s);

				return rowView;
*/
			}
		}).run();

	}
	
	static class ViewHolder {
	    public TextView text;
	    public ImageView delete;
	}
}