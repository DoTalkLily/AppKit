package com.randian.win.ui.order;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.ErrorCode;
import com.randian.win.model.Order;
import com.randian.win.model.PayResult;
import com.randian.win.model.WxInfo;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.HomeActivity;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-7-5.
 */
public class OrderPayActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.goods)
    TextView mGoods;
    @InjectView(R.id.mobile)
    TextView mMobile;
    @InjectView(R.id.address)
    TextView mAddress;
    @InjectView(R.id.sport_order_btn)
    TextView mOrderBtn;
    @InjectView(R.id.order_pay_fee)
    TextView mOrderPayFee;
    @InjectView(R.id.order_paid_fee)
    TextView mOrderAlreadyPaid;//成功支付后显示支付金额
    @InjectView(R.id.pay_status)
    TextView mOrderState;//订单状态
    @InjectView(R.id.coach_contact_time)
    TextView mCoachContactTime;//支付成功显示教练联系时间

    private Order mOrder;
    private final String TAG = OrderPayActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_pay);
        ButterKnife.inject(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String orderNo = (String) intent.getSerializableExtra(Consts.EXTRA_PARAM_0);

        if (orderNo == null) {
            Toaster.showShort(this, R.string.order_not_found);
            finish();
            return;
        }

        startGetOrderDetailTask(orderNo);
    }

    private void initView() {
        mMobile.setText(String.valueOf(mOrder.getConsignee_mobile()));
        mGoods.setText(mOrder.getSport_name() + " x " + mOrder.getSport_order_num());
        mAddress.setText(mOrder.getConsignee_street() + " " + mOrder.getConsignee_address());
        mOrderPayFee.setText("￥" + mOrder.getOrder_pay_fee() + getResources().getString(R.string.yuan));
        mOrderBtn.setOnClickListener(this);
        registerBoradcastReceiver();//注册广播
    }

    private void startGetOrderDetailTask(String orderNo) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(OrderPayActivity.this, error.getError());
                    return;
                }

                Order order = null;
                try {
                    Type type = new TypeToken<Order>() {
                    }.getType();
                    order = RanDianApplication.getGson().fromJson(result.trim(), type);
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }

                if (order == null) {
                    Toaster.showShort(OrderPayActivity.this, getString(R.string.order_not_found));
                    return;
                }
                mOrder = order;
                initView();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(OrderPayActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.getOrderDetail(listener, errorListener, orderNo);
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
        if (view.getId() == R.id.sport_order_btn) {
            new AlertDialog.Builder(this)
                    .setTitle("请选择支付方式")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(new String[]{"支付宝支付", "微信支付"}, 0, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                startCreateOrderPay(Consts.ALIPAY_PLATFORM);
                            } else {
                                startCreateOrderPay(Consts.WX_PAY_PLATFORM);
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    //注册广播接收者
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Consts.WX_PAY_ACTION)) {//WX处理结果
                int status = intent.getIntExtra("status", -1);//0 成功 其他失败
                processWxPayResult(status);
            } else if (action.equals(Consts.ALIPAY_ACTION)) {
                String result = intent.getStringExtra("result");
                PayResult payResult = new PayResult(result);
                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                String resultStatus = payResult.getResultStatus();

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    paySuccessProcess();
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        payFailProcess(true);
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        payFailProcess(false);
                    }
                }
            }
        }
    };

    private void processWxPayResult(int errCode) {
        if (errCode == 0) {//如果显示支付成功，也调一次后台判断是否成功
            paySuccessProcess();//支付成功处理
        } else {//否则失败
            payFailProcess(false);
        }
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Consts.WX_PAY_ACTION);
        myIntentFilter.addAction(Consts.ALIPAY_ACTION);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    //开始创建微信订单
    private void startCreateOrderPay(final int platform) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result.contains(Consts.ERROR_CODE)) {
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result, type);
                    Toaster.showShort(OrderPayActivity.this, error.getError());
                    return;
                }

                //youmeng 统计支付事件
                HashMap<String,String> map = new HashMap<>();
                map.put("platform",String.valueOf(platform));
                map.put("cash",String.valueOf(mOrder.getOrder_pay_fee()));
                MobclickAgent.onEvent(OrderPayActivity.this, "pay_platform", map);

                try {
                    if (Consts.ALIPAY_PLATFORM == platform) {
                        JSONObject obj = new JSONObject(result);
                        String appRequestParams = obj.getString("app_request_params");

                        if (TextUtils.isEmpty(appRequestParams)) {
                            LogUtils.e(TAG, "app_request_params is null for order_no :" + mOrder.getOrder_no());
                            return;
                        }
                        startAlipay(appRequestParams);
                    } else {
                        WxInfo wxInfo = RanDianApplication.getGson().fromJson(result, new TypeToken<WxInfo>() {
                        }.getType());
                        startWeixinPay(wxInfo);
                    }
                } catch (JSONException e) {
                    LogUtils.e(TAG, e);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(OrderPayActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.createOrderPay(listener, errorListener, mOrder.getOrder_no(), platform);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //开始调用微信支付
    private void startWeixinPay(WxInfo wxInfo) {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        // 将该app注册到微信
        msgApi.registerApp(wxInfo.getAppid());

        PayReq request = new PayReq();
        request.appId = wxInfo.getAppid();
        request.partnerId = wxInfo.getPartnerid();
        request.prepayId = wxInfo.getPrepayid();
        request.packageValue = "Sign=WXPay";
        request.nonceStr = wxInfo.getNoncestr();
        request.timeStamp = wxInfo.getTimestamp();
        request.sign = wxInfo.getSign();
        msgApi.sendReq(request);
    }

    //开始支付宝支付
    private void startAlipay(final String appPayInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(OrderPayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(appPayInfo);
                Intent mIntent = new Intent(Consts.ALIPAY_ACTION);
                mIntent.putExtra("result", result);
                sendBroadcast(mIntent);//发送广播
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //支付失败处理 isCheckingState是支付宝返回的结果有一个结果确认中的状态
    private void payFailProcess(boolean isCheckingState) {
        mOrderPayFee.setVisibility(View.GONE);
        mOrderAlreadyPaid.setVisibility(View.VISIBLE);
        if (isCheckingState) {
            mOrderState.setText(getString(R.string.pay_checking));
            mOrderAlreadyPaid.setText(getString(R.string.pay_checking_tips));
        } else {
            mOrderState.setText(getString(R.string.pay_fail));
            mOrderAlreadyPaid.setText(getString(R.string.repay_tips));
        }
        goOrderList();
    }

    //支付成功处理
    private void paySuccessProcess() {
        mOrderPayFee.setVisibility(View.GONE);
        mOrderAlreadyPaid.setVisibility(View.VISIBLE);
        mOrderState.setText(getString(R.string.pay_success));
        mOrderAlreadyPaid.setText(getString(R.string.paid) + "¥" + mOrder.getOrder_cash_fee() + getString(R.string.yuan));
        mCoachContactTime.setVisibility(View.VISIBLE);
        //教练联系时间
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int min = rightNow.get(Calendar.MINUTE);
        String coachContact = ((hour < 21 && hour > 8) || (hour == 21 && min < 50)) ? "教练会在90分钟内和你联系" : "教练会在上班后和你联系";
        mCoachContactTime.setText(coachContact);
        goOrderList();
    }

    //倒计时跳转到订单列表
    private void goOrderList() {
        mOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到订单列表页面
                UIUtils.startHomeActivityToFragment(OrderPayActivity.this, HomeActivity.ORDER_ICON);
            }
        });
    }

    @Override
    protected void onStop() {
        try{
            unregisterReceiver(mBroadcastReceiver);
        }catch(IllegalArgumentException e){

        }
        super.onStop();
    }
}
