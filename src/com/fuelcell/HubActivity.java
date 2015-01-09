package com.fuelcell;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fuelcell.action.ButtonSettings;
import com.fuelcell.models.Car;
import com.fuelcell.models.CarFrame;
import com.fuelcell.util.CarDatabase;

public class HubActivity extends NavActivity {

	Button search;
	Button findRoute;
	Button favourite;
	ImageView home;

	public static List<CarFrame> filtered;
	ArrayList<Car> cars;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater.inflate(R.layout.activity_hub,  null, false);
		mDrawer.addView(contentView,0);
		
		search = (Button) findViewById(R.id.search);
		findRoute = (Button) findViewById(R.id.findRoute);
		favourite = (Button) findViewById(R.id.favourite);
		home = (ImageView) findViewById(R.id.banner);

		filtered = new ArrayList<CarFrame>();
		
		setIndicator("NavActivity");

		setClick();
	}

	private void setClick() {
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HubActivity.this,
						SearchActivity.class);
				startActivity(intent);
			}

		});
		findRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences defaultCarPrefs = getSharedPreferences("default", MODE_PRIVATE);
				if (defaultCarPrefs.getBoolean("hasDefault", false)){
				Intent intent = new Intent(HubActivity.this,
						DirectionsActivity.class);
				startActivity(intent);
				} else {
					Toast.makeText(HubActivity.this, "A default car is needed to try Find Route. Please select one through Search or Favourites.", Toast.LENGTH_LONG).show();
				}
			}

		});
		favourite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startStatsActivity(CarDatabase.obtain(HubActivity.this).getFavCarFrames(), "Saved",
						"You do not currently have any car profiles saved.",
						Car.getSavedCars(HubActivity.this).size() > 0);
			}

		});
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawer.openDrawer(Gravity.LEFT);
			}

		});
	}

	private void startStatsActivity(List<CarFrame> list, String title, String hint,
			boolean showClear) {
		Intent intent = new Intent(HubActivity.this, StatsActivity.class);
		filtered = list;
		intent.putExtra("title", title);
		intent.putExtra("hint", hint);
		intent.putExtra("clear", showClear);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private ArrayList<Car> filterList() {
		ArrayList<Car> filtered = new ArrayList<Car>();
		for (int i = 0; i < cars.size(); i++) {
			filtered.add(cars.get(i));

		}

		return filtered;
	}
}