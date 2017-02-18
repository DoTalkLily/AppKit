package com.randian.win.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.ErrorCode;
import com.randian.win.model.Order;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.ListBaseFragment;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/5/31.
 */
public class OrderListFragment extends ListBaseFragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.order_list)
    PullToRefreshListView mOrderListView;
    @InjectView(R.id.login_btn)
    TextView mLoginBtn;

    private List<Order> orders;
    private OrderListAdapter mAdapter;
    private final String TAG = OrderListFragment.this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_list, container, false);
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
        orders = new ArrayList<>();
        mAdapter = new OrderListAdapter(getActivity(), orders);

        mOrderListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                startGetRemoteListTask(false);
            }
        });

        mOrderListView.setAdapter(mAdapter);
        mOrderListView.setOnScrollListener(this);
        mOrderListView.setOnItemClickListener(this);
        mOrderListView.setMode(PullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mListView = mOrderListView.getRefreshableView();
        initHeaderWidthPic(R.drawable.order_list_header, R.color.white);
        initFooter();//init footer view
        //TODO 判断是否登录
        boolean isLogin = false;
        //如果没登录 跳转到登录页面
        if(!isLogin) {
            mLoginBtn.setVisibility(View.VISIBLE);
            mLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIUtils.startLoginActivity(getActivity());
                }
            });
        }else {
            startGetRemoteListTask(false);
        }
        List<Order> orderList = getMockOrderList();
        //更新数据
        mAdapter.setData(orderList);
    }

    protected void startGetRemoteListTask(final boolean needMore) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                onLoadComplete();

                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(getActivity(), error.getError());
                    onLoadComplete();
                    return;
                }

                List<Order> orderList = null;
                try {
                    Type type = new TypeToken<List<Order>>() {
                    }.getType();
                    orderList = RanDianApplication.getGson().fromJson(result.trim(), type);
                    onLoadComplete();
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }

                if (orderList == null || orderList.isEmpty()) {
                    updateFootView(false);
                    return;
                }

                if (needMore) {
                    mAdapter.addData(orderList);
                } else {
                    mAdapter.setData(orderList);
                }

                hasMore = true;
                if (orderList.size() < Consts.PAGE_SIZE) {
                    hasMore = false;
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
            BaseRequest request = HttpClient.getOrderList(listener, errorListener, orders.size());
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onLoadComplete() {
        mOrderListView.onRefreshComplete();
    }

    private List<Order> getMockOrderList() {
        String content = readFile();
        Type type = new TypeToken<List<Order>>() {
        }.getType();
        List<Order> orderList = RanDianApplication.getGson().fromJson(content.trim(), type);
        return orderList;
    }

    public String readFile() {
        InputStream myFile;
        myFile = getResources().openRawResource(R.raw.order_list);//cet4为一个TXT文件
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


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        int checkedPosition = position - mListView.getHeaderViewsCount();
        if (checkedPosition >= 0) {
            Order order = mAdapter.getItem(checkedPosition);
            if (order == null) {
                LogUtils.e(TAG, "order is null,positon:" + position);
                return;
            }
            UIUtils.startOrderDetailActivity(getActivity(),order.getOrder_no());
        }
    }

}
