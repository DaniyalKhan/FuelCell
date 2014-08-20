package com.fuelcell.action;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ButtonSettings {
	
	private static Rect rect;
	private static boolean state = false;
	
	public static void pressSize (final Button button, final int change){
		button.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
			
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) button.getLayoutParams();
				
				//When Button Pressed
		        if(event.getAction() == MotionEvent.ACTION_DOWN && state == false) {
		        	setSize(button, lp, change);
					rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
					state = true;
		        }
		        //Finger moved from appropriate spot
		        if(event.getAction() == MotionEvent.ACTION_MOVE && state == true) {
		        	if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
		        		setSize(button, lp, (-1)*change);
						state = false;
		        	}
		        }
		        //When Finger Lifted after Button Press
		        if (event.getAction() == MotionEvent.ACTION_UP && state == true) {
		        	setSize(button, lp, (-1)*change);
					state = false;
		        }
		        return false;
			}
		});
	}
	//Set size of button, standard is when it shrinks on press, add (-1) when undoing (action up)
	private static void setSize(Button button, LinearLayout.LayoutParams lp, int change) {
		button.setHeight(button.getHeight() - change);
    	lp.setMargins(lp.leftMargin, lp.topMargin + change, lp.rightMargin, lp.bottomMargin);
		button.setLayoutParams(lp);
	}
	
}