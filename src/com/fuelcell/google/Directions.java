package com.fuelcell.google;

public class Directions {

	private String origin;
	private String destination;
	
	private final String DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/json";
	
	public void setPoints(String origin, String destination) {
		this.origin = origin;
		this.destination = destination;
	}
	
	public void makeRequest() {
		
	}
	
}
