package com.randian.win.ui.sport;

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
import com.randian.win.model.Sport;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.ListBaseFragment;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.MockUtils;
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
public class SportListFragment extends ListBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    @InjectView(R.id.sport_list)
    PullToRefreshListView mSportListView;

    //TODO 重构以适应多种类型
    private boolean gymHasMore = true;
    private boolean yogaHasMore = true;
    private String mCurrentTab = "gym";
    private List<Sport> mGymSports;
    private List<Sport> mYogaSports;
    private SportListAdapter mAdapter;
    private final String TAG = SportListFragment.this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sport_list, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView() {
        initData();//初始化数据
        initHeader();//初始化header
        initFooter();//初始化footer
        initTabEvent();//tab 切换事件绑定
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

    private void initData() {
        mGymSports = new ArrayList<>();
        mYogaSports = new ArrayList<>();
        mAdapter = new SportListAdapter(getActivity(), R.layout.sport_list_item, "sport");
        mSportListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                startGetRemoteListTask(false);
            }
        });

        mSportListView.setAdapter(mAdapter);
        mSportListView.setOnScrollListener(this);
        mSportListView.setOnItemClickListener(this);
        mSportListView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mListView = mSportListView.getRefreshableView();
        startGetRemoteListTask(false);
    }

    private void initTabEvent() {
        switchTab("gym".equals(mCurrentTab)?Consts.GYM_CATEGORY:Consts.YOGA_CATEGORY);//从第三个tab切换回来的时候更新当前tab状态
        mGymTabBtn.setOnClickListener(this);
        mYogaTabBtn.setOnClickListener(this);
    }

    protected void startGetRemoteListTask(final boolean needMore) {
        onLoadComplete();

        List<Sport> sportList = MockUtils.getSportList(mCurrentTab);
        if("gym".equals(mCurrentTab)){
            mGymSports.addAll(sportList);
        }else{
            mYogaSports.addAll(sportList);
        }

        if (needMore) {
            mAdapter.addData(sportList);
        } else {
            mAdapter.setData(sportList);
        }

        hasMore = true;
        if (sportList.size() < Consts.PAGE_SIZE) {
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
                List<Sport> sportList = null;
                try {
                    Type type = new TypeToken<List<Sport>>() {
                    }.getType();
                    sportList = RanDianApplication.getGson().fromJson(result.trim(), type);
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }

                if (sportList == null || sportList.isEmpty()) {
                    updateFootView(false);
                    return;
                }

                if("gym".equals(mCurrentTab)){
                    mGymSports.addAll(sportList);
                }else{
                    mYogaSports.addAll(sportList);
                }

                if (needMore) {
                    mAdapter.addData(sportList);
                } else {
                    mAdapter.setData(sportList);
                }

                hasMore = true;
                if (sportList.size() < Consts.PAGE_SIZE) {
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
                updateFootView(true);
                ErrorHandler.handleException(getActivity(), volleyError);
            }
        };

        try {
            BaseRequest request;
            if ("gym".equals(mCurrentTab)) {
                request = HttpClient.getSportList(listener, errorListener, mGymSports.size(), Consts.GYM_CATEGORY);
            } else {
                request = HttpClient.getSportList(listener, errorListener, mYogaSports.size(), Consts.YOGA_CATEGORY);
            }
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
         **/
    }

    private void onLoadComplete() {
        mSportListView.onRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        int checkedPosition = position - mListView.getHeaderViewsCount();
        if (checkedPosition >= 0) {
            Sport sport = mAdapter.getItem(checkedPosition);
            if (sport == null) {
                LogUtils.e(TAG, "sport is null,positon:" + position);
                return;
            }
            UIUtils.startSportDetailActivity(getActivity(), sport);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }

        if (R.id.category_js == view.getId()) {
            mCurrentTab = "gym";
            mAdapter.setData(mGymSports);
            switchTab(Consts.GYM_CATEGORY);
            updateFootView(gymHasMore);
        } else if (R.id.category_yoga == view.getId()) {
            mCurrentTab = "yoga";
            mAdapter.setData(mYogaSports);
            switchTab(Consts.YOGA_CATEGORY);

            if(mYogaSports.size() == 0){
                startGetRemoteListTask(false);
            }

            updateFootView(yogaHasMore);
        }
    }

}
