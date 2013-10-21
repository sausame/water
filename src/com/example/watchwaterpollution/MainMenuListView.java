package com.example.watchwaterpollution;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainMenuListView extends ListView implements OnItemClickListener {

	private InnerAdapter mAdapter;

	public MainMenuListView(Context context) {
		super(context);
		init(context);
	}

	public MainMenuListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init(context);
	}

	public MainMenuListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mAdapter = new InnerAdapter(context);
		setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		switch (position) {
			case 0:
				break;
			case 1:
				break;
		}
	}

	public class InnerAdapter extends BaseAdapter {
		private Context mContext;

		public InnerAdapter(Context context) {
			mContext = context;
		}

		// ====================================================================
		@Override
		public int getCount() {
			return mPairedResIDGroup.length;
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
				view = factory.inflate(R.layout.main_menu_list_item, null);

				viewGroup = new ItemViewGroup(view);

				view.setTag(viewGroup);
			} else {
				viewGroup = (ItemViewGroup) view.getTag();
			}

			viewGroup.setValues(mPairedResIDGroup[position]);

			return view;
		}

		// ====================================================================
		private class ItemViewGroup {
			public TextView mText;
			public ImageView mImage;

			private ItemViewGroup(View parent) {
				setViews(parent);
			}

			public void setViews(View parent) {
				mText = (TextView) parent.findViewById(R.id.text);
				mImage = (ImageView) parent.findViewById(R.id.image);
			}

			public void setValues(int resIDs[]) {
				mText.setText(resIDs[0]);
				mImage.setImageResource(resIDs[1]);
			}
		}
	}

	// ====================================================================
	private final int mPairedResIDGroup[][] = {
		{ R.string.action_settings, R.drawable.ic_side_user_setting },
		{ R.string.action_sync, R.drawable.ic_side_sync_soft },
		{ R.string.action_about, R.drawable.ic_side_about },
		{ R.string.action_exit, R.drawable.ic_side_exit } };

}
