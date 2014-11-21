package com.fuelcell.util;

public class HistoryItem {

	String time;
	String value;

	public HistoryItem(String value, String time) {
		this.time = time;
		this.value = value;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return time;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}