package com.fuelcell;

import com.fuelcell.models.Car;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
	TextView carName;
	
	Button planTrip;
	Button save;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_car_profile);
		Intent intent = getIntent();
		
		car = (Car) intent.getParcelableExtra("car");
		
		carName = (TextView) findViewById(R.id.carName);
		cylinderInfo = (TextView) findViewById(R.id.cylinderInfo);
		engineInfo = (TextView) findViewById(R.id.engineInfo);
		tranInfo = (TextView) findViewById(R.id.tranInfo);
		fuelInfo = (TextView) findViewById(R.id.fuelInfo);
		vehicleInfo = (TextView) findViewById(R.id.vehicleInfo);
		
		carName.setText(car.getYear() + " " + car.getManufacturer() + " " + car.getModel());
		cylinderInfo.setText(Integer.toString(car.getCylinders()));
		engineInfo.setText(Double.toString(car.getEngineSize()));
		tranInfo.setText(car.getTransmission());
		fuelInfo.setText(car.getFuelType().toString());
		vehicleInfo.setText(car.getVehicleClass());
		
		planTrip = (Button) findViewById(R.id.tripButton);
		save = (Button) findViewById(R.id.saveButton);
				
		planTrip.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intentTrip = new Intent(CarProfileActivity.this, DirectionsActivity.class);
				intentTrip.putExtra("car", car);
				startActivity(intentTrip);
				
			}
			
		});
		
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				car.saveToProfile(CarProfileActivity.this);
			}
			
		});
	}
}