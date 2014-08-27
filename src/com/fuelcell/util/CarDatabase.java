package com.fuelcell.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CarDatabase extends SQLiteOpenHelper {

	private Context context;
	private static CarDatabase instance;
	
	private static final String DATABASE_NAME = "FUEL_CELL_DB";
	private static final int DATABASE_VERSION = 1;
    
    private static final String PRIMARY_KEYS[] = { "YEAR", "MANUFACTURER", "MODEL", "VEHICLECLASS" };
    private static final String[] PRIMARY_KEY_TYPES = {"INTEGER", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)" };
    
    private static final String CAR_DATA_TABLE = "car_data";
    private static final String[] CAR_DATA = { "ENGINESIZE", "CYLINDERS", "TRANSMISSION", "FUELTYPE" };
    private static final String[] CAR_DATA_TYPES = { "VARCHAR(255)", "INTEGER", "VARCHAR(255)", "VARCHAR(255)" };
    
    private static final String FUEL_DATA_TABLE = "fuel_data";
    private static final String[] FUEL_DATA = { "CITYEFFL", "HIGHWAYEFFL", "CITYEFFM", "HIGHWAYEFFM", "FUELUSAGE", "EMISSIONS" };
    private static final String[] FUEL_DATA_TYPES = { "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT" };
    
    private static final String SAVED_CARS_TABLE = "saved_cars";
    private static final String[] SAVED_CARS_DATA = {};
    private static final String[] SAVED_CARS_TYPES = {};
    
    private static final String COLUMN = "<column>";
    private static final String TABLE = "<table>";
    
    private static String columnSearch = "select distinct " + COLUMN +" from " + TABLE + "order by " + COLUMN; 

	public static CarDatabase obtain(Context c) {
		if (instance == null) instance = new CarDatabase(c);
		return instance;
	}
	
	private CarDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
	
	public List<Integer> getYears() {
		ArrayList<Integer> years = new ArrayList<Integer>();
		Cursor c = getReadableDatabase().rawQuery(columnSearch.replace(TABLE, CAR_DATA_TABLE).replace(COLUMN, PRIMARY_KEYS[0]), null);
		while(c.moveToNext()) years.add(c.getInt(0));
		return years;
	}
	
	public List<String> getManufacturers() {
		ArrayList<String> manufacturers = new ArrayList<String>();
		Cursor c = getReadableDatabase().rawQuery(columnSearch.replace(TABLE, CAR_DATA_TABLE).replace(COLUMN, PRIMARY_KEYS[1]), null);
		while(c.moveToNext()) manufacturers.add(c.getString(0));
		return manufacturers;
	}
	
	public List<String> getModels() {
		ArrayList<String> models = new ArrayList<String>();
		Cursor c = getReadableDatabase().rawQuery(columnSearch.replace(TABLE, CAR_DATA_TABLE).replace(COLUMN, PRIMARY_KEYS[2]), null);
		while(c.moveToNext()) models.add(c.getString(0));
		return models;
	}
	
	public List<String> getVehicleClasses() {
		ArrayList<String> classes = new ArrayList<String>();
		Cursor c = getReadableDatabase().rawQuery(columnSearch.replace(TABLE, CAR_DATA_TABLE).replace(COLUMN, PRIMARY_KEYS[3]), null);
		while(c.moveToNext()) classes.add(c.getString(0));
		return classes;
	}
	
//	/**
//	 * Returns the distinct values for all entries in a given table's column
//	 * @param tableName the table to query
//	 * @param columnName The column name in the table
//	 */
//	private List<String> getDistinctTupleValues(String tableName, String columnName, ) {
//		ArrayList<String> results = new ArrayList<String>();
//		Cursor c = getReadableDatabase().rawQuery(columnSearch.replace(TABLE, tableName).replace(COLUMN, columnName), null);
//		while(c.moveToNext()) results.add(c.getString(0));
//		return results;
//	}
	
	public void insertCar(String[] car) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		for (int i = 0 ; i < PRIMARY_KEYS.length; i++) {
			values.put(PRIMARY_KEYS[i], car[i]);
		}
		for (int i = 0 ; i < CAR_DATA.length; i++) {
			values.put(CAR_DATA[i], car[i + PRIMARY_KEYS.length]);
		}
		//insert car data
		db.insertWithOnConflict(CAR_DATA_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		
		values.clear();
		for (int i = 0 ; i < PRIMARY_KEYS.length; i++) {
			values.put(PRIMARY_KEYS[i], car[i]);
		}
		for (int i = 0 ; i < FUEL_DATA.length; i++) {
			values.put(FUEL_DATA[i], car[i + PRIMARY_KEYS.length + CAR_DATA.length]);
		}
		//insert fuel data
		db.insertWithOnConflict(FUEL_DATA_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);		
	}
	
    @Override
    public void onCreate(SQLiteDatabase db) {
    	//car data table
    	db.execSQL(createTable(PRIMARY_KEYS, CAR_DATA, CAR_DATA_TYPES, CAR_DATA_TABLE));
    	//fuel consumption table
    	db.execSQL(createTable(PRIMARY_KEYS, FUEL_DATA, FUEL_DATA_TYPES, FUEL_DATA_TABLE));
    	//table for saved cars
    	db.execSQL(createTable(PRIMARY_KEYS, SAVED_CARS_DATA, SAVED_CARS_TYPES, SAVED_CARS_TABLE));
    }
    
    public static String createTable(String[] primaryKeys, String[] columns, String[] columnsTypes, String tableName) {
    	String createTable = "create table " + tableName + " (";
    	for (int i = 0 ; i < primaryKeys.length; i++) {
    		createTable += (primaryKeys[i] + " " + PRIMARY_KEY_TYPES[i] + " not null, ");
    	}
    	for (int i = 0 ; i < columns.length; i++) {
    		createTable += (columns[i] + " " + columnsTypes[i] + " not null, ");
    	}
    	createTable += "primary key (";
    	for (String key: primaryKeys) {
    		createTable += (key + ", ");
    	}
    	//get rid of ending comma (and ending space)
    	createTable = createTable.substring(0, createTable.length() - 2);
    	createTable += "))";
    	return createTable;
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
}
