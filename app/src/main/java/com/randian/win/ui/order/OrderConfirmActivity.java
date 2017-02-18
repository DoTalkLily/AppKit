package com.randian.win.ui.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.Coupon;
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

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-7-5.
 */
public class OrderConfirmActivity extends BaseActivity implements View.OnClickListener {
    //项目信息
    @InjectView(R.id.avatar_image)
    SimpleDraweeView mSportImage;
    @InjectView(R.id.sport_title)
    TextView mSportTitle;
    @InjectView(R.id.coach_name)
    TextView mCoachName;
    @InjectView(R.id.sport_date)
    TextView mSportDate;
    @InjectView(R.id.cost)
    TextView mCost;

    //联系人信息
    @InjectView(R.id.contact)
    TextView mContact;
    @InjectView(R.id.mobile)
    TextView mMobile;
    @InjectView(R.id.address)
    TextView mAddress;
    @InjectView(R.id.mark)
    TextView mMark;

    @InjectView(R.id.sport_order_btn)
    TextView mOrderBtn;
    @InjectView(R.id.use_coupon)
    View mUseCouponBtn;
    @InjectView(R.id.coupon_state)
    TextView mCouponState;

    private Order mOrder;
    private final String TAG = OrderConfirmActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this.getApplication());
        setContentView(R.layout.order3);
        ButterKnife.inject(this);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mOrder = (Order) intent.getSerializableExtra(Consts.EXTRA_PARAM_0);

        if (mOrder == null) {
            Toaster.showShort(this, R.string.order_not_found);
            return;
        }

        mMark.setText(mOrder.getMark());
        mContact.setText(mOrder.getConsignee_name());
        mMobile.setText(mOrder.getConsignee_mobile() + "");
        mAddress.setText(mOrder.getConsignee_street() + " " + mOrder.getConsignee_address());

        mSportTitle.setText(mOrder.getSport_name());
        mCoachName.setText(getString(R.string.name) + mOrder.getCoach_name());
        mCost.setText(getString(R.string.cash_unit) + mOrder.getOrder_pay_fee() + getString(R.string.yuan));
        mSportDate.setText(getString(R.string.duration) + mOrder.getSport_detail_time());
        mSportImage.setImageURI(Uri.parse(mOrder.getSport_img_url()));
    }

    private void initView() {
        mOrderBtn.setOnClickListener(this);
        mUseCouponBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (null == view) {
            return;
        }

        if(view.getId() == R.id.use_coupon){
            UIUtils.startChooseCouponActivity(OrderConfirmActivity.this,this);
        }else{
            startCreateOrder();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            Coupon coupon = (Coupon)bundle.get(Consts.EXTRA_PARAM_0);
            mOrder.setCoupon_id(coupon.getId());
            mOrder.setOrder_coupon_fee(coupon.getCoupon_fee());
            if(mOrder.getOrder_pay_fee() <= coupon.getCoupon_fee()){
                mOrder.setOrder_pay_fee(0.01f);
            }else{
                mOrder.setOrder_pay_fee(mOrder.getOrder_pay_fee()-coupon.getCoupon_fee());
            }
            mCouponState.setText("使用"+coupon.getCoupon_fee()+"元券");
            mCost.setText("￥" + mOrder.getOrder_pay_fee() + getString(R.string.yuan));
        }
    }

    private void startCreateOrder() {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result.contains(Consts.ERROR_CODE)) {
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result, type);
                    Toaster.showShort(OrderConfirmActivity.this, error.getError());
                    return;
                }
                try {
                    JSONObject obj = new JSONObject(result);
                    String orderNo = obj.getString("order_no");

                    if(TextUtils.isEmpty(orderNo)){
                        Toaster.showShort(OrderConfirmActivity.this,Consts.SERVER_ERROR);
                        return;
                    }

                    UIUtils.startOrderPayActivity(OrderConfirmActivity.this, orderNo);
                }catch (Exception e){
                    LogUtils.e("error:",e);
                    Toaster.showShort(OrderConfirmActivity.this,Consts.SERVER_ERROR);
                    return;
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(OrderConfirmActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.generateOrder(listener, errorListener,mOrder);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
