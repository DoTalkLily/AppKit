package com.randian.win.ui.coupon;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.randian.win.R;
import com.randian.win.model.Coupon;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/6/8.
 */
public class CouponListAdapter extends BaseAdapter {
    Context mContext;
    List<Coupon> mData;
    SimpleDateFormat sdf;
    private final String TAG = CouponListAdapter.this.getClass().getSimpleName();


    public CouponListAdapter(Context context, List<Coupon> coupons) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.coupon_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Coupon coupon = mData.get(position);
        if (TextUtils.equals(mContext.getString(R.string.coupon_available), coupon.getCoupon_state_text())) {
            holder.mCouponFee.setText(String.valueOf(coupon.getCoupon_fee()));
            holder.mCouponDate.setText(mContext.getString(R.string.coupon_date) + coupon.getCoupon_expire_at());
            holder.mCouponTips.setText(mContext.getString(R.string.coupon_tips) + coupon.getCoupon_description());
            holder.mCouponLeftDay.setText(mContext.getString(R.string.coupon_left_date) + TimeUtils.getDateLength(coupon.getCoupon_expire_at()) + mContext.getString(R.string.day));
        }

        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.coupon_fee)
        TextView mCouponFee;
        @InjectView(R.id.coupon_tips)
        TextView mCouponTips;
        @InjectView(R.id.coupon_date)
        TextView mCouponDate;
        @InjectView(R.id.coupon_day_left)
        TextView mCouponLeftDay;
        @InjectView(R.id.coupon_btn)
        ImageView mChooseBtn;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
