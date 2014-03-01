package com.fuelcell;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.EditText;

public class SearchActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		EditText searchCorp = (EditText) findViewById(R.id.searchCorp);
		EditText searchYear = (EditText) findViewById(R.id.searchYear);
		EditText searchModel = (EditText) findViewById(R.id.searchModel);
		
		searchCorp.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchYear.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchModel.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		
		
		
	}
	
}
