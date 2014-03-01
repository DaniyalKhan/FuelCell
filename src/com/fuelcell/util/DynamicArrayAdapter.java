package com.fuelcell.util;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class DynamicArrayAdapter extends ArrayAdapter<String>{
	
	private Filter filter;
	private ArrayList<String> objects;
	
	public DynamicArrayAdapter(Context context, int resource, ArrayList<String> objects) {
		super(context, resource, objects);
		//need to store copy so when we clear the adapter, we dont clear all objects by accident
		this.objects = new ArrayList<String>(objects);
	}
	
	@Override
	public Filter getFilter() {
		if (filter == null) filter = new DynamicFilter();
		return filter;
	}

	public class DynamicFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<String> filtered = new ArrayList<String>();
                for(int i = 0; i < objects.size(); i++) {
                    String s = objects.get(i);
                    if (s.toLowerCase().contains(constraint)) filtered.add(s);
                }
                result.values = filtered;
                result.count = filtered.size();
            }
            else {
            	result.values = objects;
                result.count = getCount();
            }
            return result;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			ArrayList<String> filtered = (ArrayList<String>) results.values;
            clear();
            for(int i = 0, l = filtered.size(); i < l; i++) add(filtered.get(i));
            notifyDataSetChanged();
		}
		
	}
	
}