package com.fuelcell.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Car implements Parcelable {
	
	public static enum FuelType {Diesel, Ethanol, Natural, Regular, Premium};
	
	private final int year;
	private final String manufacturer;
	private final String model;
	private final String vehicleClass;
	private final double engineSize;
	private final int cylinders;
	private final String transmission;
	private final FuelType fuelType;
	
	//Litres/100 KM
	private final double cityEffL;
	private final double highwayEffL;

	//Miles/gallon
	private final double cityEffM;
	private final double highwayEffM;
	
	//Litres/year
	private final double fuelUsage;
	
	//kg/year
	private final double emissions;

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
		dest.writeString(vehicleClass);
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
				in.readDouble(), in.readInt(), in.readString(), in.readString(), 
				in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble(), 
				in.readDouble(), in.readDouble());
	}
	
	public Car(int year, String manufacturer, String model,
			String vehicleClass, double engineSize, int cylinders,
			String transmission, String fuelType, double cityEffL,
			double highwayEffL, double cityEffM, double highwayEffM,
			double fuelUsage, double emissions) {
		super();
		this.year = year;
		this.manufacturer = manufacturer;
		this.model = model;
		this.vehicleClass = vehicleClass;
		this.engineSize = engineSize;
		this.cylinders = cylinders;
		this.transmission = transmission;
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

	public Car(String[] car) {
		year = Integer.parseInt(car[0].trim());
		manufacturer = car[1].trim();
		model = car[2].trim();
		vehicleClass = car[3].trim();
		engineSize = Double.parseDouble(car[4].trim());
		cylinders = Integer.parseInt(car[5].trim());
		transmission = car[6].trim();
		if (car[7].trim().equals("D")) fuelType = FuelType.Diesel;
		else if (car[7].trim().equals("E")) fuelType = FuelType.Ethanol;
		else if (car[7].trim().equals("N")) fuelType = FuelType.Natural;
		else if (car[7].trim().equals("X")) fuelType = FuelType.Regular;
		else fuelType = FuelType.Premium;
		cityEffL = Double.parseDouble(car[8].trim());
		highwayEffL = Double.parseDouble(car[9].trim());
		cityEffM = Double.parseDouble(car[10].trim());
		highwayEffM = Double.parseDouble(car[11].trim());
		fuelUsage = Double.parseDouble(car[12].trim());
		emissions = Double.parseDouble(car[13].trim());
	}

	public int getYear() {
		return year;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getModel() {
		return model;
	}

	public String getVehicleClass() {
		return vehicleClass;
	}

	public double getEngineSize() {
		return engineSize;
	}

	public int getCylinders() {
		return cylinders;
	}

	public String getTransmission() {
		return transmission;
	}

	public FuelType getFuelType() {
		return fuelType;
	}

	public double getCityEffL() {
		return cityEffL;
	}

	public double getHighwayEffL() {
		return highwayEffL;
	}

	public double getCityEffM() {
		return cityEffM;
	}

	public double getHighwayEffM() {
		return highwayEffM;
	}

	public double getFuelUsage() {
		return fuelUsage;
	}

	public double getEmissions() {
		return emissions;
	}
	
}
