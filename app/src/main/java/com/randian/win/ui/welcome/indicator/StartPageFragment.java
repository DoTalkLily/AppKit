package com.randian.win.ui.welcome.indicator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.randian.win.R;
import com.randian.win.ui.base.BaseFragment;
import com.randian.win.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * User: lily
 */
public class StartPageFragment extends BaseFragment implements View.OnClickListener {

    @InjectView(R.id.login_button)
    Button mStartAppBtn;

    public static StartPageFragment newInstance() {
        StartPageFragment fragment = new StartPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_image, container, false);
        ButterKnife.inject(this,view);
        mStartAppBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        UIUtils.startLoginActivity(getActivity());
        getActivity().finish();
    }
}
