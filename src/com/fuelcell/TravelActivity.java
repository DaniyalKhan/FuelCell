package com.fuelcell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.fuelcell.google.Directions.Route;
import com.fuelcell.models.Car;
import com.fuelcell.util.JSONUtil;

public class TravelActivity extends Activity {
	
	private Car car;
	private Route route;
	
	// m/s
	private static final double HIGHWAY_LIMIT = 80.0 * 1000.0/3600.0; 
	
	private double highwayMetres;
	private double cityMetres;
	private double highwaySeconds;
	private double citySeconds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.travel_activity);
//		Intent intent = getIntent();
//		car = (Car) intent.gxent.getParcelableExtra("route");
//		
//		JSONArray stepsJSON = null;
//		
//		try {
//			stepsJSON = new JSONArray(route.stepString);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		if (stepsJSON != null) {
//			for (int i = 0; i < stepsJSON.length(); i++) {
//				JSONObject stepJSON = JSONUtil.getJSONObject(stepsJSON, i);
//				if (stepJSON != null) {
//					double distance = Double.parseDouble(JSONUtil.getString(JSONUtil.getJSONObject(stepJSON, "distance"), "value"));
//					double duration = Double.parseDouble(JSONUtil.getString(JSONUtil.getJSONObject(stepJSON, "duration"), "value"));
//					double speedMS = distance/duration;
//					if (speedMS >= HIGHWAY_LIMIT) {
//						highwayMetres += distance;
//						highwaySeconds += duration;
//					} else {
//						cityMetres += distance;
//						citySeconds += duration;
//					}
//				}
//			}
//		}
//		
//		double highwayKM = highwayMetres/1000.0;
//		double cityKM = cityMetres/1000.0;
//		
//		//litres / KM
//		double hEffM = car.getHighwayEffL()/100.0;
//		double cEffL = car.getCityEffL()/100.0;
//		
//		//g / s assuming driving every second
//		double emissions = car.getEmissions() * 1000.0 / (31557600.0 / 36000.0);
//		
//		double litresSpent = hEffM * highwayKM + cEffL * cityKM;
//		
//		TextView usage = (TextView) findViewById(R.id.usage_value);
//		usage.setText(Double.toString(litresSpent));
//		
//		TextView co2Emission = (TextView) findViewById(R.id.co2_value);
//		co2Emission.setText(Double.toString(emissions));
		
	}

	
	
	
}
