package com.fuelcell.util;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.fuelcell.models.Car;

public class CarDatabase extends SQLiteOpenHelper {

	private Context context;
	private static CarDatabase instance;
	
	private static final String DATABASE_NAME = "cars_database";
	private static final int DATABASE_VERSION = 1;
    
	private static final String CAR_TABLE = "car_info";
	private static final String FAVOURITES = "saved_cars";
	
    private static final String[] PRIMARY_KEYS = { "Year", "Manufacturer", "Model", "Vehicle_Class", "Engine_Size_L", "Cylinders", "Transmission", "Gears", "Fuel_Type" };
    private static final String[] PRIMARY_KEY_TYPES = {"INTEGER", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "FLOAT",  "INTEGER", "VARCHAR(255)", "INTEGER", "VARCHAR(255)" };
    
    private static final String[] CAR_ATTRIBUTES = {"City_Efficienty_L_100KM", "Highway_Efficienty_L_100KM", "City_Efficienty_MPG", "Highway_Efficienty_MPG", "Fuel_Usage_L_Year", "Emissions_G_KM" };
    private static final String[] CAR_ATTRIBUTE_TYPES = {"FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT" };
    
    static ArrayList<Car> cars = new ArrayList<Car>();
    
//    private static final String CAR_DATA_TABLE = "car_data";
//    private static final String[] CAR_DATA = { "ENGINESIZE", "CYLINDERS", "TRANSMISSION", "FUELTYPE" };
//    private static final String[] CAR_DATA_TYPES = { "VARCHAR(255)", "INTEGER", "VARCHAR(255)", "VARCHAR(255)" };
//    
//    private static final String FUEL_DATA_TABLE = "fuel_data";
//    private static final String[] FUEL_DATA = { "CITYEFFL", "HIGHWAYEFFL", "CITYEFFM", "HIGHWAYEFFM", "FUELUSAGE", "EMISSIONS" };
//    private static final String[] FUEL_DATA_TYPES = { "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT" };
//    
//    private static final String SAVED_CARS_TABLE = "saved_cars";
//    private static final String[] SAVED_CARS_DATA = {};
//    private static final String[] SAVED_CARS_TYPES = {};
//    
//    private static final String COLUMN = "<column>";
//    private static final String TABLE = "<table>";
    
//	private static String QuerySearch = "select distinct ? from ? order by ?"; 
    private static String QueryInsertCarData = "insert into " + CAR_TABLE;
//  private static String QueryAddFavourite = "insert into " + CAR_TABLE + "(" + PRIMARY_KEYS[0] + ", " + PRIMARY_KEYS[1] + ", " + PRIMARY_KEYS[2] + ", " + PRIMARY_KEYS[3] + ", " + ") values (?, ?, ?, ?)";
    
    //construct precompiled query strings
    static {
    	QueryInsertCarData += " (";
    	for (String key: PRIMARY_KEYS) {
    		QueryInsertCarData += (key + ", ");	
    	}
    	for (String attribute: CAR_ATTRIBUTES) {
    		QueryInsertCarData += (attribute + ", ");	
    	}
    	//get rid of ending space and comma
    	QueryInsertCarData = QueryInsertCarData.substring(0, QueryInsertCarData.length() - 2);
    	QueryInsertCarData += ") values (?";
    	for (int i = 0; i < PRIMARY_KEYS.length + CAR_ATTRIBUTES.length - 1; i++) {
    		QueryInsertCarData += ", ?";
    	}
    	QueryInsertCarData += ")";
    	
    }
    
    
	public static CarDatabase obtain(Context c) {
		if (instance == null) instance = new CarDatabase(c);
		return instance;
	}
	
	private CarDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
	
//	public List<Integer> getYears() {
//		ArrayList<Integer> years = new ArrayList<Integer>();
//		Cursor c = getReadableDatabase().rawQuery(search.replace(TABLE, CAR_DATA_TABLE).replace(COLUMN, PRIMARY_KEYS[0]), null);
//		while(c.moveToNext()) years.add(c.getInt(0));
//		return years;
//	}
//	
//	public List<String> getManufacturers() {
//		ArrayList<String> manufacturers = new ArrayList<String>();
//		Cursor c = getReadableDatabase().rawQuery(search.replace(TABLE, CAR_DATA_TABLE).replace(COLUMN, PRIMARY_KEYS[1]), null);
//		while(c.moveToNext()) manufacturers.add(c.getString(0));
//		return manufacturers;
//	}
//	
//	public List<String> getModels() {
//		ArrayList<String> models = new ArrayList<String>();
//		Cursor c = getReadableDatabase().rawQuery(search.replace(TABLE, CAR_DATA_TABLE).replace(COLUMN, PRIMARY_KEYS[2]), null);
//		while(c.moveToNext()) models.add(c.getString(0));
//		return models;
//	}
//	
//	public List<String> getVehicleClasses() {
//		ArrayList<String> classes = new ArrayList<String>();
//		Cursor c = getReadableDatabase().rawQuery(search.replace(TABLE, CAR_DATA_TABLE).replace(COLUMN, PRIMARY_KEYS[3]), null);
//		while(c.moveToNext()) classes.add(c.getString(0));
//		return classes;
//	}
	
	public void begin() {
		getWritableDatabase().beginTransaction();
	}
	
	public void end() {
		getWritableDatabase().setTransactionSuccessful();
		getWritableDatabase().endTransaction();
	}
	
//	public ArrayList<Car> getCars() {
//		String query = "select distinct * from "
//	}
	
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
	
	public void insertCar(Car car) {
		//TODO use dynamic sql with SQLiteStatement: http://stackoverflow.com/questions/3501516/android-sqlite-database-slow-insertion
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement statement = db.compileStatement(QueryInsertCarData);
		
		//primary keys
		statement.bindDouble(1, car.year);
		statement.bindString(2, car.manufacturer);
		statement.bindString(3, car.model);
		statement.bindString(4, car.vehicleClass);
		statement.bindDouble(5, car.engineSize);
		statement.bindDouble(6, car.cylinders);
		statement.bindString(7, car.transmission.toString());
		//other keys
		statement.bindDouble(8, car.gears);
		statement.bindString(9, car.fuelType.toString());
		statement.bindDouble(10, car.cityEffL);
		statement.bindDouble(11, car.highwayEffL);
		statement.bindDouble(12, car.cityEffM);
		statement.bindDouble(13, car.highwayEffM);
		statement.bindDouble(14, car.fuelUsage);
		statement.bindDouble(15, car.emissions);
		statement.executeInsert();		
	}
	
    @Override
    public void onCreate(SQLiteDatabase db) {
    	//car data table
    	db.execSQL(createTable(CAR_ATTRIBUTES, CAR_ATTRIBUTE_TYPES, CAR_TABLE));
    	//table for saved cars (just need primary key to identify car)
    	db.execSQL(createTable(new String[]{}, new String[]{}, FAVOURITES));
    }
    
    public static String createTable(String[] columns, String[] columnsTypes, String tableName) {
    	String createTable = "create table " + tableName + " (";
    	for (int i = 0 ; i < PRIMARY_KEYS.length; i++) {
    		createTable += (PRIMARY_KEYS[i] + " " + PRIMARY_KEY_TYPES[i] + " not null, ");
    	}
    	for (int i = 0 ; i < columns.length; i++) {
    		createTable += (columns[i] + " " + columnsTypes[i] + " not null, ");
    	}
    	createTable += "primary key (";
    	for (String key: PRIMARY_KEYS) {
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
