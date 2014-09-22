package com.fuelcell;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.fuelcell.google.Directions.Route;
import com.fuelcell.google.GMapV2Direction;
import com.fuelcell.google.GMapV2Direction.DocCallback;
import com.fuelcell.models.Car;
import com.fuelcell.util.JSONUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TravelActivity extends FragmentActivity {
	
	private Car car;
	private Route route;
	
	// m/s
	private static final double HIGHWAY_LIMIT = 80.0 * 1000.0/3600.0; 
	
	private double highwayMetres;
	private double cityMetres;
	private double highwaySeconds;
	private double citySeconds;
	
	private GMapV2Direction md;
	private GoogleMap map;
	
	private Button expand;
	private boolean isExpanded;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.travel_activity);
		
		getWindow().setBackgroundDrawableResource(R.drawable.background);
		
		Intent intent = getIntent();
		car = (Car) intent.getParcelableExtra("car");
		route = (Route) intent.getParcelableExtra("route");
				
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Loading Google Map Route...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
		
		md = new GMapV2Direction();
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		final LatLng src = new LatLng(intent.getDoubleExtra("srcLat", 0), intent.getDoubleExtra("srcLng", 0));
		final LatLng dst = new LatLng(intent.getDoubleExtra("dstLat", 0), intent.getDoubleExtra("dstLng", 0));
		md.getDocument(src, dst, GMapV2Direction.MODE_DRIVING, new DocCallback() {
			@Override
			public void onFinished(final Document doc) {
				TravelActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (doc == null) {
							Toast.makeText(TravelActivity.this, "Unable to get route from Google Maps", Toast.LENGTH_LONG).show();
							dialog.dismiss();
							return;
						}
						ArrayList<LatLng> directionPoint = md.getDirection(doc);
			            PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
			            for (int i = 0; i < directionPoint.size(); i++) {
			                rectLine.add(directionPoint.get(i));
			            }
			            double dist = Math.sqrt((dst.latitude * dst.latitude - src.latitude * src.latitude) +
			            		(dst.longitude * dst.longitude - src.longitude * src.longitude));
			            Polyline polylin = map.addPolyline(rectLine);
			            map.animateCamera(CameraUpdateFactory.newLatLngZoom(src, 10f));
			            dialog.dismiss();
					}
				});
			}
		});
		
		ListView instructions = (ListView) findViewById(R.id.steps);
		final ArrayList<Step> steps = new ArrayList<Step>();
		final ArrayList<String> summaries = new ArrayList<String>();
		
		JSONArray stepsJSON = null;
		
		try {
			stepsJSON = new JSONArray(route.stepString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (stepsJSON != null) {
			for (int i = 0; i < stepsJSON.length(); i++) {
				JSONObject stepJSON = JSONUtil.getJSONObject(stepsJSON, i);
				if (stepJSON != null) {
					double distance = Double.parseDouble(JSONUtil.getString(JSONUtil.getJSONObject(stepJSON, "distance"), "value"));
					double duration = Double.parseDouble(JSONUtil.getString(JSONUtil.getJSONObject(stepJSON, "duration"), "value"));
					double speedMS = distance/duration;
					double fuelUsed;
					if (speedMS >= HIGHWAY_LIMIT) {
						highwayMetres += distance;
						highwaySeconds += duration;
						fuelUsed = (car.getHighwayEffL()/100.0) / 1000.0 * distance;
					} else {
						cityMetres += distance;
						citySeconds += duration;
						fuelUsed = (car.getCityEffL()/100.0) / 1000.0 * distance;
					}
					String summary = JSONUtil.getString(stepJSON, "html_instructions");
					if (summary != null) {
						summaries.add(summary);
						steps.add(new Step(summary, JSONUtil.getString(JSONUtil.getJSONObject(stepJSON, "distance"), "text"), 
								JSONUtil.getString(JSONUtil.getJSONObject(stepJSON, "duration"), "text"),  fuelUsed));
					}
				}
			}
		}
		
		instructions.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, summaries) {
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View row = convertView;
				if (row == null) {
					LayoutInflater inflater = TravelActivity.this.getLayoutInflater();
					row = inflater.inflate(R.layout.list_item, null);
					final ViewHolder viewHolder = new ViewHolder();
					viewHolder.text = (TextView) row.findViewById(R.id.text);
					row.setTag(viewHolder);
				}
				row.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						StepDialog s = new StepDialog();
						Step step = steps.get(position);
						System.out.println(position);
						System.out.println(steps.size());
						s.setStep(step);
						s.show(getSupportFragmentManager(), "STEP_TAG");
					}
				});
				ViewHolder holder = (ViewHolder) row.getTag();
			    String s = getItem(position);
		    	holder.text.setText(Html.fromHtml(s));
				return row;
			}
			
		});
		
		
		
		double highwayKM = highwayMetres/1000.0;
		double cityKM = cityMetres/1000.0;
		
		//litres / KM
		double hEffM = car.getHighwayEffL()/100.0;
		double cEffL = car.getCityEffL()/100.0;
		
		double emissions = car.getEmissions() * 1000.0 * (citySeconds + highwaySeconds) / 31557600.0 * (36000.0 / 31557600.0);
		
		final double litresSpent = twoDecimalPlaces(hEffM * highwayKM + cEffL * cityKM);
		
		TextView usage = (TextView) findViewById(R.id.usage_value);
		usage.setText(Double.toString(litresSpent) + " litres");
		
		TextView co2Emission = (TextView) findViewById(R.id.co2_value);
		co2Emission.setText(Double.toString(twoDecimalPlaces(emissions)) + " grams");
		
		((EditText) findViewById(R.id.price)).addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				try {
					double price = Double.parseDouble(s.toString());
					((TextView) findViewById(R.id.price_value)).setText("$" + Double.toString(twoDecimalPlaces(price * litresSpent)) + " CAD");
				
				} catch (Exception e) {
					((TextView) findViewById(R.id.price_value)).setText("$0.00 CAD");
				}
			}

			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
			}
		});

		((Button) findViewById(R.id.homeButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent homeIntent = new Intent(TravelActivity.this, SearchActivity.class);
				homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP+Intent.FLAG_ACTIVITY_SINGLE_TOP );
				startActivity(homeIntent);
			}
		});
		
		expand = (Button) findViewById(R.id.expandButton);
		isExpanded = false;
		expand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View map = TravelActivity.this.findViewById(R.id.map);
				if(isExpanded){
					map.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
					isExpanded = false;
					expand.setBackgroundResource(R.drawable.expand);
				} else {
					LayoutParams params = (LayoutParams) map.getLayoutParams();
					params.height = 500;
					map.setLayoutParams(params);
					isExpanded = true;
					expand.setBackgroundResource(R.drawable.shrink);
				}
			}
		});
		
	}
	
	public static class Step {
		public String summary;
		public String time;
		public String distance;
		public Double fuel;
		public Step(String summary, String distance, String time, Double fuel) {
			this.summary = summary;
			this.time = time;
			this.distance = distance;
			this.fuel = fuel;
		}
	}
	
	public static class StepDialog extends DialogFragment {
		
		public Step s;
		
		public void setStep(Step s) {
			this.s = s;
		}
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        ViewGroup v = (ViewGroup)getActivity().getLayoutInflater().inflate(R.layout.step_dialog, null);
	        ((TextView)v.findViewById(R.id.summary)).setText(Html.fromHtml(s.summary));
	        ((TextView)v.findViewById(R.id.distance_key)).setText(s.distance);
	        ((TextView)v.findViewById(R.id.time_key)).setText(s.time);
	        ((TextView)v.findViewById(R.id.fuel_usage_key)).setText(Double.toString(twoDecimalPlaces(s.fuel)) + " litres");
	        builder.setView(v);
	        return builder.create();
	    }
	}
	
	private static double twoDecimalPlaces(double d) {
		return ((int)(d * 100))/100.0;
	}

	static class ViewHolder {
	    public TextView text;
	}
	
	
}
