package com.randian.win.ui.welcome;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.ErrorCode;
import com.randian.win.support.dialogfragment.ProgressDialogFragment;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;
import com.randian.win.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by a42 on 14-4-7.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    @InjectView(R.id.phone)
    EditText mUserPhoneET;
    @InjectView(R.id.verify_code)
    EditText mVerifyCodeET;
    @InjectView(R.id.invite_code)
    EditText mInviteCodeET;
    @InjectView(R.id.send_code)
    TextView mSendVerifyCodeTV;
    @InjectView(R.id.voice_certify)
    TextView mVoiceCertityTV;
    @InjectView(R.id.login_button)
    Button mLoginBtn;

    private long mPhone;
    private int mVerifyCode;
    private String mInviteCode;
    private boolean isInValidDuration =true;
    private final String TAG = LoginActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.inject(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView() {
        mLoginBtn.setOnClickListener(this);
        mVoiceCertityTV.setOnClickListener(this);
        mSendVerifyCodeTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == null){
            return;
        }

        switch (view.getId()){
            case R.id.send_code:
                sendVerifyCode(false);
                break;
            case R.id.voice_certify:
                sendVerifyCode(true);
                break;
            default:
                login();
        }
    }

    private void login(){
        if(isValidInput() && isInValidDuration) {
            startValidateAccountTask();
        }
    }

    private void startValidateAccountTask() {
        if(isValidInput()){
            return;
        }

        ProgressDialogFragment.show(LoginActivity.this, R.string.waiting, 0);
        mQueue = RanDianApplication.getApp().getVolleyQueue();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                ProgressDialogFragment.dismiss(LoginActivity.this);

                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(LoginActivity.this, error.getError());
                    return;
                }

                UIUtils.startHomeActivity(LoginActivity.this);
                //启动账号统计
                MobclickAgent.onProfileSignIn("RANDIAN", "user_id");//TODO 记录用户信息
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(LoginActivity.this, volleyError);
                ProgressDialogFragment.dismiss(LoginActivity.this);
            }
        };

        try {
            BaseRequest request = HttpClient.login(listener, errorListener, mPhone, mVerifyCode);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendVerifyCode(final boolean isVoice){
        if(isValidInput()){
            return;
        }
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(LoginActivity.this, error.getError());
                    return;
                }

                if(isVoice){
                    Toaster.showShort(LoginActivity.this,"已发送语音验证码，请注意接听电话");
                }else{
                    Toaster.showShort(LoginActivity.this,"已发送短信，请注意查收");
                }

                countDown();
                isInValidDuration = true;
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(LoginActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.sendVerifyCode(listener, errorListener, mPhone, isVoice);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidInput(){
        String phone = mUserPhoneET.getText().toString();
        String code = mVerifyCodeET.getText().toString();

        if (TextUtils.isEmpty(code)) {
            Toaster.showShort(LoginActivity.this, R.string.error_verify_code);
            return false;
        }

        if (!TextUtils.isDigitsOnly(code) || code.length() > 9) {
            Toaster.showShort(LoginActivity.this, R.string.error_code_format);
            return false;
        }

        mVerifyCode = Integer.parseInt(code);

        if (!Utils.checkPhoneFormat(phone)) {
            Toaster.showShort(LoginActivity.this, R.string.error_phone);
            return false;
        }

        mPhone = Long.parseLong(phone);
        return true;
    }

    private void countDown(){
        mSendVerifyCodeTV.setClickable(false);
        mVoiceCertityTV.setClickable(false);
        CountDownClock clock = new CountDownClock(60000,1000);
        clock.start();
    }


    private class CountDownClock extends CountDownTimer {

        public CountDownClock(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long left) {
            mSendVerifyCodeTV.setText(left/1000+"秒后重新获取");
        }

        @Override
        public void onFinish() {
            mSendVerifyCodeTV.setText("短信获取");
            mSendVerifyCodeTV.setClickable(true);
            mVoiceCertityTV.setClickable(true);
            isInValidDuration = false;
        }
    }

}

