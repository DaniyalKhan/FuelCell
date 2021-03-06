package com.fuelcell;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuelcell.action.ButtonSettings;
import com.fuelcell.google.Directions.Route;
import com.fuelcell.google.GMapV2Direction;
import com.fuelcell.google.GMapV2Direction.DocCallback;
import com.fuelcell.models.Car;
import com.fuelcell.models.CarFrame;
import com.fuelcell.util.CarDatabase;
import com.fuelcell.util.GasPickerDialog;
import com.fuelcell.util.JSONUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TravelActivity extends NavActivity implements OnFocusChangeListener{
	
	private Car car;
	private Route route;
	
	// m/s
	private static final double HIGHWAY_LIMIT = 80.0 * 1000.0/3600.0; 
	private static final double L_TO_GAL = 0.264172;
	
	private double highwayMetres;
	private double cityMetres;
	private double highwaySeconds;
	private double citySeconds;
	
	private GMapV2Direction md;
	private GoogleMap map;
	
	private Button expand;
	private boolean isExpanded;
	
	private Fragment mapFragment;
	private ScrollView scrollView;
	private EditText priceInput;
	
	private enum GasUnit {LITRE, GALLON};
	private static GasUnit outputUnit = GasUnit.LITRE;
	private TextView unitText;
	private double unitsSpent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater.inflate(R.layout.travel_activity,  null, false);
		mDrawer.addView(contentView,0);
		
		getWindow().setBackgroundDrawableResource(R.drawable.background);
		
		Intent intent = getIntent();
		SharedPreferences defaultCarPrefs = getSharedPreferences("default", MODE_PRIVATE);
		CarFrame defaultCarFrame = new CarFrame(defaultCarPrefs.getInt("year", -1),
				defaultCarPrefs.getString("manufacturer", null), 
				defaultCarPrefs.getString("model", null), 
				defaultCarPrefs.getString("vehicleClass", null));
		car = ((Car) intent.getParcelableExtra("car") != null) ? 
				(Car) intent.getParcelableExtra("car") : CarDatabase.obtain(getApplicationContext()).getCarFull(defaultCarFrame) ;
		route = (Route) intent.getParcelableExtra("route");
		priceInput = (EditText) findViewById(R.id.price);
		unitText = (TextView) findViewById(R.id.unit);
		priceInput.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				showGasPickerDialog();
			}
		});
		priceInput.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				showGasPickerDialog();
			}	
		});
		unitText.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				//Change to either litres or gallons
				//Directions Popup 
				//Gas Usage Unit
				//Gas Usage Value
				//CO2 Emissions Value
				//Trip Cost Value
				if ((s.toString()).equalsIgnoreCase("Gal")) {
					outputUnit = GasUnit.GALLON;
				} else {
					outputUnit = GasUnit.LITRE;
				}
				calculateAndSet(outputUnit);
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Loading Google Map Route...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
		
		scrollView = (ScrollView) findViewById(R.id.scroll_view);
		
		md = new GMapV2Direction();
		final SupportMapFragment myMAPF = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		mapFragment = getSupportFragmentManager().findFragmentById(R.id.map);
		map = myMAPF.getMap();
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
						fuelUsed = (car.highwayEffL/100.0) / 1000.0 * distance;
					} else {
						cityMetres += distance;
						citySeconds += duration;
						fuelUsed = (car.cityEffL/100.0) / 1000.0 * distance;
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
		
		((EditText) findViewById(R.id.price)).addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				try {
					double price = Double.parseDouble(s.toString());
					((TextView) findViewById(R.id.price_value)).setText("$" + Double.toString(twoDecimalPlaces(price * unitsSpent)));
				
				} catch (Exception e) {
					((TextView) findViewById(R.id.price_value)).setText("$0.00");
				}
			}

			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
			}
		});

		calculateAndSet(outputUnit);
		setExpandButton();
		ButtonSettings.setHomeButton((ImageView) findViewById(R.id.mainicon), mDrawer);
	}
	
	public void calculateAndSet(GasUnit unit) {
		if (unit == GasUnit.LITRE) {
			double highwayKM = highwayMetres/1000.0;
			double cityKM = cityMetres/1000.0;
			
			//litres / KM
			double hEffM = car.highwayEffL/100.0;
			double cEffL = car.cityEffL/100.0;

			//emissions = g/km
			double emissions = car.emissions * ((highwayMetres + cityMetres) / 1000);
			
			unitsSpent = twoDecimalPlaces(hEffM * highwayKM + cEffL * cityKM);
			
			TextView usage = (TextView) findViewById(R.id.usage_value);
			usage.setText(Double.toString(unitsSpent) + " litres");
			
			TextView co2Emission = (TextView) findViewById(R.id.co2_value);
			co2Emission.setText(Double.toString(twoDecimalPlaces(emissions)) + " grams");
		} else if (unit == GasUnit.GALLON) {
			//metres to miles
			double highwayM = highwayMetres/1000.0 * 0.621371;
			double cityM = cityMetres/1000.0 * 0.621371;
			
			//MPG
			double hEffM = car.highwayEffM;
			double cEffL = car.cityEffM;

			double emissions = car.emissions * ((highwayMetres + cityMetres) / 1000);
			
			unitsSpent = twoDecimalPlaces(1/hEffM * highwayM + 1/cEffL * cityM);
			
			TextView usage = (TextView) findViewById(R.id.usage_value);
			usage.setText(Double.toString(unitsSpent) + " gallons");
			
			TextView co2Emission = (TextView) findViewById(R.id.co2_value);
			co2Emission.setText(Double.toString(twoDecimalPlaces(emissions)) + " grams");
		}
		double price = Double.parseDouble(priceInput.getText().toString());
		((TextView) findViewById(R.id.price_value)).setText("$" + Double.toString(twoDecimalPlaces(price * unitsSpent)));
	}
	
	public void setExpandButton() {
		expand = (Button) findViewById(R.id.expandButton);
		isExpanded = false;
		expand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View map = TravelActivity.this.findViewById(R.id.map);
				if(isExpanded){
					LayoutParams params = (LayoutParams) map.getLayoutParams();
					params.height = 200;
					map.setLayoutParams(params);
					isExpanded = false;
					expand.setBackgroundResource(R.drawable.route_expand);
				} else {
					LayoutParams params = (LayoutParams) map.getLayoutParams();
					params.height = 500;
					map.setLayoutParams(params);
					isExpanded = true;
					expand.setBackgroundResource(R.drawable.route_shrink);
				}
			}
		});
	}
	
	public void showGasPickerDialog(){
		final Dialog d = GasPickerDialog.getGasPickerDialog((Context) TravelActivity.this, R.layout.gas_dialog, priceInput, unitText);
		d.show();
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
	        if (outputUnit == GasUnit.GALLON) {
	        	((TextView)v.findViewById(R.id.fuel_usage_key)).setText(Double.toString(twoDecimalPlaces(s.fuel * L_TO_GAL)) + " gallons");
	        } else {
	        	((TextView)v.findViewById(R.id.fuel_usage_key)).setText(Double.toString(twoDecimalPlaces(s.fuel)) + " litres");
	        }
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

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus && v.getId() == R.id.map){ 
			scrollView.setEnabled(false);			
		} else if (hasFocus && v.getId() == R.id.map){
			scrollView.setEnabled(true);
		}
		
	}
	
	
}
