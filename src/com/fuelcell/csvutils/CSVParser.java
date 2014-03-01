package com.fuelcell.csvutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
		}
	}
	
	public ArrayList<Car> parse() throws IOException {
		ArrayList<Car> cars = new ArrayList<Car>();
		if (csvReader != null) {
				String[] line = csvReader.readNext();
				while (line != null) {
					try {
						cars.add(new Car(csvReader.readNext()));
					} catch (Exception e) {
//						e.printStackTrace();
					}
					line = csvReader.readNext();	
				}
				
		}
		return cars;
	}
	
}
