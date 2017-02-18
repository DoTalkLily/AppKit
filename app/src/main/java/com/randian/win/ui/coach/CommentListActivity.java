package com.randian.win.ui.coach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.Comment;
import com.randian.win.model.Coupon;
import com.randian.win.model.ErrorCode;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.ui.base.FooterView;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-8-18.
 */
public class CommentListActivity extends BaseActivity implements AbsListView.OnScrollListener{
    @InjectView(R.id.comment_star)
    ViewGroup mCommentStars;
    @InjectView(R.id.comment_score)
    TextView mCommentScore;
    @InjectView(R.id.comment_count)
    TextView mCommentCount;
    @InjectView(R.id.comment_list)
    ListView mListView;

    private boolean hasMore;
    private long mCoachId;
    private int mTotalCount;
    private int mTotalLoadedCount;
    private FooterView mFooterView;
    private CommentListAdapter mAdapter;
    private final String TAG = CommentListActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_list);
        ButterKnife.inject(this);
        initView();
        initData();
    }

    private void initView(){
        mAdapter = new CommentListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(this);
        initFooter();
    }

    private void initData(){
        hasMore = true;
        Intent intent = getIntent();
        mCoachId = intent.getLongExtra(Consts.EXTRA_PARAM_0, 0);
        if (mCoachId == 0) {
            Toaster.showShort(this, R.string.coach_not_found);
            return;
        }
        startGetRemoteCommentTask();
    }

    private void startGetRemoteCommentTask() {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(CommentListActivity.this, error.getError());
                    return;
                }
//                result = readFile();
                hasMore = true;
                Comment comment = null;
                try {
                    Type type = new TypeToken<Comment>() {
                    }.getType();
                    comment = RanDianApplication.getGson().fromJson(result.trim(), type);
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }

                if (comment == null || comment.getComments() == null) {
                    hasMore = false;
                    updateFootView(false);
                    Toaster.showShort(CommentListActivity.this,getString(R.string.no_comment));
                    return;
                }
                //设置一次
                if(mTotalLoadedCount == 0) {
                    Utils.commentStar(comment.getScore(),mCommentStars,getApplicationContext());
                    mTotalCount = comment.getComment_num();
                    mCommentCount.setText(comment.getComment_num() + "条");
                    mCommentScore.setText(String.valueOf(comment.getScore()));
                }
                mTotalLoadedCount += comment.getComments().size();
                mAdapter.addData(comment.getComments());

                if(mTotalLoadedCount ==  mTotalCount){
                    hasMore = false;
                }
                updateFootView(hasMore);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(CommentListActivity.this, volleyError);
            }
        };

        try {
            BaseRequest request = HttpClient.getCommentList(listener, errorListener, mCoachId,mTotalLoadedCount,Consts.PAGE_SIZE);
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean load = totalItemCount != mTotalCount && firstVisibleItem + visibleItemCount >= totalItemCount;
        if (load && hasMore) {
            mTotalLoadedCount = totalItemCount;
            mFooterView.showProgress();
            startGetRemoteCommentTask();
        }
    }

    private void initFooter() {
        View view = getLayoutInflater().inflate(R.layout.view_empty_footer, null);
        mFooterView = (FooterView) view.findViewById(R.id.footer);
        mListView.addFooterView(view);
    }

    private void updateFootView(boolean hasMore) {
        if (!hasMore) {
            mFooterView.showText(R.string.no_more);
            return;
        }
        mFooterView.showText(getString(R.string.get_more), new FooterView.CallBack() {
            @Override
            public void callBack() {
                mFooterView.showProgress();
                startGetRemoteCommentTask();
            }
        });
    }

    private String readFile() {
        InputStream myFile;
        myFile = getResources().openRawResource(R.raw.comment);//cet4为一个TXT文件
        BufferedReader br;

        String tmp;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(myFile, "utf-8"));//注意编码

            while ((tmp = br.readLine()) != null) {
                sb.append(tmp);
            }
            br.close();
            myFile.close();
        } catch (IOException e) {

        }
        return sb.toString();
    }
}
