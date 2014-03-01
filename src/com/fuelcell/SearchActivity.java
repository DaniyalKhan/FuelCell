package com.fuelcell;

import java.io.File;
import com.fuelcell.google.Directions;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
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
	ArrayAdapter<String> adapter;
	ContextWrapper wrapper;

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

//		File[] filesArray = wrapper.getFilesDir().listFiles();
//		for(int i = 0 ; i < filesArray.length ; i++){
//			new CSVParser(filesArray[i]);
//		}
		
		String[] carList = { "a", "b", "c" ,"d","e","f","g","h","i","j"};
		adapter = new ArrayAdapter<String>(this, R.layout.list_item, carList);
		searchList.setAdapter(adapter);

		// need these so the text fields can reshow everything when user presses
		// back,
		// needs a reference to everything that needs to show back up
		searchCorp.set(searchCorp, searchModel, searchYear, logo, searchList);
		searchYear.set(searchCorp, searchModel, searchYear, logo, searchList);
		searchModel.set(searchCorp, searchModel, searchYear, logo, searchList);

		searchCorp.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchYear.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchModel.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);

		setClick(searchCorp);
		setClick(searchYear);
		setClick(searchModel);

		setTextChange(searchCorp);
		setTextChange(searchYear);
		setTextChange(searchModel);
		
//		Directions d = new Directions();
//		d.setPoints("toronto", "vancouver");
//		d.makeRequest();
//		new Directions().setPoints("toronto", "vancouver");
		
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
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				searchList.setVisibility(View.GONE);

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				searchList.setVisibility(View.VISIBLE);
				adapter.getFilter().filter(s);
			}
			

		});
	}

	public void makeInVisible(View v) {
		if (v.hasFocus()) {
			logo.setVisibility(View.GONE);
			if (!v.equals(searchCorp)) searchCorp.setVisibility(View.GONE);
			if (!v.equals(searchYear)) searchYear.setVisibility(View.GONE);
			if (!v.equals(searchModel)) searchModel.setVisibility(View.GONE);
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
		
		public boolean onKeyPreIme (final int keyCode, KeyEvent event) {
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
								for (View v: views) {
						    		v.setVisibility(View.VISIBLE);
						    	}
								views[views.length - 1].setVisibility(View.GONE);
							}
						}
					});
				}
			}).start();
			return false;
		}


	}

}
