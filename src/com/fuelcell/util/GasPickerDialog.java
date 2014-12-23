package com.fuelcell.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.fuelcell.R;

public class GasPickerDialog {
	private static Dialog dialog;
	
	public static Dialog getGasPickerDialog(Context context, int layoutResId, EditText output, TextView unitText){
		if (dialog == null) {
			createDialog(context, layoutResId, output, unitText);
		}
		return dialog;
	}
	
	private static void createDialog(Context context, int layoutResId, final EditText output, final TextView unitText) {
		dialog = new Dialog(context);
		dialog.setTitle("Enter Gas Price");
		dialog.setContentView(layoutResId);
		
		Button accept = (Button) dialog.findViewById(R.id.accept);
		Button reject= (Button) dialog.findViewById(R.id.reject);
		final NumberPicker dollars = (NumberPicker) dialog.findViewById(R.id.dollars);
		final NumberPicker centTens = (NumberPicker) dialog.findViewById(R.id.centTens);
		final NumberPicker centOnes = (NumberPicker) dialog.findViewById(R.id.centOnes);
		
		//Spinner for Units
		final Spinner units = (Spinner) dialog.findViewById(R.id.units);
		final ArrayAdapter<String> unitsAdapter = new ArrayAdapter<String>(context, R.layout.step_list_item);
		unitsAdapter.addAll(new String[]{"L","Gal"});
		units.setAdapter(unitsAdapter);
		
		//Dollars Number Pickers
		dollars.setMaxValue(9);
		dollars.setMinValue(0);
		dollars.setWrapSelectorWheel(true);
		
		//Cents Number Pickers
		centTens.setMaxValue(9);
		centTens.setMinValue(0);
		centTens.setWrapSelectorWheel(true);
		
		centOnes.setMaxValue(9);
		centOnes.setMinValue(0);
		centOnes.setWrapSelectorWheel(true);
		
		accept.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				output.setText(dollars.getValue() + "." + centTens.getValue() + centOnes.getValue());
				unitText.setText((String)units.getSelectedItem());
				dialog.dismiss();
			}
			
		});
		reject.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
			
		});
	}
}