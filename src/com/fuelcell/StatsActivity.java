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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.fuelcell.action.ButtonSettings;
import com.fuelcell.models.Car;
import com.fuelcell.util.CarDatabase;

public class StatsActivity extends Activity {
	
	ListView resultList;
	List<Car> filtered;
	TextView hint;
	Button clearButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_stats);
		
		getWindow().setBackgroundDrawableResource(R.drawable.background);
		
		Intent intentLast = getIntent();
		if (intentLast.getStringExtra("title").equalsIgnoreCase("Results")) {
			((ImageView) findViewById(R.id.resulticon)).setImageResource(R.drawable.header_results);
		} else if (intentLast.getStringExtra("title").equalsIgnoreCase("Saved")){
			((ImageView) findViewById(R.id.resulticon)).setImageResource(R.drawable.header_fav);
		}
		
		clearButton = (Button) findViewById(R.id.clearButton);
		
		hint = (TextView) findViewById(R.id.hint);
		hint.setText(intentLast.getStringExtra("hint"));
		ButtonSettings.pressSize(clearButton, 15);
		
		boolean showClear = intentLast.getBooleanExtra("clear", false);
		if (!showClear) {
			clearButton.setVisibility(View.VISIBLE);
		} else {
			clearButton.setVisibility(View.GONE);
			clearButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CarDatabase.reCreate(StatsActivity.this);
					Intent homeIntent = new Intent(StatsActivity.this, HubActivity.class);
					homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP+Intent.FLAG_ACTIVITY_SINGLE_TOP );
					startActivity(homeIntent);
				}
			});
//			LinearLayout rl = new LinearLayout(this);
//			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//			params.addRule(RelativeLayout.ABOVE, clearButton.getId());
//			rl.addView(findViewById(R.id.listLayout), params);
			
			
			
		}
		
		resultList = (ListView) findViewById(R.id.searchedList);
		ButtonSettings.setHomeButton(((ImageView) findViewById(R.id.mainicon)),this);
		
		filtered = SearchActivity.filtered;
		List<String> resultsOutput = new ArrayList<String>();
		
		for (int i = 0 ; i < filtered.size() ; i++) {
			resultsOutput.add(filtered.get(i).getYear() + " " + filtered.get(i).getManufacturer() + " " + filtered.get(i).getModel());
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