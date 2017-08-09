package com.zzb.scrollswitch.scroll;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 进来有可能是第一个，也有可能是中间，也有可能是最后一个
 * Created by ZZB on 2017/8/7.
 */

public class SmallVideoSwitchHelper {

    private static final String TAG = "SmallVideoSwitchHelper";
    private static final int TRANSLATE_DURATION_MS = 10000;
    private final int TRIGGER_DISTANCE;
    private float mStartX, mStartY;
    private View mCenterView, mPreView, mNextView;
    private ViewGroup mRootLayout;
    private GestureDetectorCompat mGestureDetector;
    private Context mContext;
    private int mScreenHeight;
    private ViewSwitchAdapter mAdapter;

    public SmallVideoSwitchHelper(Context context, ViewGroup rootView, View centerView) {
        mContext = context;
        mScreenHeight = getScreenHeight();
//        mAdapter = new ViewSwitchAdapterImpl();
        TRIGGER_DISTANCE = mScreenHeight / 5;
        initDetector();
        initViews(rootView, centerView);
    }

    private void initDetector() {
        mGestureDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "dY:" + distanceX);
                SmallVideoSwitchHelper.this.onScroll(distanceY);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    private void initViews(ViewGroup rootView, View centerView) {
        mRootLayout = rootView;
        mCenterView = centerView;
        initPreAndNext();
    }

    private void initPreAndNext() {
        mPreView = new ImageView(mContext);
        mNextView = new ImageView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenHeight);
        mRootLayout.addView(mPreView, params);
        mRootLayout.addView(mNextView, params);
        mPreView.setBackgroundColor(Color.BLUE);
        mNextView.setBackgroundColor(Color.RED);
        mPreView.setTranslationY(-mScreenHeight);
        mNextView.setTranslationY(mScreenHeight);
    }

    private int getScreenHeight() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        return dm.heightPixels - getStatusBarHeight();
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void onActionDown(MotionEvent ev) {
        mStartX = ev.getX();
        mStartY = ev.getY();
        Log.d(TAG, "onActionDown, x: " + mStartX + " y: " + mStartY);
    }


    private void onActionUp(MotionEvent ev) {
        Log.d(TAG, "onActionUp, x: " + ev.getX() + " y: " + ev.getY());
        float distanceY = ev.getY() - mStartY;
        if (Math.abs(distanceY) < TRIGGER_DISTANCE) {
            Log.d(TAG, "restore");
            restoreLayout();
        } else if (distanceY > 0) {
            Log.d(TAG, "move up");
            moveToPre();
        } else {
            Log.d(TAG, "move down");
            moveToNext();
        }
    }

    private void restoreLayout() {
        mCenterView.setTranslationY(0);
        mPreView.setTranslationY(-mScreenHeight);
        mNextView.setTranslationY(mScreenHeight);
    }

    private void moveToNext() {
        translateAnimator(mCenterView, -mScreenHeight);
        translateAnimator(mPreView, -2 * mScreenHeight);
        translateAnimator(mNextView, 0);
    }

    private void moveToPre() {
        translateAnimator(mCenterView, mScreenHeight);
        translateAnimator(mPreView, 0);
        translateAnimator(mNextView, 2 * mScreenHeight);
    }

    private Animator translateAnimator(View view, int to) {
        return ObjectAnimator
                .ofFloat(view, "translationY", to)
                .setDuration(TRANSLATE_DURATION_MS);
    }

    private void onAnimationEnd(View currentCenterView) {
        //restore other two position
        //callback to play video
        //on video ready, hide current view and restore position
    }

    private void hideCoverView() {

    }

    public void onDispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(ev);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(ev);
                break;
        }
    }

    private void onScroll(float distanceY) {
        if (!canScroll(distanceY)) {
            return;
        }
        Log.d(TAG, "onScroll, dy:" + distanceY);
        translateView(mNextView, distanceY);
        translateView(mPreView, distanceY);
        translateView(mCenterView, distanceY);
    }

    private void translateView(View view, float distanceY) {
        float tY = (-distanceY + view.getY());
        view.setTranslationY(tY);
    }

    private boolean canScroll(float distanceY) {
        // TODO: 2017/8/8
        //adapter.getCount <= 1, return false
        //再判断位置，第几个，第一个不能上，最后一个不能下

        return true;
    }
}
