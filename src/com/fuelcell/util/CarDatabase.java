package com.fuelcell.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CarDatabase extends SQLiteOpenHelper {

	private Context context;
	private static CarDatabase instance;
	
	private static final String DATABASE_NAME = "FUEL_CELL_DB";
	private static final int DATABASE_VERSION = 1;
    public static final String CAR_TABLE_NAME = "cars";
    public static final String[] COLUMNS_NAMES = {
    	"YEAR", "MANUFACTURER", "MODEL", "VEHICLECLASS", 
    	"ENGINESIZE", "CYLINDERS", "TRANSMISSION", "FUELTYPE",
    	"CITYEFFL", "HIGHWAYEFFL", "CITYEFFM", "HIGHWAYEFFM",
    	"FUELUSAGE", "EMISSIONS"
    };
    
	private static final String[] COLUMNS_TYPES = {
		"INTEGER", "TEXT", "TEXT", "FLOAT", 
		"TEXT", "INTEGER", "TRANSMISSION", "TEXT",
		"FLOAT", "FLOAT", "FLOAT", "FLOAT",
		"FLOAT", "FLOAT"
	};

	public static CarDatabase obtain(Context c) {
		if (instance == null) instance = new CarDatabase(c);
		return instance;
	}
	
	private CarDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableString());
    }
    
    public static void reCreate(Context c) {
    	SQLiteDatabase db = obtain(c).getWritableDatabase();
    	db.execSQL("DROP TABLE IF EXISTS " + CAR_TABLE_NAME);
    	db.execSQL(createTableString());
	}
    
    public static String createTableString() {
		String exec = "CREATE TABLE" + " '" + CAR_TABLE_NAME + "' ('id' " + "INTEGER PRIMARY KEY NOT NULL";
		for (int i = 0; i < COLUMNS_NAMES.length; i++) {
			exec += ", '" + COLUMNS_NAMES[i] + "' " + COLUMNS_TYPES[i] + " NOT NULL";
		}
		exec += "); ";
		return exec;

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
}
