package com.fuelcell.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

	public static final JSONArray getJSONArray(JSONObject object, String field) {
		if (object == null) return null;
		try {
			return object.getJSONArray(field);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final JSONObject getJSONObject(JSONArray object, int index) {
		if (object == null) return null;
		try {
			return object.getJSONObject(index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final JSONObject getJSONObject(JSONObject object, String field) {
		if (object == null) return null;
		try {
			return object.getJSONObject(field);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final String getString(JSONObject object, String field) {
		if (object == null) return null;
		try {
			return object.getString(field);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
