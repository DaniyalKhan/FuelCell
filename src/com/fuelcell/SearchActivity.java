package com.fuelcell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
import android.view.inputmethod.InputMethodManager;
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
	MyEditText searchVType;
	ImageView logo;
	ListView searchList;
	RelativeLayout layout;
	DynamicArrayAdapter yearAdapter;
	DynamicArrayAdapter manufactureAdapter;
	DynamicArrayAdapter modelAdapter;
	DynamicArrayAdapter vTypeAdapter;
	ContextWrapper wrapper;
	Button search;
	Button saved;
	Button refresh;
	ArrayList<Car> cars;
	int lastClicked;
	public static List<Car> filtered;
	public static Double bestFuelEfficiency;
	public static Double worstFuelEfficiency;
	private Context context;
	

	TextCallback callback = new TextCallback() {
		
		@Override
		public void onClick(CharSequence text) {
			if(lastClicked == searchCorp.getId()) {
				searchCorp.setText(text.toString().replaceAll("<\\/?[b]>", ""));
				searchCorp.setBackgroundResource((R.drawable.textbargreen));
			}
			else if(lastClicked == searchYear.getId()){
				searchYear.setText(text.toString().replaceAll("<\\/?[b]>", ""));
				searchYear.setBackgroundResource((R.drawable.textbargreen));
			}
			else if(lastClicked == searchModel.getId()){
				searchModel.setText(text.toString().replaceAll("<\\/?[b]>", ""));
				searchModel.setBackgroundResource((R.drawable.textbargreen));
			}
			else if(lastClicked == searchVType.getId()){
				searchVType.setText(text.toString().replaceAll("<\\/?[b]>", ""));
				searchVType.setBackgroundResource((R.drawable.textbargreen));
			}
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchCorp.getWindowToken(), 0);
			context = getBaseContext();

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					SearchActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							searchVType.setVisibility(View.VISIBLE);
							searchModel.setVisibility(View.VISIBLE);
							searchYear.setVisibility(View.VISIBLE);
							searchCorp.setVisibility(View.VISIBLE);
							search.setVisibility(View.VISIBLE);
							refresh.setVisibility(View.VISIBLE);
							logo.setVisibility(View.VISIBLE);
							saved.setVisibility(View.VISIBLE);
							searchList.setVisibility(View.GONE);
						}
					});
				}
			}).start();
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
		searchVType = (MyEditText) findViewById(R.id.searchVType);
		logo = (ImageView) findViewById(R.id.mainicon);
		search = (Button) findViewById(R.id.searchButton);
		saved = (Button) findViewById(R.id.saved);
		refresh = (Button) findViewById(R.id.refresh);
		
		filtered = new ArrayList<Car>();

		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cars != null) startStatsActivity(filterList(),"Results");
			}
		});
		
		searchList = (ListView) findViewById(R.id.searchList);
		layout = (RelativeLayout) findViewById(R.layout.activity_search);
		wrapper = new ContextWrapper(this);

		// need these so the text fields can reshow everything when user presses back,
		// needs a reference to everything that needs to show back up
		searchCorp.set(searchCorp, searchModel, searchYear, searchVType, logo, search, saved, refresh, searchList);
		searchYear.set(searchCorp, searchModel, searchYear, searchVType, logo, search, saved, refresh, searchList);
		searchModel.set(searchCorp, searchModel, searchYear, searchVType, logo, search, saved, refresh, searchList);
		searchVType.set(searchCorp, searchModel, searchYear, searchVType, logo, search, saved, refresh, searchList);

		setClick(searchCorp);
		setClick(searchYear);
		setClick(searchModel);
		setClick(searchVType);

		setTextChange(searchCorp);
		setTextChange(searchYear);
		setTextChange(searchModel);
		setTextChange(searchVType);

		saved.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startStatsActivity(Car.getSavedCars(SearchActivity.this),"Saved");
			}
		});
		
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchCorp.getText().clear();
				searchModel.getText().clear();
				searchYear.getText().clear();
				searchVType.getText().clear();
			}
		});
		
	}
	
	private void startStatsActivity(List<Car> cars,String title) {
		Intent intent = new Intent(this, StatsActivity.class);
		filtered = cars;
		intent.putExtra("title", title);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private ArrayList<Car> filterList()
	{
		ArrayList<Car> filtered = new ArrayList<Car>();
			for (int i = 0 ; i < cars.size(); i++) {
				if ( (cars.get(i).getManufacturer().toLowerCase().contains(searchCorp.getText().toString().toLowerCase())  || searchCorp.getText().toString().equals(""))
						&& (Integer.toString(cars.get(i).getYear()).toLowerCase().contains(searchYear.getText().toString().toLowerCase()) || searchYear.getText().toString().equals(""))
						&& (cars.get(i).getModel().toLowerCase().contains(searchModel.getText().toString().toLowerCase())  || searchModel.getText().toString().equals(""))
						&& (cars.get(i).getVehicleClass().toLowerCase().contains(searchVType.getText().toString().toLowerCase())  || searchVType.getText().toString().equals(""))) {
					filtered.add(cars.get(i));
				}
			}
		
		return filtered;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (cars == null) {
			new AsyncTask<Integer, Integer, Boolean>() {

				ProgressDialog progress;
				@Override
				protected Boolean doInBackground(Integer... arg0) {
					try {
						File[] filesArray = wrapper.getFilesDir().listFiles();
						cars = new ArrayList<Car>();
//						for (int i = 0; i < filesArray.length; i++) {
//							try {
//								cars.addAll(new CSVParser(filesArray[i]).parse());
//								progress.setProgress((int) (100 * ((float)i)/filesArray.length));
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
						try {
							cars.addAll(new CSVParser(filesArray[0]).parse());
						} catch (IOException e) {
							e.printStackTrace();
						}
						determineFuel();
						yearAdapter = new DynamicArrayAdapter(SearchActivity.this, R.layout.list_item, cars, callback) {
							@Override
							protected String getFieldForCar(Car c) {
								return Integer.toString(c.getYear());
							}							
						};
						manufactureAdapter = new DynamicArrayAdapter(SearchActivity.this, R.layout.list_item, cars, callback) {
							@Override
							protected String getFieldForCar(Car c) {
								return c.getManufacturer();
							}
						};
						modelAdapter = new DynamicArrayAdapter(SearchActivity.this, R.layout.list_item, cars, callback) {
							@Override
							protected String getFieldForCar(Car c) {
								return c.getModel();
							}
							@Override
							protected boolean shouldContain(Car c) {
								return c.getManufacturer().contains(searchCorp.getText()) && 
										Integer.toString(c.getYear()).contains(searchYear.getText()) &&
										c.getVehicleClass().contains(searchVType.getText());
							}
						};
						vTypeAdapter = new DynamicArrayAdapter(SearchActivity.this, R.layout.list_item, cars, callback) {

							@Override
							protected String getFieldForCar(Car c) {
								return c.getVehicleClass();
							}
							
						};
						return true;
					} catch (Exception unfinishedException) {
						return false;
					}
				}

				private void determineFuel() {
					Set<Double> fuelEffeciency = new HashSet<Double>();
					for(int i = 0 ; i < cars.size() ; i++) {
						fuelEffeciency.add(cars.get(i).getHighwayEffL());
					}
					worstFuelEfficiency = Collections.max(fuelEffeciency);
					bestFuelEfficiency = Collections.min(fuelEffeciency);
					
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
						AlertDialog.Builder failure = new AlertDialog.Builder(SearchActivity.this);
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
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				filter(s);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filter(s);
				if (searchCorp.getVisibility() == View.VISIBLE)
					searchCorp.setBackgroundResource(R.drawable.textbarwhite);
				if (searchYear.getVisibility() == View.VISIBLE)
					searchYear.setBackgroundResource(R.drawable.textbarwhite);
				if (searchModel.getVisibility() == View.VISIBLE)
					searchModel.setBackgroundResource(R.drawable.textbarwhite);
				if (searchVType.getVisibility() == View.VISIBLE)
					searchVType.setBackgroundResource(R.drawable.textbarwhite);
			}


		});
	}
	
	public void filter(CharSequence s) {
		if (searchCorp.getVisibility() == View.VISIBLE) {
			manufactureAdapter.getFilter().filter(s);
		}
		if (searchYear.getVisibility() == View.VISIBLE) {
			yearAdapter.getFilter().filter(s);
		}
		if (searchModel.getVisibility() == View.VISIBLE) {
			modelAdapter.getFilter().filter(s);
		}
		if (searchVType.getVisibility() == View.VISIBLE) {
			vTypeAdapter.getFilter().filter(s);
		}
	}

	public void makeInVisible(View v) {
		if (v.hasFocus()) {
			logo.setVisibility(View.GONE);
			refresh.setVisibility(View.GONE);
			if (!v.equals(searchCorp))
				searchCorp.setVisibility(View.GONE);
			if (!v.equals(searchYear))
				searchYear.setVisibility(View.GONE);
			if (!v.equals(searchModel))
				searchModel.setVisibility(View.GONE);
			if (!v.equals(searchVType))
				searchVType.setVisibility(View.GONE);
			searchList.setVisibility(View.VISIBLE);
			search.setVisibility(View.GONE);
			saved.setVisibility(View.GONE);

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
			if (v.equals(searchVType))
				searchList.setAdapter(vTypeAdapter);
			//first time filter so when we click the button it populates list
//			filter("");
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
