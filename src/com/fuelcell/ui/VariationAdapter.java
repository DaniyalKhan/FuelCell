package com.fuelcell.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuelcell.R;
import com.fuelcell.models.Car;
import com.fuelcell.ui.DrawerItem.DrawerItemType;

public class VariationAdapter extends ArrayAdapter<Car> {

	Context context;
	Car[] items;
	
	public VariationAdapter(Context context, int resource, Car[] items) {
		super(context, resource, items);
		this.context = context;
		this.items = items;
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView;
	    rowView = inflater.inflate(R.layout.variations_list_item, parent, false);
	    TextView fuelType = (TextView) rowView.findViewById(R.id.fuelType);
		TextView cylinders = (TextView) rowView.findViewById(R.id.cylinders);
		TextView engineSize = (TextView) rowView.findViewById(R.id.engineSize);
		TextView transmission = (TextView) rowView.findViewById(R.id.transmission);
		TextView gears = (TextView) rowView.findViewById(R.id.gears);
		
		fuelType.setText("Fuel Type: " + items[position].fuelType.toString());
		cylinders.setText("Cylinders: " + Integer.toString(items[position].cylinders));
		engineSize.setText("Engine Size: " + Double.toString(items[position].engineSize));
		transmission.setText("Transmission: " + items[position].transmission.toString());
		gears.setText("Gears: " + Integer.toString(items[position].gears));

	    return rowView;
	  }
	
}