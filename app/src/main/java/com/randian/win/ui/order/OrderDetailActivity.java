package com.randian.win.ui.order;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.Comment;
import com.randian.win.model.Consumer;
import com.randian.win.model.ErrorCode;
import com.randian.win.model.Order;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;
import com.randian.win.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-7-5.
 */
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener{
    @InjectView(R.id.order_num)
    TextView mOrderNum;
    @InjectView(R.id.sport_title)
    TextView mSportTitle;
    @InjectView(R.id.sport_duration)
    TextView mSportDuration;
    @InjectView(R.id.order_pay_fee)
    TextView mOrderPayFee;
    @InjectView(R.id.order_off_fee)
    TextView mOrderOffFee;
    @InjectView(R.id.order_coupon)
    TextView mOrderCoupon;
    @InjectView(R.id.order_real_pay)
    TextView mOrderRealPay;
    @InjectView(R.id.coach_name)
    TextView mCoachName;
    @InjectView(R.id.sport_start_time)
    TextView mSportStartTime;
    @InjectView(R.id.consignee_name)
    TextView mConsigneeName;
    @InjectView(R.id.consignee_mobile)
    TextView mConsigneeMobile;
    @InjectView(R.id.consignee_address)
    TextView mConsigneeAddress;
    @InjectView(R.id.mark)
    TextView mMark;
    @InjectView(R.id.service_scope)
    TextView mServiceScope;
    @InjectView(R.id.not_payed)
    TextView mNotPaid;
    @InjectView(R.id.btn_order_again)
    TextView mOrderAgainBtn;
    @InjectView(R.id.pay_btn)
    Button payBtn;
    @InjectView(R.id.comment_container)
    View mCommentContainer;
    @InjectView(R.id.comment_content)
    TextView mCommentContent;
    @InjectView(R.id.comment_star)
    ViewGroup mCommentStar;

    private Order mOrder;
    private String mOrderNo;
    private final String TAG = OrderDetailActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);
        ButterKnife.inject(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        mOrderNo = intent.getStringExtra(Consts.EXTRA_PARAM_0);

        if (TextUtils.isEmpty(mOrderNo)) {
            Toaster.showShort(this, R.string.order_not_found);
            finish();
        }
        startGetOrderDetailTask();
    }

    private void initView() {
        if (mOrder == null) {
            Toaster.showShort(this, R.string.order_not_found);
            return;
        }

        Resources res = getResources();
        mOrderNum.setText(getString(R.string.order_no) + mOrder.getOrder_no());
        mSportTitle.setText(getString(R.string.sport_name) + mOrder.getSport_name());
        mSportDuration.setText(getString(R.string.sport_duration) + mOrder.getSport_duration() + getString(R.string.minute));
        mOrderPayFee.setText(getString(R.string.order_pay_fee) + mOrder.getOrder_pay_fee() + getString(R.string.yuan));
        mOrderRealPay.setText(getString(R.string.order_real_pay) + mOrder.getOrder_cash_fee() + getString(R.string.yuan));
        mCoachName.setText(getString(R.string.coach_name) + mOrder.getCoach_name());
        mSportStartTime.setText(getString(R.string.sport_start_time) + mOrder.getSport_start_time());
        mConsigneeName.setText(getString(R.string.consignee_name) + mOrder.getConsignee_name());
        mConsigneeMobile.setText(getString(R.string.consignee_mobile) + mOrder.getConsignee_mobile());
        mConsigneeAddress.setText(getString(R.string.consignee_address) + mOrder.getConsignee_address() + " " + mOrder.getConsignee_street());
        if (!TextUtils.isEmpty(mOrder.getMark())) {
            mMark.setText(getString(R.string.mark) + mOrder.getMark());
        }
        if(mOrder.getOrder_coupon_fee() != 0){
            mOrderCoupon.setVisibility(View.VISIBLE);
            mOrderCoupon.setText(getString(R.string.order_coupon_fee)+"-"+mOrder.getOrder_coupon_fee()+getString(R.string.yuan));
        }
        if(mOrder.getOrder_off_fee() != 0){
            mOrderOffFee.setVisibility(View.VISIBLE);
            mOrderOffFee.setText(getString(R.string.order_off_fee)+"-"+mOrder.getOrder_off_fee()+getString(R.string.yuan));
        }
        SpannableString spannableString = new SpannableString(getString(R.string.service_distance));
        spannableString.setSpan(new ForegroundColorSpan(res.getColor(R.color.orange)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mServiceScope.setText(spannableString);

        if (getString(R.string.already_created).equals(mOrder.getState())
                || getString(R.string.wait_to_pay).equals(mOrder.getState())) {
            mNotPaid.setText(getString(R.string.not_paied) + mOrder.getOrder_cash_fee() + getString(R.string.yuan));
            mNotPaid.setVisibility(View.VISIBLE);
            payBtn.setVisibility(View.VISIBLE);
            mOrderAgainBtn.setVisibility(View.GONE);
        }else{
            mOrderAgainBtn.setOnClickListener(this);
        }

        //评论区域
        if(mOrder.getComment_info() != null && mOrder.getComment_info().getId() != 0) {
            mCommentContainer.setVisibility(View.VISIBLE);
            mCommentContent.setText(mOrder.getComment_info().getContent());
            Utils.commentStar(mOrder.getComment_info().getStars(), mCommentStar, getApplicationContext());

        }
    }

    private void startGetOrderDetailTask() {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(OrderDetailActivity.this, error.getError());
                    return;
                }
//                result = readFile();

                Order order = null;
                try {
                    Type type = new TypeToken<Order>() {
                    }.getType();
                    order = RanDianApplication.getGson().fromJson(result.trim(), type);
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }

                if (order == null) {
                    Toaster.showShort(OrderDetailActivity.this, getString(R.string.order_not_found));
                    return;
                }
                mOrder = order;
                initView();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(OrderDetailActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.getOrderDetail(listener, errorListener, mOrderNo);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        if (null == view || mOrder == null) {
            return;
        }
        if (view.getId() == R.id.btn_order_again) {
            UIUtils.startOrder1Activity(OrderDetailActivity.this, mOrder, Consts.FROM_COACH);
        }
    }

    private String readFile() {
        InputStream myFile;
        myFile = getResources().openRawResource(R.raw.order_detail);//cet4为一个TXT文件
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
