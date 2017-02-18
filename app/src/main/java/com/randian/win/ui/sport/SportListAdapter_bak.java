package com.randian.win.ui.sport;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.randian.win.R;
import com.randian.win.model.Sport;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/6/8.
 */
public class SportListAdapter_bak extends BaseAdapter{
    private Context mContext;
    private List<Sport> mData;
    private int mLayout;
    private String mFrom;//来自哪个页面，项目列表——sport 还是教练详情的项目列表——coach

    public SportListAdapter_bak(Context context, List<Sport> sports, int layout, String from){
        mContext = context;
        mLayout = layout;
        mFrom = from;
        mData = new ArrayList<>();
        if (sports != null) {
            mData.addAll(sports);
        }
    }

    public void setData(List<Sport> data) {
        if (data == null) {
            return;
        }
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<Sport> data) {
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
    public Sport getItem(int i) {
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

        Sport sport = mData.get(position);
        holder.mTitle.setText(sport.getName());
        holder.mDuration.setText("时间:" + sport.getDuration()+"分钟");

        if("sport".equals(mFrom)) {
            holder.mPrice.setText("¥" + sport.getPrice());
        }else{
            holder.mPrice.setText("¥"+ sport.getCoach_price());
        }

        String suggest = "建议人数：" + getSuggest(sport.getMax_user_num(), sport.getMin_user_num());
        holder.mSuggest.setText(suggest);
        sport.setSuggest(suggest);
        holder.mImage.setImageURI(Uri.parse(sport.getHead_image_url()));

        if(sport.getOriginal_price() > 0){
            holder.mOriginPrice.setText("原价：¥"+String.valueOf(sport.getOriginal_price()));
            holder.mOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.avatar_image)
        SimpleDraweeView mImage;
        @InjectView(R.id.sport_title)
        TextView mTitle;
        @InjectView(R.id.suggest)
        TextView mSuggest;
        @InjectView(R.id.duration)
        TextView mDuration;
        @InjectView(R.id.cost)
        TextView mPrice;
        @InjectView(R.id.origin_cost)
        TextView mOriginPrice;
        @InjectView(R.id.btn_order)
        TextView mOrderBtn;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private String getSuggest(int maxNum,int minNum) {
        if(minNum > 0 && maxNum>0){
            return minNum+" - "+maxNum+"人";
        }
        if(minNum > 0){
            return "大于"+minNum+"人";
        }
        if(maxNum > 0 ){
            return " 小于"+maxNum+"人";
        }
        return "";
    }
}
