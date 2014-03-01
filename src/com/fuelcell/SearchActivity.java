package com.fuelcell;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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

		EditText searchCorp = (EditText) findViewById(R.id.searchCorp);
		EditText searchYear = (EditText) findViewById(R.id.searchYear);
		EditText searchModel = (EditText) findViewById(R.id.searchModel);
		
		searchCorp.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchYear.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchModel.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);

		searchCorp = (EditText) findViewById(R.id.searchCorp);
		searchYear = (EditText) findViewById(R.id.searchYear);
		searchModel = (EditText) findViewById(R.id.searchModel);
		logo = (ImageView) findViewById(R.id.mainicon);

		searchCorp.setGravity(Gravity.CENTER_HORIZONTAL
				+ Gravity.CENTER_VERTICAL);
		searchYear.setGravity(Gravity.CENTER_HORIZONTAL
				+ Gravity.CENTER_VERTICAL);
		searchModel.setGravity(Gravity.CENTER_HORIZONTAL
				+ Gravity.CENTER_VERTICAL);
		
		setClick(searchCorp);
		setClick(searchYear);
		setClick(searchModel);
	}

	protected void setClick(final EditText textField) {
		textField.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					logo.setVisibility(View.GONE);
					
					if (textField != searchCorp)
						searchCorp.setVisibility(View.GONE);
					if (textField != searchYear)
						searchYear.setVisibility(View.GONE);
					if (textField != searchModel)
						searchModel.setVisibility(View.GONE);
					
			}

		});
	}
}
