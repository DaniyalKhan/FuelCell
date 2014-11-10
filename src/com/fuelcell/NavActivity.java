package com.fuelcell;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fuelcell.models.CarFrame;
import com.fuelcell.ui.DrawerItem;
import com.fuelcell.ui.DrawerNavAdapter;
import com.fuelcell.ui.DrawerItem.DrawerItemType;

public class NavActivity extends FragmentActivity {
	protected DrawerLayout mDrawer;
	protected ListView mDrawerList;
	public String[] entries = {"home","search","find route","favourites","default"};
	public static String[] keys = {"home","search","find route","favourites","default"};
	public DrawerItem[] items;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.base_layout);
		
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		setDrawerItems();

		mDrawerList.setAdapter(new DrawerNavAdapter(this, R.layout.drawer_list_item, R.id.label, items));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawer.setScrimColor(getResources().getColor(R.color.black_overlay));
		
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
			    if (items[position].header.equalsIgnoreCase("Home")) {
			    	Intent intent = new Intent(NavActivity.this,
							SearchActivity.class);
					startActivity(intent);
					//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
			    } else if (items[position].header.equalsIgnoreCase("Search")) {
			    	Intent intent = new Intent(NavActivity.this,
							SearchActivity.class);
					startActivity(intent);
					//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
			    } else if (items[position].header.equalsIgnoreCase("Find Routes")) {
			    	Intent intent = new Intent(NavActivity.this,
							DirectionsActivity.class);
					startActivity(intent);
					//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
			    } else if (items[position].header.equalsIgnoreCase("Favourites")) {
			    	Intent intent = new Intent(NavActivity.this,
							StatsActivity.class);
					startActivity(intent);
					//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
			    } else if (position != 0 && items[position - 1].header.equalsIgnoreCase("Default Car")) {
			    	//Check if header is default car
			    	Intent intent = new Intent(NavActivity.this,
							CarProfileActivity.class);
					startActivity(intent);
					//Close drawer after leaving page
					mDrawer.closeDrawer(Gravity.LEFT);
			    } 
			  }
			});
	}
	public void setDrawerItems(){
		items = new DrawerItem[7];
		items[0] = new DrawerItem("Navigation", DrawerItemType.Header);
		items[1] = new DrawerItem("Home", DrawerItemType.Item);
		items[2] = new DrawerItem("Search", DrawerItemType.Item);
		items[3] = new DrawerItem("Find Route", DrawerItemType.Item);
		items[4] = new DrawerItem("Favourites", DrawerItemType.Item);
		
		SharedPreferences defaultCarPrefs = getSharedPreferences("default", MODE_PRIVATE);
		CarFrame defaultCarFrame = new CarFrame(defaultCarPrefs.getInt("year", -1),
				defaultCarPrefs.getString("manufacturer", null), 
				defaultCarPrefs.getString("model", null), 
				defaultCarPrefs.getString("vehicleClass", null));
		
		items[5] = new DrawerItem("Default Car", DrawerItemType.Header);
		items[6] = new DrawerItem(defaultCarFrame.year + " " 
								+ defaultCarFrame.manufacturer + " " 
								+ defaultCarFrame.model, DrawerItemType.Item);
		
	}
}

class DrawerItemClickListener implements ListView.OnItemClickListener {

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (NavActivity.keys[position] == NavActivity.keys[0]) {
			
		} else if (NavActivity.keys[position] == NavActivity.keys[1]) {
			
		} else if (NavActivity.keys[position] == NavActivity.keys[2]) {
			
		}
	}
}