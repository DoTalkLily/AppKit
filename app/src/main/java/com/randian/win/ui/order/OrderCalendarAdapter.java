package com.randian.win.ui.order;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randian.win.model.Order;
import com.randian.win.ui.base.BaseFragment;
import com.randian.win.ui.coach.CoachListFragment;
import com.randian.win.ui.personal.PersonalFragment;
import com.randian.win.ui.sport.SportListFragment;
import com.randian.win.utils.Consts;
import com.randian.win.utils.LogUtils;

import java.util.HashMap;


public class OrderCalendarAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private HashMap<Integer, BaseFragment> mFragments;

    public OrderCalendarAdapter(Order order,int from,String[] dates,Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragments = new HashMap<>();

        for(int i = 0; i < Consts.DAY_COUNT;i++){
            CalendarFragment fragment = new CalendarFragment(order,from,dates[i]);
            mFragments.put(i, fragment);
        }
    }

    @Override
    public BaseFragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getItemPosition(Object object) {
        return FragmentPagerAdapter.POSITION_NONE;
    }


    @Override
    public int getCount() {
        return Consts.DAY_COUNT;
    }

    public Fragment getFragment(int index) {
        BaseFragment fragment = mFragments.get(index);
        if (fragment != null) {
            return fragment;
        }
        return null;
    }

    public void changeFragment(int position, BaseFragment fragment) {
        if (position >= getCount() && fragment == null) {
            return;
        }
        mFragments.put(position, fragment);
        notifyDataSetChanged();
    }

}
