package com.randian.win.ui.coach;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.randian.win.R;
import com.randian.win.model.Coach;
import com.randian.win.utils.Consts;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/6/8.
 */
public class CoachListAdapter extends BaseAdapter{
    Context mContext;
    List<Coach> mData;

    public CoachListAdapter(Context context){
        mContext = context;
        mData = new ArrayList<>();
    }

    public void setData(List<Coach> data) {
        if (data == null) {
            return;
        }
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<Coach> data) {
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
    public Coach getItem(int i) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.coach_list_item, parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Coach coach = mData.get(position);
        holder.mName.setText(coach.getName());
        holder.mGender.setText(coach.getSex());
        holder.mCategories.setText(coach.getCategories());
        holder.mAvilableAreas.setText(coach.getAvailable_areas());
        holder.mImage.setImageURI(Uri.parse(coach.getProfile_image_url()));
        holder.mDescription.setText(Html.fromHtml(coach.getDescription()));
        holder.mOrderNum.setText(String.valueOf(coach.getOrder_num())+"单");
        holder.mImageWrapper.setImageResource(Consts.COACH_LEVEL_IMG_WRAPPER.get(coach.getLevel()));

        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.avatar_bubble)
        ImageView mImageWrapper;
        @InjectView(R.id.coach_image)
        SimpleDraweeView mImage;
        @InjectView(R.id.name)
        TextView mName;
        @InjectView(R.id.gender)
        TextView mGender;
        @InjectView(R.id.description)
        TextView mDescription;
        @InjectView(R.id.categories)
        TextView mCategories;
        @InjectView(R.id.order_num)
        TextView mOrderNum;
        @InjectView(R.id.available_area)
        TextView mAvilableAreas;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
