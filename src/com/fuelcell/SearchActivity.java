package com.fuelcell;

import java.io.File;
import java.io.IOException;

import com.fuelcell.google.Directions;
import com.fuelcell.google.Directions.DirectionCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import com.fuelcell.csvutils.CSVParser;
import com.fuelcell.models.Car;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SearchActivity extends Activity {

	MyEditText searchCorp;
	MyEditText searchYear;
	MyEditText searchModel;
	ImageView logo;
	ListView searchList;
	RelativeLayout layout;
	ArrayAdapter<Integer> yearAdapter;
	ContextWrapper wrapper;
	ArrayList<Car> cars = new ArrayList<Car>();;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);

		searchCorp = (MyEditText) findViewById(R.id.searchCorp);
		searchYear = (MyEditText) findViewById(R.id.searchYear);
		searchModel = (MyEditText) findViewById(R.id.searchModel);
		logo = (ImageView) findViewById(R.id.mainicon);

		searchList = (ListView) findViewById(R.id.searchList);
		layout = (RelativeLayout) findViewById(R.layout.activity_search);
		wrapper = new ContextWrapper(this);

		File[] filesArray = wrapper.getFilesDir().listFiles();
		
		for (int i = 0; i < filesArray.length; i++) {
			try {
				cars.addAll(new CSVParser(filesArray[i]).parse());
				System.out.println(i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Set<Integer> year = new HashSet<Integer>();
		
		for (int i = 0; i < cars.size(); i++) {
			year.add(cars.get(i).getYear());
		}

		// String[] carList = { "a", "b", "c" ,"d","e","f","g","h","i","j"};
		yearAdapter = new ArrayAdapter<Integer>(this, R.layout.list_item, year.toArray(new Integer[year.size()]));
		searchList.setAdapter(yearAdapter);

		// need these so the text fields can reshow everything when user presses
		// back,
		// needs a reference to everything that needs to show back up
		searchCorp.set(searchCorp, searchModel, searchYear, logo, searchList);
		searchYear.set(searchCorp, searchModel, searchYear, logo, searchList);
		searchModel.set(searchCorp, searchModel, searchYear, logo, searchList);

		setClick(searchCorp);
		setClick(searchYear);
		setClick(searchModel);

		setTextChange(searchCorp);
		setTextChange(searchYear);
		setTextChange(searchModel);

		startDirectionsActivity(cars.get(0));
		
//		 Directions d = new Directions("toronto", "vancouver", new DirectionCallback() {			
//			@Override
//			public void onDirectionsReceived(String result) {
//				startDirectionsActivity(cars.get(0), result);
//			}
//		});
//		 d.setPoints("toronto", "vancouver");
//		 d.makeRequest();
//		 d.routesExist();
//		 new Directions().setPoints("toronto", "vancouver");

	}
	
	private void startDirectionsActivity(Car car) {
		Intent intent = new Intent(this, DirectionsActivity.class);
		intent.putExtra("car", car);
		startActivity(intent);
	}

	protected void setClick(EditText textField) {
		textField.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				makeInVisible(v);
			}
		});
		textField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeInVisible(v);
			}
		});
	}

	protected void setTextChange(EditText textField) {
		textField.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				searchList.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				searchList.setVisibility(View.GONE);

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				searchList.setVisibility(View.VISIBLE);
				yearAdapter.getFilter().filter(s);
			}

		});
	}

	public void makeInVisible(View v) {
		if (v.hasFocus()) {
			logo.setVisibility(View.GONE);
			if (!v.equals(searchCorp))
				searchCorp.setVisibility(View.GONE);
			if (!v.equals(searchYear))
				searchYear.setVisibility(View.GONE);
			if (!v.equals(searchModel))
				searchModel.setVisibility(View.GONE);
			searchList.setVisibility(View.VISIBLE);
		}
	}

	public static class MyEditText extends EditText {

		View[] views;
		Context context;

		public MyEditText(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			this.context = context;
		}

		public MyEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.context = context;
		}

		public MyEditText(Context context) {
			super(context);
			this.context = context;
		}

		public void set(View... v) {
			this.views = v;
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
								for (View v : views) {
									v.setVisibility(View.VISIBLE);
								}
								views[views.length - 1]
										.setVisibility(View.GONE);
							}
						}
					});
				}
			}).start();
			return false;
		}

	}

}
