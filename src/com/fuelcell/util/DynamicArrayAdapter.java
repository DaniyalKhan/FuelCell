package com.fuelcell.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
import com.fuelcell.models.Car;

public abstract class DynamicArrayAdapter extends ArrayAdapter<String>{
	
	private DynamicFilter filter;
	private ArrayList<String> fields;
	private ArrayList<Car> cars;
	private Activity context;
	private TextCallback callback;
	
	final Comparator<String> stringComparator = new Comparator<String>() {
		@Override
		public int compare(String lhs, String rhs) {
			return lhs.compareTo(rhs);
		}
	};

	public DynamicArrayAdapter(Activity context, int resource, ArrayList<Car> cars, TextCallback callback) {
		super(context, resource);
		sort(stringComparator);
		this.callback = callback;
		this.context = context;
		this.cars = cars;
		this.fields = new ArrayList<String>();
		toFields("");
		for(String s: fields) {
			add(s);
	    }
		sort(stringComparator);
		notifyDataSetChanged();
	}
	
//	private ArrayList<String> firstCall(ArrayList<Car> cars) {
//		ArrayList<String> toReturn = new ArrayList<String>();
//		for(Car c: cars) {
//	        toReturn.add(getFieldForCar(c));
//	    } 
//		return toReturn;
//	}
	
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
	
	private void toFields(CharSequence filter) {
	    Set<String> filtered = new HashSet<String>();
	    for(int i = 0; i < cars.size(); i++) {
	        String s = getFieldForCar(cars.get(i));
	        if (s.toLowerCase().contains(filter) && shouldContain(cars.get(i))) {
	        	filtered.add(s);
	        }
	    }
	    fields.addAll(filtered);
	}
	
	protected boolean shouldContain(Car c) {
		return true;
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
			fields.clear();
            constraint = constraint.toString().toLowerCase();
            this.constraint = constraint;
            FilterResults result = new FilterResults();
            Set<String> filtered = new HashSet<String>();
            toFields(constraint);
            result.values = fields;
            result.count = fields.size();
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
	
	protected abstract String getFieldForCar(Car c);
	
}