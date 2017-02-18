package com.randian.win.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.randian.win.RanDianApplication;

/**
 * Created by Lily
 */
public class BaseFragment extends Fragment {

    protected RequestQueue mQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueue = RanDianApplication.getApp().getVolleyQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
}
