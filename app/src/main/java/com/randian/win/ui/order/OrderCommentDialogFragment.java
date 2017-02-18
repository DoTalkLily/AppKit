package com.randian.win.ui.order;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.ErrorCode;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-8-24.
 */
public class OrderCommentDialogFragment extends DialogFragment implements View.OnClickListener, TextWatcher {

    @InjectView(R.id.close_dialog_btn)
    TextView mCloseDialogBtn;
    @InjectView(R.id.star_1)
    ImageView mStar1;
    @InjectView(R.id.star_2)
    ImageView mStar2;
    @InjectView(R.id.star_3)
    ImageView mStar3;
    @InjectView(R.id.star_4)
    ImageView mStar4;
    @InjectView(R.id.star_5)
    ImageView mStar5;
    @InjectView(R.id.comment_content)
    EditText mCommentContent;
    @InjectView(R.id.comment_btn)
    TextView mCommentBtn;
    @InjectView(R.id.left_word_count)
    TextView mLeftWordCount;
    @InjectView(R.id.comment_tips)
    TextView mCommentTips;

    private ImageView[] mStars;
    private boolean isStarChosen;
    private int mStarChosenCount;
    private String mOrderNo;
    private int mOrderIndex;
    private static final int STAR_COUNT = 5;
    private RequestQueue mQueue;
    private static OrderListAdapter mOrderListAdapter;

    public static OrderCommentDialogFragment newInstance(String orderNo,int orderIndex,OrderListAdapter orderListAdapter){
        Bundle bundle = new Bundle();
        bundle.putString("orderNo", orderNo);
        bundle.putInt("orderIndex", orderIndex);
        OrderCommentDialogFragment dialogFragment =  new OrderCommentDialogFragment();
        dialogFragment.setArguments(bundle);
        mOrderListAdapter = orderListAdapter;
        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_comment_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.inject(this, view);
        init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;

        int dialogWidth = 680;// specify a value here
        int dialogHeight = 810;// specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    public void onClick(View view) {
        if (null == view) {
            return;
        }

        switch (view.getId()) {
            case R.id.close_dialog_btn:
                dismiss();
                break;
            case R.id.star_1:
                resetStarColor(1);
                setCommentEnable();
                mCommentContent.setHint(getString(R.string.negative_comment));
                break;
            case R.id.star_2:
                resetStarColor(2);
                setCommentEnable();
                mCommentContent.setHint(getString(R.string.moderate_comment));
                break;
            case R.id.star_3:
                resetStarColor(3);
                setCommentEnable();
                mCommentContent.setHint(getString(R.string.moderate_comment));
                break;
            case R.id.star_4:
                resetStarColor(4);
                setCommentEnable();
                mCommentContent.setHint(getString(R.string.positive_comment));
                break;
            case R.id.star_5:
                resetStarColor(5);
                setCommentEnable();
                mCommentContent.setHint(getString(R.string.positive_comment));
                break;
            case R.id.comment_btn:
                submitComment();
                break;
        }
    }

    private void resetStarColor(int index){
        mStarChosenCount = index;
        for(int i = 0;i<STAR_COUNT;i++){
            if(i<index){
                mStars[i].setImageResource(R.drawable.star_yellow);
            }else{
                mStars[i].setImageResource(R.drawable.star_gray);
            }
        }
        mCommentTips.setText(getString(R.string.order_comment_tips));
        mCommentTips.setTextColor(getResources().getColor(R.color.black));
        isStarChosen = true;
    }

    private void init() {
        mOrderNo = getArguments().getString("orderNo");
        mOrderIndex = getArguments().getInt("orderIndex");

        if (mStars == null) {
            mStars = new ImageView[STAR_COUNT];
        }
        if (mQueue == null) {
            mQueue = RanDianApplication.getApp().getVolleyQueue();
        }

        mStars[0] = mStar1;
        mStars[1] = mStar2;
        mStars[2] = mStar3;
        mStars[3] = mStar4;
        mStars[4] = mStar5;

        for (int i = 0; i < STAR_COUNT; i++) {
            mStars[i].setOnClickListener(this);
        }

        mCommentBtn.setOnClickListener(this);
        mCloseDialogBtn.setOnClickListener(this);
        mCommentContent.addTextChangedListener(this);
    }

    private void submitComment() {
        //如果没选星星 提示
        if(!isStarChosen){
            mCommentTips.setText(getString(R.string.star_not_chosen));
            mCommentTips.setTextColor(getResources().getColor(R.color.orange));
            return;
        }

        if (mCommentBtn.isEnabled()) {
            if(Integer.valueOf(mLeftWordCount.getText().toString()) >= 0){
                setCommentDisable();
//mock                startSendComment(mCommentContent.getText().toString());//发送评论
                mOrderListAdapter.updateOrderState(mOrderIndex,812);
                dismiss();
                return;
            }else{
                setCommentDisable();
            }
        }
    }

    private void startSendComment(String content) {
        if(TextUtils.isEmpty(content)){
            content = mCommentContent.getHint().toString();
        }
        mCommentBtn.setEnabled(false);
        mCommentBtn.setText(R.string.waiting);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result.contains(Consts.ERROR_CODE)) {
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result, type);
                    Toaster.showShort(getActivity(), error.getError());
                    setCommentEnable();
                    return;
                }

                Toaster.showShort(getActivity(), getString(R.string.submit_comment_success));
                try {
                    JSONObject obj = new JSONObject(result);
                    mOrderListAdapter.updateOrderState(mOrderIndex,obj.getLong("id"));
                    dismiss();
                }catch(JSONException e){
                    LogUtils.e(getTag(),getString(R.string.error_server));
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toaster.showShort(getActivity(), ErrorHandler.getErrorMessage(volleyError));
            }
        };

        try {
            BaseRequest request = HttpClient.sendComment(listener, errorListener, mOrderNo, mStarChosenCount,content);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setCommentEnable() {
        mCommentBtn.setEnabled(true);
        mCommentBtn.setBackgroundColor(getResources().getColor(R.color.orange));
        mCommentBtn.setTextColor(getResources().getColor(R.color.white));
    }

    private void setCommentDisable() {
        mCommentBtn.setEnabled(false);
        mCommentBtn.setBackgroundColor(getResources().getColor(R.color.f4f4f4));
        mCommentBtn.setTextColor(getResources().getColor(R.color.feedback));
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        int count = mCommentContent.getText().toString().trim().length();

        if(count <= Consts.COMMENT_WORD_COUNT){
            setCommentEnable();
        }else{
            setCommentDisable();
        }

        mLeftWordCount.setText(String.valueOf(Consts.COMMENT_WORD_COUNT - count));
    }
}
