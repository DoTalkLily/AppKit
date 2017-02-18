package com.randian.win.support;

import android.content.Context;

import com.randian.win.utils.UIUtils;

/**
 * Created by lily on 15-7-18.
 */
public class AccountController {

    private Context mContext;
    private Session mSession;

    private static String TAG = AccountController.class.getName();

    public AccountController(Context context) {
        this.mContext = context;
        init();
    }

    public void init() {
        mSession = Session.get(mContext);
    }

    public boolean isLogin() {
        return mSession!=null && !"0".equals(mSession.getUserId());
    }

    public String getCurrentUserId() {
        return mSession.getUserId();
    }

    public void logout() {
        mSession.delete(mContext);
        UIUtils.startSplashActivity(mContext);
    }

}
