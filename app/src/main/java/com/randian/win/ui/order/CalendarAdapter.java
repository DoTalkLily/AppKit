package com.randian.win.ui.order;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.randian.win.R;
import com.randian.win.model.Time;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/6/8.
 */
public class CalendarAdapter extends BaseAdapter{
    private Context mContext;
    private List<Time> mData;
    private int mLayout;
    private Resources mResource;

    public CalendarAdapter(Context context, List<Time> timeList, int layout){
        mContext = context;
        mLayout = layout;
        mData = new ArrayList<>();
        if (timeList != null) {
            mData.addAll(timeList);
        }
        mResource = mContext.getResources();
    }

    public void setData(List<Time> data) {
        if (data == null) {
            return;
        }
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<Time> data) {
        if (data != null && !data.isEmpty()) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData .size();
    }

    @Override
    public Time getItem(int i) {
        if(mData == null || mData.size() <= i){
            return null;
        }
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayout, parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Time time = mData.get(position);
        holder.mCalendar.setText(time.getTime());

        if(time.isAvailable()){
            holder.mCalendar.setBackgroundResource(R.drawable.icon_time_available);
            holder.mCalendar.setTextColor(mResource.getColor(R.color.white));
        }else{
            holder.mCalendar.setBackgroundResource(R.drawable.icon_time_unavailable);
            holder.mCalendar.setTextColor(mResource.getColor(R.color.black));
        }

        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.tv_calendar)
        TextView mCalendar;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
