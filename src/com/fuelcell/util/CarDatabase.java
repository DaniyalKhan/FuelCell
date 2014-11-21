package com.fuelcell.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.fuelcell.models.Car;
import com.fuelcell.models.Car.FuelType;
import com.fuelcell.models.Car.TransmissionType;
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
    
    private static final String HISTORY_TABLE = "history_table";
	
	private static final String[] HISTORY_PRIMARY_KEYS = {"Search", "Time"};
    
	private static String QueryColumn = "select distinct ? from ? order by ?"; 
	private static String QueryCarFrame = "select distinct " + PRIMARY_KEYS[0] + ", " + PRIMARY_KEYS[1] + ", " + PRIMARY_KEYS[2] + ", " + PRIMARY_KEYS[3] + " from " + CAR_TABLE;
	private static String QueryFavouriteCarFrame = "select distinct " + PRIMARY_KEYS[0] + ", " + PRIMARY_KEYS[1] + ", " + PRIMARY_KEYS[2] + ", " + PRIMARY_KEYS[3] + " from " + FAVOURITES;
	private static String QueryCarFull = "select " + PRIMARY_KEYS[0] + ", " + PRIMARY_KEYS[1] + ", " + PRIMARY_KEYS[2] + ", " + PRIMARY_KEYS[3] + ", " 
			+ PRIMARY_KEYS[4] + ", " + PRIMARY_KEYS[5] + ", " + PRIMARY_KEYS[6] + ", " + PRIMARY_KEYS[7] + ", " + PRIMARY_KEYS[8] + ", " 
			+ CAR_ATTRIBUTES[0] + ", " + CAR_ATTRIBUTES[1] + ", " + CAR_ATTRIBUTES[2] + ", " + CAR_ATTRIBUTES[3] + ", " + CAR_ATTRIBUTES[4] + ", " + CAR_ATTRIBUTES[5]
			+ " from " + CAR_TABLE;
    private static String QueryInsertCarData = "insert into " + CAR_TABLE;
    private static String QueryInsertFavourite = "insert into " + FAVOURITES 
    		+ "(" + PRIMARY_KEYS[0] + ", " 
    		+ PRIMARY_KEYS[1] + ", " 
    		+ PRIMARY_KEYS[2] + ", " 
    		+ PRIMARY_KEYS[3] + ") values (?, ?, ?, ?)";
    private static String QueryRemoveFavourite = "delete from " + FAVOURITES 
    		+ " WHERE " 
    		+ PRIMARY_KEYS[0] + " = ? AND "
    		+ PRIMARY_KEYS[1] + " = ? AND " 
    		+ PRIMARY_KEYS[2] + " = ? AND " 
    		+ PRIMARY_KEYS[3] + " = ? ";
    private static String QueryRemoveAllFavourite = "delete from " + FAVOURITES;
    private static String QueryHistory =      "select * from " + HISTORY_TABLE + " ORDER BY " + HISTORY_PRIMARY_KEYS[1] + " DESC" ;
    private static String QueryValueHistory =      "select * from " + HISTORY_TABLE + " WHERE " + HISTORY_PRIMARY_KEYS[0] + " = ?" ;
    private static String QueryInsertOriginHistory = "insert into " + HISTORY_TABLE 
    		+ " (" + HISTORY_PRIMARY_KEYS[0] + ", " 
    		+ HISTORY_PRIMARY_KEYS[1] + ") values (?, ?)";
    private static String QueryUpdateHistory = "UPDATE " + HISTORY_TABLE + 
    		" SET " + HISTORY_PRIMARY_KEYS[1] + " = ? " + 
    		" WHERE " + HISTORY_PRIMARY_KEYS[0] + " = " + " ? ";
    private static String QueryRemoveHistory = "delete from " + HISTORY_TABLE + " WHERE " 
    		+ HISTORY_PRIMARY_KEYS[0] + " = ? AND "
    		+ HISTORY_PRIMARY_KEYS[1] + " = ?";
    
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
	
	//Get the variations of each car
	public String[] getCarVariations(CarFrame carFrame) {			
		List<Car> variations = getCarsFull(carFrame);
		String[] x = new String[variations.size()];
		for (int i = 0 ; variations.size() > i ; i++ ) {
			x[i] = variations.get(i).cylinders + " " + variations.get(i).engineSize + " " + variations.get(i).fuelType + " " + variations.get(i).transmission + " " + variations.get(i).gears;
		}
		return x;
	}
	
	/**
	 * Construct a car frame query with the specified columns in the (optional) where clause
	 * @param columns
	 * @param selectionArgCount
	 * @return
	 */
	private String constructSearchQueryCarFrame(String[] columns, boolean hasWhereClause) {
		if (!hasWhereClause) return QueryCarFrame;
		StringBuilder builder = new StringBuilder(QueryCarFrame);
		builder.append(" where ");
		for (int i = 0; i < columns.length; i++) {
			builder.append("UPPER(" + columns[i] + ") LIKE UPPER(?) and ");
		}
		return builder.subSequence(0, builder.length() - 5).toString();
	}
	
	/**
	 * Construct a car query for all profile information using where to get certain car
	 * @param carArgs
	 * @return query
	 */
	private String constructQueryCarFull(String[] carArgs) {
		StringBuilder builder = new StringBuilder(QueryCarFull);
		if (carArgs.length > 0) {
			builder.append(" where ");
			for (int i = 0 ; i < carArgs.length ; i++){
				builder.append(carArgs[i] + " = ? and ");
			}
			return builder.subSequence(0, builder.length() - 5).toString();
		}
		return "";
	}
	/**
	 * Construct a car query for finding a single favourite car
	 * @param carArgs
	 * @return query
	 */
	private String constructQueryCarFav(String[] carArgs) {
		StringBuilder builder = new StringBuilder(QueryFavouriteCarFrame);
		if (carArgs.length > 0) {
			builder.append(" where ");
			for (int i = 0 ; i < carArgs.length ; i++){
				builder.append(carArgs[i] + " = ? and ");
			}
			return builder.subSequence(0, builder.length() - 5).toString();
		}
		return "";
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
			selectionArgs.add("%" + Integer.toString(year) + "%");		
		}
		if (manufacturer != null && !manufacturer.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[1]);
			selectionArgs.add("%" + manufacturer + "%");
		}
		if (model != null && !model.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[2]);
			selectionArgs.add("%" + model + "%");
		}
		if (vehicleClass != null && !vehicleClass.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[3]);
			selectionArgs.add("%" + vehicleClass + "%");
		}
		ArrayList<CarFrame> carFrames = new ArrayList<CarFrame>();
		int argSize = selectionArgs.size();
		Cursor c = getReadableDatabase().rawQuery(constructSearchQueryCarFrame(primaryKeys.toArray(new String[primaryKeys.size()]), argSize != 0), selectionArgs.toArray(new String[argSize]));
		while (c.moveToNext()) carFrames.add(new CarFrame(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)));
		return carFrames;
	}
	/**
	 * Get all favourites car frames in list
	 * @return carFrame
	 */
	public List<CarFrame> getFavCarFrames() {
		ArrayList<String> selectionArgs = new ArrayList<String>();
		Cursor c = getReadableDatabase().rawQuery(QueryFavouriteCarFrame,selectionArgs.toArray(new String[selectionArgs.size()]));
		ArrayList<CarFrame> carFrames = new ArrayList<CarFrame>();
			while (c.moveToNext()) 
				carFrames.add(new CarFrame(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)));
		return carFrames;
	}
	
	public void addFavCarFrames(CarFrame car) {
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement statement = db.compileStatement(QueryInsertFavourite);
		
		//primary keys
		statement.bindDouble(1, car.year);
		statement.bindString(2, car.manufacturer);
		statement.bindString(3, car.model);
		statement.bindString(4, car.vehicleClass);
		statement.executeInsert();
	}
	
	@SuppressLint("NewApi")
	public void removeFavCarFrames(CarFrame car) {
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement statement = db.compileStatement(QueryRemoveFavourite);
		
		//primary keys
		statement.bindDouble(1, car.year);
		statement.bindString(2, car.manufacturer);
		statement.bindString(3, car.model);
		statement.bindString(4, car.vehicleClass);
		statement.executeUpdateDelete();
	}
	
	@SuppressLint("NewApi")
	public void removeFavCars() {
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement statement = db.compileStatement(QueryRemoveAllFavourite);
		statement.executeUpdateDelete();
	}
	
	public boolean isFav(Car car) {
		ArrayList<String> primaryKeys = new ArrayList<String>();
		ArrayList<String> selectionArgs = new ArrayList<String>();
		if (car.year > 0) {
			primaryKeys.add(PRIMARY_KEYS[0]);	
			selectionArgs.add(Integer.toString(car.year));
		}
		if (car.manufacturer != null && !car.manufacturer.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[1]);
			selectionArgs.add(car.manufacturer);
		}
		if (car.model != null && !car.model.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[2]);
			selectionArgs.add(car.model);
		}
		if (car.vehicleClass != null && !car.vehicleClass.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[3]);
			selectionArgs.add(car.vehicleClass);
		}
		Cursor c = getReadableDatabase().rawQuery(constructQueryCarFav(primaryKeys.toArray(new String[primaryKeys.size()])), selectionArgs.toArray(new String[selectionArgs.size()]));
		return(c.moveToNext()); 
	}
	
	/**
	 * Get distinct car for car profile
	 * @param 
	 * @return
	 */
	public Car getCarFull(CarFrame carFrame) {
		Car car = new Car();
		ArrayList<String> primaryKeys = new ArrayList<String>();
		ArrayList<String> selectionArgs = new ArrayList<String>();
		
		if (carFrame.year > 0) {
			primaryKeys.add(PRIMARY_KEYS[0]);	
			selectionArgs.add(Integer.toString(carFrame.year));
		}
		if (carFrame.manufacturer != null && !carFrame.manufacturer.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[1]);
			selectionArgs.add(carFrame.manufacturer);
		}
		if (carFrame.model != null && !carFrame.model.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[2]);
			selectionArgs.add(carFrame.model);
		}
		if (carFrame.vehicleClass != null && !carFrame.vehicleClass.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[3]);
			selectionArgs.add(carFrame.vehicleClass);
		}
	
		Cursor c = getReadableDatabase().rawQuery(constructQueryCarFull(primaryKeys.toArray(new String[primaryKeys.size()])), selectionArgs.toArray(new String[selectionArgs.size()]));
		c.moveToNext(); 

		//Set from Primary Keys
		car.year=c.getInt(0);
		car.manufacturer=c.getString(1);
		car.model=c.getString(2);
		car.vehicleClass=c.getString(3);
		car.engineSize=c.getDouble(4);
		car.cylinders=c.getInt(5);
		car.transmission=TransmissionType.valueOf(c.getString(6));
		car.gears=c.getInt(7);
		car.fuelType=FuelType.valueOf(c.getString(8));
		//Set for car attributes
		car.cityEffL=c.getFloat(9);
		car.highwayEffL=c.getFloat(10);
		car.cityEffM=c.getFloat(11);
		car.highwayEffM=c.getFloat(12);
		car.fuelUsage=c.getDouble(13);
		car.emissions=c.getDouble(14);
		
		return car;
	}
	
	/**
	 * Get distinct car for car profile
	 * @param 
	 * @return
	 */
	public List<Car> getCarsFull(CarFrame carFrame) {
		List<Car> cars = new ArrayList<Car>();
		ArrayList<String> primaryKeys = new ArrayList<String>();
		ArrayList<String> selectionArgs = new ArrayList<String>();
		
		if (carFrame.year > 0) {
			primaryKeys.add(PRIMARY_KEYS[0]);	
			selectionArgs.add(Integer.toString(carFrame.year));
		}
		if (carFrame.manufacturer != null && !carFrame.manufacturer.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[1]);
			selectionArgs.add(carFrame.manufacturer);
		}
		if (carFrame.model != null && !carFrame.model.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[2]);
			selectionArgs.add(carFrame.model);
		}
		if (carFrame.vehicleClass != null && !carFrame.vehicleClass.equals("")) {
			primaryKeys.add(PRIMARY_KEYS[3]);
			selectionArgs.add(carFrame.vehicleClass);
		}
	
		Cursor c = getReadableDatabase().rawQuery(constructQueryCarFull(primaryKeys.toArray(new String[primaryKeys.size()])), selectionArgs.toArray(new String[selectionArgs.size()]));
		Car car;
		while(c.moveToNext()){
				car = new Car();
			//Set from Primary Keys
			car.year=c.getInt(0);
			car.manufacturer=c.getString(1);
			car.model=c.getString(2);
			car.vehicleClass=c.getString(3);
			car.engineSize=c.getDouble(4);
			car.cylinders=c.getInt(5);
			car.transmission=TransmissionType.valueOf(c.getString(6));
			car.gears=c.getInt(7);
			car.fuelType=FuelType.valueOf(c.getString(8));
			//Set for car attributes
			car.cityEffL=c.getFloat(9);
			car.highwayEffL=c.getFloat(10);
			car.cityEffM=c.getFloat(11);
			car.highwayEffM=c.getFloat(12);
			car.fuelUsage=c.getDouble(13);
			car.emissions=c.getDouble(14);
			cars.add(car);
		}
		return cars;
	}
	
	/**
	 * Get all distinct CarFrame data
	 * @return
	 */
	public List<CarFrame> getCarFrames() {
		ArrayList<CarFrame> carFrames = new ArrayList<CarFrame>();
		Cursor c = getReadableDatabase().rawQuery(constructSearchQueryCarFrame(PRIMARY_KEYS_FRAME, false), null);
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
    	db.execSQL(createTable(PRIMARY_KEYS, CAR_ATTRIBUTES, CAR_ATTRIBUTE_TYPES, CAR_TABLE));
    	//table for saved cars (just need primary key to identify car)
    	db.execSQL(createTable(new String[]{PRIMARY_KEYS[0],PRIMARY_KEYS[1],PRIMARY_KEYS[2],PRIMARY_KEYS[3]}, new String[]{}, new String[]{}, FAVOURITES));
    	db.execSQL(createTable(HISTORY_PRIMARY_KEYS, new String[]{}, new String[]{}, HISTORY_TABLE));
    }
    
    public static String createTable(String[] primaryKeys, String[] columns, String[] columnsTypes, String tableName) {
    	String createTable = "create table " + tableName + " (";
    	for (int i = 0 ; i < primaryKeys.length; i++) {
    		createTable += (primaryKeys[i] + " " + primaryKeys[i] + " not null, ");
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
	
	public ArrayList<HistoryItem> getHistory() {
		ArrayList<HistoryItem> search = new ArrayList<HistoryItem>();
		Cursor c = null;
			c = getReadableDatabase().rawQuery(QueryHistory,null);
		while (c.moveToNext()) 
			search.add(new HistoryItem(c.getString(0), c.getString(1)));
		return search;
	}

	public HistoryItem setHistory(String value) {
		//add as origin + destination
		//TODO clean up any too old (20 entries each)
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement statement = db.compileStatement(QueryInsertOriginHistory);
		
		//primary keys
		String time = new Timestamp((new Date()).getTime()).toString();
		
		statement.bindString(1, value);
		statement.bindString(2, time);
		statement.executeInsert();
		
		return new HistoryItem(value, time);
		
	}
	
	@SuppressLint("NewApi")
	public void removeHistory(HistoryItem item) {
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement statement = db.compileStatement(QueryRemoveHistory);
		
		//primary keys
		statement.bindString(1, item.value);
		statement.bindString(2, item.time);
		statement.executeUpdateDelete();
	}
	
	@SuppressLint("NewApi")
	public void updateHistory(String item) {
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement statement = db.compileStatement(QueryUpdateHistory);
		
		//primary keys
		statement.bindString(1, new Timestamp((new Date()).getTime()).toString());
		statement.bindString(2, item);
		statement.executeUpdateDelete();
	}

	public boolean isUniqueHistory(String value) {

		Cursor c = getReadableDatabase().rawQuery(QueryValueHistory,new String[]{value});

		while (c.moveToNext()) 
			return false;
		return true;
	}
	
}
