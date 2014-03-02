package com.fuelcell;

import com.fuelcell.models.Car;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CarProfileActivity extends Activity {
	Car car;
	TextView cylinderInfo;
	TextView engineInfo;
	TextView fuelInfo;
	TextView tranInfo;
	TextView vehicleInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_car_profile);
		Intent intent = getIntent();
		
		car = (Car) intent.getParcelableExtra("car");
		
		cylinderInfo = (TextView) findViewById(R.id.cylinderInfo);
		engineInfo = (TextView) findViewById(R.id.engineInfo);
		tranInfo = (TextView) findViewById(R.id.tranInfo);
		fuelInfo = (TextView) findViewById(R.id.fuelInfo);
		vehicleInfo = (TextView) findViewById(R.id.vehicleInfo);
		
		cylinderInfo.setText(Integer.toString(car.getCylinders()));
		engineInfo.setText(Double.toString(car.getEngineSize()));
		tranInfo.setText(car.getTransmission());
		fuelInfo.setText(car.getFuelType().toString());
		vehicleInfo.setText(car.getVehicleClass());
	}
}