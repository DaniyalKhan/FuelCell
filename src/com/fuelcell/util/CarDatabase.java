package com.fuelcell.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.fuelcell.models.Car;
import com.fuelcell.models.CarFrame;

public class CarDatabase extends SQLiteOpenHelper {

	private Context context;
	private static CarDatabase instance;
	
	private static final String DATABASE_NAME = "cars_database";
	private static final int DATABASE_VERSION = 1;
    
	private static final String CAR_TABLE = "car_info";
	private static final String FAVOURITES = "saved_cars";
	
    private static final String[] PRIMARY_KEYS = { "Year", "Manufacturer", "Model", "Vehicle_Class", "Engine_Size_L", "Cylinders", "Transmission", "Gears", "Fuel_Type" };
    private static final String[] PRIMARY_KEY_TYPES = {"INTEGER", "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)", "FLOAT",  "INTEGER", "VARCHAR(255)", "INTEGER", "VARCHAR(255)" };
    
    private static final String[] PRIMARY_KEYS_FRAME = { "Year", "Manufacturer", "Model", "Vehicle_Class"};
    
    private static final String[] CAR_ATTRIBUTES = {"City_Efficienty_L_100KM", "Highway_Efficienty_L_100KM", "City_Efficienty_MPG", "Highway_Efficienty_MPG", "Fuel_Usage_L_Year", "Emissions_G_KM" };
    private static final String[] CAR_ATTRIBUTE_TYPES = {"FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT" };
        
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
    
	private static String QueryColumn = "select distinct ? from ? order by ?"; 
//	private static String QueryCarFrame = "select distinct ?, ?, ?, ? from " + CAR_TABLE;
	private static String QueryCarFrame = "select distinct " + PRIMARY_KEYS[0] + ", " + PRIMARY_KEYS[1] + ", " + PRIMARY_KEYS[2] + ", " + PRIMARY_KEYS[3] + " from " + CAR_TABLE;
    private static String QueryInsertCarData = "insert into " + CAR_TABLE;
//  private static String QueryAddFavourite = "insert into " + CAR_TABLE + "(" + PRIMARY_KEYS[0] + ", " + PRIMARY_KEYS[1] + ", " + PRIMARY_KEYS[2] + ", " + PRIMARY_KEYS[3] + ", " + ") values (?, ?, ?, ?)";
    
    //construct precompiled query strings
    static {
    	//TODO use string builder
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
	
	private abstract class ColumnQuery<T> {
		List<T> queryDistinct(String[] selectionArgs) {
			SQLiteDatabase db = getReadableDatabase();
			ArrayList<T> values = new ArrayList<T>();
			Cursor c = db.rawQuery(QueryColumn, selectionArgs);
			while(c.moveToNext()) values.add(getData(c));
			c.close();
			db.close();
			return values;
		}
		abstract T getData(Cursor c);
	}
	
	private final ColumnQuery<Integer> integerQuery = new ColumnQuery<Integer>() {
		@Override
		Integer getData(Cursor c) {
			return c.getInt(0);
		}
	};
	
	private final ColumnQuery<String> stringQuery = new ColumnQuery<String>() {
		@Override
		String getData(Cursor c) {
			return c.getString(0);
		}
	};
	
	/**
	 * Construct a car frame query with the specified columns in the (toptional) where clause
	 * @param columns
	 * @param selectionArgCount
	 * @return
	 */
	private String constructQueryCarFrame(String[] columns, boolean hasWhereClause) {
		if (!hasWhereClause) return QueryCarFrame;
		StringBuilder builder = new StringBuilder(QueryCarFrame);
		builder.append(" where ");
		for (int i = 0; i < columns.length; i++) {
			builder.append(columns[i] + " = ? and ");
		}
		return builder.subSequence(0, builder.length() - 5).toString();
	}
	
	/**
	 * Get distinct carFrame data matching the given arguments (binded to placeholders in the where clause).
	 * If any parameters are null or empty, they are not included in the where clause
	 * @param year
	 * @param manufacturer
	 * @param model
	 * @param vehicleClass
	 * @return
	 */
	public List<CarFrame> getCarFrames(int year, String manufacturer, String model, String vehicleClass) {
		ArrayList<String> primaryKeys = new ArrayList<String>();
		ArrayList<String> selectionArgs = new ArrayList<String>();
		if (year > 0) {
			primaryKeys.add(PRIMARY_KEYS[0]);
			selectionArgs.add(Integer.toString(year));		
		}
		if (manufacturer != null && !manufacturer.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[1]);
			selectionArgs.add(manufacturer);
		}
		if (model != null && !model.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[2]);
			selectionArgs.add(model);
		}
		if (vehicleClass != null && !vehicleClass.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[3]);
			selectionArgs.add(vehicleClass);
		}
		ArrayList<CarFrame> carFrames = new ArrayList<CarFrame>();
		int argSize = selectionArgs.size();
		Cursor c = getReadableDatabase().rawQuery(constructQueryCarFrame(primaryKeys.toArray(new String[primaryKeys.size()]), argSize != 0), selectionArgs.toArray(new String[argSize]));
		while (c.moveToNext()) carFrames.add(new CarFrame(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)));
		return carFrames;
	}
	
	/**
	 * Get all distinct CarFrame data
	 * @return
	 */
	public List<CarFrame> getCarFrames() {
		ArrayList<CarFrame> carFrames = new ArrayList<CarFrame>();
		Cursor c = getReadableDatabase().rawQuery(constructQueryCarFrame(PRIMARY_KEYS_FRAME, false), null);
		while (c.moveToNext()) carFrames.add(new CarFrame(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)));
		return carFrames;
	}
	
	public List<Integer> getYears() {
		return integerQuery.queryDistinct(new String[] {PRIMARY_KEYS[0], CAR_TABLE, PRIMARY_KEYS[0]});
	}
	
	public List<String> getManufacturers() {
		return stringQuery.queryDistinct(new String[] {PRIMARY_KEYS[1], CAR_TABLE, PRIMARY_KEYS[0]});
	}
	
	public List<String> getModels() {
		return stringQuery.queryDistinct(new String[] {PRIMARY_KEYS[2], CAR_TABLE, PRIMARY_KEYS[2]});
	}
	
	public List<String> getVehicleClasses() {
		return stringQuery.queryDistinct(new String[] {PRIMARY_KEYS[3], CAR_TABLE, PRIMARY_KEYS[3]});
	}
	
	public void beginInsert() {
		getWritableDatabase().beginTransaction();
	}
	
	public void endInsert() {
		SQLiteDatabase db = getWritableDatabase();
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	public void insertCar(Car car) {
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
