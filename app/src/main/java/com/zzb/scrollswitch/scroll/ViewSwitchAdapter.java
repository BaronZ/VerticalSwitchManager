package com.zzb.scrollswitch.scroll;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ZZB on 2017/8/7.
 */

public abstract class ViewSwitchAdapter<DATA, VH extends ViewSwitchAdapter.ViewHolder> {
    protected List<DATA> mData;


    public abstract void onBindView(VH viewHolder, int pos);

    public abstract VH createViewHolder(ViewGroup parent);

    public abstract DATA getItemData(int pos);

    public abstract void setData(List<DATA> data);

    public int getCount() {
        return mData == null ? 0 : mData.size();
    }


    public static class ViewHolder {
        public final View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }

}
