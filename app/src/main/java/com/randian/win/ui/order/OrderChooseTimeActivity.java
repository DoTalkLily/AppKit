package com.randian.win.ui.order;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.randian.win.R;
import com.randian.win.model.Order;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.Toaster;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-7-26.
 */
public class OrderChooseTimeActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    @InjectView(R.id.tv_day1)
    TextView day1;
    @InjectView(R.id.tv_day2)
    TextView day2;
    @InjectView(R.id.tv_day3)
    TextView day3;
    @InjectView(R.id.tv_day4)
    TextView day4;
    @InjectView(R.id.tv_day5)
    TextView day5;
    @InjectView(R.id.vp_calender)
    ViewPager mViewPager;

    @InjectView(R.id.iv_arrow_day1)
    ImageView arrow1;
    @InjectView(R.id.iv_arrow_day2)
    ImageView arrow2;
    @InjectView(R.id.iv_arrow_day3)
    ImageView arrow3;
    @InjectView(R.id.iv_arrow_day4)
    ImageView arrow4;
    @InjectView(R.id.iv_arrow_day5)
    ImageView arrow5;

    private int mFrom;//来自教练详情还是项目列表
    private Order mOrder;
    private String[] mDates;
    private TextView[] mDays;
    private ImageView[] mArrows;
    private TextView lastClickedDay;
    private ImageView lastClickedArrow;
    private int[] mWeekDays = {R.string.sunday, R.string.monday, R.string.tuesday, R.string.wednesday, R.string.thursday, R.string.friday, R.string.saturday};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order2);
        ButterKnife.inject(this);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mOrder = (Order) intent.getSerializableExtra(Consts.EXTRA_PARAM_0);
        mFrom = (Integer) intent.getSerializableExtra(Consts.EXTRA_PARAM_1);
        if (mOrder == null) {
            Toaster.showShort(this, R.string.order_not_found);
            return;
        }

        mDates = new String[Consts.DAY_COUNT];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //记录日期格式
        for (int i = 0; i < Consts.DAY_COUNT; i++) {
            calendar.add(Calendar.DATE, 1);
            mDates[i] = format.format(calendar.getTime());
        }

        mDays = new TextView[Consts.DAY_COUNT];
        mDays[0] = day1;
        mDays[1] = day2;
        mDays[2] = day3;
        mDays[3] = day4;
        mDays[4] = day5;
        mArrows = new ImageView[Consts.DAY_COUNT];
        mArrows[0] = arrow1;
        mArrows[1] = arrow2;
        mArrows[2] = arrow3;
        mArrows[3] = arrow4;
        mArrows[4] = arrow5;
    }

    private void initView() {
        Resources res = getResources();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, 2);
        day3.setText(res.getString(mWeekDays[calendar.get(Calendar.DAY_OF_WEEK) % 7]));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        day4.setText(res.getString(mWeekDays[calendar.get(Calendar.DAY_OF_WEEK) % 7]));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        day5.setText(res.getString(mWeekDays[calendar.get(Calendar.DAY_OF_WEEK) % 7]));

        for (int i = 0; i < Consts.DAY_COUNT; i++) {
            mDays[i].setOnClickListener(this);
        }

        OrderCalendarAdapter adapter = new OrderCalendarAdapter(mOrder, mFrom, mDates, this, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
        setIconSelected(0);
    }

    private void setIconSelected(int position) {
        if (position >= mDays.length) {
            return;
        }

        mViewPager.setCurrentItem(position);
        if(lastClickedDay != null && lastClickedDay != mDays[position]) {
            lastClickedDay.setBackgroundColor(getResources().getColor(R.color.transparent));
            lastClickedDay.setTextColor(getResources().getColor(R.color.black));
            lastClickedArrow.setVisibility(View.INVISIBLE);
        }
        mDays[position].setBackgroundResource(R.drawable.pebble);
        mDays[position].setTextColor(getResources().getColor(R.color.white));
        mArrows[position].setVisibility(View.VISIBLE);

        lastClickedDay = mDays[position];
        lastClickedArrow = mArrows[position];
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.tv_day1:
                setIconSelected(0);
                break;
            case R.id.tv_day2:
                setIconSelected(1);
                break;
            case R.id.tv_day3:
                setIconSelected(2);
                break;
            case R.id.tv_day4:
                setIconSelected(3);
                break;
            case R.id.tv_day5:
                setIconSelected(4);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setIconSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
