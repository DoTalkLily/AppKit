package com.randian.win.ui.coupon;

import android.content.Intent;
import android.os.Bundle;
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
import com.randian.win.ui.order.OrderConfirmActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;

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
public class CouponListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @InjectView(R.id.coupon_list)
    ListView mCouponListView;
    @InjectView(R.id.no_coupon_tips)
    TextView mNoCouponTips;
    @InjectView(R.id.coupon_state)
    TextView mCouponState;
    @InjectView(R.id.use_coupon)
    TextView mUseCouponBtn;

    private View mCurrentBtn;
    private Coupon mCurrentCoupon;
    private List<Coupon> mCouponList;
    private CouponListAdapter mAdapter;
    private final String TAG = CouponListActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_list);
        ButterKnife.inject(this);
        initView();
//        mock();
        startGetRemoteCouponListTask();
    }

    private void initView() {
        mAdapter = new CouponListAdapter(CouponListActivity.this, new ArrayList<Coupon>());
        mCouponListView.setAdapter(mAdapter);
        mCouponListView.setOnItemClickListener(this);
        mUseCouponBtn.setOnClickListener(this);
    }

    private void startGetRemoteCouponListTask() {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result.contains(Consts.ERROR_CODE)) {
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result, type);
                    Toaster.showShort(CouponListActivity.this, error.getError());
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
                ErrorHandler.handleException(CouponListActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.getCouponList(listener, errorListener);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (null == view) {
            return;
        }

        Intent intent = new Intent();
        if(mCurrentCoupon == null){
            setResult(Consts.NO_COUPON);
        }else{
            intent.putExtra(Consts.EXTRA_PARAM_0, mCurrentCoupon);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position >= 0 && position < mCouponList.size()) {
            Coupon coupon = mAdapter.getItem(position);
            if (coupon == null) {
                LogUtils.e(TAG, "Coupon is null in position:" + position);
                return;
            }

            if (mCurrentCoupon != null) {
                //选中当前被选中的 则反选
                mCurrentBtn.setAlpha(0.5f);
                if (mCurrentCoupon.getId() == coupon.getId()) {
                    mCurrentBtn = null;
                    mCurrentCoupon = null;
                    mCouponState.setText(getString(R.string.coupon_choose));
                    mUseCouponBtn.setText(getString(R.string.coupon_no_use));
                    return;
                }
            }

            mCurrentCoupon = coupon;
            mCurrentBtn = view.findViewById(R.id.coupon_btn);
            mCurrentBtn.setAlpha(1);
            mUseCouponBtn.setText(getString(R.string.use_coupon));
            mCouponState.setText(getString(R.string.already_chosen_coupon) + coupon.getCoupon_fee() + getString(R.string.yuan));
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
        myFile = getResources().openRawResource(R.raw.order_coupon_list);//cet4为一个TXT文件
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
