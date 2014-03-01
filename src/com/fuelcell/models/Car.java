package com.fuelcell.models;

public class Car {
	
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

	public Car(int year, String manufacturer, String model,
			String vehicleClass, double engineSize, int cylinders,
			String transmission, FuelType fuelType, double cityEffL,
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
		this.fuelType = fuelType;
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
