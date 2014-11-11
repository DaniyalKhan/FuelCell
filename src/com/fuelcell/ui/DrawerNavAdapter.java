package com.fuelcell.ui;

import android.content.Context;
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
	    //inflate different layout for different type
	    View rowView;
	    if (items[position].type == DrawerItemType.Header) {
	    	rowView = inflater.inflate(R.layout.drawer_header_item, parent, false);
	    	TextView textView = (TextView) rowView.findViewById(R.id.headerLabel);
	    	textView.setText(items[position].header);
//	    	rowView.findViewById(R.id.icon).setVisibility(ImageView.GONE);
	    } else {
		    rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);
		    TextView textView = (TextView) rowView.findViewById(R.id.label);
		    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		    textView.setText(items[position].header);
		    // change the icon for Windows and iPhone
		    String s = items[position].header;
		    if (s.startsWith("Home")) {
		    	
		    } else if (s.startsWith("Search")){
		      
		    } else if (s.startsWith("Find Route")) {
		    	
		    } else if (s.startsWith("Favourites")) {
		    	
		    } else if (position != 0 && items[position - 1].header.equalsIgnoreCase("Default Car")) {
		    	
		    }
	    }

	    return rowView;
	  }
	
}