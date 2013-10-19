package com.example.watchwaterpollution;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollLayout extends LinearLayout {

//    private static final String TAG = "scroller";

    private Scroller scroller;

    private int currentScreenIndex;

    private GestureDetector gestureDetector;

    // 设置一个标志位，防止底层的onTouch事件重复处理UP事件
    private boolean fling;

    /**
     * 菜单栏的宽度
     */
    int menuWidth=80;
    
    /**
     * 显示左边菜单
     * 否则显示右边菜单
     */
    private boolean showLeft=true;
    
    /**
     * 滚出边界监听器
     */
    private OnScrollSideChangedListener scrollSideChangedListener;
    
    public Scroller getScroller() {
            return scroller;
    }

    public OnScrollSideChangedListener getScrollSideChangedListener() {
        return scrollSideChangedListener;
    }

    public void setScrollSideChangedListener(
            OnScrollSideChangedListener scrollSideChangedListener) {
        this.scrollSideChangedListener = scrollSideChangedListener;
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            initView(context);
    }

    public ScrollLayout(Context context) {
            super(context);
            initView(context);
    }

    private void initView(final Context context) {
            this.scroller = new Scroller(context,AnimationUtils.loadInterpolator(context,
                    android.R.anim.overshoot_interpolator));

            this.gestureDetector = new GestureDetector(new OnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                            return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {

                            {// 防止向第一页之前移动
                                if(1==currentScreenIndex)
                                {
                                    int screenLeft=getWidth()-menuWidth;
                                    if(showLeft && getScrollX()>screenLeft)
                                    {
                                        showLeft=false;
//                                        Log.e("TAG","显示右边菜单栏");
                                        if(null!=scrollSideChangedListener)
                                            scrollSideChangedListener.onScrollSideChanged(ScrollLayout.this, showLeft);
                                    }
                                    else if(!showLeft && getScrollX()<screenLeft)
                                    {
                                        showLeft=true;
//                                        Log.e("TAG","显示左边菜单栏");
                                        if(null!=scrollSideChangedListener)
                                            scrollSideChangedListener.onScrollSideChanged(ScrollLayout.this, showLeft);
                                    }
                                }
                                
                                    fling = true;
                                    scrollBy((int) distanceX, 0);
//                                    Log.d("TAG", "on scroll>>>>>>>>>>>>>>>>>移动<<<<<<<<<<<<<<>>>");
                            }
                            return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                    float velocityX, float velocityY) {
                        
                            if (Math.abs(velocityX) > ViewConfiguration.get(context)
                                            .getScaledMinimumFlingVelocity()) 
                            {// 判断是否达到最小轻松速度，取绝对值的
                                fling = true;
                                snapToDestination();
//                                Log.d(TAG, "on scroll>>>>>>>>>>>>>>>>>滑动<<<<<<<<<<<<<<>>>");
                            }

                            return true;
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                            return false;
                    }
            });
            
    }
    //每一个屏的边界值
    //0----[getWidth()-20]----[2*getWidth()-20]-----[3*getWidth()-40]
    
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                    int bottom) {
            /**
             * 设置布局，将子视图顺序横屏排列
             */
            super.onLayout(changed, left, top, right, bottom);
            int move=getWidth()-menuWidth;
            for (int i = 0; i < getChildCount(); i++) 
            {
                    View child = getChildAt(i);
//                    child.setVisibility(View.VISIBLE);
                    //移动一定的距离
                    child.layout(child.getLeft()+move,child.getTop(),child.getRight()+move,child.getBottom());
            }
    }

    @Override
    public void computeScroll() {
            if (scroller.computeScrollOffset()) {
//                    Log.d(TAG, ">>>>>>>>>>computeScroll>>>>>"+scroller.getCurrX());
                    scrollTo(scroller.getCurrX(), 0);
                    postInvalidate();
            }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
            
            float x2s=getScrollX()+event.getX();
            
            if(x2s<getWidth()-menuWidth || x2s>2*getWidth()-menuWidth)
            {//动作在区域外面
                if(!fling)//没有在滑动
                {
//                    Log.d(TAG, "on scroll>>>>>>>>>>>>>>>>>动作在区域外面 没有在滑动<<<<<<<<<<<<<<>>>");
                    return false;
                }
                else if(MotionEvent.ACTION_UP!=event.getAction())
                {//否则如果也不是抬起手势，则强制模拟抬起
                    snapToDestination();
                    fling = false;
//                    Log.d(TAG, "on scroll>>>>>>>>>>>>>>>>>动作在区域外面 在滑动 也不是抬起手势<<<<<<<<<<<<<<>>>");
                    return false;
                }
//                Log.e(TAG, "on scroll>>>>>>>>>>>>>>>>>动作在区域外面 在滑动 是抬起手势<<<<<<<<<<<<<<>>>");
            }
            
            gestureDetector.onTouchEvent(event);

            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    break;
            case MotionEvent.ACTION_MOVE:
                    break;
            case MotionEvent.ACTION_UP:
//                    Log.d(TAG, ">>ACTION_UP:>>>>>>>> MotionEvent.ACTION_UP>>>>>");
//                    if (!fling) 
                    {
                            snapToDestination();
                    }
                    fling = false;
                    break;
            default:
                    break;
            }
            return true;
    }

    /**
     * 切换到指定屏
     * 
     * @param whichScreen
     */
    public void scrollToScreen(int whichScreen) {
            if (getFocusedChild() != null && whichScreen != currentScreenIndex
                            && getFocusedChild() == getChildAt(currentScreenIndex)) {
                    getFocusedChild().clearFocus();
            }
            int delta = 0;
            
            if(whichScreen==0)
                delta= - getScrollX();
            else if(whichScreen==1)
                delta= getWidth()-menuWidth- getScrollX();
            else if(whichScreen==2)
                delta= 2*(getWidth()-menuWidth)- getScrollX();
            else
                return;
//                delta = whichScreen * getWidth() - getScrollX();
            
            scroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
            invalidate();

            currentScreenIndex = whichScreen;
    }

    /**
     * 根据当前x坐标位置确定切换到第几屏
     */
    private void snapToDestination() {
        
        if(getScrollX()<(getWidth()-menuWidth)/2)
            scrollToScreen(0);
        else if(getScrollX()<(getWidth()-menuWidth+getWidth()/2))
            scrollToScreen(1);
        else
            scrollToScreen(2);
    }

    public interface OnScrollSideChangedListener
    {
        public void onScrollSideChanged(View v,boolean leftSide);
    }
}
