package com.fuelcell.csvutils;

import java.util.Calendar;

public class CSVFileUtils {

	public static final String URL_PREFIX = "http://oee.nrcan.gc.ca/sites/oee.nrcan.gc.ca/files/files/csv/MY";
	public static final String URL_SUFFIX = "-Fuel-Consumption-Ratings.csv";
	private static final int START_YEAR = 2000;
	private static final int CLAMP_YEAR = 2013;

	public static String getNameFromURL(String stringURL) {
        String canonName = stringURL.substring(stringURL.lastIndexOf("/")+1, stringURL.length());
        canonName = canonName.substring(0, canonName.indexOf("-"));
        return canonName;
	}
	
	public static String[] getAllCSV() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		String[] csvURLs = new String[Math.max(CLAMP_YEAR, year) - START_YEAR];
		for (int i = 0; i < Math.max(CLAMP_YEAR, year) - START_YEAR; i++) {
			csvURLs[i] = getCSVForYear(i + START_YEAR);
		}
		return csvURLs;
	}
	
	
	private static String getCSVForYear(int year) {
		return URL_PREFIX + year + URL_SUFFIX;
	}
	
}
