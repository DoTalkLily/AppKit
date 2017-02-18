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
public class SportListAdapter extends BaseAdapter {
    private Context mContext;
    private int mLayout;
    private List<Sport> mSports;
    private String mFrom;//来自哪个页面，项目列表——sport 还是教练详情的项目列表——coach

    public SportListAdapter(Context context, int layout, String from) {
        mFrom = from;
        mLayout = layout;
        mContext = context;
        mSports = new ArrayList<>();
    }

    public void setData(List<Sport> data) {
        if (data == null) {
            return;
        }
        mSports = data;
        notifyDataSetChanged();
    }

    public void addData(List<Sport> data) {
        if (data != null && !data.isEmpty()) {
            mSports.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mSports == null ? 0:mSports.size();
    }

    @Override
    public Sport getItem(int i) {
        if (mSports == null || mSports.size() <= i) {
            return null;
        }
        return mSports.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Sport sport = mSports.get(position);
        holder.mTitle.setText(sport.getName());
        holder.mDuration.setText(mContext.getString(R.string.duration)+String.valueOf(sport.getDuration())+mContext.getString(R.string.minute));

        if ("sport".equals(mFrom)) {
            holder.mPrice.setText("￥"+String.valueOf(sport.getPrice())+"起");
        } else {
            holder.mPrice.setText("￥"+String.valueOf(sport.getCoach_price())+"元");
        }

        String suggest = getSuggest(sport.getMax_user_num(), sport.getMin_user_num());
        holder.mSuggest.setText(mContext.getString(R.string.suggest_people) + suggest + mContext.getString(R.string.ren));
        sport.setSuggest(suggest);
        holder.mImage.setImageURI(Uri.parse(sport.getHead_image_url()));
        if (sport.getOriginal_price() > 0) {
            holder.mOriginPrice.setText(mContext.getString(R.string.origin_price)+String.valueOf(sport.getOriginal_price()));
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

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private String getSuggest(int maxNum, int minNum) {
        if (minNum > 0 && maxNum > 0) {
            return minNum + " - " + maxNum;
        }
        if (minNum > 0) {
            return "大于" + minNum;
        }
        if (maxNum > 0) {
            return " 小于" + maxNum;
        }
        return "";
    }
}
