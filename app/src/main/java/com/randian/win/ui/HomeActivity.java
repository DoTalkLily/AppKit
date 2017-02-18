package com.randian.win.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.randian.win.R;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.ui.base.ListBaseFragment;
import com.randian.win.ui.personal.PersonalFragment;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class HomeActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    @InjectView(R.id.vp_home)
    ViewPager mViewPager;
    @InjectView(R.id.sport_tab)
    View mSportTab;
    @InjectView(R.id.coach_tab)
    View mCoachTab;
    @InjectView(R.id.order_tab)
    View mOrderTab;
    @InjectView(R.id.personal_tab)
    View mPersonalTab;
    @InjectView(R.id.iv_sport_tab)
    ImageView mSportIcon;
    @InjectView(R.id.iv_coach_tab)
    ImageView mCoachIcon;
    @InjectView(R.id.iv_order_tab)
    ImageView mOrderIcon;
    @InjectView(R.id.iv_personal_tab)
    ImageView mPersonalIcon;
    @InjectView(R.id.tv_sport_tab)
    TextView mSportTitle;
    @InjectView(R.id.tv_coach_tab)
    TextView mCoachTitle;
    @InjectView(R.id.tv_order_tab)
    TextView mOrderTitle;
    @InjectView(R.id.tv_personal_tab)
    TextView mPersonalTitle;
    @InjectView(R.id.menu_container)
    View mMenuContainer;

    public static final int SPORT_ICON = 0;
    public static final int COACH_ICON = 1;
    public static final int ORDER_ICON = 2;
    public static final int PERSONAL_ICON = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UmengUpdateAgent.update(this);
        MobclickAgent.updateOnlineConfig(this);//youmeng 发送数据到服务器
        AnalyticsConfig.enableEncrypt(true);//youmeng 日志加密
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        initView();
    }

    public void initView() {
        getSupportActionBar().hide();
        mViewPager.setAdapter(new HomePagerAdapter(this, getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);
        setIconSelected(SPORT_ICON);

        mSportTab.setOnClickListener(this);
        mCoachTab.setOnClickListener(this);
        mOrderTab.setOnClickListener(this);
        mPersonalTab.setOnClickListener(this);
        //如果是从别的页面跳转过来
        Intent intent = getIntent();
        if(intent != null){
            int tabIndex = intent.getIntExtra("tabIndex",-1);
            if(tabIndex != -1){
                setIconSelected(tabIndex);
            }
        }
        mMenuContainer.setOnClickListener(this);
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

    public View getMenuContainer(){
        return mMenuContainer;
    }

    public void setIconSelected(int pos) {
        mSportIcon.setImageResource(R.drawable.tab1_normal);
        mCoachIcon.setImageResource(R.drawable.tab2_normal);
        mOrderIcon.setImageResource(R.drawable.tab3_normal);
        mPersonalIcon.setImageResource(R.drawable.tab4_normal);
        int grey = getResources().getColor(R.color.b3b3b3);
        mSportTitle.setTextColor(grey);
        mCoachTitle.setTextColor(grey);
        mOrderTitle.setTextColor(grey);
        mPersonalTitle.setTextColor(grey);

        switch (pos) {
            case SPORT_ICON:
                mSportIcon.setImageResource(R.drawable.tab1_selected);
                mSportTitle.setTextColor(getResources().getColor(R.color.orange));
                break;
            case COACH_ICON:
                mCoachIcon.setImageResource(R.drawable.tab2_selected);
                mCoachTitle.setTextColor(getResources().getColor(R.color.orange));
                break;
            case ORDER_ICON:
                mOrderIcon.setImageResource(R.drawable.tab3_selected);
                mOrderTitle.setTextColor(getResources().getColor(R.color.orange));
                break;
            case PERSONAL_ICON:
                ListBaseFragment fragment = (PersonalFragment)((HomePagerAdapter)mViewPager.getAdapter()).getFragment(PERSONAL_ICON);
                fragment.animateBack();
                mPersonalIcon.setImageResource(R.drawable.tab4_selected);
                mPersonalTitle.setTextColor(getResources().getColor(R.color.orange));
                break;
        }

        mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.sport_tab:
                setIconSelected(SPORT_ICON);
                break;
            case R.id.coach_tab:
                setIconSelected(COACH_ICON);
                break;
            case R.id.order_tab:
                setIconSelected(ORDER_ICON);
                break;
            case R.id.personal_tab:
                setIconSelected(PERSONAL_ICON);
                break;
        }
    }

}

