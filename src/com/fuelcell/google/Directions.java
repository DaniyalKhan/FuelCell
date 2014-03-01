package com.fuelcell.google;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.fuelcell.SearchActivity;

import android.app.Activity;
import android.os.AsyncTask;

public class Directions {

	private String origin;
	private String destination;
	
	
	private JSONObject jsonResult;
	
	private final String DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/json";
	
	public void setPoints(String origin, String destination) {
		this.origin = origin;
		this.destination = destination;
	}
	
	public void makeRequest() {
		new DirectionTask().execute(DIRECTIONS_URL + "?" + "origin=" + origin + "&" + "destination=" + destination + "&sensor=true");
	}
	
	public boolean routesExist() {
//		if (jsonResult != null) 
		return false;
	}
	
	public Double getDistanceKm() {
//		if (jsonResult == null) return null;
		return null;
	}
	
	public class DirectionTask extends AsyncTask<String, String, String> {

		public DirectionTask() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(params[0]));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	            //TODO Handle problems..
	        } catch (IOException e) {
	            //TODO Handle problems..
	        }
	        return responseString;
//			try {
//				return new BufferedReader(new InputStreamReader(c.getAssets().open("test.json"))).readLine();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return null;
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        if (result != null) {
				try {
					jsonResult = new JSONObject(result);
				} catch (JSONException e) {
					jsonResult = null;
					e.printStackTrace();
				}
	        }
	    }
		
	}
	
}
