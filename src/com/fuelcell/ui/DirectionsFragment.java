package com.fuelcell.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fuelcell.R;
import com.fuelcell.google.Directions.Route;
import com.fuelcell.models.Car;


public class DirectionsFragment extends ListFragment {
	
	ArrayList<Route> routes = new ArrayList<Route>();
	RouteCallback callback;
	
	public void setCallback(RouteCallback callback) {
		this.callback = callback;
	}
	
	public void addRoute(Route r) {
		this.routes.add(r);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		setListAdapter(new RouteAdapter(getActivity(), R.layout.route_item, routes));
		return inflater.inflate(R.layout.directions_fragment, container, false);
	}
	
	static class ViewHolder {
		TextView route;
		TextView distance;
		TextView time;
	}
	
	private class RouteAdapter extends ArrayAdapter<Route> {

		public RouteAdapter(Context context, int resource, List<Route> routes) {
			super(context, resource, routes);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
		    // reuse views
		    if (rowView == null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				rowView = inflater.inflate(R.layout.route_item, null);
				// configure view holder
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.route = (TextView) rowView.findViewById(R.id.route);
				viewHolder.distance = (TextView) rowView.findViewById(R.id.distance);
				viewHolder.time = (TextView) rowView.findViewById(R.id.time);
				rowView.setTag(viewHolder);
				rowView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) callback.onRouteSelect(routes.get(position));
					}
				});
		    }

		    // fill data
		    ViewHolder holder = (ViewHolder) rowView.getTag();
		    Route r = getItem(position);
		    holder.route.setText(Html.fromHtml("<b>" + holder.route.getText() + "</b>" + "  " + r.summary));
		    holder.distance.setText(Html.fromHtml("<b>" + holder.distance.getText()+ "</b>" + "  " + r.cDistance));
		    holder.time.setText(Html.fromHtml("<b>" +holder.time.getText() + "</b>" + "  " + r.cTime));
		    return rowView;
		}
		
	}
	
	public interface RouteCallback {
		void onRouteSelect(Route r);
	}
	
}
