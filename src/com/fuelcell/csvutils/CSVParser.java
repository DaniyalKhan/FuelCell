package com.fuelcell.csvutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.fuelcell.models.Car;

public class CSVParser {

	File csvFile;
	CSVReader csvReader;
	
	public CSVParser(File csvFile) {
		this.csvFile = csvFile;
		try {
			this.csvReader = new CSVReader(new FileReader(csvFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(getClass().toString(), "Error opening CSVReader on file: " + csvFile.getAbsolutePath());
		}
	}
	
	public List<Car> parseCars() throws IOException {
		ArrayList<Car> cars = new ArrayList<Car>();
		if (csvReader != null) {
			String[] line = csvReader.readNext();
			while (line != null && line.length != 0) {
				Car c = parseCar(line);
				if (c != null) cars.add(c);
				line = csvReader.readNext();
			}
			csvReader.close();
		}
		return cars;
	}
	
	private Car parseCar(String[] data) {
		Car car = new Car();
		try {
			car.year = Integer.parseInt(data[0]);
			car.manufacturer = data[1];
			car.model = data[2];
			car.vehicleClass = data[3];
			car.engineSize = Double.parseDouble(data[4]);
			car.cylinders = Integer.parseInt(data[5]);
			//transmission contains info
			car.transmission = transmissionType(data[6].substring(0, data[6].length() - 1));
			car.gears = Integer.parseInt(data[6].substring(data[6].length() - 2));
			car.fuelType = fuelType(data[7]);
			car.cityEffL = Double.parseDouble(data[8]);
			car.highwayEffL = Double.parseDouble(data[9]);
			car.cityEffM = Double.parseDouble(data[10]);
			car.highwayEffM = Double.parseDouble(data[11]);
			car.fuelUsage = Double.parseDouble(data[12]);
			car.emissions = Double.parseDouble(data[13]);
		} catch (NumberFormatException e) {
			//sometimes we might get lines in the csv that are not valid car data (legend data for example)
			e.printStackTrace();
			return null;
		}
		return car;
	}
	
	private Car.TransmissionType transmissionType(String tr) {
		if (tr.trim().equals("A")) return Car.TransmissionType.Automatic;
		else if (tr.trim().equals("AM")) return Car.TransmissionType.AutomaticManual;
		else if (tr.trim().equals("AS")) return Car.TransmissionType.AutomaticWithSelectShift;
		else if (tr.trim().equals("AV")) return Car.TransmissionType.ContinuouslyVariable;
		else if (tr.trim().equals("M")) return Car.TransmissionType.Manual;
		Log.e(getClass().toString(), "Unable to determine transmission for: " + tr);
		return null;
	}
	
	private Car.FuelType fuelType(String ft) {
		if (ft.trim().equals("D")) return Car.FuelType.Diesel;
		else if (ft.trim().equals("E")) return Car.FuelType.Ethanol;
		else if (ft.trim().equals("N")) return Car.FuelType.Natural;
		else if (ft.trim().equals("X")) return Car.FuelType.Regular;
		else if (ft.trim().equals("Z")) return Car.FuelType.Premium;
		Log.e(getClass().toString(), "Unable to determine fuel type for: " + ft);
		return null;
	}
	
	//TODO get rid of
	
	public ArrayList<String []> parseRaw() throws IOException { 
		ArrayList<String []> cars = new ArrayList<String []>();
		if (csvReader != null) {
			String[] line = csvReader.readNext();
			while (isValidCarData(line)) {
				cars.add(line);
				line = csvReader.readNext();
			}
			csvReader.close();
		}
		return cars;
	}
	
	/**
	 * Determine if a string array is valid car data 
	 * @param data
	 * @return true if the string array represents a car, false otherwise
	 */
	private boolean isValidCarData(String[] data) {
		if (data == null || data.length == 0) return false;
		try {
			//TODO need to check if all required fields are here, not just by simple checking that the first column is a number
			Integer.parseInt(data[0]);
			return true;
		} catch (Exception e) {
			return false; //line does not start with a number (i.e. a car year)
		}
	}
	
}
