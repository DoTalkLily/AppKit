package com.randian.win.ui.personal;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.randian.win.support.SafeAsyncTask.SafeAsyncTask;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lily on 15-8-15.
 */
public class MyAgreementActivity extends BaseActivity {
    private WebView mWebView;
    private String mContent;
    private final String TAG = MyAgreementActivity.this.getClass().getSimpleName();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = new WebView(this);
        setContentView(mWebView);
        mContext = this;
        new BuildHtml().execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    private class BuildHtml extends SafeAsyncTask{
        @Override
        public Object call() throws Exception {
            buildHtml();
            return null;
        }

        @Override
        protected void onSuccess(Object o) throws Exception {
            mWebView.loadDataWithBaseURL("", mContent, "text/html", "UTF-8", "");
        }

        private void buildHtml() {
            try {
                InputStream input = getAssets().open(Consts.PROTOCAL_URL);
                int i;
                ByteArrayOutputStream temp = new ByteArrayOutputStream();
                while ((i = input.read()) != -1) {
                    temp.write(i);
                }
                mContent = temp.toString();
            } catch (IOException e) {
                LogUtils.w(TAG, e.toString());
            }


        }
    }

}
