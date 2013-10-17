package com.example.watchwaterpollution;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Water implements Serializable {

	private static final String TAG = "Water";
	private static final int PARAM_NUM = 4;
	private static final String DATETIME_FORMAT = "yyyy-MM-dd hh:mm";

	private int mIndexParam;
	private int mParamGroup[] = new int[PARAM_NUM];
	private Date mUpdateTime;
	private String mCity;
	private String mLocation;

	public int getIndexParam() {
		return mIndexParam;
	}

	public void setIndexParam(int param) {
		mIndexParam = param;
	}

	public int getParam(int id) {
		return mParamGroup[id];
	}

	public int setParam(int id, int param) {
		return mParamGroup[id] = param;
	}

	public Date getUpdateTime() {
		return mUpdateTime;
	}

	public void setUpdateTime(Date date) {
		mUpdateTime = date;
	}

	public String getFormatUpdateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_FORMAT);
		return formatter.format(mUpdateTime);
	}

	public void setFormatUpdateTime(String formatTime) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_FORMAT);
			mUpdateTime = formatter.parse(formatTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String city) {
		mCity = city;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String city) {
		mLocation = city;
	}

	public static Water parseWater(JSONObject object) {
		try {
			Water water = new Water();

			water.setIndexParam(Integer.parseInt(object.getString("IndexParam")));
			for (int i = 0; i < Water.PARAM_NUM; i++) {
				water.setParam(i,
						Integer.parseInt(object.getString("Param_" + i)));
			}

			water.setFormatUpdateTime(object.getString("UpdateTime"));
			water.setCity(object.getString("City"));
			water.setLocation(object.getString("Location"));

			return water;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONObject toJSONObject() {
		try {
			JSONObject object = new JSONObject();

			object.put("IndexParam", mIndexParam);
			for (int i = 0; i < Water.PARAM_NUM; i++) {
				object.put("Param_" + i, getParam(i));
			}

			object.put("UpdateTime", getFormatUpdateTime());
			object.put("City", getCity());
			object.put("Location", getLocation());

			return object;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String toString() {
		String str = "DateTime: " + mUpdateTime + "\n";
		str += "City: " + mCity + "\n";
		str += "mLocation: " + mLocation + "\n";
		str += "IndexParam: " + mIndexParam + "\n";

		for (int i = 0; i < mParamGroup.length; i++) {
			str += "NO." + i + ": " + mParamGroup[i] + "\n";
		}
		str += "\n";

		return str;
	}

	private static Date getDay(int diff) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, diff);

		return calendar.getTime();
	}

	public static Water createRandomWater() {
		Random random = new Random((new Date()).getTime());
		Water water = new Water();

		water.setUpdateTime(getDay(-1 * (Math.abs(random.nextInt()) % 10)));
		water.setIndexParam(Math.abs(random.nextInt()) % 1000);
		for (int i = 0; i < Water.PARAM_NUM; i++) {
			water.setParam(i, Math.abs(random.nextInt()) % 1000);
		}

		water.mCity = "City-" + Math.abs(random.nextInt()) % 10;
		water.mLocation = "Location-" + Math.abs(random.nextInt()) % 10;

		return water;
	}
}
