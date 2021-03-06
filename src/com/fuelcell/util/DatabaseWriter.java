package com.fuelcell.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.util.Log;

import com.fuelcell.csvutils.CSVParser;
import com.fuelcell.models.Car;
import com.fuelcell.models.Car.CarComparator;

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
		db.beginInsert();
		for (String file: files) {
			try {
				List<Car> cars = new CSVParser(wrapper.getFileStreamPath(file)).parseCars();
				removeDuplicates(cars);
				for (Car car: cars) {
					db.insertCar(car);
				}
			} catch (IOException e) {
				Log.e(getClass().toString(), "Error reading from file: " + file);
				e.printStackTrace();
			}
		}
		db.endInsert();
		return null;
	}
	
	private void removeDuplicates(List<Car> cars) {
		if (cars.isEmpty()) return;
		CarComparator cc = new CarComparator();
		Collections.sort(cars, cc);
		Iterator<Car> carIterator = cars.iterator();
		Car prev = carIterator.next();
		while (carIterator.hasNext()) {
			Car next = carIterator.next();
			if (cc.compare(prev, next) == 0) {
				carIterator.remove();
			} else {
				prev = next;
			}
		}
	}

}
