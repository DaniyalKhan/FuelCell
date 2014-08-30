package com.fuelcell;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fuelcell.models.Car;
import com.fuelcell.util.CarDatabase;

public class StatsActivity extends Activity {
	
	ListView resultList;
	List<Car> filtered;
	TextView hint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_stats);
		
		Intent intentLast = getIntent();
		TextView header = (TextView) findViewById(R.id.header);
		header.setText(intentLast.getStringExtra("title"));
		
		hint = (TextView) findViewById(R.id.hint);
		hint.setText(intentLast.getStringExtra("hint"));
		
		boolean showClear = intentLast.getBooleanExtra("clear", false);
		if (!showClear) {
			findViewById(R.id.clearButton).setVisibility(View.GONE);
		} else {
			findViewById(R.id.clearButton).setVisibility(View.VISIBLE);
			findViewById(R.id.clearButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					CarDatabase.reCreate(StatsActivity.this);
//					Intent homeIntent = new Intent(StatsActivity.this, SearchActivity.class);
//					homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP+Intent.FLAG_ACTIVITY_SINGLE_TOP );
//					startActivity(homeIntent);
				}
			});
		}
		
		resultList = (ListView) findViewById(R.id.searchedList);
		((Button) findViewById(R.id.homeButton)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent homeIntent = new Intent(StatsActivity.this, SearchActivity.class);
				homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP+Intent.FLAG_ACTIVITY_SINGLE_TOP );
				
				startActivity(homeIntent);
			}
			
		});
		
		filtered = SearchActivity.filtered;
		List<String> resultsOutput = new ArrayList<String>();
		
		for (int i = 0 ; i < filtered.size() ; i++) {
			resultsOutput.add(filtered.get(i).year + " " + filtered.get(i).manufacturer + " " + filtered.get(i).model);
		}
		ArrayAdapter<String> resultsAdapter = new ArrayAdapter<String>(StatsActivity.this, R.layout.list_item, resultsOutput) {

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View row = convertView;
				//Hide message when there are items
				hint.setVisibility(View.GONE);
				
				if (row == null) {
					LayoutInflater inflater = StatsActivity.this.getLayoutInflater();
					row = inflater.inflate(R.layout.list_item, null);
					final ViewHolder viewHolder = new ViewHolder();
					viewHolder.text = (TextView) row.findViewById(R.id.text);
					row.setTag(viewHolder);
				}
				row.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(StatsActivity.this, CarProfileActivity.class);
						Car c = filtered.get(position);
						intent.putExtra("car", c);
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
	
	static class ViewHolder {
	    public TextView text;
	}
}