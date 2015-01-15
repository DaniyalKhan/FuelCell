package com.fuelcell.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuelcell.R;
import com.fuelcell.ui.DrawerItem.DrawerItemType;

public class DrawerNavAdapter extends ArrayAdapter<DrawerItem> {

	Context context;
	DrawerItem[] items;
	static TextView defaultCar;
	
	public DrawerNavAdapter(Context context, int resource,
			int textViewResourceId, DrawerItem[] items) {
		super(context, resource, textViewResourceId, items);
		this.context = context;
		this.items = items;
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView;
	    if (items[position].type == DrawerItemType.Header) {
	    	rowView = inflater.inflate(R.layout.drawer_header_item, parent, false);
	    	TextView textView = (TextView) rowView.findViewById(R.id.headerLabel);
	    	textView.setText(items[position].header);
	    	rowView.setClickable(false);
	    } else if (items[position].type == DrawerItemType.MainHeader){
	    	rowView = inflater.inflate(R.layout.drawer_main_header, parent, false);
	    	rowView.setClickable(false);
	    } else {
		    rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);
		    TextView textView = (TextView) rowView.findViewById(R.id.label);
		    ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
		    textView.setText(items[position].header);
		    String s = items[position].header;
		    if (s.startsWith("HOME")) {
		    	icon.setImageResource(R.drawable.icon_home);
		    } else if (s.startsWith("SEARC")){
		    	icon.setImageResource(R.drawable.icon_search);
		    } else if (s.startsWith("FIND ROUTE")) {
		    	icon.setImageResource(R.drawable.icon_find_route);
		    } else if (s.startsWith("FAVOURITES")) {
		    	icon.setImageResource(R.drawable.icon_favourite);
		    } else if (position != 0 && items[position - 1].header.equalsIgnoreCase("Default Car")) {
		    	icon.setImageResource(R.drawable.icon_default_car);
		    	textView.setTextSize(12);
		    	defaultCar = textView;
		    }
	    }

	    return rowView;
	  }
	public static void changeDefaultCarNavDrawer(String carName){
		if (!((String) defaultCar.getText()).equalsIgnoreCase(carName)){
			defaultCar.setText(carName);
		}
	}
	
}