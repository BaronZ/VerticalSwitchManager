package com.zzb.scrollswitch.scroll;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zzb.scrollswitch.R;

import java.util.List;

/**
 * Created by ZZB on 2017/8/8.
 */

public class ViewSwitchAdapterImpl extends ViewSwitchAdapter<String> {

    @Override
    public void onBindView(View view, int pos) {
        // TODO: 2017/8/8  换成holder一样，不用每次bindView都find
        TextView tv = view.findViewById(R.id.tv_content);
        tv.setText(getItemData(pos));
    }

    @Override
    public View createView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom, parent, false);
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
