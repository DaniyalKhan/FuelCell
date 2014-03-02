package com.fuelcell;

import java.util.ArrayList;

import com.fuelcell.models.Car;
import com.fuelcell.util.DynamicArrayAdapter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StatsActivity extends Activity {
	
	ListView resultList;
	ArrayList<Car> filtered;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_stats);
		
		resultList = (ListView) findViewById(R.id.searchedList);
		filtered = SearchActivity.filtered;
		ArrayList<String> resultsOutput = new ArrayList<String>();
		for (int i = 0 ; i < filtered.size() ; i++) {
			resultsOutput.add(filtered.get(i).getYear() + " " + filtered.get(i).getManufacturer() + " " + filtered.get(i).getModel());
		}
		ArrayAdapter<String> resultsAdapter = new ArrayAdapter<String>(StatsActivity.this, R.layout.list_item, resultsOutput) {

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View row = convertView;
				if (row == null) {
					LayoutInflater inflater = StatsActivity.this.getLayoutInflater();
					row = inflater.inflate(R.layout.list_item, null);
					final ViewHolder viewHolder = new ViewHolder();
					viewHolder.text = (TextView) row.findViewById(R.id.text);
					row.setTag(viewHolder);
					row.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(StatsActivity.this, CarProfileActivity.class);
							intent.putExtra("car", filtered.get(position));
							startActivity(intent);
							
						}
					});
				}
				ViewHolder holder = (ViewHolder) row.getTag();
			    String s = getItem(position);
		    		holder.text.setText(s);

				return row;
			}
			
		};
		resultList.setAdapter(resultsAdapter);
//		resultList.setOnItemClickListener() {
			
//		}
	}
	
	static class ViewHolder {
	    public TextView text;
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
	}
}