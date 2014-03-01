package com.fuelcell;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.fuelcell.csvutils.CSVParser;
import com.fuelcell.models.Car;

public class SearchActivity extends Activity {

	EditText searchCorp;
	EditText searchYear;
	EditText searchModel;
	ImageView logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		
		searchCorp = (EditText) findViewById(R.id.searchCorp);
		searchYear = (EditText) findViewById(R.id.searchYear);
		searchModel = (EditText) findViewById(R.id.searchModel);
		logo = (ImageView) findViewById(R.id.mainicon);
	
		
		searchCorp.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchYear.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchModel.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		
		setClick(searchCorp);
		setClick(searchYear);
		setClick(searchModel);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    // Checks whether a hardware keyboard is available
	    if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
	    	mainViewVisibility();
	    } 
	}

	protected void setClick(EditText textField) {
		textField.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				searchViewVisibility(v);				
			}
		});
	}
	
	private void searchViewVisibility(View v){
		logo.setVisibility(View.GONE);
		
		if (!v.equals(searchCorp))
			searchCorp.setVisibility(View.GONE);
		if (!v.equals(searchYear))
			searchYear.setVisibility(View.GONE);
		if (!v.equals(searchModel))
			searchModel.setVisibility(View.GONE);
	}
	
	private void mainViewVisibility(){
		logo.setVisibility(View.VISIBLE);
		searchCorp.setVisibility(View.VISIBLE);
		searchYear.setVisibility(View.VISIBLE);
		searchModel.setVisibility(View.VISIBLE);
	}
}
