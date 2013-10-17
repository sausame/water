package com.example.watchwaterpollution;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

public class SwipeListView extends ListView implements SwipeHelper.Callback {
	private static final String TAG = SwipeListView.class.getSimpleName();

	public interface ListViewCallBack {
		public void onChildDismissed(View v, int position);

		public void showCannotSwipe();

		public boolean canDismissed(View v, int position);
	}

	private SwipeHelper mSwipeHelper = null;
	private ListViewCallBack mCallBack = null;
	private Context mContext;
	private Window mWindow;
	private PopupWindow mUndoWindow = null;
	private int mChildIndex;
	private boolean mIsToDismiss = false;

	public SwipeListView(Context context) {
		this(context, null);
	}

	public SwipeListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		float densityScale = getResources().getDisplayMetrics().density;
		float pagingTouchSlop = ViewConfiguration.get(context)
				.getScaledPagingTouchSlop();
		mSwipeHelper = new SwipeHelper(SwipeHelper.X, this, densityScale,
				pagingTouchSlop, context);
	}

	public void setWindow(Window window) {
		mWindow = window;
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		float densityScale = getResources().getDisplayMetrics().density;
		mSwipeHelper.setDensityScale(densityScale);
		float pagingTouchSlop = ViewConfiguration.get(getContext())
				.getScaledPagingTouchSlop();
		mSwipeHelper.setPagingTouchSlop(pagingTouchSlop);
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mSwipeHelper.onInterceptTouchEvent(ev)
				| super.onInterceptTouchEvent(ev);
	}

	private boolean mIsCancelClick = false;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean flag = mSwipeHelper.onTouchEvent(ev);

		// Cancel a click action immediately.
		if (flag && MotionEvent.ACTION_UP == ev.getAction()) {
			mIsCancelClick = true;
			final Timer timer = new Timer(true);
			TimerTask task = new TimerTask() {
				public void run() {
					mIsCancelClick = false;
					timer.cancel();
				}
			};

			timer.schedule(task, 100);
			return true;
		}

		return super.onTouchEvent(ev);
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		if (mIsCancelClick) {
			Log.v(TAG, "Click action is canceled.");
			return true;
		}
		return super.performItemClick(view, position, id);
	}

	@Override
	public View getChildAtPosition(MotionEvent ev) {
		// Skip the footer views.
		final int count = getChildCount() - getFooterViewsCount();

		int touchY = (int) ev.getY();
		int childIdx = getHeaderViewsCount(); // Skip the header views.

		for (; childIdx < count; childIdx++) {
			View slidingChild = getChildAt(childIdx);
			if (touchY >= slidingChild.getTop()
					&& touchY <= slidingChild.getBottom()) {
				mChildIndex = childIdx;
				return slidingChild;
			}
		}

		mChildIndex = -1;
		return null;
	}

	@Override
	public View getChildContentView(View v) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			v.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
			ImageView imageView = new ImageView(mContext);
			imageView.setBackgroundColor(0xffffffff);
			imageView.setImageBitmap(bitmap);
			return imageView;
		}

		return v;
	}

	@Override
	public boolean canChildBeDismissed(View v) {
		if (mCallBack != null) {
			return mCallBack.canDismissed(v, mChildIndex);
		}
		return true;
	}

	public void setStatusBarHeight(int height) {
		mSwipeHelper.setStatusBarHeight(height);
	}

	public void setListViewCallBack(ListViewCallBack callback) {
		mCallBack = callback;
	}

	@Override
	public void onBeginDrag(View v) {
		requestDisallowInterceptTouchEvent(true);
	}

	@Override
	public void onChildDismissed(View v) {
		mIsToDismiss = true;

		try {
			showUndoWindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showCannotSwipe() {
		if (mCallBack != null) {
			mCallBack.showCannotSwipe();
		}
	}

	@Override
	public void onDragCancelled(View v) {
	}

	// -----------------------------------------------------------------------------
	private void showUndoWindow() throws Exception {
		if (mWindow == null) {
			throw new Exception("Window is NOT set.");
		}

		LayoutInflater factory = LayoutInflater.from(mContext);
		View view = factory.inflate(R.layout.undo_delete, null);

		mUndoWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, 80,
				true);
		mUndoWindow.setOutsideTouchable(true);
		mUndoWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				dismiss();
			}
		});
		mUndoWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		view.findViewById(R.id.action_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						undoDismiss();
					}
				});

		Rect rect = new Rect();
		mWindow.getDecorView().getWindowVisibleDisplayFrame(rect);
		mUndoWindow.showAtLocation(this, Gravity.CENTER_HORIZONTAL
				| Gravity.TOP, 0, rect.top + getTop() + getHeight());

	}

	private void undoDismiss() {
		mIsToDismiss = false;
		mSwipeHelper.snapChild();

		mUndoWindow.dismiss();
	}

	private void dismiss() {
		if (mIsToDismiss && mCallBack != null) {
			invalidateNextItem();
			mCallBack.onChildDismissed(mSwipeHelper.getCurrentView(),
					mChildIndex);
		}
	}

	private void invalidateNextItem() {
		mSwipeHelper.snapChild();

		int index = mChildIndex + 1;
		if (index >= getChildCount()) {
			return;
		}

		View view = getChildAt(index);
		// SwipeHelper.invalidateGlobalRegion(view); // XXX It doesn't work!!!
	}
}
