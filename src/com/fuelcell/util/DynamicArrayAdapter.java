package com.fuelcell.util;

import java.util.ArrayList;
import java.util.Comparator;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.fuelcell.R;

public class DynamicArrayAdapter extends ArrayAdapter<String>{
	
	private DynamicFilter filter;
	private ArrayList<String> objects;
	private Activity context;
	private TextCallback callback;
	private final Comparator<String> stringComparator = new Comparator<String>() {
		@Override
		public int compare(String lhs, String rhs) {
			return lhs.compareTo(rhs);
		}
	};

	public DynamicArrayAdapter(Activity context, int resource, ArrayList<String> objects, TextCallback callback) {
		super(context, resource, objects);
		sort(stringComparator);
		this.callback = callback;
		this.context = context;
		//need to store copy so when we clear the adapter, we dont clear all objects by accident
		this.objects = new ArrayList<String>(objects);
	}
	
	static class ViewHolder {
	    public TextView text;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View rowView = convertView;
	    // reuse views
	    if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_item, null);
			// configure view holder
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.text);
			rowView.setTag(viewHolder);
			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onClick(viewHolder.text.getText());
				}
			});
	    }

	    // fill data
	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    String s = getItem(position);
	    
	    String lower = s.toLowerCase();
	    if (filter != null && !filter.constraint.equals("") && lower.contains(filter.constraint.toString())) {
		    int indexStart = lower.indexOf(filter.constraint.toString(), 0);
	    	int indexEnd = indexStart + filter.constraint.length();
	    	holder.text.setText(Html.fromHtml(s.substring(0, indexStart) + 
	    			"<b>" + s.substring(indexStart, indexEnd) + "</b>" + 
	    			s.substring(indexEnd, s.length())));
    	} else {
    		holder.text.setText(s);
    	}
	    return rowView;
	}
	
	@Override
	public Filter getFilter() {
		if (filter == null) filter = new DynamicFilter();
		return filter;
	}

	public class DynamicFilter extends Filter {

		CharSequence constraint ="";
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            this.constraint = constraint;
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<String> filtered = new ArrayList<String>();
                for(int i = 0; i < objects.size(); i++) {
                    String s = objects.get(i);
                    if (s.toLowerCase().contains(constraint)) {
                    	filtered.add(s);
                    }
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
            sort(stringComparator);
            notifyDataSetChanged();
		}
		
	}
	
	public interface TextCallback {
		void onClick(CharSequence charSequence);
	}
	
}