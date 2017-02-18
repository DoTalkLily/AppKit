package com.randian.win.ui.coach;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.Coach;
import com.randian.win.model.ErrorCode;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.ListBaseFragment;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.MockUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/5/31.
 */
public class CoachListFragment extends ListBaseFragment implements AdapterView.OnItemClickListener,View.OnClickListener{

    @InjectView(R.id.coach_list)
    PullToRefreshListView mCoachListView;

    //TODO 重构以适应多种类型
    private boolean gymHasMore = true;
    private boolean yogaHasMore = true;
    private String mCurrentTab = "gym";
    private List<Coach> mGymCoach;
    private List<Coach> mYogaCoach;
    private CoachListAdapter mAdapter;
    private final String TAG = CoachListFragment.this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coach_list, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    private void initView() {
        initData();
        initHeader();
        initFooter();//init footer view
        initTabEvent();
    }

    private void initData(){
        mGymCoach = new ArrayList<>();
        mYogaCoach = new ArrayList<>();
        mAdapter = new CoachListAdapter(getActivity());

        mCoachListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                startGetRemoteListTask(false);
            }
        });

        mCoachListView.setAdapter(mAdapter);
        mCoachListView.setOnScrollListener(this);
        mCoachListView.setOnItemClickListener(this);
        mCoachListView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mListView = mCoachListView.getRefreshableView();
        startGetRemoteListTask(false);
    }

    private void initTabEvent() {
        switchTab("gym".equals(mCurrentTab) ? Consts.GYM_CATEGORY : Consts.YOGA_CATEGORY);//从第三个tab切换回来的时候更新当前tab状态
        mGymTabBtn.setOnClickListener(this);
        mYogaTabBtn.setOnClickListener(this);
    }

    protected void startGetRemoteListTask(final boolean needMore) {
        onLoadComplete();

        List<Coach> coachList = MockUtils.getCoachList(mCurrentTab);
        if("gym".equals(mCurrentTab)){
            mGymCoach.addAll(coachList);
        }else{
            mYogaCoach.addAll(coachList);
        }

        if (needMore) {
            mAdapter.addData(coachList);
        } else {
            mAdapter.setData(coachList);
        }

        hasMore = true;
        if (coachList.size() < Consts.PAGE_SIZE) {
            hasMore = false;
            if ("gym".equals(mCurrentTab)) {
                gymHasMore = false;
            } else {
                yogaHasMore = false;
            }
        }
        /**
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                onLoadComplete();
                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(getActivity(), error.getError());
                    return;
                }

                List<Coach> coachList = null;

                try {
                    Type type = new TypeToken<List<Coach>>() {
                    }.getType();
                    coachList = RanDianApplication.getGson().fromJson(result.trim(), type);
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }
                //获得错误的数据
                if (coachList == null || coachList.isEmpty()) {
                    updateFootView(false);
                    return;
                }
                //category的结构 ［{"name":"瑜伽"},{"name":"健身"}］
                for (Coach coach : coachList) {
                    if (coach.getCategoriesList() != null) {
                        String categoryStr = "";
                        for (Coach.Category category : coach.getCategoriesList()) {
                            categoryStr += category.getName() + " ";
                        }
                        coach.setCategories(categoryStr.trim());
                    }
                }

                if("gym".equals(mCurrentTab)){
                    mGymCoach.addAll(coachList);
                }else{
                    mYogaCoach.addAll(coachList);
                }

                if (needMore) {
                    mAdapter.addData(coachList);
                } else {
                    mAdapter.setData(coachList);
                }

                hasMore = true;
                if (coachList.size() < Consts.PAGE_SIZE) {
                    hasMore = false;
                    if ("gym".equals(mCurrentTab)) {
                        gymHasMore = false;
                    } else {
                        yogaHasMore = false;
                    }
                }
                updateFootView(hasMore);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onLoadComplete();
                ErrorHandler.handleException(getActivity(), volleyError);
            }
        };

        try {
            BaseRequest request;
            if ("gym".equals(mCurrentTab)) {
                request = HttpClient.getCoachList(listener, errorListener, mGymCoach.size(), Consts.GYM_CATEGORY);
            } else {
                request = HttpClient.getCoachList(listener, errorListener, mYogaCoach.size(), Consts.YOGA_CATEGORY);
            }
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        **/
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        int checkedPosition = position - mListView.getHeaderViewsCount();
        if (checkedPosition >= 0) {
            Coach coach = mAdapter.getItem(checkedPosition);
            if (coach == null) {
                LogUtils.e(TAG, "coach is null,positon:" + position);
                return;
            }
            UIUtils.startCoachDetailActivity(getActivity(), coach);
        }
    }

    private void onLoadComplete() {
        mCoachListView.onRefreshComplete();
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }

        if (R.id.category_js == view.getId()) {
            mCurrentTab = "gym";
            mAdapter.setData(mGymCoach);
            switchTab(Consts.GYM_CATEGORY);
            updateFootView(gymHasMore);
        } else if (R.id.category_yoga == view.getId()) {
            mCurrentTab = "yoga";
            mAdapter.setData(mYogaCoach);
            switchTab(Consts.YOGA_CATEGORY);

            if(mYogaCoach.size() == 0){
                startGetRemoteListTask(false);
            }

            updateFootView(yogaHasMore);
        }
    }
}
