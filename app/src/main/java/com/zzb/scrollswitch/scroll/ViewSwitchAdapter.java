package com.zzb.scrollswitch.scroll;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ZZB on 2017/8/7.
 */

public abstract class ViewSwitchAdapter<T> {
    protected List<T> mData;


    public abstract void onBindView(View view, int pos);

    public abstract View createView(ViewGroup parent);

    public abstract T getItemData(int pos);

    public abstract void setData(List<T> data);

    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

}
