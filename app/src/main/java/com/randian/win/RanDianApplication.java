package com.randian.win;

import android.app.Application;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.randian.win.support.AccountController;
import com.randian.win.utils.LogUtils;

/**
 * Created by li.lli on 2015/6/9.
 */
public class RanDianApplication extends Application {
    private static final String TAG = RanDianApplication.class.getSimpleName();
    private static RanDianApplication mApp;
    private RequestQueue mRequestQueue;
    private static Gson mGson;

    private AccountController mAccountController;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initLogUtils();
        initImageLoader();
        initNetwork();
        initGson();
        initController();
    }

    public static RanDianApplication getApp() {
        return mApp;
    }

    private void initLogUtils() {
        if (BuildConfig.DEBUG) {
            LogUtils.e(TAG, "Device Info: model=" + Build.MODEL);
            LogUtils.e(TAG, "Device Info: brand=" + Build.BRAND);
            LogUtils.e(TAG, "Device Info: device=" + Build.DEVICE);
            LogUtils.e(TAG, "Device Info: board=" + Build.BOARD);
            LogUtils.e(TAG, "Device Info: product=" + Build.PRODUCT);
            LogUtils.e(TAG, "Device Info: manufacturer=" + Build.MANUFACTURER);
            LogUtils.e(TAG, "Device Info: fingerprint=" + Build.FINGERPRINT);
            LogUtils.enable();
        } else {
            LogUtils.disable();
        }
    }

    private void initImageLoader() {
//        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
//                .threadPoolSize(5)
//                .memoryCacheSizePercentage(25)
//                .memoryCacheSize(2 * 1024 * 1024)
//                .tasksProcessingOrder(QueueProcessingType.FIFO)
//                .build();
//
//        ImageLoader.getInstance().init(configuration);

        Fresco.initialize(this);
    }

    private void initNetwork() {
        mRequestQueue = Volley.newRequestQueue(RanDianApplication.getApp());
    }

    private void initGson() {
        mGson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    public static Gson getGson() {
        if (mGson == null) {
            mGson = new GsonBuilder()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
        }
        return mGson;
    }

    public void initController(){
        mAccountController = new AccountController(getApp());
    }

    public RequestQueue getVolleyQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(RanDianApplication.getApp());
        }
        return mRequestQueue;
    }

    public AccountController getAccountController() {
        if (mAccountController == null) {
            mAccountController = new AccountController(getApp());
        }
        return mAccountController;
    }
}
