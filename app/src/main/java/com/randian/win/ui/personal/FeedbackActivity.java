package com.randian.win.ui.personal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.ErrorCode;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-8-8.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    @InjectView(R.id.feedback_content)
    EditText mFeedbackContent;
    @InjectView(R.id.feedback_btn)
    TextView mFeedbackBtn;

    private int VALID_TEXT_COLOR;
    private int VALID_BACKGROUND;
    private int INVALID_BACKGROUND;
    private int INVALID_TEXT_COLOR;
    private String TAG = FeedbackActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        VALID_TEXT_COLOR = getResources().getColor(R.color.white);
        VALID_BACKGROUND = getResources().getColor(R.color.orange);
        INVALID_BACKGROUND = getResources().getColor(R.color.f4f4f4);
        INVALID_TEXT_COLOR = getResources().getColor(R.color.feedback);

        mFeedbackContent.addTextChangedListener(this);
        mFeedbackBtn.setOnClickListener(this);
    }

    private void startSendFeedback(String content) {
        LogUtils.d(TAG, "start to send feedback");
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Utils.setBtnEnable(mFeedbackBtn, R.string.feedback_btn, getResources());

                if (result.contains(Consts.ERROR_CODE)) {
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result, type);
                    Toaster.showShort(FeedbackActivity.this, error.getError());
                    return;
                }

                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.get("id") != null) {
                        Toaster.showShort(FeedbackActivity.this, getResources().getString(R.string.feedback_thanks));
                    }
                } catch (JSONException e) {
                    Toaster.showShort(FeedbackActivity.this, getResources().getString(R.string.error_server));
                    LogUtils.e(TAG, e);
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(FeedbackActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.sendFeedback(listener, errorListener, content);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        String content = mFeedbackContent.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            Utils.setBtnDisable(mFeedbackBtn,R.string.submitting,getResources());
            startSendFeedback(content);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (TextUtils.isEmpty(mFeedbackContent.getText().toString().trim())) {
            mFeedbackBtn.setTextColor(INVALID_TEXT_COLOR);
            mFeedbackBtn.setBackgroundColor(INVALID_BACKGROUND);
        } else {
            mFeedbackBtn.setTextColor(VALID_TEXT_COLOR);
            mFeedbackBtn.setBackgroundColor(VALID_BACKGROUND);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
