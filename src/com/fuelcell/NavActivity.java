package com.fuelcell;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NavActivity extends FragmentActivity {
	protected DrawerLayout mDrawer;
	protected ListView mDrawerList;
	public String[] entries = {"home","search","find route","favourites","default"};
	public static String[] keys = {"home","search","find route","favourites","default"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.base_layout);
		
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
//		DrawerAdapter mDrawerAdapter = new DrawerAdapter(this, R.layout.drawer_list_header, drawerListItems);
//		ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, entries));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawer.setScrimColor(getResources().getColor(R.color.black_overlay));
		//allow headers
//		LayoutInflater inflater = getLayoutInflater();
//	    ViewGroup mTop = (ViewGroup)inflater.inflate(R.layout.drawer_header_item, mDrawerList, false);
//	    mDrawerList.addHeaderView(mTop, null, false);
	    //override adapter to take know which are titles, which have icons, maybe more
	    //create nav drawer header
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