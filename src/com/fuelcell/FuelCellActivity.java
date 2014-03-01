package com.fuelcell;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.cookie.CookieSpecFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;

import com.fuelcell.util.CSVFileUtils;
import com.fuelcell.util.DownloadTask;

public class FuelCellActivity extends Activity {
	
	ProgressDialog progressDialog;
	ContextWrapper wrapper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fuel_cell);
		wrapper = new ContextWrapper(this);
		
		ArrayList<String> toDownload = new ArrayList<String>();
		ArrayList<File> files = new ArrayList<File>();
		
		{
			File[] filesArray = wrapper.getFilesDir().listFiles();
			for (int i = 0; i < filesArray.length; i++) {
				files.add(filesArray[i]);
			}
		}
		
		
		{
			File[] filesArray = wrapper.getFilesDir().listFiles();
			for (int i = 0; i < filesArray.length; i++) {
				filesArray[i].delete();
			}
		}
		
		String[] csvURLs = CSVFileUtils.getAllCSV();
		for (int i = 0; i < csvURLs.length; i++) {
			String url = csvURLs[i];
			String canonName = CSVFileUtils.getNameFromURL(url);
			if (!files.contains(canonName)) toDownload.add(url);
		}
		
		if (toDownload != null) {
			// instantiate it within the onCreate method
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Downloading Fuel Data From Government of Canada Site");
			progressDialog.setIndeterminate(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(true);
			
			// execute this when the downloader must be fired
			final DownloadTask downloadTask = new DownloadTask(this, progressDialog);
			downloadTask.execute(toDownload.toArray(new String[toDownload.size()]));
	
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			    @Override
			    public void onCancel(DialogInterface dialog) {
			        downloadTask.cancel(true);
			    }
			});
		}
		
		{
			File[] filesArray = wrapper.getFilesDir().listFiles();
			for (int i = 0; i < filesArray.length; i++) {
				System.out.println(filesArray[i]);
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fuel_cell, menu);
		return true;
	}

}
