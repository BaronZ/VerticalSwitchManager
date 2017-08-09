package com.zzb.scrollswitch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zzb.scrollswitch.scroll.VerticalSwitchManager;
import com.zzb.scrollswitch.scroll.ViewSwitchAdapterImpl;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewGroup mLayoutRoot;
    private View mCenterView;
    //    private SmallVideoSwitchHelper mSwitchHelper;
    private VerticalSwitchManager<String> mSwitchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayoutRoot = (ViewGroup) findViewById(R.id.layout_root);
        mCenterView = findViewById(R.id.view_center);
//        mSwitchHelper = new SmallVideoSwitchHelper(this, mLayoutRoot, mCenterView);

        mSwitchHelper = new VerticalSwitchManager<>(mLayoutRoot, new ViewSwitchAdapterImpl());
        List<String> data = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
        mSwitchHelper.setData(data, "a");
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mSwitchHelper.onDispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
