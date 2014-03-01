package com.fuelcell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.fuelcell.csvutils.CSVParser;
import com.fuelcell.models.Car;
import com.fuelcell.util.DynamicArrayAdapter;
import com.fuelcell.util.DynamicArrayAdapter.TextCallback;

public class SearchActivity extends Activity {

	MyEditText searchCorp;
	MyEditText searchYear;
	MyEditText searchModel;
	ImageView logo;
	ListView searchList;
	RelativeLayout layout;
	DynamicArrayAdapter yearAdapter;
	DynamicArrayAdapter manufactureAdapter;
	DynamicArrayAdapter modelAdapter;
	ContextWrapper wrapper;
	Button search;
	ArrayList<Car> cars;
	int lastClicked;

	TextCallback callback = new TextCallback() {
		
		@Override
		public void onClick(CharSequence text) {
			if(lastClicked == searchCorp.getId()) {
				searchCorp.setText(text);
				searchCorp.setBackgroundResource((R.drawable.textbargreen));
			}
			else if(lastClicked == searchYear.getId()){
				searchYear.setText(text);
				searchYear.setBackgroundResource((R.drawable.textbargreen));
			}
			else if(lastClicked == searchModel.getId()){
				searchModel.setText(text);
				searchModel.setBackgroundResource((R.drawable.textbargreen));
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);

		searchCorp = (MyEditText) findViewById(R.id.searchCorp);
		searchYear = (MyEditText) findViewById(R.id.searchYear);
		searchModel = (MyEditText) findViewById(R.id.searchModel);
		logo = (ImageView) findViewById(R.id.mainicon);
		search = (Button) findViewById(R.id.searchButton);

		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cars != null) startDirectionsActivity(cars.get(0));
			}
		});
		
		searchList = (ListView) findViewById(R.id.searchList);
		layout = (RelativeLayout) findViewById(R.layout.activity_search);
		wrapper = new ContextWrapper(this);

		// need these so the text fields can reshow everything when user presses back,
		// needs a reference to everything that needs to show back up
		searchCorp.set(searchCorp, searchModel, searchYear, logo, search, searchList);
		searchYear.set(searchCorp, searchModel, searchYear, logo, search, searchList);
		searchModel.set(searchCorp, searchModel, searchYear, logo, search, searchList);

		setClick(searchCorp);
		setClick(searchYear);
		setClick(searchModel);

		setTextChange(searchCorp);
		setTextChange(searchYear);
		setTextChange(searchModel);
		
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
		intent.putExtra("corp", searchCorp.getText());
		intent.putExtra("year", searchYear.getText());
		intent.putExtra("model", searchModel.getText());
		startActivity(intent);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (cars == null) {
			new AsyncTask<Integer, Integer, Boolean>() {

				ProgressDialog progress;
				float exactProgress = 0;

				protected int addProgress(float increment) {
					exactProgress = exactProgress + increment;
					return (int) exactProgress;
				}

				@Override
				protected Boolean doInBackground(Integer... arg0) {
					try {
						File[] filesArray = wrapper.getFilesDir().listFiles();
						cars = new ArrayList<Car>();
						for (int i = 0; i < filesArray.length; i++) {
							try {
								cars.addAll(new CSVParser(filesArray[i])
										.parse());
								progress.incrementProgressBy(addProgress(filesArray.length/200f *100f));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						Set<String> year = new HashSet<String>();
						Set<String> manufacture = new HashSet<String>();
						Set<String> model = new HashSet<String>();

						for (int i = 0; i < cars.size(); i++) {
							year.add(Integer.toString(cars.get(i).getYear()));
							manufacture.add(cars.get(i).getManufacturer());
							model.add(cars.get(i).getModel());
							progress.incrementProgressBy(addProgress(cars.size()/200f *100f));
						}

						yearAdapter = new DynamicArrayAdapter(SearchActivity.this, R.layout.list_item, new ArrayList<String>(year), callback);
						manufactureAdapter = new DynamicArrayAdapter(SearchActivity.this, R.layout.list_item, new ArrayList<String>(manufacture), callback);
						modelAdapter = new DynamicArrayAdapter(SearchActivity.this, R.layout.list_item, new ArrayList<String>(model), callback);
						return true;
					} catch (Exception unfinishedException) {
						return false;
					}
				}

				@Override
				protected void onPreExecute() {
					progress = new ProgressDialog(SearchActivity.this);
					progress.setMessage("Loading");
					progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progress.setProgress(0);
					progress.setMax(100);
					progress.setIndeterminate(false);
					progress.show();
				}

				@Override
				protected void onPostExecute(Boolean result) {
					progress.dismiss();
					if (result == false) {
						AlertDialog.Builder failure = new AlertDialog.Builder(
								SearchActivity.this);
						failure.setMessage("Loading Failed");
					}

				}

			}.execute();
		}
	}
	
	

	protected void setClick(EditText textField) {
		textField.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				makeInVisible(v);
				setListAdapter(v);
				lastClicked = v.getId();
			}
		});
		textField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeInVisible(v);
				setListAdapter(v);
				lastClicked = v.getId();
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
				filter(s);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				searchList.setVisibility(View.VISIBLE);
				filter(s);
				if (searchCorp.getVisibility() == View.VISIBLE)
					searchCorp.setBackgroundResource(R.drawable.textbarwhite);
				if (searchYear.getVisibility() == View.VISIBLE)
					searchCorp.setBackgroundResource(R.drawable.textbarwhite);
				if (searchModel.getVisibility() == View.VISIBLE)
					searchCorp.setBackgroundResource(R.drawable.textbarwhite);
			}

			public void filter(CharSequence s) {
				if (searchCorp.getVisibility() == View.VISIBLE)
					manufactureAdapter.getFilter().filter(s);
				if (searchYear.getVisibility() == View.VISIBLE)
					yearAdapter.getFilter().filter(s);
				if (searchModel.getVisibility() == View.VISIBLE)
					modelAdapter.getFilter().filter(s);
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
			search.setVisibility(View.GONE);

		}
	}

	public void setListAdapter(View v) {
		if (v.hasFocus()) {
			if (v.equals(searchCorp))
				searchList.setAdapter(manufactureAdapter);
			if (v.equals(searchYear))
				searchList.setAdapter(yearAdapter);
			if (v.equals(searchModel))
				searchList.setAdapter(modelAdapter);
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
