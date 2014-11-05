package com.fuelcell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuelcell.action.ButtonSettings;
import com.fuelcell.models.Car;
import com.fuelcell.models.CarFrame;
import com.fuelcell.util.CarDatabase;

public class CarProfileActivity extends Activity {
	Car car;
	TextView cylinderInfo;
	TextView engineInfo;
	TextView fuelInfo;
	TextView tranInfo;
	TextView vehicleInfo;
	TextView carName;
	TextView efficiencyInfo;
	TextView emissionsInfo;
	TextView gearInfo;
	
	Button planTrip;
	Button save;
	Button defaultButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_car_profile);
		
		getWindow().setBackgroundDrawableResource(R.drawable.background);
		
		planTrip = (Button) findViewById(R.id.tripButton);
		save = (Button) findViewById(R.id.saveButton);
		defaultButton = (Button) findViewById(R.id.defaultButton);
		
		ButtonSettings.pressSize(planTrip, 10);
		ButtonSettings.pressSize(defaultButton, 10);
		ButtonSettings.pressSize(save, 5);
				
		planTrip.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intentTrip = new Intent(CarProfileActivity.this, DirectionsActivity.class);
				intentTrip.putExtra("car", car);
				startActivity(intentTrip);
			}
			
		});
		
		ButtonSettings.setHomeButton(((ImageView) findViewById(R.id.mainicon)),this);
		
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				boolean saved = car.saveToProfile(CarProfileActivity.this);
				Context context = getApplicationContext();
				CharSequence text;
				if (saved) {
					text = car.model + " saved to profile";
					save.setBackground(getResources().getDrawable(R.drawable.favourite_set_on_pressing));
					CarDatabase.obtain(context).addFavCarFrames(car);
				} else {
					text = car.model + " is removed from profile";
					save.setBackground(getResources().getDrawable(R.drawable.favourite_set_off_pressing));
					CarDatabase.obtain(context).removeFavCarFrames(car);
				}
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.show();
			}
			
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		Intent intent = getIntent();
		
		final CarFrame carFrame = CarFrame.loadCarFromIntent(intent);
		
		this.car = CarDatabase.obtain(CarProfileActivity.this).getCarFull(carFrame);
		
		carName = (TextView) findViewById(R.id.carName);
		cylinderInfo = (TextView) findViewById(R.id.cylinderInfo);
		engineInfo = (TextView) findViewById(R.id.engineInfo);
		tranInfo = (TextView) findViewById(R.id.tranInfo);
		fuelInfo = (TextView) findViewById(R.id.fuelInfo);
		vehicleInfo = (TextView) findViewById(R.id.vehicleInfo);
		efficiencyInfo = (TextView) findViewById(R.id.efficiencyInfo);
		emissionsInfo = (TextView) findViewById(R.id.emissionsInfo);
		gearInfo = (TextView) findViewById(R.id.gearInfo);
				
		carName.setText(car.year + " " + car.manufacturer + " " + car.model);
		cylinderInfo.setText(Integer.toString(car.cylinders));
		engineInfo.setText(Float.toString((float) ((int) (car.engineSize*100)/100)));
		tranInfo.setText(car.transmission.toString());
		fuelInfo.setText(car.fuelType.toString());
		vehicleInfo.setText(car.vehicleClass);
		efficiencyInfo.setText(Float.toString((float) ((int) (car.highwayEffL*100)/100)));
		emissionsInfo.setText(Double.toString(car.emissions));
		gearInfo.setText(Integer.toString(car.gears));
	}
}