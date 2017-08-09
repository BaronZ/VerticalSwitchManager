package com.zzb.scrollswitch.scroll;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动，切换，切换成功->显示cover->恢复其他两位置，开始播放，播放成功，隐藏view
 * Created by ZZB on 2017/8/7.
 */

public class VerticalSwitchManager<T> {
    private static final String TAG = "VerticalSwitchManager";
    private static final int TRANSLATE_DURATION_MS = 3000;
    private final int TRIGGER_DISTANCE;
    //position(0, -H), (0,0), (0, H)
    private static final int POS_BOTTOM = 0;
    private static final int POS_CENTER = 1;
    private static final int POS_TOP = 2;

    private final float POSITIONS[] = new float[3];
    private ViewSwitchAdapter<T> mAdapter;
    private List<ScrollItem> mItems = new ArrayList<>();
    private GestureDetectorCompat mGestureDetector;
    private ViewGroup mRootLayout;
    private Context mContext;
    private int mScreenHeight;
    private float mStartX, mStartY;
    private boolean mIsInAnimation;
    private int mCenterDataPosition;

    public VerticalSwitchManager(ViewGroup rootLayout, ViewSwitchAdapter<T> adapter) {
        mContext = rootLayout.getContext();
        mAdapter = adapter;
        mRootLayout = rootLayout;
        mScreenHeight = getScreenHeight();
        TRIGGER_DISTANCE = mScreenHeight / 5;
        POSITIONS[0] = -mScreenHeight;
        POSITIONS[1] = 0;
        POSITIONS[2] = mScreenHeight;
        initDetector();
        initViews();
    }

    private void initViews() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenHeight);
        for (int i = 0; i < 3; i++) {
            View view = mAdapter.createView(mRootLayout);
            mRootLayout.addView(view, params);
            mItems.add(new ScrollItem(view, i));
        }
    }

    private void initDetector() {
        mGestureDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "dY:" + distanceX);
                VerticalSwitchManager.this.onScroll(distanceY);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    public void setData(List<T> data, T centerData) {
        mCenterDataPosition = data.indexOf(centerData);
        mAdapter.setData(data);
        updateViewsPositionAndData(true);
    }


    private void onAnimationEnd() {
        Log.d(TAG, "onAnimationEnd");
        mIsInAnimation = false;
        updateViewsPositionAndData(false);
    }

    /**
     * 遍历各个view，如果位置不对，要更新位置，还有设置view的数据
     */
    private void updateViewsPositionAndData(boolean forceUpdateData) {
        for (ScrollItem item : mItems) {
            float targetY = getViewYByIndex(item.viewIndex);
            updateDataPosition(item, targetY);
            View view = item.mView;
            float y = view.getY();

            item.viewY = targetY;
            if (y != targetY) {
                Log.d(TAG, "updateView, diff position, update position and data, y:" + targetY);
                view.setTranslationY(targetY);
                bindView(item, view);
            } else if (forceUpdateData) { //位置一样也更新数据
                Log.d(TAG, "updateView, same position, forceUpdateData");
                bindView(item, view);
            } else {
                Log.d(TAG, "updateView, same position, skip");
            }
            Log.d(TAG, "updateView, data:" + item.toString());
        }
        //callback to make video view move to center
    }

    private void bindView(ScrollItem item, View view) {
        int itemPos = item.dataPosition;
        if (itemPos >= 0 && itemPos < mAdapter.getCount()) {
            mAdapter.onBindView(view, item.dataPosition);
        } else {
            Log.d(TAG, "skip bind view, invalid position:" + itemPos);
        }
    }

    private void updateDataPosition(ScrollItem item, float targetY) {
        Log.d(TAG, "updateDataPosition, currentPos:" + mCenterDataPosition);
        if (targetY == POSITIONS[POS_BOTTOM]) {
            item.dataPosition = mCenterDataPosition - 1;
        } else if (targetY == POSITIONS[POS_CENTER]) {
            item.dataPosition = mCenterDataPosition;
        } else if (targetY == POSITIONS[POS_TOP]) {
            item.dataPosition = mCenterDataPosition + 1;
        }
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
        for (ScrollItem item : mItems) {
            View view = item.mView;
            float tY = (-distanceY + view.getY());
            view.setTranslationY(tY);
        }
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
            restoreLayout();
        } else if (distanceY > 0) {
            moveToPre();
        } else {
            moveToNext();
        }
    }

    private void moveToNext() {
        mCenterDataPosition++;
        Log.d(TAG, "moveToNext");
        List<Animator> animators = new ArrayList<>();
        for (ScrollItem item : mItems) {
            item.viewY -= getScreenHeight();
            animators.add(translateAnimator(item.mView, item.viewY));
        }
        doTranslateAnimation(animators);
    }

    //遍历view然后移动
    private void moveToPre() {
        Log.d(TAG, "moveToPre");
        mCenterDataPosition--;
        List<Animator> animators = new ArrayList<>();
        for (ScrollItem item : mItems) {
            item.viewY += getScreenHeight();
            animators.add(translateAnimator(item.mView, item.viewY));
        }
        doTranslateAnimation(animators);
    }

    private void restoreLayout() {
        Log.d(TAG, "restoreLayout");
        // TODO: 2017/8/8
    }

    private void doTranslateAnimation(List<Animator> animators) {
        mIsInAnimation = true;
        AnimatorSet set = new AnimatorSet();
        set.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                VerticalSwitchManager.this.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                VerticalSwitchManager.this.onAnimationEnd();
            }
        });
        set.playTogether(animators);
        set.start();
    }

    private Animator translateAnimator(View view, float to) {
        Log.d(TAG, "translateAnimator, to:" + to);
        return ObjectAnimator
                .ofFloat(view, "translationY", to)
                .setDuration(TRANSLATE_DURATION_MS);
    }

    private float getViewYByIndex(int index) {
        while (index < 0) {
            index += 3;
        }
        return POSITIONS[Math.abs(index % 3)];
    }

    private boolean canScroll(float distanceY) {
        //adapter.getCount <= 1, return false
        //再判断位置，第几个，第一个不能上，最后一个不能下
        //还要再结合当前view的位置判断，比如当前view已经移动了，还是能上移的，直到恢复原位为止
        return !mIsInAnimation;
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
}
