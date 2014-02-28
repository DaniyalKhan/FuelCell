package com.fuelcell;

import com.fuelcell.util.DownloadTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;

public class FuelCellActivity extends Activity {

	ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fuel_cell);
		// instantiate it within the onCreate method
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Downloading cat data for 2013");
		progressDialog.setIndeterminate(true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(true);
		
		// execute this when the downloader must be fired
		final DownloadTask downloadTask = new DownloadTask(this, progressDialog);
		downloadTask.execute("http://oee.nrcan.gc.ca/sites/oee.nrcan.gc.ca/files/files/csv/MY2013-Fuel-Consumption-Ratings.csv");

		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		    @Override
		    public void onCancel(DialogInterface dialog) {
		        downloadTask.cancel(true);
		    }
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fuel_cell, menu);
		return true;
	}

}
