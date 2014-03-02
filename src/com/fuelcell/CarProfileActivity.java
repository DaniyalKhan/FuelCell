package com.fuelcell;

import com.fuelcell.models.Car;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class CarProfileActivity extends Activity {
	Car car;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_car_profile);
		Intent intent = getIntent();
		car = (Car) intent.getParcelableExtra("car");
	}
}