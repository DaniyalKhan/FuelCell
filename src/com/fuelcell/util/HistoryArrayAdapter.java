package com.fuelcell.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuelcell.R;

public class HistoryArrayAdapter extends ArrayAdapter<HistoryItem> {

	Context context;
	public List<HistoryItem> list = new ArrayList<HistoryItem>();
	public List<HistoryItem> all = new ArrayList<HistoryItem>();
	public HistoryFilter filter;

	public HistoryArrayAdapter(Context context, int resource,
			List<HistoryItem> objects) {
		super(context, resource, objects);
		this.context = context;
		this.list.addAll(objects);
		this.all.addAll(objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final ViewHolder viewHolder = new ViewHolder();
			rowView = inflater.inflate(R.layout.fav_list_item, parent, false);
			viewHolder.text = (TextView) rowView.findViewById(R.id.text);
			viewHolder.text.setText(list.get(position).getValue());
			viewHolder.delete = (ImageView) rowView.findViewById(R.id.delete);
			rowView.setTag(viewHolder);
			
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		HistoryItem s = getItem(position);
		
		holder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CarDatabase.obtain(context).removeHistory(
						all.get(position));
				list.remove(all.get(position));
				all.remove(position);
				notifyDataSetChanged();
				getFilter().filter(filter.constraint);
			}

		});
		
		String lower = s.getValue().toLowerCase();
		if (filter != null || !filter.constraint.equals("") || lower.contains(filter.constraint.toString())){
			int indexStart = lower.indexOf(filter.constraint.toString(), 0);
	    	int indexEnd = indexStart + filter.constraint.length();
	    	if (indexStart >= 0 && indexEnd >= indexStart && indexEnd <= s.getValue().length()) {
	    		holder.text.setText(Html.fromHtml(s.getValue().substring(0, indexStart) + 
	    			"<b>" + s.getValue().substring(indexStart, indexEnd) + "</b>" + 
	    			s.getValue().substring(indexEnd, s.getValue().length())));
	    	}
		} else {
			holder.text.setText(s.getValue());
		}
		
		return rowView;

	}
	
	public void setFilter(HistoryFilter filter, String fieldText) {
		clear();
		this.filter = filter;
		filter.filter(fieldText);
		notifyDataSetChanged();
	}
	
	@Override
	public Filter getFilter() {
		return filter;
	}

	public abstract class HistoryFilter extends Filter {
		public CharSequence constraint = "";

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			list.clear();
			for (int i = 0 ; i < all.size() ; i++) {
				if (all.get(i).getValue().toLowerCase().contains(constraint)) {
					list.add(all.get(i));
				}
			}
			result.values = list;
			result.count = list.size();
			this.constraint = constraint;
			return result;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			ArrayList<HistoryItem> filtered = (ArrayList<HistoryItem>) results.values;
			clear();
	        for(int i = 0, l = filtered.size(); i < l; i++) {
	        	try {
					add(filtered.get(i));
				} catch (IndexOutOfBoundsException e) {
					clear();
					break;
				}
	        }
	        sort(stringComparator);
	        notifyDataSetChanged();
		}

	}
	
	static class ViewHolder {
	    public TextView text;
	    public ImageView delete;
	}
	
	final Comparator<HistoryItem> stringComparator = new Comparator<HistoryItem>() {
		@Override
		public int compare(HistoryItem lhs, HistoryItem rhs) {
			return lhs.getValue().compareTo(rhs.getValue());
		}
	};

}