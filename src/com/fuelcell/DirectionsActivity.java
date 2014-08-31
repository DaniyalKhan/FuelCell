package com.fuelcell;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fuelcell.google.Directions;
import com.fuelcell.google.Directions.DirectionCallback;
import com.fuelcell.google.Directions.Route;
import com.fuelcell.models.Car;
import com.fuelcell.ui.DirectionsFragment;
import com.fuelcell.ui.DirectionsFragment.RouteCallback;
import com.fuelcell.util.JSONUtil;

public class DirectionsActivity extends FragmentActivity {
	
	private Car car;
	private Directions directions;
	
	private RouteCallback routeCallback = new RouteCallback() {
		@Override
		public void onRouteSelect(Route r) {
			Intent intent = new Intent(DirectionsActivity.this, TravelActivity.class);
//			intent.putExtra("car", car);
			intent.putExtra("route", r);
			intent.putExtra("srcLat", r.srcLat);
			intent.putExtra("srcLng", r.srcLng);
			intent.putExtra("dstLat", r.dstLat);
			intent.putExtra("dstLng", r.dstLng);
			startActivity(intent);
		}
	};
	
	
	private DirectionCallback callback = new DirectionCallback() {		
		@Override
		public void onDirectionsReceived(String result) {
			if (result == null) {
				Toast.makeText(DirectionsActivity.this, "Unable to get route information, please check your internet connection.", Toast.LENGTH_LONG).show();
				return;
			}
			List<Route> routes = new ArrayList<Route>();
			JSONObject resultJSON = null;
			try {
				resultJSON = new JSONObject(result);
			} catch (JSONException e) {
				e.printStackTrace();
				enterFragment(routes);
				return;
			}
			JSONArray routesJSON = JSONUtil.getJSONArray(resultJSON, "routes");
			if (routesJSON == null || routesJSON.length() == 0) {
				enterFragment(routes);
			} else {
				for (int i = 0; i < routesJSON.length(); i++) {
					JSONObject routeJSON = JSONUtil.getJSONObject(routesJSON, i);
					if (routeJSON != null) routes.add(new Route(routeJSON));
					enterFragment(routes);
				}
			}
		}
	};
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_directions);
		Intent intent = getIntent();
		car = (Car) intent.getParcelableExtra("car");
		directions = new Directions(this, callback );
		
		final EditText origin = (EditText) findViewById(R.id.origin);
		final EditText destination = (EditText) findViewById(R.id.destination);
		final Button findRoutes = (Button) findViewById(R.id.find_routes);
		
		findRoutes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (findViewById(R.id.root) != null) {
					directions.setPoints(origin.getText().toString(), destination.getText().toString());
					directions.makeRequest();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		        }
			}
		});
		
		((Button) findViewById(R.id.homeButton)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent homeIntent = new Intent(DirectionsActivity.this, SearchActivity.class);
				homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP+Intent.FLAG_ACTIVITY_SINGLE_TOP );
				
				startActivity(homeIntent);
			}
			
		});
		
	}
	
	private void enterFragment(List<Route> routes) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
        DirectionsFragment firstFragment = new DirectionsFragment();
        for (Route r: routes) firstFragment.addRoute(r);
        firstFragment.setCallback(routeCallback);
        firstFragment.setArguments(getIntent().getExtras());
        ft.add(R.id.root, firstFragment, "DIRECTION_TAG").addToBackStack(null).commit();
	}

}
