package com.fuelcell.util;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class DynamicArrayAdapter extends ArrayAdapter<String>{
	
	private Filter filter;
	
	public DynamicArrayAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
	}

	
//	
//	@Override
//	public Filter getFilter() {
//		if (filter == null) filter = new DynamicFilter();
//		return filter;
//	}
//


	public class DynamicFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
//            constraint = constraint.toString().toLowerCase();
//            FilterResults result = new FilterResults();
//            if(constraint != null && constraint.toString().length() > 0) {
//                ArrayList<Manga> filt = new ArrayList<Manga>();
//                ArrayList<Manga> lItems = new ArrayList<Manga>();
//                synchronized (items) {
//                    Collections.copy(lItems, items);
//                }
//                for(int i = 0, l = lItems.size(); i < l; i++) {
//                    Manga m = lItems.get(i);
//                    if(m.getName().toLowerCase().contains(constraint))
//                        filt.add(m);
//                }
//                result.count = filt.size();
//                result.values = filt;
//            }
//            else
//            {
//                synchronized(items)
//                {
//                    result.values = items;
//                    result.count = items.size();
//                }
//            }
//            return result;
			return null;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			
		}
		
	}
	
}