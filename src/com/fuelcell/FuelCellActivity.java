package com.fuelcell;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.fuelcell.csvutils.CSVFileUtils;
import com.fuelcell.util.DownloadTask;
import com.fuelcell.util.DownloadTask.DownloadCallback;

public class FuelCellActivity extends Activity {
	
	ProgressDialog progressDialog;
	ContextWrapper wrapper;
	
	private void deleteContents() {
		File[] filesArray = wrapper.getFilesDir().listFiles();
		for (int i = 0; i < filesArray.length; i++) {
			filesArray[i].delete();
		}
	}
	
	private void printContents() {
		File[] filesArray = wrapper.getFilesDir().listFiles();
		for (int i = 0; i < filesArray.length; i++) {
			System.out.println(filesArray[i]);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fuel_cell);
		wrapper = new ContextWrapper(this);
		deleteContents();
		//list of all files to download
		ArrayList<String> toDownload = new ArrayList<String>();
		//list of all files that are currently in the folder
		ArrayList<String> files = new ArrayList<String>();
		
		{
			File[] filesArray = wrapper.getFilesDir().listFiles();
			for (int i = 0; i < filesArray.length; i++) {
				String name = filesArray[i].getName();
				//add the file names (without parent directory to the file list)
				files.add(name.substring(name.lastIndexOf("/")+1, name.length()));
			}
		}
		
		//construct a list of missing files
		String[] csvURLs = CSVFileUtils.getAllCSV();
		for (int i = 0; i < csvURLs.length; i++) {
			String url = csvURLs[i];
			String canonName = CSVFileUtils.getNameFromURL(url);
			if (!files.contains(canonName)) toDownload.add(url);
		}
		
		if (!toDownload.isEmpty()) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Downloading Fuel Data From Government of Canada Site");
			progressDialog.setIndeterminate(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(true);
			
			//download for each file
			final DownloadTask downloadTask = new DownloadTask(this, progressDialog, new DownloadCallback() {
				@Override
				public void onDownloadComplete() {
					startSearch();
				}
			});
			downloadTask.execute(toDownload.toArray(new String[toDownload.size()]));
	
			progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			    @Override
			    public void onCancel(DialogInterface dialog) {
			        downloadTask.cancel(true);
			        startSearch();
			    }
			});
		} else {
			startSearch();
		}
		
	}
	
	private void startSearch() {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivity(intent);
	}

}
