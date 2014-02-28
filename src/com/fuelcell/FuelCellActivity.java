package com.fuelcell;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FuelCellActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fuel_cell);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fuel_cell, menu);
		return true;
	}

}
