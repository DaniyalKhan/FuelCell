package com.fuelcell;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.TextView;

import com.fuelcell.action.ButtonSettings;
import com.fuelcell.models.CarFrame;
import com.fuelcell.util.CarDatabase;
import com.fuelcell.util.DynamicArrayAdapter;
import com.fuelcell.util.DynamicArrayAdapter.CarFilter;
import com.fuelcell.util.DynamicArrayAdapter.TextCallback;

public class SearchActivity extends Activity {

	List<CarFrame> frames;
	
	MyEditText searchManu;
	MyEditText searchYear;
	MyEditText searchModel;
	MyEditText searchVType;
	ImageView searchHeaderManu;
	ImageView searchHeaderYear;
	ImageView searchHeaderModel;
	ImageView searchHeaderVType;
	ImageView logo;
	ImageView searchLogo;
	ListView searchList;
	RelativeLayout layout;
	
	DynamicArrayAdapter carAdapter; 
	CarFilter yearFilter;
	CarFilter manufacturerFilter;
	CarFilter modelFilter;
	CarFilter vehicleFilter;
	
	//list of views that disappear during filter/search
	List<View> invisibleViews;
	
	MyEditText lastClicked;
	
	ContextWrapper wrapper;
	Button search;
	Button refresh;
	TextView hint;

	//callback that a list item has been clicked
	TextCallback callback = new TextCallback() {
		@Override
		public void onClick(CharSequence text) {
			lastClicked.setText(text.toString().replaceAll("<\\/?[b]>", ""));
			lastClicked.setBackgroundResource((R.drawable.text_input_search_green));
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchManu.getWindowToken(), 0);

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
							visible();
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
		
		getWindow().setBackgroundDrawableResource(R.drawable.background);

		searchManu = (MyEditText) findViewById(R.id.searchCorp);
		searchYear = (MyEditText) findViewById(R.id.searchYear);
		searchModel = (MyEditText) findViewById(R.id.searchModel);
		searchVType = (MyEditText) findViewById(R.id.searchVType);
		
		searchHeaderManu = (ImageView) findViewById(R.id.searchHeaderCorp);
		searchHeaderYear = (ImageView) findViewById(R.id.searchHeaderYear);
		searchHeaderModel = (ImageView) findViewById(R.id.searchHeaderModel);
		searchHeaderVType = (ImageView) findViewById(R.id.searchHeaderVType);
		
		logo = (ImageView) findViewById(R.id.mainicon);
		searchLogo = (ImageView) findViewById(R.id.searchicon);
		search = (Button) findViewById(R.id.searchButton);
		refresh = (Button) findViewById(R.id.refresh);
		hint = (TextView) findViewById(R.id.hint);
		
		ButtonSettings.pressSize(search, 15);
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startStatsActivity("Results","There does not seem to be any cars with the given information: "
						+ " Manufacturer: "+ (searchManu.getText().toString().equals("") == true ? "Any" : searchManu.getText().toString()) + " " 
						+ " Year: "+ (searchYear.getText().toString().equals("") == true ? "Any" : searchYear.getText().toString()) + " " 
						+ " Model: "+ (searchModel.getText().toString().equals("") == true ? "Any" : searchModel.getText().toString()) + " " 
						+ " Vehicle Type: "+ (searchVType.getText().toString().equals("") == true ? "Any" : searchVType.getText().toString())
						+ ". Maybe try leaving some parameters blank to broaden your search.", false);
			}
		});
		
		searchList = (ListView) findViewById(R.id.searchList);
		layout = (RelativeLayout) findViewById(R.layout.activity_search);
		wrapper = new ContextWrapper(this);

		invisibleViews = Arrays.asList(searchManu, searchModel, searchYear, searchVType, logo, search, searchLogo, refresh, searchHeaderManu, searchHeaderYear, searchHeaderModel, searchHeaderVType, logo, refresh);
		
		searchManu.setTextHeader(searchHeaderManu);
		searchYear.setTextHeader(searchHeaderYear);
		searchModel.setTextHeader(searchHeaderModel);
		searchVType.setTextHeader(searchHeaderVType);
		
		setClick(searchManu);
		setClick(searchYear);
		setClick(searchModel);
		setClick(searchVType);

		setTextChange(searchManu);
		setTextChange(searchYear);
		setTextChange(searchModel);
		setTextChange(searchVType);
		
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchManu.getText().clear();
				searchModel.getText().clear();
				searchYear.getText().clear();
				searchVType.getText().clear();
			}
		});
		
		setKeyboardTypes();
	}
	
	private void setKeyboardTypes() {
		searchYear.setRawInputType(Configuration.KEYBOARD_QWERTY);
	}
	
	private void startStatsActivity(String title,String hint, boolean showClear) {
		Intent intent = new Intent(this, StatsActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("hint", hint);
		intent.putExtra("clear", showClear);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	

	@Override
	public void onStart() {
		super.onStart();
		if (frames == null) {
			//TODO can also move this into a thread instead
			new AsyncTask<Integer, Integer, Boolean>() {
				ProgressDialog progress;
				@Override
				protected Boolean doInBackground(Integer... arg0) {
					try {
						frames = CarDatabase.obtain(SearchActivity.this).getCarFrames();
						return true;
					} catch (Exception unfinishedException) {
						return false;
					}
					
				}

				@Override
				protected void onPreExecute() {
					progress = new ProgressDialog(SearchActivity.this);
					progress.setMessage("Loading car data onto Fuel Cell.");
					progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progress.show();
				}

				@Override
				protected void onPostExecute(Boolean result) {
					progress.dismiss();
					if (result == false) {
						AlertDialog.Builder failure = new AlertDialog.Builder(SearchActivity.this);
						failure.setMessage("Loading Failed");
					} else {
						
						carAdapter = new DynamicArrayAdapter(SearchActivity.this, R.layout.list_item, frames, callback);
						
						yearFilter = carAdapter.new CarFilter() {
							@Override
							protected String filterField(CarFrame c) {
								return Integer.toString(c.year);
							}
						};
						
						manufacturerFilter = carAdapter.new CarFilter() {
							@Override
							protected String filterField(CarFrame c) {
								return c.manufacturer;
							}
						};
						
						modelFilter = carAdapter.new CarFilter() {
							@Override
							protected String filterField(CarFrame c) {
								return c.model;
							}
						};
						
						vehicleFilter = carAdapter.new CarFilter() {
							@Override
							protected String filterField(CarFrame c) {
								return c.vehicleClass;
							}
						};
						
						searchManu.setFilter(manufacturerFilter);
						searchYear.setFilter(yearFilter);
						searchModel.setFilter(modelFilter);
						searchVType.setFilter(vehicleFilter);
						
						searchList.setAdapter(carAdapter);
					}

				}

			}.execute();
		}
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
			carAdapter.setFilter(editText.filter, editText.getText().toString());
			lastClicked = editText;
		}
	}
	
	protected void setTextChange(MyEditText textField) {
		textField.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				filter(s);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filter(s);
				//TODO fix this up so we don't have 4 if statements
				if (searchManu.getVisibility() == View.VISIBLE)
					searchManu.setBackgroundResource(R.drawable.text_input_search);
				if (searchYear.getVisibility() == View.VISIBLE)
					searchYear.setBackgroundResource(R.drawable.text_input_search);
				if (searchModel.getVisibility() == View.VISIBLE)
					searchModel.setBackgroundResource(R.drawable.text_input_search);
				if (searchVType.getVisibility() == View.VISIBLE)
					searchVType.setBackgroundResource(R.drawable.text_input_search);
			}


		});
	}
	
	public void filter(CharSequence s) {
		carAdapter.getFilter().filter(s);
	}
	
	public void visible() {
		for (View v: invisibleViews) v.setVisibility(View.VISIBLE);
		searchList.setVisibility(View.GONE);
		hint.setVisibility(View.GONE);
	}

	public void makeInvisible(MyEditText t) {
		if (t.hasFocus() || t.textHeader.hasFocus()) {
			for (View v: invisibleViews) {
				//only turn invisible the views that are not in focus! (the ones that are not clicked)
				if (!v.equals(t) &&  !v.equals(t.textHeader)) v.setVisibility(View.GONE);
			}
			searchList.setVisibility(View.VISIBLE);
			hint.setVisibility(View.VISIBLE);
			searchLogo.setVisibility(View.GONE);
		}
	}

	public static class MyEditText extends EditText {

		CarFilter filter;
		View textHeader;
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

		public void setTextHeader(View header) {
			this.textHeader = header;
		}
		
		public void setFilter(CarFilter filter) {
			this.filter = filter;
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
								((SearchActivity)context).visible();
							}
						}
					});
				}
			}).start();
			return false;
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		
	}

}
