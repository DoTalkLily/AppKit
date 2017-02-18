package com.randian.win.ui.personal;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randian.win.R;
import com.randian.win.ui.HomeActivity;
import com.randian.win.ui.base.BaseFragment;
import com.randian.win.ui.base.ListBaseFragment;
import com.randian.win.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by li.lli on 2015/5/31.
 */
public class PersonalFragment extends ListBaseFragment implements View.OnClickListener{

    @InjectView(R.id.my_order)
    View myOrder;
    @InjectView(R.id.my_coupon)
    View myCoupon;
    @InjectView(R.id.about_us)
    View aboutUs;
    @InjectView(R.id.feedback)
    View feedback;

    private final String TAG = PersonalFragment.this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal, container, false);
        ButterKnife.inject(this, view);
        initView();
        initMenu();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    private void initView(){
        myOrder.setOnClickListener(this);
        myCoupon.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        feedback.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == null){
            return;
        }

        switch (view.getId()){
            case R.id.my_order:
                if(getActivity() != null){
                    ((HomeActivity)getActivity()).setIconSelected(HomeActivity.ORDER_ICON);
                }
                break;
            case R.id.my_coupon:
                UIUtils.startMyCouponActivity(getActivity());
                break;
            case R.id.about_us:
                UIUtils.startAboutUsActivity(getActivity());
                break;
            case R.id.feedback:
                UIUtils.startFeedbackActivity(getActivity());
                break;

        }
    }
}
