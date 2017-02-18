package com.randian.win.ui.order;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-7-5.
 */
public class OrderCustomerInfoActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener, OnGetSuggestionResultListener {
    @InjectView(R.id.contact)
    EditText mContact;
    @InjectView(R.id.mobile)
    EditText mMobile;
    @InjectView(R.id.street)
    AutoCompleteTextView mStreet;
    @InjectView(R.id.address)
    EditText mAddress;
    @InjectView(R.id.mark)
    EditText mMark;
    @InjectView(R.id.sport_order_btn)
    TextView mOrderBtn;
    @InjectView(R.id.service_scope)
    TextView mServiceScope;
    @InjectView(R.id.order_state)
    TextView mOrderState;

    private Order mOrder;
    private String mCity;
    private int mFrom;
    private Consumer mConsumer;
    private ArrayAdapter<String> mSuggestAdapter;
    private SuggestionSearch mSuggestionSearch = null;
    private final String TAG = OrderCustomerInfoActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this.getApplication());
        setContentView(R.layout.order1);
        ButterKnife.inject(this);
        initData();
        initView();
        startGetRemoteConsumerDetailTask();
    }

    private void initData() {
        Intent intent = getIntent();
        mOrder = (Order) intent.getSerializableExtra(Consts.EXTRA_PARAM_0);
        mFrom = (Integer) intent.getSerializableExtra(Consts.EXTRA_PARAM_1);

        if (mOrder == null) {
            Toaster.showShort(this, R.string.order_not_found);
            return;
        }

        mCity = getResources().getString(R.string.beijing);

        //mock
//        mContact.setText("李里");
//        mMobile.setText("18210227588");
//        mStreet.setText("我想出去玩");
    }

    private void initView() {
        Resources res = getResources();
        SpannableString spannableString = new SpannableString(res.getString(R.string.service_distance));
        spannableString.setSpan(new ForegroundColorSpan(res.getColor(R.color.orange)), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mServiceScope.setText(spannableString);

        mOrderBtn.setOnClickListener(this);
        mContact.setOnFocusChangeListener(this);
        mMobile.setOnFocusChangeListener(this);
        mStreet.setOnFocusChangeListener(this);

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mSuggestAdapter = new ArrayAdapter<>(this, R.layout.address_list_item);//simple_dropdown_item_1line
        mStreet.setAdapter(mSuggestAdapter);
        mStreet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <= 0) {
                    return;
                }
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(charSequence.toString()).city(mCity));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initComponent() {
        if (mOrder == null) {
            Toaster.showShort(this, R.string.order_not_found);
            return;
        }

        mContact.setText(mOrder.getConsignee_name());
        mMobile.setText(mOrder.getConsignee_mobile() + "");
        mStreet.setText(mOrder.getConsignee_street());
        mStreet.setText(mOrder.getConsignee_address());

        if (isValid()) {
            mOrderBtn.setEnabled(true);
            mOrderState.setText(getResources().getString(R.string.info_completed));
            mOrderBtn.setBackgroundColor(getResources().getColor(R.color.orange));
        }
    }


    private void startGetRemoteConsumerDetailTask() {
        LogUtils.d(TAG, "start to sync consignee info from remote");
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(OrderCustomerInfoActivity.this,error.getError());
                    return;
                }

                List<Consumer> consumers = null;
                try {
                    Type type = new TypeToken<List<Consumer>>() {
                    }.getType();
                    consumers = RanDianApplication.getGson().fromJson(result.trim(), type);
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }

                if (consumers == null || consumers.isEmpty()) {
                    LogUtils.d(TAG, "consumer info not found");
                    return;
                }

                mConsumer = consumers.get(consumers.size()-1);
                mOrder.setConsignee_address_id(mConsumer.getId());
                mOrder.setConsignee_name(mConsumer.getConsignee_name());
                mOrder.setConsignee_mobile(mConsumer.getConsignee_mobile());
                mOrder.setConsignee_street(mConsumer.getConsignee_street());
                mOrder.setConsignee_address(mConsumer.getConsignee_address());

                initComponent();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(OrderCustomerInfoActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.getConsumerInfo(listener, errorListener);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSaveConsumerInfo() {
        LogUtils.d(TAG, "start to save consumer info to remote");
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
//                UIUtils.startOrder2Activity(OrderCustomerInfoActivity.this, mOrder, mFrom);

                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(OrderCustomerInfoActivity.this,error.getError());
                    return;
                }

                //进入第二步
                UIUtils.startOrder2Activity(OrderCustomerInfoActivity.this, mOrder, mFrom);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(OrderCustomerInfoActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.postConsumerInfo(listener, errorListener, mConsumer);
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
        if (isValid()) {
            mOrderBtn.setEnabled(false);
            mOrderBtn.setBackgroundColor(getResources().getColor(R.color.orange));
            mOrderState.setText(getResources().getString(R.string.info_completed));
            mOrder.setConsignee_name(mContact.getText().toString().trim());
            mOrder.setConsignee_street(mStreet.getText().toString().trim());
            mOrder.setConsignee_address(mAddress.getText().toString().trim());
            mOrder.setConsignee_mobile(Long.parseLong(mMobile.getText().toString().trim()));
            mOrder.setMark(mMark.getText().toString().trim());
            //更新Customer信息
            mConsumer = mConsumer == null ? new Consumer():mConsumer;
            mConsumer.setConsignee_name(mContact.getText().toString().trim());
            mConsumer.setConsignee_street(mStreet.getText().toString().trim());
            mConsumer.setConsignee_address(mAddress.getText().toString().trim());
            mConsumer.setConsignee_mobile(Long.parseLong(mMobile.getText().toString().trim()));
            startSaveConsumerInfo();
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            if (!TextUtils.isEmpty(mContact.getText().toString().trim()) && !TextUtils.isEmpty(mMobile.getText().toString().trim()) && !TextUtils.isEmpty(mStreet.getText().toString().trim())) {
                mOrderBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                mOrderState.setText(getResources().getString(R.string.info_completed));
            } else {
                mOrderBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                mOrderState.setText(getResources().getString(R.string.info_uncompleted));
            }
        }
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(mContact.getText().toString().trim())) {
            Toaster.showShort(this, R.string.error_no_contact);
            return false;
        }
        if (TextUtils.isEmpty(mMobile.getText().toString().trim())) {
            Toaster.showShort(this, R.string.error_no_mobile);
            return false;
        }
        if (TextUtils.isEmpty(mStreet.getText().toString().trim())) {
            Toaster.showShort(this, R.string.error_no_street);
            return false;
        }
        if (!Utils.checkPhoneFormat(mMobile.getText().toString().trim())) {
            Toaster.showShort(this, R.string.error_phone);
            return false;
        }
        return true;
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }
        mSuggestAdapter.clear();
        for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
            if (info.key != null)
                mSuggestAdapter.add(info.key);
        }
        mSuggestAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        mSuggestionSearch.destroy();
        super.onDestroy();
    }
}
