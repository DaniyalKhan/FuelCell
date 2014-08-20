package com.fuelcell.csvutils;

import java.util.Calendar;

public class CSVFileUtils {
	//TODO hit api properly
	public static final String URL_PREFIX = "http://www.nrcan.gc.ca/sites/www.nrcan.gc.ca/files/oee/files/csv/MY";
	public static final String URL_SUFFIX = "%20Fuel%20Consumption%20Ratings.csv";
	private static final int START_YEAR = 2000;
//	private static final int CLAMP_YEAR = Calendar.getInstance().get(Calendar.YEAR);
	private static final int CLAMP_YEAR = 2012;
	private static final int NUM_YEARS = CLAMP_YEAR - START_YEAR + 1;

	public static String getNameFromURL(String stringURL) {
        String canonName = stringURL.substring(stringURL.lastIndexOf("/")+1, stringURL.length());
        canonName = canonName.substring(0, canonName.indexOf("%20"));
        return canonName;
	}
	
	public static String[] getAllCSV() {
		String[] csvURLs = new String[NUM_YEARS];
		for (int i = 0; i < NUM_YEARS; i++) {
			csvURLs[i] = getCSVForYear(i + START_YEAR);
		}
		return csvURLs;
	}
	
	
	private static String getCSVForYear(int year) {
		return URL_PREFIX + year + URL_SUFFIX;
	}
	
}
