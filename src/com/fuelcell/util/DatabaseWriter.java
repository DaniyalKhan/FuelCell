package com.fuelcell.util;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.util.Log;

import com.fuelcell.csvutils.CSVParser;
import com.fuelcell.models.Car;

public class DatabaseWriter extends AsyncTask<String, Integer, String> {

	Context context;
	ContextWrapper wrapper;
	
	public DatabaseWriter(Context context, ContextWrapper wrapper) {
		this.context = context;
		this.wrapper = wrapper;
	}


	@Override
	protected String doInBackground(String... files) {
		CarDatabase db = CarDatabase.obtain(context);
		db.begin();
		for (String file: files) {
			try {
				List<Car> cars = new CSVParser(wrapper.getFileStreamPath(file)).parseCars();
				for (Car car: cars) {
					db.insertCar(car);
				}
			} catch (IOException e) {
				Log.e(getClass().toString(), "Error reading from file: " + file);
				e.printStackTrace();
			}
		}
		db.end();
		return null;
	}

}
