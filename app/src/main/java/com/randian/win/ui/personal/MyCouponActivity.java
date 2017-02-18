package com.randian.win.ui.personal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.Coupon;
import com.randian.win.model.ErrorCode;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-8-8.
 */
public class MyCouponActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @InjectView(R.id.coupon_list)
    ListView mCouponListView;
    @InjectView(R.id.no_coupon_tips)
    TextView mNoCouponTips;

    private List<Coupon> mCouponList;
    private MyCouponListAdapter mAdapter;
    private final String TAG = MyCouponActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_coupon);
        ButterKnife.inject(this);
        initView();
//        mock();
        startGetRemoteCouponListTask();
    }

    private void initView() {
        mAdapter = new MyCouponListAdapter(MyCouponActivity.this, new ArrayList<Coupon>());
        mCouponListView.setAdapter(mAdapter);
        mCouponListView.setOnItemClickListener(this);
    }

    private void startGetRemoteCouponListTask() {
        LogUtils.d(TAG, "start to sync sports list from remote");
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result.contains(Consts.ERROR_CODE)) {
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result, type);
                    Toaster.showShort(MyCouponActivity.this, error.getError());
                    return;
                }

                List<Coupon> couponList = null;

                try {
                    Type type = new TypeToken<List<Coupon>>() {
                    }.getType();
                    couponList = RanDianApplication.getGson().fromJson(result.trim(), type);
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }

                //没有则提示
                if (couponList == null || couponList.isEmpty()) {
                    mNoCouponTips.setVisibility(View.VISIBLE);
                    return;
                }
                mCouponList = couponList;
                mAdapter.setData(couponList);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(MyCouponActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.getMyCouponList(listener, errorListener);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position >= 0 && position < mCouponList.size()) {
            Coupon coupon = mAdapter.getItem(position);
            if (coupon != null && TextUtils.equals(coupon.getCoupon_state_text(), getResources().getString(R.string.coupon_available))) {
                UIUtils.startHomeActivity(this);
                finish();
            }
        }
    }

    private void mock() {
        String content = readFile();
        List<Coupon> couponList = null;

        try {
            Type type = new TypeToken<List<Coupon>>() {
            }.getType();
            couponList = RanDianApplication.getGson().fromJson(content.trim(), type);
        } catch (Exception e) {
            LogUtils.d(TAG, e.toString());
        }

        //没有则提示
        if (couponList == null || couponList.isEmpty()) {
            mNoCouponTips.setVisibility(View.VISIBLE);
            return;
        }
        mCouponList = couponList;
        mAdapter.setData(couponList);
    }

    private String readFile() {
        InputStream myFile;
        myFile = getResources().openRawResource(R.raw.my_coupon);//cet4为一个TXT文件
        BufferedReader br;

        String tmp;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(myFile, "utf-8"));//注意编码

            while ((tmp = br.readLine()) != null) {
                sb.append(tmp);
            }
            br.close();
            myFile.close();
        } catch (IOException e) {

        }
        return sb.toString();
    }
}
