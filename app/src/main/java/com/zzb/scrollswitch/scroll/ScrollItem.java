package com.zzb.scrollswitch.scroll;

import android.view.View;

/**
 * Created by ZZB on 2017/8/7.
 */

public class ScrollItem<T> {

    View mView;
    T data;
    int viewIndex;
    int dataPosition;
    float viewY;

    public ScrollItem(View view, int viewIndex) {
        mView = view;
        this.viewIndex = viewIndex;
    }

    @Override
    public String toString() {
        return "ScrollItem{" +
                "mView=" + mView.getY() +
                ", data=" + data +
                ", viewIndex=" + viewIndex +
                ", dataPosition=" + dataPosition +
                '}';
    }
}
