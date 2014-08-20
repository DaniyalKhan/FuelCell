package com.fuelcell;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.fuelcell.action.ButtonSettings;
import com.fuelcell.models.Car;

public class HubActivity extends Activity {

	Button search;
	Button findRoute;
	Button favourite;
	
	public static List<Car> filtered;
	ArrayList<Car> cars;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_hub);
		
		search = (Button) findViewById(R.id.search);
		findRoute = (Button) findViewById(R.id.findRoute);
		favourite = (Button) findViewById(R.id.favourite);
		
		ButtonSettings.pressSize(search, 15);
		ButtonSettings.pressSize(findRoute, 15);
		ButtonSettings.pressSize(favourite, 15);
		
		filtered = new ArrayList<Car>();
		
		setClick();
	}
	
	private void setClick(){
		search.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HubActivity.this, SearchActivity.class);
				startActivity(intent);
			}
			
		});
		findRoute.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HubActivity.this, DirectionsActivity.class);
				startActivity(intent);
			}
			
		});
		favourite.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startStatsActivity(Car.getSavedCars(HubActivity.this),"Saved",
						"You do not currently have any car profiles saved.", Car.getSavedCars(HubActivity.this).size() > 0);
//				Intent intent = new Intent(HubActivity.this, StatsActivity.class);
//				startActivity(intent);
			}
			
		});
	}
	
	private void startStatsActivity(List<Car> cars,String title,String hint, boolean showClear) {
		Intent intent = new Intent(HubActivity.this, StatsActivity.class);
		filtered = cars;
		intent.putExtra("title", title);
		intent.putExtra("hint", hint);
		intent.putExtra("clear", showClear);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private ArrayList<Car> filterList()
	{
		ArrayList<Car> filtered = new ArrayList<Car>();
			for (int i = 0 ; i < cars.size(); i++) {
				filtered.add(cars.get(i));
				
			}
		
		return filtered;
	}
}