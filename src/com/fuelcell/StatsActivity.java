package com.fuelcell;

import java.util.ArrayList;

import com.fuelcell.models.Car;
import com.fuelcell.util.DynamicArrayAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StatsActivity extends Activity {
	
	ListView resultList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_stats);
		
		resultList = (ListView) findViewById(R.id.searchedList);
		ArrayList<Car> filtered= SearchActivity.filtered;
		
		ArrayAdapter resultsAdapter = new ArrayAdapter(StatsActivity.this, R.layout.list_item, filtered);
		resultList.setAdapter(resultsAdapter);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
	}
}