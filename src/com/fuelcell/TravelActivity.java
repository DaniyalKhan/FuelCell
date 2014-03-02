package com.fuelcell;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.TextView;

import com.fuelcell.google.Directions.Route;
import com.fuelcell.google.GMapV2Direction;
import com.fuelcell.google.GMapV2Direction.DocCallback;
import com.fuelcell.models.Car;
import com.fuelcell.util.JSONUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TravelActivity extends FragmentActivity {
	
	private Car car;
	private Route route;
	
	// m/s
	private static final double HIGHWAY_LIMIT = 80.0 * 1000.0/3600.0; 
	
	private double highwayMetres;
	private double cityMetres;
	private double highwaySeconds;
	private double citySeconds;
	
	private GMapV2Direction md;
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.travel_activity);
		Intent intent = getIntent();
		car = (Car) intent.getParcelableExtra("car");
		route = (Route) intent.getParcelableExtra("route");
		
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Loading Google Map Route...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
		
		md = new GMapV2Direction();
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		final LatLng src = new LatLng(intent.getDoubleExtra("srcLat", 0), intent.getDoubleExtra("srcLng", 0));
		final LatLng dst = new LatLng(intent.getDoubleExtra("dstLat", 0), intent.getDoubleExtra("dstLng", 0));
		md.getDocument(src, dst, GMapV2Direction.MODE_DRIVING, new DocCallback() {
			@Override
			public void onFinished(final Document doc) {
				TravelActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ArrayList<LatLng> directionPoint = md.getDirection(doc);
			            PolylineOptions rectLine = new PolylineOptions().width(3).color(
			                    Color.RED);

			            for (int i = 0; i < directionPoint.size(); i++) {
			                rectLine.add(directionPoint.get(i));
			            }
			            double dist = Math.sqrt((dst.latitude * dst.latitude - src.latitude * src.latitude) +
			            		(dst.longitude * dst.longitude - src.longitude * src.longitude));
			            Polyline polylin = map.addPolyline(rectLine);
			            map.animateCamera(CameraUpdateFactory.newLatLngZoom(src, (float)dist/40f));
			            dialog.dismiss();
					}
				});
			}
		});
		
		JSONArray stepsJSON = null;
		
		try {
			stepsJSON = new JSONArray(route.stepString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (stepsJSON != null) {
			for (int i = 0; i < stepsJSON.length(); i++) {
				JSONObject stepJSON = JSONUtil.getJSONObject(stepsJSON, i);
				if (stepJSON != null) {
					double distance = Double.parseDouble(JSONUtil.getString(JSONUtil.getJSONObject(stepJSON, "distance"), "value"));
					double duration = Double.parseDouble(JSONUtil.getString(JSONUtil.getJSONObject(stepJSON, "duration"), "value"));
					double speedMS = distance/duration;
					if (speedMS >= HIGHWAY_LIMIT) {
						highwayMetres += distance;
						highwaySeconds += duration;
					} else {
						cityMetres += distance;
						citySeconds += duration;
					}
				}
			}
		}
		
		double highwayKM = highwayMetres/1000.0;
		double cityKM = cityMetres/1000.0;
		
		//litres / KM
		double hEffM = car.getHighwayEffL()/100.0;
		double cEffL = car.getCityEffL()/100.0;
		
		//g / s assuming driving every second
		double emissions = car.getEmissions() * 1000.0 / (31557600.0 / 36000.0);
		
		double litresSpent = hEffM * highwayKM + cEffL * cityKM;
		
		TextView usage = (TextView) findViewById(R.id.usage_value);
		usage.setText(Double.toString(litresSpent));
		
		TextView co2Emission = (TextView) findViewById(R.id.co2_value);
		co2Emission.setText(Double.toString(emissions));
		
	}

	
	
	
}
