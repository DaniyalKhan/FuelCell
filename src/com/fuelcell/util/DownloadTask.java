package com.fuelcell.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fuelcell.csvutils.CSVFileUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

public class DownloadTask extends AsyncTask<String, Integer, String> {
	
	HttpClient httpclient;
	Context context;
	ProgressDialog dialog;
	WakeLock wakeLock;
	
	public DownloadTask(Context context, ProgressDialog dialog) {
		this.context = context;
		this.httpclient = new DefaultHttpClient();
		this.dialog = dialog;
	}
	
	protected String doInBackground(String... stringUrls) {
		InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        int totalProgress = 0;
        try {
        	for (String stringUrl: stringUrls) {
        		URL url = new URL(stringUrl);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();
	
	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                return "Server returned HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }
	
	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            int fileLength = connection.getContentLength();
	            
	            String canonName = CSVFileUtils.getNameFromURL(stringUrl);
	            System.out.println("Downloading: " + canonName);
	            
	            // download the file
	            input = connection.getInputStream();
	            output = context.openFileOutput(canonName, Context.MODE_PRIVATE);
	
	            byte data[] = new byte[4096];
	            int count;
	            while ((count = input.read(data)) != -1) {
	                // allow cancelling with back button
	                if (isCancelled()) {
	                    input.close();
	                    return null;
	                }
	                totalProgress += count;
	                // publishing the progress....
	                if (fileLength > 0) // only if total length is known
	                    publishProgress((int) (totalProgress * 100 / fileLength)/stringUrls.length);
	                output.write(data, 0, count);
	            }
        	}
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
    	super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgress(progress[0]);
    }

    protected void onPostExecute(String result) {
    	wakeLock.release();
        dialog.dismiss();
        if (result != null)
            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context,"Files downloaded", Toast.LENGTH_LONG).show();
    }

	@Override
	protected void onPreExecute() {
    	super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user 
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wakeLock.acquire();
        dialog.show();
	}
    
}
