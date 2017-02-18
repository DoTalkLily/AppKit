package com.randian.win.ui.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.randian.win.R;
import com.randian.win.ui.base.BaseActivity;
import com.randian.win.utils.UIUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lily on 15-8-15.
 */
public class AboutUsActivity extends BaseActivity implements View.OnClickListener{
    @InjectView(R.id.agreement)
    TextView mAgreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        ButterKnife.inject(this);
        mAgreement.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(null == view){
            return;
        }

        UIUtils.startAgreementActivity(this);
    }
}
