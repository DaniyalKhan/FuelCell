package com.fuelcell.action;

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ButtonSettings {
	
	public static void setHomeButton(ImageView button, final DrawerLayout mDrawer) {
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mDrawer.openDrawer(Gravity.LEFT);
			}
			
		});
	}
}