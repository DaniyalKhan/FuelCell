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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.fuelcell.models.Car;
import com.fuelcell.util.JSONUtil;

public class Directions {

	private String origin;
	private String destination;
	private DirectionCallback callback;
	private Activity activity;
	
	private final String DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/json";
	
	public Directions(Activity activity, String origin, String destination, DirectionCallback callback) {
		this.origin = origin;
		this.destination = destination;
		this.callback = callback;
		this.activity = activity;
	}
	
	public Directions(Activity activity, DirectionCallback callback) {
		this.callback = callback;
		this.activity = activity;
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
	
	public static class Route implements Parcelable {
		public final String summary;
		public final double distance;
		public final double time;
		public final String cDistance;
		public final String cTime;
		public Route(String summary, double distance, double time) {
			this.summary = summary;
			this.distance = distance;
			this.time = time;
			this.cDistance = "";
			this.cTime = "";
		}
		public Route(JSONObject routeJSON) {
			if (routeJSON != null) summary = JSONUtil.getString(routeJSON, "summary");
			else summary = "No Route Summary Available";
			JSONArray legs = JSONUtil.getJSONArray(routeJSON, "legs");
			JSONObject leg = JSONUtil.getJSONObject(legs, 0);//only one leg for no waypoints
			if (leg != null) {
				distance = Double.parseDouble(JSONUtil.getString(JSONUtil.getJSONObject(leg, "distance"), "value"));
				time = Double.parseDouble(JSONUtil.getString(JSONUtil.getJSONObject(leg, "duration"), "value"));
				cDistance = JSONUtil.getString(JSONUtil.getJSONObject(leg, "distance"), "text");
				cTime = JSONUtil.getString(JSONUtil.getJSONObject(leg, "duration"), "text");
			} else {
				distance = 0;
				time = 0;
				cDistance = null;
				cTime = null;
			}
		}
		public Route(Parcel in) {
			
		}
		
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(summary);
			dest.writeString(cTime);
			dest.writeDouble(distance);
			dest.writeDouble(time);
			dest.writeString(cDistance);
		}
		
		public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
			public Route createFromParcel(Parcel in) {
				return new Route(in); 
		    }
			public Route[] newArray(int size) {
				return new Route[size];
			}
		};
		
	}
	
	public interface DirectionCallback {
		void onDirectionsReceived(String result);
	}
	
	private class DirectionTask extends AsyncTask<String, String, String> {

		ProgressDialog progress;
		
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
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        progress.dismiss();
	        if (result != null) {
				if (callback != null) callback.onDirectionsReceived(result);
	        }
	    }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(activity);
			progress.setMessage("Finding routes");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(false);
			progress.show();
		}
		
	}
	
}
