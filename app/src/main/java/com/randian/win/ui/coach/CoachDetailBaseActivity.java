package com.randian.win.ui.coach;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.randian.win.R;
import com.randian.win.model.Coach;
import com.randian.win.ui.base.BaseActivity;


/**
 * Created by lily on 15-7-5.
 */
public class CoachDetailBaseActivity extends BaseActivity {

    protected View mHeaderView;
    protected Coach mCoach;
    protected TextView mDescription;
    protected ImageView mDescBtn;
    protected ListView mListView;
    protected TextView mTips;
    protected TextView mAboutCoach;
    protected ViewGroup mCommentStars;
    protected TextView mCommentCount;
    protected TextView mCommentScore;
    protected View mCommentDivider;
    protected View mCommentContainer;
    protected ImageView mImageWrapper;

    protected void initHeader() {
        mHeaderView =  getLayoutInflater().inflate(R.layout.view_coach_detail_header, null);
        mDescBtn = (ImageView)mHeaderView.findViewById(R.id.desc_btn);
        mImageWrapper = (ImageView)mHeaderView.findViewById(R.id.avatar_wrapper);
        mAboutCoach = (TextView)mHeaderView.findViewById(R.id.about_coach);
        mDescription = (TextView)mHeaderView.findViewById(R.id.desc_content);
        mTips = (TextView) mHeaderView.findViewById(R.id.tips);
        mCommentContainer = mHeaderView.findViewById(R.id.comment_container);
        mCommentDivider = mHeaderView.findViewById(R.id.comment_divider);
        mCommentCount = (TextView) mHeaderView.findViewById(R.id.comment_count);
        mCommentScore = (TextView) mHeaderView.findViewById(R.id.comment_score);
        mCommentStars = (LinearLayout) mHeaderView.findViewById(R.id.comment_star);
        ((TextView)mHeaderView.findViewById(R.id.coach_title)).setText(mCoach.getName());
        ((TextView)mHeaderView.findViewById(R.id.coach_gender)).setText(mCoach.getSex());
        ((TextView)mHeaderView.findViewById(R.id.category)).setText(mCoach.getCategories());
        ((TextView)mHeaderView.findViewById(R.id.order_num)).setText(mCoach.getOrder_num() + " 单");
        ((TextView)mHeaderView.findViewById(R.id.available_area)).setText(mCoach.getAvailable_areas());
        mListView.addHeaderView(mHeaderView);
        ((SimpleDraweeView) mHeaderView.findViewById(R.id.avatar_image)).setImageURI(Uri.parse(mCoach.getProfile_image_url()));
    }

}
