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

				viewGroup = new ItemViewGroup();

				viewGroup.mDate = (TextView) view.findViewById(R.id.date);

				view.setTag(viewGroup);
			} else {
				viewGroup = (ItemViewGroup) view.getTag();
			}

			showItemInfos(position, viewGroup);

			return view;
		}

		private void showItemInfos(final int position, ItemViewGroup viewGroup) {

			Water water = mData.get(position);

			viewGroup.mDate.setText(water.getFormatUpdateTime());
		}

		// ====================================================================
		private class ItemViewGroup {
			public TextView mDate;
		}
	}

}
