package com.randian.win.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import com.randian.win.support.network.BaseRequest;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-8-1.
 */
public class OrderChooseCoachActivity extends OrderChooseCoachBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @InjectView(R.id.next_step_btn)
    TextView mNextStepBtn;
    @InjectView(R.id.price_state)
    TextView mPriceState;
    @InjectView(R.id.coach_list)
    ListView mCoachListView;

    private int mCurrentLevel;//当前选中的等级
    private TextView mLastBtnClicked;//上一个选中的教练右侧按钮
    private Coach mCurrentCoach;
    private List<Coach> mCoachList;
    private OrderCoachListAdapter mAdapter;
    private Map<Integer, List<Coach>> mLevelCoachListMap;
    private View mLastCoachLevelTabChosen;//上一个选中的教练等级tab
    private final String TAG = OrderChooseCoachActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_choose_coach);
        ButterKnife.inject(this);
        initData();
        initHeader();
        initView();
    }

    private void initData() {
        mCoachList = new ArrayList<>();
        Intent intent = getIntent();
        mOrder = (Order) intent.getSerializableExtra(Consts.EXTRA_PARAM_0);
        mChosenDate = (String) intent.getSerializableExtra(Consts.EXTRA_PARAM_1);
        mFrom = (Integer) intent.getSerializableExtra(Consts.EXTRA_PARAM_2);
        mChosenTime = (String) intent.getSerializableExtra(Consts.EXTRA_PARAM_3);
        mListView = mCoachListView;

        if (mOrder == null) {
            Toaster.showShort(this, R.string.order_not_found);
            return;
        }

        if ((mFrom != Consts.FROM_COACH && mFrom != Consts.FROM_SPORT) || TextUtils.isEmpty(mChosenDate) || TextUtils.isEmpty(mChosenTime) || mChosenTime.indexOf(':') < 0) {
            LogUtils.e(TAG, "paramater invalid! from:" + mFrom + "  chosen time:" + mChosenTime);
            Toaster.showShort(this, R.string.error_paramater);
            return;
        }
        calEndTime();
    }

    private void initView() {
        mNextStepBtn.setOnClickListener(this);
        mAdapter = new OrderCoachListAdapter(OrderChooseCoachActivity.this, new ArrayList<Coach>());
        mCoachListView.setAdapter(mAdapter);
        mCoachListView.setOnItemClickListener(this);

        if (mFrom == Consts.FROM_SPORT) {
            startGetRemoteCoachForOrderTask();
        } else {
            Coach coach = new Coach();
            coach.setName(mOrder.getCoach_name());
            coach.setSex(mOrder.getCoach_gender());
            coach.setLevel(mOrder.getCoach_level());
            coach.setDescription(mOrder.getCoach_description());
            coach.setProfile_image_url(mOrder.getCoach_img_url());
            coach.setCoach_price(mOrder.getOrder_pay_fee());
            coach.setAvailable_areas(mOrder.getCoach_available_areas());
            mCurrentCoach = coach;
            mCoachList.add(coach);
            processLevel(mCoachList);
            mAdapter.setData(mCoachList);
            mPriceState.setText(String.valueOf(mOrder.getOrder_pay_fee()));
        }
    }

    private void startGetRemoteCoachForOrderTask() {
        LogUtils.d(TAG, "start to sync coach list for order from remote");
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result.contains(Consts.ERROR_CODE)) {
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result, type);
                    Toaster.showShort(OrderChooseCoachActivity.this, error.getError());
                    return;
                }

                List<Coach> coaches = null;
                try {
                    Type type = new TypeToken<List<Coach>>() {
                    }.getType();
                    coaches = RanDianApplication.getGson().fromJson(result.trim(), type);
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }

                if (coaches == null || coaches.isEmpty()) {
                    LogUtils.e(TAG, "coach not found for order id:" + mOrder.getOrder_no());
                    return;
                }

                processLevel(coaches);//处理等级显示
                mAdapter.setData(coaches);
                mCoachList.addAll(coaches);
                mCurrentCoach = coaches.get(0);
                mAdapter.setmCurrentSelectedCoachId(mCurrentCoach.getId());
                mPriceState.setText(String.valueOf(mCurrentCoach.getCoach_price()));
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(OrderChooseCoachActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.getCoachListForOrder(listener, errorListener, mOrder.getSport_id(), mChosenDate, mChosenTime, mEndTime);
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

        //下一步按钮被按下
        if (R.id.next_step_btn == view.getId()) {
            if(mCurrentCoach == null){
                return;
            }
            mOrder.setSport_detail_time(mDate.getText().toString());

            if (mFrom == Consts.FROM_SPORT) {
                if (mCurrentCoach == null) {
                    Toaster.showShort(this, R.string.coach_not_found);
                    return;
                }
                mOrder.setSport_start_date(mChosenDate);
                mOrder.setSport_start_time(mChosenTime);
                mOrder.setCoach_id(mCurrentCoach.getId());
                mOrder.setCoach_name(mCurrentCoach.getName());
                mOrder.setCoach_gender(mCurrentCoach.getSex());
                mOrder.setOrder_pay_fee(mCurrentCoach.getCoach_price());
                mOrder.setCoach_description(mCurrentCoach.getDescription());
                mOrder.setCoach_img_url(mCurrentCoach.getProfile_image_url());
                mOrder.setCoach_available_areas(mCurrentCoach.getAvailable_areas());
            } else {
                mOrder.setSport_start_time(mChosenTime);
                mOrder.setSport_start_date(mChosenDate);
            }

            UIUtils.startOrder3Activity(this, mOrder);
        } else {//教练等级按钮被按下
            if (Consts.FROM_SPORT == mFrom) {
                if (mLevelCoachListMap == null) {
                    mLevelCoachListMap = new HashMap<>();
                    Coach coach;
                    for (int i = 0, len = mCoachList.size(); i < len; i++) {
                        coach = mCoachList.get(i);
                        if (mLevelCoachListMap.containsKey(coach.getLevel())) {
                            mLevelCoachListMap.get(coach.getLevel()).add(coach);
                        } else {
                            List<Coach> coachList = new ArrayList<>();
                            coachList.add(coach);
                            mLevelCoachListMap.put(coach.getLevel(), coachList);
                        }
                    }
                }
                //重置上一个选中的tab背景色
                if (mLastCoachLevelTabChosen != null) {
                    mLastCoachLevelTabChosen.setBackgroundColor(getResources().getColor(R.color.c_ccc));
                }
                view.setBackgroundColor(getResources().getColor(R.color.orange));
                mLastCoachLevelTabChosen = view;
                mCurrentLevel = view.getId();//获得当前选中等级

                if(mLastBtnClicked != null){//清空上一个选中的教练按钮
                    mLastBtnClicked.setBackgroundResource(R.drawable.icon_unchecked);
                    mLastBtnClicked = null;
                }

                mAdapter.setData(mLevelCoachListMap.get(view.getId()));//刷新列表
                mCurrentCoach = mLevelCoachListMap.get(view.getId()).get(0);//设置第一个为选中教练
                mAdapter.setmCurrentSelectedCoachId(mCurrentCoach.getId());
                mPriceState.setText(String.valueOf(mCurrentCoach.getCoach_price()));//更新价格区域
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        int checkedPosition = position - mListView.getHeaderViewsCount();
        int count = (mCurrentLevel != 0) ? mLevelCoachListMap.get(mCurrentLevel).size() : mCoachList.size();

        if (checkedPosition >= 0 && checkedPosition <= count) {

            Coach coach = mAdapter.getItem(checkedPosition);
            if (coach != null) {
                mAdapter.setmCurrentSelectedCoachId(coach.getId());//标记当前选中的coachid
                mNextStepBtn.setBackgroundResource(R.color.orange);
                if (mLastBtnClicked == null) {
                    mLastBtnClicked = (TextView) mCoachListView.getChildAt(mCoachListView.getHeaderViewsCount()).findViewById(R.id.btn_order);
                }

                mLastBtnClicked.setBackgroundResource(R.drawable.icon_unchecked);

                mCurrentCoach = coach;
                mLastBtnClicked = (TextView) view.findViewById(R.id.btn_order);
                mLastBtnClicked.setBackgroundResource(R.drawable.icon_checked);
                mPriceState.setText(String.valueOf(mCurrentCoach.getCoach_price()));//更新价格区域
            }
        }
    }

    private void calEndTime() {
        //计算时间
        String[] arr = mChosenTime.split(":");
        int hour = Integer.parseInt(arr[0]);
        int minute = Integer.parseInt(arr[1]);
        minute += mOrder.getSport_duration() * mOrder.getSport_order_num();
        hour += minute / 60;
        minute = minute % 60;
        mEndTime = ((hour >= 10) ? hour : "0" + hour) + ":" + (minute >= 10 ? minute : "0" + minute);
    }

    private void processLevel(List<Coach> coaches) {
        TreeMap<Integer, String> coachLevel = new TreeMap<>();
        Coach coach;
        for (int i = 0, len = coaches.size(); i < len; i++) {
            coach = coaches.get(i);
            coachLevel.put(coach.getLevel(), Consts.COACH_LEVEL.get(coach.getLevel()));
        }
        //如果只有一个中级，不显示教练级别区域
        if (coachLevel.size() == 1 && coachLevel.get(Consts.JUNIOR_COACH) != null) {
            return;
        }

        mCoachLevelSec.setVisibility(View.VISIBLE);
        //生成tab区域
        Iterator it = coachLevel.keySet().iterator();
        LinearLayout.LayoutParams layoutParams;
        while (it.hasNext()) {
            int level = (Integer) it.next();
            TextView tab = new TextView(getApplicationContext());
            tab.setLayoutParams(new LinearLayout.LayoutParams(100, 50));
            tab.setTextColor(getResources().getColor(R.color.white));
            tab.setBackgroundColor(getResources().getColor(R.color.c_ccc));
            tab.setText(coachLevel.get(level));
            tab.setGravity(Gravity.CENTER);
            tab.setId(level);
            tab.setOnClickListener(this);
            mCoachLevelTab.addView(tab);
            //divider
            if (it.hasNext()) {
                TextView divider = new TextView(getApplicationContext());
                layoutParams = new LinearLayout.LayoutParams(50, 2);
                layoutParams.setMargins(0, 25, 0, 0);
                divider.setLayoutParams(layoutParams);
                divider.setBackgroundColor(getResources().getColor(R.color.order_step_divider));
                mCoachLevelTab.addView(divider);
            }
        }
        //如果有一个高级、特级、专家等，默认选中
        if(coachLevel.size() == 1){
             mCoachLevelTab.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.orange));
        }
    }
}
