package com.randian.win.ui.sport;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.ErrorCode;
import com.randian.win.model.Order;
import com.randian.win.model.Sport;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.MockUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-7-5.
 * sport detail
 */
public class SportDetailActivity extends BaseActivity implements View.OnClickListener {

    //页面控件
    @InjectView(R.id.avatar_image)
    SimpleDraweeView mImage;
    @InjectView(R.id.sport_title)
    TextView mTitle;
    @InjectView(R.id.suggest)
    TextView mSuggest;
    @InjectView(R.id.duration)
    TextView mDuration;
    @InjectView(R.id.cost)
    TextView mPrice;
    @InjectView(R.id.origin_cost)
    TextView mOriginPrice;
    @InjectView(R.id.desc_content)
    TextView mSportDesc;
    @InjectView(R.id.description)
    ViewGroup mDescContent;

    //点击事件的view
    @InjectView(R.id.sub_btn)
    TextView mSubBtn;
    @InjectView(R.id.add_btn)
    TextView mAddBtn;
    @InjectView(R.id.sport_count)
    TextView mSportCount;
    @InjectView(R.id.desc_btn)
    ImageView mDescBtn;
    @InjectView(R.id.sport_sum_time)
    TextView mSumTime;
    @InjectView(R.id.sport_order_btn)
    TextView mOrderBtn;
    @InjectView(R.id.about_class)
    TextView mAboutCoach;

    private int mCount = 1;
    private Sport mSport;
    private final String TAG = SportDetailActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sport_detail);
        ButterKnife.inject(this);
        initData();
        initView();
        startGetRemoteSportDetailTask();
    }

    private void initData() {
        Intent intent = getIntent();
        mSport = (Sport) intent.getSerializableExtra(Consts.EXTRA_PARAM_0);

        if (mSport == null) {
            Toaster.showShort(this, R.string.sport_not_found);
        }
    }


    private void initView() {
        mTitle.setText(mSport.getName());
        mDuration.setText("时间:" + mSport.getDuration() + "分钟");
        mPrice.setText("¥" + String.valueOf(mSport.getPrice()) + "起");
        mImage.setImageURI(Uri.parse(mSport.getHead_image_url()));
        mSuggest.setText("建议人数：" + getSuggest(mSport.getMax_user_num(), mSport.getMin_user_num()));
        mSumTime.setText("总时长：" + mSport.getDuration() * mCount + " 分钟");

        if (mSport.getOriginal_price() > 0) {
            mOriginPrice.setText("原价：¥" + String.valueOf(mSport.getOriginal_price()));
            mOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        //event listener
        mAddBtn.setOnClickListener(this);
        mSubBtn.setOnClickListener(this);
        mOrderBtn.setOnClickListener(this);
        mDescBtn.setOnClickListener(this);
    }

    public void startGetRemoteSportDetailTask() {
           Sport sport = MockUtils.getSportList("gym").get(0);
            mSport.setDescription(sport.getDescription());
            mSport.setDetail_image_urls(sport.getDetail_image_urls());
            //渲染页面元素
            mSportDesc.setText(Html.fromHtml(mSport.getDescription()));
            //description image
            if (mSport.getDetail_image_urls() != null) {
                for (String url : mSport.getDetail_image_urls()) {
                    if (url != null) {
                        SimpleDraweeView image = createImage(url);
                        mDescContent.addView(image);
                    }
                }
            }
//        if (mSport == null) {
//            return;
//        }
//        LogUtils.d(TAG, "start to sync sports detail from remote id:" + mSport.getId());
//        Response.Listener<String> listener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String result) {
//                if(result.contains(Consts.ERROR_CODE)){
//                    Type type = new TypeToken<ErrorCode>() {
//                    }.getType();
//                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
//                    Toaster.showShort(SportDetailActivity.this,error.getError());
//                    return;
//                }
//
//                Sport sport = null;
//                try {
//                    Type type = new TypeToken<Sport>() {
//                    }.getType();
//                    sport = RanDianApplication.getGson().fromJson(result.trim(), type);
//                } catch (Exception e) {
//                    LogUtils.d(TAG, e.toString());
//                }
//
//                if (sport == null) {
//                    LogUtils.e(TAG, "sport not found for id:" + mSport.getId());
//                    return;
//                }
//
//                mSport.setDescription(sport.getDescription());
//                mSport.setDetail_image_urls(sport.getDetail_image_urls());
//                //渲染页面元素
//                mSportDesc.setText(Html.fromHtml(mSport.getDescription()));
//                //description image
//                if (mSport.getDetail_image_urls() != null) {
//                    for (String url : mSport.getDetail_image_urls()) {
//                        if (url != null) {
//                            SimpleDraweeView image = createImage(url);
//                            mDescContent.addView(image);
//                        }
//                    }
//                }
//            }
//        };
//
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                ErrorHandler.handleException(SportDetailActivity.this, volleyError);
//            }
//        };
//
//        try {
//            BaseRequest request = HttpClient.getSportDetail(listener, errorListener, mSport.getId());
//            request.setTag(this);
//            mQueue.add(request);
//            mQueue.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onClick(View view) {
        if (null == view) {
            return;
        }

        switch (view.getId()) {
            case R.id.sub_btn:
                sub();
                break;
            case R.id.add_btn:
                add();
                break;
            case R.id.desc_btn:
                showDesc();
                break;
            case R.id.sport_order_btn:
                order();
                break;
        }
    }

    private void showDesc() {
        if (mDescContent.getVisibility() == View.VISIBLE) {
            mDescContent.setVisibility(View.GONE);
            mDescBtn.setImageResource(R.drawable.icon_open);
            mAboutCoach.setTextColor(getResources().getColor(R.color.b3b3b3));
        } else {
            mDescContent.setVisibility(View.VISIBLE);
            mDescBtn.setImageResource(R.drawable.icon_close);
            mAboutCoach.setTextColor(getResources().getColor(R.color.orange));
        }
    }

    private void sub() {
        if (mCount > 1) {
            mCount = mCount - 1;
            mSportCount.setText(String.valueOf(mCount));
            mSumTime.setText("总时长：" + mSport.getDuration() * mCount + " 分钟");
        }
    }

    private void add() {
        mCount = mCount + 1;
        mSportCount.setText(String.valueOf(mCount));
        mSumTime.setText("总时长：" + mSport.getDuration() * mCount + " 分钟");
    }

    private void order() {
        if (mSport == null) {
            Toaster.showShort(this, R.string.sport_not_found);
            return;
        }
        Order order = new Order();
        order.setSport_id(mSport.getId());
        order.setSport_order_num(mCount);
        order.setSport_name(mSport.getName());
        order.setCreated_at(mSport.getCreated_at());
        order.setSport_duration(mSport.getDuration());
        order.setOrigin_price(mSport.getOriginal_price());
        order.setSport_img_url(mSport.getHead_image_url());
        order.setOrder_pay_fee(mSport.getPrice());
        order.setSuggest("建议人数：" + getSuggest(mSport.getMax_user_num(), mSport.getMin_user_num()));
        UIUtils.startOrder1Activity(SportDetailActivity.this, order,Consts.FROM_SPORT);
    }

    private String getSuggest(int maxNum, int minNum) {
        if (minNum > 0 && maxNum > 0) {
            return minNum + " - " + maxNum + "人";
        }
        if (minNum > 0) {
            return "大于" + minNum + "人";
        }
        if (maxNum > 0) {
            return " 小于" + maxNum + "人";
        }
        return "";
    }

    private SimpleDraweeView createImage(String url){
        SimpleDraweeView image = new SimpleDraweeView(getApplicationContext());
        image.setAdjustViewBounds(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,800);
        layoutParams.setMargins(15, 0, 15, 20);
        image.setLayoutParams(layoutParams);
        image.setImageURI(Uri.parse(url));

        return image;
    }
}
