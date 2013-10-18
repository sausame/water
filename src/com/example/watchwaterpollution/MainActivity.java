package com.example.watchwaterpollution;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private static final int ADD = 0;
	private static final int MODIFY = 1;
	private static final int DELETE = 2;

	private ArrayList<Water> mData = null;
	private WaterAdapter mAdapter;
	private SwipeListView mDateStatusList;

	private WaterManager mManager = new WaterManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initData();

		mAdapter = new WaterAdapter(this, mData);
		mDateStatusList = (SwipeListView) findViewById(R.id.list);
		mDateStatusList.setAdapter(mAdapter);
		mDateStatusList.setWindow(getWindow());
		mDateStatusList
				.setListViewCallBack(new SwipeListView.ListViewCallBack() {
					@Override
					public void showCannotSwipe() {
					}

					@Override
					public void onChildDismissed(View v, int position) {
						deleteRaw(position);
					}

					@Override
					public boolean canDismissed(View v, int position) {
						return true;
					}
				});

		setData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			onActionAdd();
			return true;
		case R.id.action_settings:
			onActionSettings();
			return true;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "" + requestCode + ", " + resultCode + ", " + data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case ADD:
				break;
			case MODIFY:
				break;
			case DELETE:
				break;
			default:
				break;
			}
		}
	}

	private void onActionAdd() {
		WaterManager.test();
	}

	private void onActionSettings() {
	}

	private void deleteRaw(int position) {

	}

	public void initData() {
		mManager.setPathname(getString(R.string.filename));
		mManager.load();

		mData = new ArrayList<Water>();
	}

	public void setData() {
		Log.i(TAG, mManager.toString());

		mData.clear();
		mManager.reset();

		Water water;

		while (null != (water = mManager.getWater())) {
			mData.add(water);
		}

		mAdapter.notifyDataSetChanged();
	}

	public class WaterAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<Water> mData = null;

		public WaterAdapter(Context context, ArrayList<Water> data) {
			mContext = context;
			mData = data;
		}

		// ====================================================================
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			if (position < 0 || position >= getCount()) {
				return null;
			}

			ItemViewGroup viewGroup = null;
			if (view == null) {
				LayoutInflater factory = LayoutInflater.from(mContext);
				view = factory.inflate(R.layout.listitem, null);

				viewGroup = new ItemViewGroup(view);

				view.setTag(viewGroup);
			} else {
				viewGroup = (ItemViewGroup) view.getTag();
			}

			viewGroup.setValues(mData.get(position));

			return view;
		}

		// ====================================================================
		private class ItemViewGroup {
			public RelativeLayout mParent;

			public TextView mIndexParam;
			public TextView mCity;
			public TextView mLocation;
			public TextView mDescription;
			public TextView mWeather;
			public TextView mDate;
			public TextView mParamGroup[] = new TextView[Water.PARAM_NUM];

			private ItemViewGroup(View parent) {
				setViews(parent);
			}

			public void setViews(View parent) {
				mParent = (RelativeLayout) parent;

				mIndexParam = (TextView) parent.findViewById(R.id.index_param);
				mCity = (TextView) parent.findViewById(R.id.city);
				mLocation = (TextView) parent.findViewById(R.id.location);
				mDescription = (TextView) parent.findViewById(R.id.description);
				mWeather = (TextView) parent.findViewById(R.id.weather);
				mDate = (TextView) parent.findViewById(R.id.date);

				for (int i = 0; i < Water.PARAM_NUM; i++) {
					mParamGroup[i] = (TextView) parent
							.findViewById(mParamResIDGroup[i]);
				}
			}

			public void setValues(Water water) {
				mParent.setBackgroundResource(mBgResIDGroup[water.getLevel()]);

				mIndexParam.setText("" + water.getIndexParam());
				mCity.setText(water.getCity());
				mLocation.setText(water.getLocation());
				mDescription.setText(water.getDescription());
				mWeather.setText(water.getWeather());
				mDate.setText(water.getFormatUpdateTime());

				for (int i = 0; i < Water.PARAM_NUM; i++) {
					mParamGroup[i].setText("" + water.getParam(i));
				}
			}

			private final int mParamResIDGroup[] = { R.id.param_0,
					R.id.param_1, R.id.param_2, R.id.param_3 };

			private final int mBgResIDGroup[] = { R.drawable.pic_0,
					R.drawable.pic_1, R.drawable.pic_2, R.drawable.pic_3,
					R.drawable.pic_4, R.drawable.pic_5, R.drawable.pic_6,
					R.drawable.pic_7, R.drawable.pic_8, R.drawable.pic_9 };

		}
	}

}
