package com.randian.win.ui.welcome.indicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.randian.win.R;
import com.randian.win.ui.base.BaseActivity;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by a42 on 14-4-7.
 */
public class IndicatorActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private IntroImageAdapter mAdapter = null;

    @InjectView(R.id.vp_welcome_view_pager)
    ViewPager mViewPager;

    @InjectView(R.id.vpi_welcome_pager_indicator)
    CirclePageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indicator);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        mAdapter = new IntroImageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == mAdapter.getCount() - 1) {
            mIndicator.setVisibility(View.GONE);
        } else {
            mIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

