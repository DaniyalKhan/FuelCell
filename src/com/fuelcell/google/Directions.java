package com.fuelcell.google;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Directions {

	private String origin;
	private String destination;
	private DirectionCallback callback;
	
	private final String DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/json";
	
	public Directions(String origin, String destination, DirectionCallback callback) {
		this.origin = origin;
		this.destination = destination;
		this.callback = callback;
	}
	
	public void setPoints(String origin, String destination) {
		this.origin = origin;
		this.destination = destination;
	}
	
	public void makeRequest() {
		new DirectionTask().execute(DIRECTIONS_URL + "?" + "origin=" + origin + "&" + "destination=" + destination + "&sensor=true");
	}
	
	public boolean routesExist() {
		return false;
//		JSONArray routes = JSONUtil.getJSONArray(jsonResult, "routes");
//		return (JSONUtil.getJSONArray(jsonResult, "routes") == null) ? false : routes.length() > 0;
	}
	
	public Double getDistanceKm() {
//		if (jsonResult == null) return null;
		return null;
	}
	
	public interface DirectionCallback {
		void onDirectionsReceived(String result);
	}
	
	public class DirectionTask extends AsyncTask<String, String, String> {

		public DirectionTask() {
			super();
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
				if (callback != null) callback.onDirectionsReceived(result);
	        }
	    }
		
	}
	
}
