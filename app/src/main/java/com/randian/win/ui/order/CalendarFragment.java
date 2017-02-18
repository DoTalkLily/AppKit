package com.randian.win.ui.order;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.ErrorCode;
import com.randian.win.model.Order;
import com.randian.win.model.Time;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.ui.base.BaseFragment;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-7-26.
 */
public class CalendarFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.gv_calendar)
    GridView mGridView;

    private int mFrom;
    private String mDate;
    private Order mOrder;
    private List<Time> mTimeList;
    private CalendarAdapter calendarAdapter;
    private final String TAG = CalendarFragment.this.getClass().getSimpleName();

    public CalendarFragment(){
        super();
    }

    @SuppressLint("ValidFragment")
    public CalendarFragment(Order order,int from,String date){
        if((from != Consts.FROM_COACH && from!= Consts.FROM_SPORT)||order == null || TextUtils.isEmpty(date)){
            return;
        }

        this.mFrom = from;
        this.mDate = date;
        this.mOrder = order;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_calendar, container, false);
        ButterKnife.inject(this, view);
        initData();
        return view;
    }

    private void initData() {
        if ((mFrom != Consts.FROM_COACH && mFrom!= Consts.FROM_SPORT) || mOrder == null || TextUtils.isEmpty(mDate)) {
            return;
        }

        mGridView.setOnItemClickListener(this);

        if(mTimeList == null || mTimeList.isEmpty()) {
            startGetRemoteTimeListTask();
        } else{
            mGridView.setAdapter(calendarAdapter);
        }
    }

    private void startGetRemoteTimeListTask() {
        LogUtils.d(TAG, "start to sync time list from remote");
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if(result.contains(Consts.ERROR_CODE)){
                    Type type = new TypeToken<ErrorCode>() {
                    }.getType();
                    ErrorCode error = RanDianApplication.getGson().fromJson(result,type);
                    Toaster.showShort(getActivity(),error.getError());
                    return;
                }

                try {
                    Type type = new TypeToken<List<Time>>() {
                    }.getType();
                    mTimeList = RanDianApplication.getGson().fromJson(result.trim(), type);
                    calendarAdapter = new CalendarAdapter(getActivity(), mTimeList,R.layout.calendar_item);
                    mGridView.setAdapter(calendarAdapter);
                } catch (Exception e) {
                    LogUtils.d(TAG, e.toString());
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ErrorHandler.handleException(getActivity(), volleyError);
            }
        };

        try {
            BaseRequest request;

            if (Consts.FROM_SPORT == mFrom) {
                request = HttpClient.getTimeListOfSport(listener, errorListener, mOrder.getSport_id(), mDate, mOrder.getSport_duration());
            } else {
                request = HttpClient.getTimeListOfCoach(listener, errorListener, mOrder.getCoach_id(), mDate, mOrder.getSport_duration());
            }
            request.setTag(this);
            mQueue.add(request);
            mQueue.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView tv = (TextView) view;
        if(getResources().getColor(R.color.white) == tv.getCurrentTextColor()&& !TextUtils.isEmpty(tv.getText())){
                UIUtils.startChooseCoachOfOrderActivity(getActivity(), mOrder, mDate,mFrom,tv.getText().toString());
        }
    }

}
