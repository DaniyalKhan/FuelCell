package com.fuelcell.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.fuelcell.util.CarDatabase;

public class Car extends CarFrame implements Parcelable{
	
	public static enum FuelType {Diesel, Ethanol, Natural, Regular, Premium};
	public static enum TransmissionType {Automatic, AutomaticManual, AutomaticWithSelectShift, ContinuouslyVariable, Manual};
	
	public double engineSize;
	public int cylinders;
	public TransmissionType transmission;
	public int gears;
	public FuelType fuelType;
	
	public double cityEffL;
	public double highwayEffL;

	public double cityEffM;
	public double highwayEffM;
	
	public double fuelUsage;
	
	public double emissions;

	public static class CarComparator implements Comparator<Car> {

		@Override
		public int compare(Car c1, Car c2) {
			String ft1 = c1.fuelType.toString();
			String ft2 = c2.fuelType.toString();
			String tr2 = c1.transmission.toString();
			String tr1 = c2.transmission.toString();
			if (c1.year != c2.year) return c1.year - c2.year;
			else if (!c1.manufacturer.equals(c2.manufacturer)) return c1.manufacturer.compareTo(c2.manufacturer);
			else if (!c1.model.equals(c2.model)) return c1.model.compareTo(c2.model);
			else if (!c1.vehicleClass.equals(c2.vehicleClass)) return c1.vehicleClass.compareTo(c2.vehicleClass);
			else if (c1.cylinders != c2.cylinders) return c1.cylinders - c2.cylinders;
			else if (!ft1.equals(ft2)) return ft1.compareTo(ft2);
			else if (!tr1.equals(tr2)) return tr1.compareTo(tr2);
			else if (c1.gears != c2.gears) return c1.gears - c2.gears;
			else if (c1.engineSize != c2.engineSize) return (int) Math.signum(c1.engineSize - c2.engineSize);
			return 0;
		}
		
	}
	
	public Car() {}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(year);
		dest.writeString(manufacturer);
		dest.writeString(model);
		dest.writeString(vehicleClass);
		dest.writeDouble(engineSize);
		dest.writeInt(cylinders);
		dest.writeString(transmission.toString());
		dest.writeInt(gears);
		dest.writeString(fuelType.toString().charAt(0) + "");
		dest.writeDouble(cityEffL);
		dest.writeDouble(highwayEffL);
		dest.writeDouble(cityEffM);
		dest.writeDouble(highwayEffM);
		dest.writeDouble(fuelUsage);
		dest.writeDouble(emissions);
		
	}
	
	public static final Parcelable.Creator<Car> CREATOR = new Parcelable.Creator<Car>() {
		public Car createFromParcel(Parcel in) {
			return new Car(in); 
	    }
		public Car[] newArray(int size) {
			return new Car[size];
		}
	};
	
	public Car(Parcel in) {
		this(in.readInt(), in.readString(), in.readString(), in.readString(), 
				in.readDouble(), in.readInt(), in.readString(), in.readInt(), in.readString(), 
				in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble(), 
				in.readDouble(), in.readDouble());
	}
	
	private Car(int year, String manufacturer, String model,
			String vehicleClass, double engineSize, int cylinders,
			String transmission, int numberofGears, String fuelType, 
			double cityEffL, double highwayEffL, double cityEffM, 
			double highwayEffM, double fuelUsage, double emissions) {
		this.year = year;
		this.manufacturer = manufacturer;
		this.model = model;
		this.vehicleClass = vehicleClass;
		this.engineSize = engineSize;
		this.cylinders = cylinders;
		this.transmission = TransmissionType.valueOf(transmission);
		this.gears = numberofGears;
		if (fuelType.trim().equals("D")) this.fuelType = FuelType.Diesel;
		else if (fuelType.trim().equals("E")) this.fuelType = FuelType.Ethanol;
		else if (fuelType.trim().equals("N")) this.fuelType = FuelType.Natural;
		else if (fuelType.trim().equals("X")) this.fuelType = FuelType.Regular;
		else this.fuelType = FuelType.Premium;
		this.cityEffL = cityEffL;
		this.highwayEffL = highwayEffL;
		this.cityEffM = cityEffM;
		this.highwayEffM = highwayEffM;
		this.fuelUsage = fuelUsage;
		this.emissions = emissions;
	}
//
//	public Car(String[] car) {
//		year = Integer.parseInt(car[0].trim());
//		manufacturer = car[1].trim();
//		model = car[2].trim();
//		vehicleClass = car[3].trim();
//		engineSize = Double.parseDouble(car[4].trim());
//		cylinders = Integer.parseInt(car[5].trim());
//		transmission = car[6].trim();
//		if (car[7].trim().equals("D")) fuelType = FuelType.Diesel;
//		else if (car[7].trim().equals("E")) fuelType = FuelType.Ethanol;
//		else if (car[7].trim().equals("N")) fuelType = FuelType.Natural;
//		else if (car[7].trim().equals("X")) fuelType = FuelType.Regular;
//		else fuelType = FuelType.Premium;
//		cityEffL = Double.parseDouble(car[8].trim());
//		highwayEffL = Double.parseDouble(car[9].trim());
//		cityEffM = Double.parseDouble(car[10].trim());
//		highwayEffM = Double.parseDouble(car[11].trim());
//		fuelUsage = Double.parseDouble(car[12].trim());
//		emissions = Double.parseDouble(car[13].trim());
//	}
	
	public boolean saveToProfile(Context c) {
//		List <Car> saved = getSavedCars(c);
//		for (Car car: saved) {
//			if (car.matches(this)) return false; 
//		}
//		//to do dont write already saved cars
//		SQLiteDatabase db = CarDatabase.obtain(c).getWritableDatabase();
//		ContentValues values = new ContentValues();
//		String[] keys = CarDatabase.COLUMNS_NAMES;
//	    values.put(keys[0], year);
//	    values.put(keys[1], manufacturer);
//	    values.put(keys[2], model);
//	    values.put(keys[3], vehicleClass);
//	    values.put(keys[4], engineSize);
//	    values.put(keys[5], cylinders);
//	    values.put(keys[6], transmission);
//	    values.put(keys[7], fuelType.toString().charAt(0) + "");
//	    values.put(keys[8], cityEffL);
//	    values.put(keys[9], highwayEffL);
//	    values.put(keys[10], cityEffM);
//	    values.put(keys[11], highwayEffM);
//	    values.put(keys[12], fuelUsage);
//	    values.put(keys[13], emissions);
//	    db.insertWithOnConflict(CarDatabase.CAR_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
	    return true;
	}
	
	
	
//	public boolean matches(Car car) {
//		return car.year == year &&
//				car.manufacturer.equals(manufacturer) &&
//				car.model.equals(model) &&
//				car.vehicleClass.equals(vehicleClass) &&
//				car.cylinders == cylinders &&
//				car.transmission == transmission &&
//				car.fuelType == fuelType &&
//				car.gears == gears &&
//				car.engineSize == engineSize;
//	}

	public static List<Car> getSavedCars(Context context) {
		List<Car> saved = new ArrayList<Car>();
//		SQLiteDatabase db = CarDatabase.obtain(context).getReadableDatabase();
//		String selectQuery = "SELECT  * FROM " + CarDatabase.CAR_TABLE_NAME;
//		Cursor c = db.rawQuery(selectQuery, null);
//		String[] keys = CarDatabase.COLUMNS_NAMES;
//		
//		if (c.moveToFirst()) {
//		    while ( !c.isAfterLast() ) {
//		    	saved.add(new Car(
//		    			c.getInt(c.getColumnIndex(keys[0])), c.getString(c.getColumnIndex(keys[1])), c.getString(c.getColumnIndex(keys[2])), 
//		    			c.getString(c.getColumnIndex(keys[3])), (double)c.getFloat(c.getColumnIndex(keys[4])), c.getInt(c.getColumnIndex(keys[5])), 
//		    			c.getString(c.getColumnIndex(keys[6])), c.getString(c.getColumnIndex(keys[7])), (double)c.getFloat(c.getColumnIndex(keys[8])), 
//		    			(double)c.getFloat(c.getColumnIndex(keys[9])), (double)c.getFloat(c.getColumnIndex(keys[10])), (double)c.getFloat(c.getColumnIndex(keys[11])), 
//		    			(double)c.getFloat(c.getColumnIndex(keys[12])), (double)c.getFloat(c.getColumnIndex(keys[13]))));
//		    	c.moveToNext();
//		    }
//		}
		return saved;
	}

//	public int getYear() {
//		return year;
//	}
//
//	public String getManufacturer() {
//		return manufacturer;
//	}
//
//	public String getModel() {
//		return model;
//	}
//
//	public String getVehicleClass() {
//		return vehicleClass;
//	}
//
//	public double getEngineSize() {
//		return engineSize;
//	}
//
//	public int getCylinders() {
//		return cylinders;
//	}
//
//	public TransmissionType getTransmission() {
//		return transmission;
//	}
//
//	public FuelType getFuelType() {
//		return fuelType;
//	}
//
//	public double getCityEffL() {
//		return cityEffL;
//	}
//
//	public double getHighwayEffL() {
//		return highwayEffL;
//	}
//
//	public double getCityEffM() {
//		return cityEffM;
//	}
//
//	public double getHighwayEffM() {
//		return highwayEffM;
//	}
//
//	public double getFuelUsage() {
//		return fuelUsage;
//	}
//
//	public double getEmissions() {
//		return emissions;
//	}
	
}
