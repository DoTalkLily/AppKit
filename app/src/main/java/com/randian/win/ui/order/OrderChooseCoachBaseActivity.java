package com.randian.win.ui.order;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.randian.win.R;
import com.randian.win.model.Order;
import com.randian.win.ui.base.BaseActivity;

/**
 * Created by lily on 15-7-5.
 */
public class OrderChooseCoachBaseActivity extends BaseActivity {

    protected int mFrom;
    protected Order mOrder;
    protected View mHeaderView;
    protected String mChosenTime;
    protected String mChosenDate;
    protected String mEndTime;
    protected ListView mListView;
    protected TextView mDate;
    protected RelativeLayout mCoachLevelSec;//教练等级区域
    protected LinearLayout mCoachLevelTab;//教练等级tab


    protected void initHeader() {
        mHeaderView = getLayoutInflater().inflate(R.layout.view_order_coach_list_header, null);
        mDate = (TextView) mHeaderView.findViewById(R.id.order_date);
        mDate.setText(mChosenDate.replaceAll("-", "/") + "   " + mChosenTime + "-" + mEndTime);
        mCoachLevelTab = (LinearLayout)mHeaderView.findViewById(R.id.coach_level_tab);
        mCoachLevelSec = (RelativeLayout)mHeaderView.findViewById(R.id.coach_level_sec);
        mListView.addHeaderView(mHeaderView);
    }
}
