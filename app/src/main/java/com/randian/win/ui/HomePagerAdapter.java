package com.randian.win.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randian.win.ui.base.BaseFragment;
import com.randian.win.ui.coach.CoachListFragment;
import com.randian.win.ui.personal.PersonalFragment;
import com.randian.win.ui.order.OrderListFragment;
import com.randian.win.ui.sport.SportListFragment;

import java.util.HashMap;


public class HomePagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private HashMap<Integer, BaseFragment> mFragments;

    public HomePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragments = new HashMap<>();
        mFragments.put(0, new SportListFragment());
        mFragments.put(1, new CoachListFragment());
        mFragments.put(2, new OrderListFragment());
        mFragments.put(3, new PersonalFragment());
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
        return 4;
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
