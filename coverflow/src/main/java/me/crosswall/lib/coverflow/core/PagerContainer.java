package me.crosswall.lib.coverflow.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * PagerContainer: A layout that displays a ViewPager with its children that are outside
 * the typical pager bounds.
 *
 * @see(<a href = "https://gist.github.com/devunwired/8cbe094bb7a783e37ad1"></>)
 */
public class PagerContainer extends FrameLayout implements ViewPager.OnPageChangeListener, GestureDetector.OnGestureListener {

    private ViewPager mPager;
    boolean mNeedsRedraw = false;
    private GestureDetectorCompat gestureDetectorCompat;

    public PagerContainer(Context context) {
        super(context);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //Disable clipping of children so non-selected pages are visible
        setClipChildren(false);

        //Child clipping doesn't work with hardware acceleration in Android 3.x/4.x
        //You need to set this value here if using hardware acceleration in an
        // application targeted at these releases.
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        gestureDetectorCompat = new GestureDetectorCompat(getContext(), this);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFinishInflate() {
        try {
            mPager = (ViewPager) getChildAt(0);
            mPager.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetectorCompat.onTouchEvent(event);
                }
            });
            mPager.addOnPageChangeListener(this);
        } catch (Exception e) {
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }

    public ViewPager getViewPager() {
        return mPager;
    }

    private Point mCenter = new Point();
    private Point mInitialTouch = new Point();
    private Point bindingTouch = new Point();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("@@@", "w:" + w + "\n" + "h: " + h);
        mCenter.x = w / 2;
        mCenter.y = h / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //We capture any touches not already handled by the ViewPager
        // to implement scrolling from a touch outside the pager bounds.
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouch.x = (int) ev.getX();
                mInitialTouch.y = (int) ev.getY();
            default:
                float deltaX = mCenter.x - mInitialTouch.x;
                float deltaY = mCenter.y - mInitialTouch.y;
                //  Log.d("@@@@","deltaX:"+ deltaX + "," + "deltaY" + deltaY);
                ev.offsetLocation(deltaX, deltaY);
                break;
        }

        return gestureDetectorCompat.onTouchEvent(ev) || mPager.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Force the container to redraw on scrolling.
        //Without this the outer pages render initially and then stay static
        if (mNeedsRedraw) invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        Log.d("@@@", "onSingleTapUp() called with: " + "e = [" + e + "]");

        Rect rect = new Rect();
        mPager.getGlobalVisibleRect(rect);
        int[] location = new int[2];
        ((ViewGroup) PagerContainer.this.getParent()).getLocationOnScreen(location);
        rect.offset(-location[0], -location[1]);

        if (e.getRawX() >= rect.right) {
            Log.d("@@@", "点击右侧： index " + (mPager.getCurrentItem() + 1));
            if (mOnClickItemListener != null) {
                mOnClickItemListener.onItemClick(mPager.getCurrentItem() + 1);
            }

        } else if (e.getRawX() <= rect.left) {
            Log.d("@@@", "点击左侧： index " + (mPager.getCurrentItem() - 1));
            if (mOnClickItemListener != null) {
                mOnClickItemListener.onItemClick(mPager.getCurrentItem() - 1);
            }
        } else {
            Log.d("@@@", "点击中间： index " + (mPager.getCurrentItem()));
            if (mOnClickItemListener != null) {
                mOnClickItemListener.onItemClick(mPager.getCurrentItem());
            }
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnClickItemListener {

        void onItemClick(int index);
    }

    private OnClickItemListener mOnClickItemListener;

    public void setOnClickItemListener(OnClickItemListener mOnClickItemListener) {
        this.mOnClickItemListener = mOnClickItemListener;
    }
}
