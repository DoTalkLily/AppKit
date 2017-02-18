package com.randian.win.ui.welcome.indicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randian.win.R;

/**
 * User: 42
 */
public class IntroImageAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 4;
    private static final int START_PAGE_INDEX = 3;

    private final int[] IMAGES = new int[] {
            R.drawable.splash,
            R.drawable.splash,
            R.drawable.splash,
    };

    public IntroImageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if (i == START_PAGE_INDEX) {
            return StartPageFragment.newInstance();
        }
        return IntroImagePageFragment.newInstance(IMAGES[i]);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
