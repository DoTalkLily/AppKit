package com.randian.win.ui.order;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.randian.win.R;
import com.randian.win.RanDianApplication;
import com.randian.win.model.Coach;
import com.randian.win.model.ErrorCode;
import com.randian.win.model.Order;
import com.randian.win.model.Sport;
import com.randian.win.support.network.BaseRequest;
import com.randian.win.utils.Consts;
import com.randian.win.utils.ErrorHandler;
import com.randian.win.utils.HttpClient;
import com.randian.win.utils.LogUtils;
import com.randian.win.utils.Toaster;
import com.randian.win.utils.UIUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/6/8.
 */
public class OrderListAdapter extends BaseAdapter{
    private Activity mActivity;
    private List<Order> mData;
    private RequestQueue mQueue;
    private final String TAG = OrderListAdapter.this.getClass().getSimpleName();

    public OrderListAdapter(Activity activity,List<Order> orders){
        mActivity = activity;
        mData = orders;
    }


    public void setData(List<Order> data) {
        if (data == null) {
            return;
        }
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void updateOrderState(int index,long commentId){
        if(index< 0 || index>=mData.size()){
            return;
        }
        Order order = mData.get(index);
        if(order != null && "已完成".equals(order.getState()) && order.getComment_id() == 0){
            order.setComment_id(commentId);
            notifyDataSetChanged();
        }
    }

    public void addData(List<Order> data) {
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
    public Order getItem(int i) {
        if(mData ==  null || mData.size() <= i){
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.order_list_item, parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Order order = mData.get(position);
        holder.mImage.setImageURI(Uri.parse(order.getCoach_img_url()));

        holder.mName.setText(order.getCoach_name());
        holder.mCategory.setText(order.getCategory_name());
        holder.mOrderPayFee.setText("¥" + order.getOrder_pay_fee());
        holder.mStartTime.setText(order.getSport_start_time());
        holder.mOrderState.setText(order.getState());
        holder.mClassInfo.setText(order.getSport_name() + " * " + order.getSport_order_num());
        //reset button state
        holder.mOrderAgainBtn.setTextColor(mActivity.getResources().getColor(R.color.feedback));
        holder.mOrderAgainBtn.setBackgroundResource(R.drawable.order_btn_shape);

        if("等待付款".equals(order.getState()) || "已创建".equals(order.getState())){
            holder.mOrderAgainBtn.setText(mActivity.getString(R.string.click_to_pay));
            holder.mOrderAgainBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    UIUtils.startOrderPayActivity(mActivity, order.getOrder_no());
                }
            });
        }else if("已完成".equals(order.getState()) && order.getComment_id() == 0){
            final int index = position;
            holder.mOrderAgainBtn.setText(mActivity.getString(R.string.comment_now));
            holder.mOrderState.setText(mActivity.getString(R.string.to_be_comment));
            holder.mOrderAgainBtn.setTextColor(mActivity.getResources().getColor(R.color.orange));
            holder.mOrderAgainBtn.setBackgroundResource(R.drawable.order_btn_shape_comment);
            holder.mOrderAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { //如果是已完成但是没评价 弹出评价
                    OrderCommentDialogFragment dialogFragment = OrderCommentDialogFragment.newInstance(order.getOrder_no(),index,OrderListAdapter.this);
                    dialogFragment.show(mActivity.getFragmentManager(),"dialog");
                }
            });
        }else{
            holder.mOrderAgainBtn.setText(mActivity.getString(R.string.order_again));
            holder.mOrderAgainBtn.setOnClickListener(new View.OnClickListener() { //跳转到下单第一步
                @Override
                public void onClick(View view) {
                     reOrder(order.getOrder_no());
//                    mock();
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.avatar_image)
        SimpleDraweeView mImage;
        @InjectView(R.id.name)
        TextView mName;
        @InjectView(R.id.category)
        TextView mCategory;
        @InjectView(R.id.class_info)
        TextView mClassInfo;
        @InjectView(R.id.start_time)
        TextView mStartTime;
        @InjectView(R.id.order_state)
        TextView mOrderState;
        @InjectView(R.id.order_pay_fee)
        TextView mOrderPayFee;
        @InjectView(R.id.btn_order_again)
        TextView mOrderAgainBtn;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private void reOrder(String orderNo){
            //获取当前订单状态
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String result) {
                    if (result.contains(Consts.ERROR_CODE)) {
                        Type type = new TypeToken<ErrorCode>() {
                        }.getType();
                        ErrorCode error = RanDianApplication.getGson().fromJson(result, type);
                        Toaster.showShort(mActivity, error.getError());
                        return;
                    }

                    Order order = generateOrder(result);

                    if (order == null) {
                        Toaster.showShort(mActivity, mActivity.getString(R.string.order_not_found));
                        return;
                    }

                    UIUtils.startOrder1Activity(mActivity,order,Consts.FROM_COACH);
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toaster.showShort(mActivity, ErrorHandler.getErrorMessage(volleyError));
                }
            };

            if(mQueue == null){
                mQueue = RanDianApplication.getApp().getVolleyQueue();
            }
            try {
                BaseRequest request = HttpClient.reOrder(listener, errorListener, orderNo);
                request.setTag(this);
                mQueue.add(request);
                mQueue.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private void mock(){
        String result = readFile();
        Order order = generateOrder(result);
        UIUtils.startOrder1Activity(mActivity,order,Consts.FROM_COACH);
    }

    private Order generateOrder(String result){
        Order order = null;
        try{
            JSONObject jsonObject = new JSONObject(result);
            String sportStr = jsonObject.getString("sport");
            String coachStr = jsonObject.getString("coach");
            Sport sport = RanDianApplication.getGson().fromJson(sportStr,new TypeToken<Sport>() {}.getType());
            Coach coach = RanDianApplication.getGson().fromJson(coachStr,new TypeToken<Coach>() {}.getType());

            order = new Order();
            order.setCoach_id(coach.getId());
            order.setCoach_name(coach.getName());
            order.setCoach_gender(coach.getSex());
            order.setCoach_level(coach.getLevel());
            order.setCategory_name(coach.getCategories());
            order.setCoach_description(coach.getDescription());
            order.setCoach_img_url(coach.getProfile_image_url());
            order.setCoach_available_areas(coach.getAvailable_areas());

            order.setSport_id(sport.getId());
            order.setSport_name(sport.getName());
            order.setSuggest(sport.getSuggest());
            order.setOrder_pay_fee(sport.getPrice());
            order.setSport_duration(sport.getDuration());
            order.setSport_img_url(sport.getHead_image_url());
        }catch (Exception e){
            LogUtils.e(TAG,e);
        }
        return order;
    }
    private String readFile() {
        InputStream myFile;
        myFile = mActivity.getResources().openRawResource(R.raw.reorder);//cet4为一个TXT文件
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
}
