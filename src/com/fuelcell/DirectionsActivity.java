package com.fuelcell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuelcell.action.ButtonSettings;
import com.fuelcell.google.Directions;
import com.fuelcell.google.Directions.DirectionCallback;
import com.fuelcell.google.Directions.Route;
import com.fuelcell.models.Car;
import com.fuelcell.models.CarFrame;
import com.fuelcell.ui.DirectionsFragment;
import com.fuelcell.ui.DirectionsFragment.RouteCallback;
import com.fuelcell.util.CarDatabase;
import com.fuelcell.util.HistoryArrayAdapter;
import com.fuelcell.util.DynamicArrayAdapter.CarFilter;
import com.fuelcell.util.HistoryArrayAdapter.HistoryFilter;
import com.fuelcell.util.HistoryItem;
import com.fuelcell.util.JSONUtil;

public class DirectionsActivity extends NavActivity {
	
	private Car car;
	private Directions directions;
	MyEditText lastClicked;
	List<View> invisibleViews;
	ListView searchList;
	TextView hint;
	MyEditText origin;
	MyEditText destination;
	Button findRoutes;
	HistoryArrayAdapter adapter;
	List<HistoryItem> list;
	HistoryFilter originFilter;
	HistoryFilter destinationFilter;
	
	private RouteCallback routeCallback = new RouteCallback() {
		@Override
		public void onRouteSelect(Route r) {
			Intent intent = new Intent(DirectionsActivity.this, TravelActivity.class);
			intent.putExtra("car", car);
			intent.putExtra("route", r);
			intent.putExtra("srcLat", r.srcLat);
			intent.putExtra("srcLng", r.srcLng);
			intent.putExtra("dstLat", r.dstLat);
			intent.putExtra("dstLng", r.dstLng);
			startActivity(intent);
		}
	};
	
	private DirectionCallback callback = new DirectionCallback() {		
		@Override
		public void onDirectionsReceived(String result) {
			if (result == null) {
				Toast.makeText(DirectionsActivity.this, "Unable to get route information, please check your internet connection.", Toast.LENGTH_LONG).show();
				return;
			}
			List<Route> routes = new ArrayList<Route>();
			JSONObject resultJSON = null;
			try {
				resultJSON = new JSONObject(result);
			} catch (JSONException e) {
				e.printStackTrace();
				enterFragment(routes);
				return;
			}
			JSONArray routesJSON = JSONUtil.getJSONArray(resultJSON, "routes");
			if (routesJSON == null || routesJSON.length() == 0) {
				enterFragment(routes);
			} else {
				for (int i = 0; i < routesJSON.length(); i++) {
					JSONObject routeJSON = JSONUtil.getJSONObject(routesJSON, i);
					if (routeJSON != null) routes.add(new Route(routeJSON));
					enterFragment(routes);
				}
			}
		}
	};
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater.inflate(R.layout.activity_directions,  null, false);
		mDrawer.addView(contentView,0);
		
		getWindow().setBackgroundDrawableResource(R.drawable.background);
		
		Intent intent = getIntent();
		car = (Car) intent.getParcelableExtra("car");
		directions = new Directions(this, callback );
		
		origin = (MyEditText) findViewById(R.id.origin);
		origin.setTextHeader(findViewById(R.id.searchHeaderOrigin));
		setTextChange(origin);
		destination = (MyEditText) findViewById(R.id.destination);
		destination.setTextHeader(findViewById(R.id.searchHeaderDestination));
		setTextChange(destination);
		findRoutes = (Button) findViewById(R.id.find_routes);
		
		hint = (TextView) findViewById(R.id.hint);
		searchList = (ListView) findViewById(R.id.searchList);
		
		invisibleViews = Arrays.asList(findRoutes,findViewById(R.id.mainicon),findViewById(R.id.locationicon), origin, destination, origin.textHeader, destination.textHeader);
		list = CarDatabase.obtain(DirectionsActivity.this).getHistory();
		adapter = new HistoryArrayAdapter(DirectionsActivity.this, R.layout.fav_list_item , list);
		searchList.setAdapter(adapter);
		
		originFilter = adapter.new HistoryFilter(){};
		destinationFilter = adapter.new HistoryFilter(){};

		origin.setFilter(originFilter);
		destination.setFilter(destinationFilter);
		
		searchList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (origin.hasFocus() || origin.textHeader.hasFocus()) {
					origin.setText(adapter.getItem(position).getValue());
				} else {
					destination.setText(adapter.getItem(position).getValue());
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(destination.getWindowToken(), 0);

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						DirectionsActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								visible();
							}
						});
					}
				}).start();
			}
			
		});
		
		setClick(origin);
		setClick(destination);
		
		findRoutes.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				if (findViewById(R.id.root) != null) {
					directions.setPoints(origin.getText().toString(), destination.getText().toString());
					directions.makeRequest();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					String[] text = new String[]{origin.getText().toString(),destination.getText().toString()};

					//Stop save if it is null/etc
					if (!(text[0] == null || text[0].equals("") || text[0].isEmpty() ||
							text[1] == null || text[1].equals("") || text[1].isEmpty())){
						for (int i = 0 ; i < text.length ; i++){
							if (CarDatabase.obtain(DirectionsActivity.this).isUniqueHistory(text[i])){
								HistoryItem newItem = CarDatabase.obtain(DirectionsActivity.this).setHistory(text[i]);
								adapter.list.add(newItem);
								adapter.all.add(newItem);
								adapter.getFilter().filter(adapter.filter.constraint);
								adapter.notifyDataSetChanged();
							} else {
								//update time if already exists
								CarDatabase.obtain(DirectionsActivity.this).updateHistory(text[i]);
							}
						}
					}
		        }
			}
		});
		
		ButtonSettings.setHomeButton(((ImageView) findViewById(R.id.mainicon)),mDrawer);
		
	}
	
	private void enterFragment(List<Route> routes) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
        DirectionsFragment firstFragment = new DirectionsFragment();
        for (Route r: routes) firstFragment.addRoute(r);
        firstFragment.setCallback(routeCallback);
        firstFragment.setArguments(getIntent().getExtras());
        ft.add(R.id.root, firstFragment, "DIRECTION_TAG").addToBackStack(null).commit();
	}
	
	protected void setClick(final MyEditText textField) {
		textField.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				onActionTextField(textField);	
			}
		});
		textField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onActionTextField(textField);
			}
		});
	}

	private void onActionTextField(MyEditText editText) {
		if (editText.hasFocus() || editText.textHeader.hasFocus()) {
			makeInvisible(editText);
			lastClicked = editText;
			adapter.setFilter(editText.filter, editText.getText().toString());
			adapter.notifyDataSetChanged();
		}
	}
	
	public void makeInvisible(MyEditText t) {
		if (t.hasFocus() || t.textHeader.hasFocus()) {
			for (View v: invisibleViews) {
				//only turn invisible the views that are not in focus! (the ones that are not clicked)
				if (!v.equals(t) &&  !v.equals(t.textHeader)) v.setVisibility(View.GONE);
			}
			searchList.setVisibility(View.VISIBLE);
			hint.setVisibility(View.VISIBLE);
		}
	}
	
	public void visible() {
		for (View v: invisibleViews) v.setVisibility(View.VISIBLE);
		searchList.setVisibility(View.GONE);
		hint.setVisibility(View.GONE);
	}
	
	protected void setTextChange(MyEditText textField) {
		textField.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				adapter.getFilter().filter(s.toString());
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapter.getFilter().filter(s.toString());
			}


		});
	}
	
	public static class MyEditText extends EditText {
		
		Context context;
		View textHeader;
		HistoryFilter filter;

		public MyEditText(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			this.context = context;
		}

		public MyEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.context = context;
		}

		public void setTextHeader(View header) {
			this.textHeader = header;
		}
		
		public MyEditText(Context context) {
			super(context);
			this.context = context;
		}
		
		public boolean onKeyPreIme(final int keyCode, KeyEvent event) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (keyCode == KeyEvent.KEYCODE_BACK) {
								((DirectionsActivity)context).visible();
							}
						}
					});
				}
			}).start();
			return false;
		}

		@SuppressLint("NewApi")
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				if (((Activity)this.getContext()).findViewById(R.id.origin).isFocused()){ 
					((DirectionsActivity)context).visible();
					((Activity)this.getContext()).findViewById(R.id.destination).requestFocus();
				} else if (((Activity)this.getContext()).findViewById(R.id.destination).isFocused()) {
					((DirectionsActivity)context).visible();
					((Activity)this.getContext()).findViewById(R.id.find_routes).callOnClick();
				}
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		
		public void setFilter(HistoryFilter filter) {
			this.filter = filter;
		}
		
		public HistoryFilter getFilter() {
			return filter;
		}
	}

}
