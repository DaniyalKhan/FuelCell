package com.fuelcell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.fuelcell.google.Directions;
import com.fuelcell.models.Car;

public class DirectionsActivity extends FragmentActivity {

	private static final String TAG = "DIRECTION_TAG";
	
	private Car car;
	private Directions directions;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_directions);
		Intent intent = getIntent();
		car = (Car) intent.getParcelableExtra("car");
		
		final EditText origin = (EditText) findViewById(R.id.origin);
		final EditText destination = (EditText) findViewById(R.id.destination);
		final Button findRoutes = (Button) findViewById(R.id.find_routes);
		
		findRoutes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (findViewById(R.id.root) != null) {
					enterFragment();
		        }
			}
		});
		
	}
	
	private void enterFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
        DirectionsFragment firstFragment = new DirectionsFragment();
        firstFragment.setArguments(getIntent().getExtras());
        ft.add(R.id.root, firstFragment, "DIRECTION_TAG").addToBackStack(null).commit();
	}

}
