package com.randian.win.ui.coach;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
import com.randian.win.model.Coach;
import com.randian.win.model.ErrorCode;
import com.randian.win.model.Order;
import com.randian.win.model.Sport;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.sport.SportListAdapter;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.MockUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;
import com.randian.win.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-7-5.
 */
public class CoachDetailActivity extends CoachDetailBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @InjectView(R.id.order_state)
    TextView mOrderState;
    @InjectView(R.id.sport_order_btn)
    TextView mOrderBtn;
    @InjectView(R.id.sport_list)
    ListView mSportListView;

    private Sport mCurrentSport;
    private TextView currentClicked;
    private SportListAdapter mAdapter;
    private final String TAG = CoachDetailActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_detail);
        ButterKnife.inject(this);
        initData();
        initHeader();
        initView();
        startGetRemoteCoachDetailTask();
    }

    private void initData() {
        Intent intent = getIntent();
        mCoach = (Coach) intent.getSerializableExtra(Consts.EXTRA_PARAM_0);

        if (mCoach == null) {
            Toaster.showShort(this, R.string.coach_not_found);
            return;
        }
        mListView = mSportListView;
    }

    private void initView() {
        mOrderBtn.setOnClickListener(this);
        mDescBtn.setOnClickListener(this);
        mCommentContainer.setOnClickListener(this);
        mAdapter = new SportListAdapter(CoachDetailActivity.this, R.layout.coach_detail_sport_list_item, "coach");
        mSportListView.setAdapter(mAdapter);
        mSportListView.setOnItemClickListener(this);
    }

    public void startGetRemoteCoachDetailTask() {
        Coach coach = MockUtils.getCoachList("gym").get(0);
        coach.setSports(MockUtils.getSportList("gym"));
        mCoach = coach;
        mImageWrapper.setImageResource(Consts.COACH_LEVEL_IMG_WRAPPER.get(coach.getLevel()));

        if (TextUtils.isEmpty(mCoach.getDescription())) {
            mDescription.setText(getResources().getString(R.string.no_description));
        } else {
            mDescription.setText(Html.fromHtml(mCoach.getDescription().trim()));
        }

        if (coach.getSports() == null || coach.getSports().isEmpty()) {
            mTips.setText(getString(R.string.no_sport_found));
        } else {
            mTips.setVisibility(View.GONE);
            mAdapter.setData(coach.getSports());
        }

        if (mCoach.getComment_num() != 0) {
            mCommentDivider.setVisibility(View.VISIBLE);
            mCommentContainer.setVisibility(View.VISIBLE);
            Utils.commentStar(mCoach.getScore(), mCommentStars, getApplicationContext());
            mCommentCount.setText(mCoach.getComment_num() + "条");
            mCommentScore.setText(String.valueOf(mCoach.getScore()));
        }
//        if (mCoach == null) {
//            return;
//        }
//        LogUtils.d(TAG, "start to sync coach detail from remote id:" + mCoach.getId());
//        Response.Listener<String> listener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String result) {
//                if (result.contains(Consts.ERROR_CODE)) {
//                    Type type = new TypeToken<ErrorCode>() {
//                    }.getType();
//                    ErrorCode error = RanDianApplication.getGson().fromJson(result, type);
//                    Toaster.showShort(CoachDetailActivity.this, error.getError());
//                    return;
//                }
//
//                Coach coach = null;
//                try {
//                    Type type = new TypeToken<Coach>() {
//                    }.getType();
//                    coach = RanDianApplication.getGson().fromJson(result.trim(), type);
//                } catch (Exception e) {
//                    LogUtils.d(TAG, e.toString());
//                }
//
//                if (coach == null) {
//                    LogUtils.e(TAG, "coach not found for id:" + mCoach.getId());
//                    return;
//                }
//
//                mCoach = coach;
//                mImageWrapper.setImageResource(Consts.COACH_LEVEL_IMG_WRAPPER.get(coach.getLevel()));
//
//                if (TextUtils.isEmpty(mCoach.getDescription())) {
//                    mDescription.setText(getResources().getString(R.string.no_description));
//                } else {
//                    mDescription.setText(Html.fromHtml(mCoach.getDescription().trim()));
//                }
//
//                if (coach.getSports() == null || coach.getSports().isEmpty()) {
//                    mTips.setText(getString(R.string.no_sport_found));
//                } else {
//                    mTips.setVisibility(View.GONE);
//                    mAdapter.setData(coach.getSports());
//                }
//
//                if (mCoach.getComment_num() != 0) {
//                    mCommentDivider.setVisibility(View.VISIBLE);
//                    mCommentContainer.setVisibility(View.VISIBLE);
//                    Utils.commentStar(mCoach.getScore(), mCommentStars, getApplicationContext());
//                    mCommentCount.setText(mCoach.getComment_num() + "条");
//                    mCommentScore.setText(String.valueOf(mCoach.getScore()));
//                }
//            }
//        };
//
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                ErrorHandler.handleException(CoachDetailActivity.this, volleyError);
//            }
//        };
//
//        try {
//            BaseRequest request = HttpClient.getCoachDetail(listener, errorListener, mCoach.getId());
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
            case R.id.desc_btn:
                showDesc();
                break;
            case R.id.sport_order_btn:
                order();
                break;
            case R.id.comment_container:
                UIUtils.startCommentListActivity(this, mCoach.getId());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        int checkedPosition = position - mListView.getHeaderViewsCount();
        if (checkedPosition >= 0 && position <= mCoach.getSports().size()) {
            Sport sport = mAdapter.getItem(checkedPosition);
            if (sport != null) {
                mCurrentSport = sport;
                mOrderBtn.setBackgroundResource(R.color.orange);
                mOrderState.setText(R.string.chosen);
                if (currentClicked != null) {
                    currentClicked.setBackgroundResource(R.drawable.icon_unchecked);
                }
                currentClicked = (TextView) view.findViewById(R.id.btn_order);
                view.findViewById(R.id.btn_order).setBackgroundResource(R.drawable.icon_checked);
            }
        }
    }

    private void showDesc() {
        if (mDescription.getVisibility() == View.VISIBLE) {
            mDescription.setVisibility(View.GONE);
            mDescBtn.setImageResource(R.drawable.icon_open);
            mAboutCoach.setTextColor(getResources().getColor(R.color.b3b3b3));
        } else {
            mDescription.setVisibility(View.VISIBLE);
            mDescBtn.setImageResource(R.drawable.icon_close);
            mAboutCoach.setTextColor(getResources().getColor(R.color.orange));
        }
    }

    private void order() {
        if (mCoach == null) {
            LogUtils.e(TAG, "coach not found!");
            return;
        }
        if (mCurrentSport == null) {
            Toaster.showShort(this, R.string.sport_not_chosen);
            return;
        }

        Order order = new Order();
        order.setCoach_id(mCoach.getId());
        order.setCoach_name(mCoach.getName());
        order.setCoach_gender(mCoach.getSex());
        order.setCoach_level(mCoach.getLevel());
        order.setCoach_description(mCoach.getDescription());
        order.setCoach_img_url(mCoach.getProfile_image_url());
        order.setCoach_available_areas(mCoach.getAvailable_areas());

        order.setSport_order_num(1);
        order.setSport_id(mCurrentSport.getId());
        order.setSport_name(mCurrentSport.getName());
        order.setSuggest(mCurrentSport.getSuggest());
        order.setSport_duration(mCurrentSport.getDuration());
        order.setSport_img_url(mCurrentSport.getHead_image_url());
        order.setOrder_pay_fee(mCurrentSport.getCoach_price());
        UIUtils.startOrder1Activity(CoachDetailActivity.this, order, Consts.FROM_COACH);
    }

}
