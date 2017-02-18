package com.randian.win.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by li.lli on 2015/6/7.
 */
public class BaseActivity extends AppCompatActivity {

    protected RequestQueue mQueue;
    protected ActionBar mActionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueue = RanDianApplication.getApp().getVolleyQueue();
        initActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);//you meng
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void addRequest(Request request) {
        mQueue.add(request);
    }

    protected RequestQueue getRequestQueue() {
        return mQueue;
    }

    @Override
    public void onDestroy() {
        getRequestQueue().cancelAll(this);
        super.onDestroy();
    }

    protected void initActionBar() {
        mActionbar = getSupportActionBar();
        if (mActionbar == null) {
            return;
        }
        mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionbar.setDisplayShowCustomEnabled(true);
        mActionbar.setCustomView(R.layout.actionbar);
        mActionbar.getCustomView().findViewById(R.id.iv_tab_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
