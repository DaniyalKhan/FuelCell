package com.fuelcell;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.fuelcell.models.Car;
import com.fuelcell.models.CarFrame;
import com.fuelcell.ui.DrawerItem;
import com.fuelcell.ui.DrawerItem.DrawerItemType;
import com.fuelcell.ui.DrawerNavAdapter;
import com.fuelcell.util.CarDatabase;

public class NavActivity extends FragmentActivity {
	protected DrawerLayout mDrawer;
	protected ListView mDrawerList;
	public String[] entries = {"home","search","find route","favourites","default"};
	public static String[] keys = {"home","search","find route","favourites","default"};
	public DrawerItem[] items;
	public static List<CarFrame> filtered;
	private CarFrame defaultCarFrame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.base_layout);
		
		mDrawer =  (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		setDrawerItems();

		mDrawerList.setAdapter(new DrawerNavAdapter(this, R.layout.drawer_list_item, R.id.label, items));
		mDrawer.setScrimColor(getResources().getColor(R.color.black_overlay));
		mDrawerList.setBackgroundColor(Color.WHITE);
		
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
			    if (items[position].header.equalsIgnoreCase("Home")) {
			    	setHome();
					//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
			    } else if (items[position].header.equalsIgnoreCase("Search")) {
			    	setHome();
			    	Intent intent = new Intent(NavActivity.this,
							SearchActivity.class);
					startActivity(intent);
					//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
			    } else if (items[position].header.equalsIgnoreCase("Find Route")) {
			    	setHome();
			    	SharedPreferences defaultCarPrefs = getSharedPreferences("default", MODE_PRIVATE);
					if (defaultCarPrefs.getBoolean("hasDefault", false)){
						Intent intent = new Intent(NavActivity.this,
								DirectionsActivity.class);
					startActivity(intent);
					//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
					} else {
						Toast.makeText(NavActivity.this, "A default car is needed to try Find Route. Please select one through Search or Favourites.", Toast.LENGTH_LONG).show();
					}
			    } else if (items[position].header.equalsIgnoreCase("Favourites")) {
			    	setHome();
			    	Intent intent = new Intent(NavActivity.this,
							StatsActivity.class);
					filtered = CarDatabase.obtain(NavActivity.this).getFavCarFrames();
					intent.putExtra("title", "Saved");
					intent.putExtra("hint", "You do not currently have any car profiles saved.");
					intent.putExtra("clear", Car.getSavedCars(NavActivity.this).size() > 0);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
			    } else if (position != 0 && items[position - 1].header.equalsIgnoreCase("Default Car")) {
			    	setHome();
			    	//Check if header is default car
			    	Intent intent = new Intent(NavActivity.this,
							CarProfileActivity.class);
			    	CarFrame defaultCarFrame = getDefaultCar();
			    	if (defaultCarFrame.year > 0) {
			    	CarFrame.saveCarToIntent(intent, Integer.toString(defaultCarFrame.year), 
			    			defaultCarFrame.manufacturer, 
			    			defaultCarFrame.model, 
			    			defaultCarFrame.vehicleClass);
			    	//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
					startActivity(intent);
			    	} else {
			    		Toast.makeText(NavActivity.this, "There is currently no Default Car to open.", Toast.LENGTH_LONG).show();
			    	}
			    } 
			  }
			});

		mDrawer.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDrawerOpened(View arg0) {
				CarFrame car = getDefaultCar();
				if (car.year > 0) {
				DrawerNavAdapter.changeDefaultCarNavDrawer(car.year + " " 
						+ car.manufacturer + " " 
						+ car.model);
				} else {
					DrawerNavAdapter.changeDefaultCarNavDrawer("No Default Car Set");
				}
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	public void setDrawerItems(){
		items = new DrawerItem[7];
		items[0] = new DrawerItem("NAVIGATION", DrawerItemType.Header);
		items[1] = new DrawerItem("Home", DrawerItemType.Item);
		items[2] = new DrawerItem("Search", DrawerItemType.Item);
		items[3] = new DrawerItem("Find Route", DrawerItemType.Item);
		items[4] = new DrawerItem("Favourites", DrawerItemType.Item);
		
		CarFrame defaultCarFrame = getDefaultCar();
		
		items[5] = new DrawerItem("DEFAULT CAR", DrawerItemType.Header);
		if (defaultCarFrame.year > 0) {
			items[6] = new DrawerItem(defaultCarFrame.year + " " 
								+ defaultCarFrame.manufacturer + " " 
								+ defaultCarFrame.model, DrawerItemType.Item); 
		} else {
			items[6] = new DrawerItem("No Default Car Set", DrawerItemType.Item);
		}
		
	}
	public CarFrame getDefaultCar(){
		SharedPreferences defaultCarPrefs = getSharedPreferences("default", MODE_PRIVATE);
		defaultCarFrame = new CarFrame(defaultCarPrefs.getInt("year", -1),
				defaultCarPrefs.getString("manufacturer", null), 
				defaultCarPrefs.getString("model", null), 
				defaultCarPrefs.getString("vehicleClass", null));
			return defaultCarFrame;
	}
	//Open a new home page so go back to when using Quick Nav
	private void setHome(){
		Intent intent = new Intent(NavActivity.this,
				HubActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	protected void setIndicator(String pageName){
		//findViewById(R.id.indicator).setVisibility(View.VISIBLE);
	}
}