package com.randian.win.ui.coach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randian.win.R;
import com.randian.win.model.CommentItem;
import com.randian.win.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/6/8.
 */
public class CommentListAdapter extends BaseAdapter{
    private Context mContext;
    private List<CommentItem> mData;
    
    public CommentListAdapter(Context context){
        mContext = context;
        mData = new ArrayList<>();
    }

    public void setData(List<CommentItem> data) {
        if (data == null) {
            return;
        }
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<CommentItem> data) {
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
    public CommentItem getItem(int i) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_list_item, parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CommentItem comment = mData.get(position);
        holder.mPhone.setText(comment.getMobile());
        holder.mDate.setText(comment.getCreated_at());
        holder.commentContent.setText(comment.getContent());
        holder.viewGroup.removeAllViews();
        Utils.commentStar(comment.getStars(), holder.viewGroup, mContext);

        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.date)
        TextView mDate;
        @InjectView(R.id.phone)
        TextView mPhone;
        @InjectView(R.id.comment_star)
        ViewGroup viewGroup;
        @InjectView(R.id.comment_content)
        TextView commentContent;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
