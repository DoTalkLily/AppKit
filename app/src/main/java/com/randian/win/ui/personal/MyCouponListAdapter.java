package com.randian.win.ui.personal;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.randian.win.R;
import com.randian.win.model.Coupon;
import com.randian.win.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/6/8.
 */
public class MyCouponListAdapter extends BaseAdapter {
    Context mContext;
    List<Coupon> mData;
    SimpleDateFormat sdf;
    private final String TAG = MyCouponListAdapter.this.getClass().getSimpleName();


    public MyCouponListAdapter(Context context, List<Coupon> coupons) {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        mContext = context;
        mData = new ArrayList<>();
        if (coupons != null) {
            mData.addAll(coupons);
        }
    }

    public void setData(List<Coupon> data) {
        if (data == null) {
            return;
        }
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<Coupon> data) {
        if (data != null && !data.isEmpty()) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Coupon getItem(int i) {
        if (mData == null || mData.size() <= i) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.my_coupon_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Coupon coupon = mData.get(position);
        holder.mCouponFee.setText(String.valueOf(coupon.getCoupon_fee()));
        holder.mCouponDate.setText(mContext.getString(R.string.coupon_date) + coupon.getCoupon_expire_at());
        holder.mCouponTips.setText(mContext.getString(R.string.coupon_tips) + coupon.getCoupon_description());

        if(holder.mCouponState.getCurrentTextColor() == mContext.getResources().getColor(R.color.orange)){
            setCouponState(holder,mContext.getResources().getColor(R.color.b3b3b3),R.drawable.coupon_invalid);
        }
        if (TextUtils.equals(mContext.getString(R.string.coupon_available), coupon.getCoupon_state_text())) {//已领取，改变成橙色
            setCouponState(holder,mContext.getResources().getColor(R.color.orange),R.drawable.coupon_valid);
            holder.mCouponState.setText(mContext.getString(R.string.coupon_go_use));
            holder.mCouponLeftDay.setText(mContext.getString(R.string.coupon_left_date) + TimeUtils.getDateLength(coupon.getCoupon_expire_at()) + mContext.getString(R.string.day));
        }else if(TextUtils.equals(mContext.getString(R.string.coupon_outdate),coupon.getCoupon_state_text())){//已过期
            holder.mCouponState.setText(coupon.getCoupon_state_text());
            holder.mCouponState.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mCouponLeftDay.setText(mContext.getString(R.string.coupon_outdate_tips));
        }else{
            holder.mCouponState.setText(coupon.getCoupon_state_text());
            holder.mCouponState.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mCouponLeftDay.setText(mContext.getString(R.string.coupon_used));
        }

        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.coupon_item)
        View mView;
        @InjectView(R.id.coupon_fee)
        TextView mCouponFee;
        @InjectView(R.id.coupon_tips)
        TextView mCouponTips;
        @InjectView(R.id.coupon_date)
        TextView mCouponDate;
        @InjectView(R.id.coupon_day_left)
        TextView mCouponLeftDay;
        @InjectView(R.id.coupon_state)
        TextView mCouponState;
        @InjectView(R.id.yuan)
        TextView mYuan;
        @InjectView(R.id.coupon_title)
        TextView mCouponTitle;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private void setCouponState(ViewHolder holder,int color,int background){
        holder.mView.setBackgroundResource(background);
        holder.mYuan.setTextColor(color);
        holder.mCouponFee.setTextColor(color);
        holder.mCouponDate.setTextColor(color);
        holder.mCouponTips.setTextColor(color);
        holder.mCouponState.setTextColor(color);
        holder.mCouponTitle.setTextColor(color);
        holder.mCouponLeftDay.setTextColor(color);
        holder.mCouponState.getPaint().setFlags(0);
    }
}
