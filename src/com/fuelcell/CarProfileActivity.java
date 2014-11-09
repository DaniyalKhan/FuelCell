package com.fuelcell;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

public class CarProfileActivity extends NavActivity {
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
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater.inflate(R.layout.activity_car_profile,  null, false);
		mDrawer.addView(contentView,0);
		
		getWindow().setBackgroundDrawableResource(R.drawable.background);
		
		planTrip = (Button) findViewById(R.id.tripButton);
		save = (Button) findViewById(R.id.saveButton);
		defaultButton = (Button) findViewById(R.id.defaultButton);
		
		ButtonSettings.pressSize(planTrip, 10);
		
		ButtonSettings.pressSize(save, 6);
				
		planTrip.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intentTrip = new Intent(CarProfileActivity.this, DirectionsActivity.class);
				intentTrip.putExtra("car", car);
				startActivity(intentTrip);
			}
			
		});
		
		ButtonSettings.setHomeButton(((ImageView) findViewById(R.id.mainicon)),mDrawer);
		
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Context context = getApplicationContext();
				boolean alreadySaved = CarDatabase.obtain(context).isFav(car);
				setSavedButtonDrawable(!alreadySaved);
				CharSequence text;
				if (!alreadySaved) {
					text = car.model + " saved to profile";
					CarDatabase.obtain(context).addFavCarFrames(car);
				} else {
					text = car.model + " is removed from profile";
					CarDatabase.obtain(context).removeFavCarFrames(car);
				}
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}

	private void setSavedButtonDrawable(boolean isSaved) {
		if (isSaved) {
			save.setBackgroundDrawable(getResources().getDrawable(R.drawable.favourite_set_on_pressing));
		} else {
			save.setBackgroundDrawable(getResources().getDrawable(R.drawable.favourite_set_off_pressing));
		}
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
		
		Context context = getApplicationContext();
		boolean alreadySaved = CarDatabase.obtain(context).isFav(car);
		setSavedButtonDrawable(alreadySaved);
		
		SharedPreferences defaultCarPrefs = getSharedPreferences("default", MODE_PRIVATE);
		//If default car is this car, can't set this as default
		//Disable Click Listener and grey out button
		if ( (defaultCarPrefs.getInt("year", 0) == car.year) &&
		defaultCarPrefs.getString("manufacturer", "not").equalsIgnoreCase(car.manufacturer) &&
		defaultCarPrefs.getString("model", "not").equalsIgnoreCase(car.model) &&
		defaultCarPrefs.getString("vehicleClass", "not").equalsIgnoreCase(car.vehicleClass)) {
			defaultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.is_default_unpress));
			ButtonSettings.removePressSize(defaultButton);
		} else {
			ButtonSettings.pressSize(defaultButton, 10);
			defaultButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(CarProfileActivity.this, "This car has been saved as the default car.", Toast.LENGTH_LONG).show();
					
					SharedPreferences defaultCarPrefs = getSharedPreferences("default", MODE_PRIVATE);
					SharedPreferences.Editor prefEditor = defaultCarPrefs.edit();
					prefEditor.putBoolean("hasDefault", true);
					prefEditor.putInt("year", car.year);
					prefEditor.putString("manufacturer", car.manufacturer);
					prefEditor.putString("model", car.model);
					prefEditor.putString("vehicleClass", car.vehicleClass);
					prefEditor.clear();
					prefEditor.commit();
					
					defaultButton.setOnClickListener(null);
					ButtonSettings.removePressSize(defaultButton);
					defaultButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.is_default_unpress));
					//TODO UPDATE THE NAV BAR WITH NEW DEFAULT
				}
			});
		}
		setOverflowClick();
	}
	
	@SuppressLint("ShowToast")
	private void setOverflowClick() { 
		cylinderInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CarProfileActivity.this, "Cylinders: " + cylinderInfo.getText(), Toast.LENGTH_LONG).show();
			}
		});
		engineInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CarProfileActivity.this, "Engine Size: " + engineInfo.getText(), Toast.LENGTH_LONG).show();
			}
		});
		fuelInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CarProfileActivity.this, "Fuel Type: " + fuelInfo.getText(), Toast.LENGTH_LONG).show();
			}
		});
		tranInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CarProfileActivity.this, "Transmission: " + tranInfo.getText(), Toast.LENGTH_LONG).show();
			}
		});
		vehicleInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CarProfileActivity.this, "Vehicle Type: " + vehicleInfo.getText(), Toast.LENGTH_LONG).show();
			}
		});
		efficiencyInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CarProfileActivity.this, "Highway Fuel Efficiency: " + efficiencyInfo.getText(), Toast.LENGTH_LONG).show();
			}
		});
		emissionsInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CarProfileActivity.this, "CO2 Emissions: " + emissionsInfo.getText() + "kg/year", Toast.LENGTH_LONG).show();
			}
		});
		gearInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(CarProfileActivity.this, "Gears: " + gearInfo.getText(), Toast.LENGTH_LONG).show();
			}
		});
	}
}