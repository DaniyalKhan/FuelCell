package com.fuelcell;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

public class SearchActivity extends Activity {

	MyEditText searchCorp;
	MyEditText searchYear;
	MyEditText searchModel;
	ImageView logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		
		searchCorp = (MyEditText) findViewById(R.id.searchCorp);
		searchYear = (MyEditText) findViewById(R.id.searchYear);
		searchModel = (MyEditText) findViewById(R.id.searchModel);
		logo = (ImageView) findViewById(R.id.mainicon);
		
		//need these so the text fields can reshow everything when user presses back, 
		//needs a reference to everything that needs to show back up
		searchCorp.set(searchCorp, searchModel, searchYear, logo);
		searchYear.set(searchCorp, searchModel, searchYear, logo);
		searchModel.set(searchCorp, searchModel, searchYear, logo);
		
		searchCorp.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchYear.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		searchModel.setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
		
		setClick(searchCorp);
		setClick(searchYear);
		setClick(searchModel);
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
	
	public void makeInVisible(View v) {
		if (v.hasFocus()) {
			logo.setVisibility(View.GONE);
			if (!v.equals(searchCorp)) searchCorp.setVisibility(View.GONE);
			if (!v.equals(searchYear)) searchYear.setVisibility(View.GONE);
			if (!v.equals(searchModel)) searchModel.setVisibility(View.GONE);				
		}
	}
	
	public static class MyEditText extends EditText {

		View[] views;
		
		public MyEditText(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
		}

		public MyEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		public MyEditText(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		public void set(View ... v) {
			this.views = v;
		}
		
		public boolean onKeyPreIme (int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				for (View v: views) {
		    		v.setVisibility(View.VISIBLE);
		    	}
			}
			return false;
		}
		
	}
	

	
}
