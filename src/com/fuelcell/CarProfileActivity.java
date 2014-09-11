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

import com.fuelcell.models.Car;

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
	
	Button planTrip;
	Button save;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_car_profile);
		
		getWindow().setBackgroundDrawableResource(R.drawable.background);
		
		Intent intent = getIntent();
		
		car = (Car) intent.getParcelableExtra("car");
		
		carName = (TextView) findViewById(R.id.carName);
		cylinderInfo = (TextView) findViewById(R.id.cylinderInfo);
		engineInfo = (TextView) findViewById(R.id.engineInfo);
		tranInfo = (TextView) findViewById(R.id.tranInfo);
		fuelInfo = (TextView) findViewById(R.id.fuelInfo);
		vehicleInfo = (TextView) findViewById(R.id.vehicleInfo);
		efficiencyInfo = (TextView) findViewById(R.id.efficiencyInfo);
		emissionsInfo = (TextView) findViewById(R.id.emissionsInfo); 
				
		carName.setText(car.getYear() + " " + car.getManufacturer() + " " + car.getModel());
		cylinderInfo.setText(Integer.toString(car.getCylinders()));
		engineInfo.setText(Float.toString((float) ((int) (car.getEngineSize()*100)/100)));
		tranInfo.setText(car.getTransmission());
		fuelInfo.setText(car.getFuelType().toString());
		vehicleInfo.setText(car.getVehicleClass());
		efficiencyInfo.setText(Float.toString((float) ((int) (car.getHighwayEffL()*100)/100)));
		emissionsInfo.setText(Double.toString(car.getEmissions()));
		
		planTrip = (Button) findViewById(R.id.tripButton);
		save = (Button) findViewById(R.id.saveButton);
				
		int stars = (int) Math.round(5 * (1.0 - ((car.getHighwayEffL() - SearchActivity.bestFuelEfficiency)/(SearchActivity.worstFuelEfficiency - SearchActivity.bestFuelEfficiency))));
		if(stars>=1)
			findViewById(R.id.star1).setBackgroundResource(R.drawable.startrue);
		if(stars>=2)
			findViewById(R.id.star2).setBackgroundResource(R.drawable.startrue);
		if(stars>=3)
			findViewById(R.id.star3).setBackgroundResource(R.drawable.startrue);
		if(stars>=4)
			findViewById(R.id.star4).setBackgroundResource(R.drawable.startrue);
		if(stars>=5)
			findViewById(R.id.star5).setBackgroundResource(R.drawable.startrue);
		
		planTrip.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intentTrip = new Intent(CarProfileActivity.this, DirectionsActivity.class);
				intentTrip.putExtra("car", car);
				startActivity(intentTrip);
				
			}
			
		});
		
		((ImageView) findViewById(R.id.mainicon)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent homeIntent = new Intent(CarProfileActivity.this, SearchActivity.class);
				homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP+Intent.FLAG_ACTIVITY_SINGLE_TOP );
				startActivity(homeIntent);
			}
			
		});
		
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				boolean saved = car.saveToProfile(CarProfileActivity.this);
				Context context = getApplicationContext();
				CharSequence text = saved ? car.getModel() + " saved to profile" : car.getModel() + " already saved to profile";
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.show();
			}
			
		});
	}
}