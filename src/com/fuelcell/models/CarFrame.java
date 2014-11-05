package com.fuelcell.models;

import android.content.Intent;

public class CarFrame {

	//intent tages to pass carframes between activities
	protected static final String INTENT_YEAR = "year";
	protected static final String INTENT_MANUFACTURER = "manufacturer";
	protected static final String INTENT_MODEL = "model";
	protected static final String INTENT_VCLASS = "vclass";
	
	public int year;
	public String manufacturer;
	public String model;
	public String vehicleClass;

	public CarFrame() {}
	
	public CarFrame(int year, String manufacturer, String model, String vehicleClass) {
		this.year = year;
		this.manufacturer = manufacturer;
		this.model = model;
		this.vehicleClass = vehicleClass;
	}
	
	public static void saveCarToIntent(Intent intent, String year, String manufacturer, String model, String vehicleClass) {
		intent.putExtra(INTENT_YEAR, year);
		intent.putExtra(INTENT_MANUFACTURER, manufacturer);
		intent.putExtra(INTENT_MODEL, model);
		intent.putExtra(INTENT_VCLASS, vehicleClass);
	}
	
	public static CarFrame loadCarFromIntent(Intent intent) {
		return new CarFrame(intent.getStringExtra(INTENT_YEAR) == null || intent.getStringExtra(INTENT_YEAR).equalsIgnoreCase("") ? -1 : Integer.parseInt(intent.getStringExtra(INTENT_YEAR)), 
							intent.getStringExtra(INTENT_MANUFACTURER) == null ? "" : intent.getStringExtra(INTENT_MANUFACTURER), 
							intent.getStringExtra(INTENT_MODEL) == null ? "" : intent.getStringExtra(INTENT_MODEL), 
							intent.getStringExtra(INTENT_VCLASS) == null ? "" : intent.getStringExtra(INTENT_VCLASS));
	}

}
