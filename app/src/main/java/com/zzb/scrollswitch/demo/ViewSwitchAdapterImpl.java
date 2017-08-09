package com.zzb.scrollswitch.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzb.scrollswitch.R;
import com.zzb.scrollswitch.scroll.ViewSwitchAdapter;

import java.util.List;
import java.util.Random;

/**
 * Created by ZZB on 2017/8/8.
 */

public class ViewSwitchAdapterImpl extends ViewSwitchAdapter<String, ViewSwitchAdapter.ViewHolder> {

    @Override
    public void onBindView(ViewHolder viewHolder, int pos) {
        // TODO: 2017/8/8  换成holder一样，不用每次bindView都find
        TextView tv = viewHolder.itemView.findViewById(R.id.tv_content);
        tv.setText(getItemData(pos));
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom, parent, false);
        view.setBackgroundColor(new Random().nextInt());
        return new ViewHolder(view);
    }

    @Override
    public String getItemData(int pos) {
        return mData.get(pos);
    }

    @Override
    public void setData(List<String> data) {
        mData = data;
    }


}
