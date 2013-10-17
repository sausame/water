package com.example.watchwaterpollution;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WaterManager {

	private static final String TAG = "WaterManager";

	private JSONArray mJsonArray = null;
	private int mCurrentIndex = 0;
	private String mPathname;

	public void setPathname(String path) {
		mPathname = path;
	}

	public int getSize() {
		return mJsonArray == null ? 0 : mJsonArray.length();
	}

	public void load() {
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(mPathname)));

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();

			setWaterBuffer(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void save() {
		if (null == mJsonArray || 0 == mJsonArray.length()) {
			Log.e(TAG, "Nothing is needed to save.");
			return;
		}

		try {
			String jsonString = mJsonArray.toString();
			OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(mPathname));

			osw.write(jsonString, 0, jsonString.length());

			osw.flush();
			osw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setWaterBuffer(String buffer) {
		try {
			mJsonArray = new JSONArray(buffer);
			mCurrentIndex = 0;

			if (0 == mJsonArray.length()) {
				mJsonArray = null;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		mCurrentIndex = 0;
	}

	public boolean add(Water water) {
		reset();

		if (mJsonArray == null) {
			mJsonArray = new JSONArray();
		}

		mJsonArray.put(water.toJSONObject());

		return true;
	}

	public boolean del(int id) {
		reset();

		JSONArray array = new JSONArray();
		Water water = null;

		for (; null != (water = getWater()); id--) {
			if (0 == id) {
				continue;
			}

			array.put(water.toJSONObject());
		}

		mJsonArray = array;
		return true;
	}

	public boolean modify(int id, Water water) {
		if (!del(id)) {
			return false;
		}

		return add(water);
	}

	public Water getWater() {
		if (null == mJsonArray || mCurrentIndex >= mJsonArray.length()) {
			Log.d(TAG, (mJsonArray == null) ? "NO array" : "" + mCurrentIndex
					+ " >= " + mJsonArray.length());
			return null;
		}

		try {
			JSONObject obj = mJsonArray.getJSONObject(mCurrentIndex++);
			return Water.parseWater(obj);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String toString() {

		String str = "\n-------------------------------------------------------------------------------\n";
		str += "Pathname: " + mPathname + "\n";

		try {
			str += mJsonArray.toString(2);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		str += "\n-------------------------------------------------------------------------------\n";

		return str;
	}

	public static void test() {
		WaterManager manager = new WaterManager();
		manager.setPathname("/sdcard/water.json");

		manager.load();
		Log.i(TAG, manager.toString());

		manager.add(Water.createRandomWater());

		manager.save();
		Log.i(TAG, manager.toString());
	}
}
