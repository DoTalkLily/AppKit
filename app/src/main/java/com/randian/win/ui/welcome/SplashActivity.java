package com.randian.win.ui.welcome;

import android.os.Bundle;
import android.os.Handler;

import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.support.AccountController;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.UIUtils;

/**
 * Created by a42 on 14-4-7.
 */
public class SplashActivity extends BaseActivity {

    private Handler mHandler;
    private static final int SPLASH_TIME = 1000;
    public static enum USER_STATUS {
        NOT_LOGIN,
        LOGIN,
        BANNED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mHandler = new Handler();
        mHandler.postDelayed(mShowHomeRunnable, SPLASH_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private Runnable mShowHomeRunnable = new Runnable() {
        @Override
        public void run() {
            showActivity(getUserStatus());
        }
    };

    private USER_STATUS getUserStatus() {
        AccountController accountController = RanDianApplication.getApp().getAccountController();
        if (!accountController.isLogin()) {
            return USER_STATUS.NOT_LOGIN;
        }

        return USER_STATUS.LOGIN;
    }

    private void showActivity(USER_STATUS status) {
        if (status == USER_STATUS.BANNED) {
            //todo
        } else if (status == USER_STATUS.LOGIN) {
            UIUtils.startHomeActivity(SplashActivity.this);
        } else if (status == USER_STATUS.NOT_LOGIN) {
            UIUtils.startIndicatorActivity(SplashActivity.this);
        }
        finish();
    }

}
