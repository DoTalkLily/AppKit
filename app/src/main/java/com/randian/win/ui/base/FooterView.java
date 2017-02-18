package com.randian.win.ui.base;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.randian.win.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FooterView extends FrameLayout {

    @InjectView(R.id.footer_progress)
    ProgressBar mFooterProgress;

    @InjectView(R.id.text_content)
    TextView mTextContent;

    public FooterView(Context context) {
        super(context);
        init(null, 0);
        init();
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
        init();
    }

    public FooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
        init();
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.FooterView, defStyle, 0);


    }

    private void init() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.view_list_footer, null);
        ButterKnife.inject(this, root);
        setBackgroundColor(getResources().getColor(R.color.transparent));
        setForeground(new ColorDrawable(getResources().getColor(R.color.transparent)));
        addView(root);
    }

    public void showProgress() {
        mFooterProgress.setVisibility(View.VISIBLE);
        mTextContent.setVisibility(View.GONE);
    }

    public void showText(CharSequence text) {
        showText(text, null);
    }

    public void showText(int text) {
        showText(text, null);
    }

    public void showText(CharSequence text, final CallBack callBack) {
        mTextContent.setVisibility(View.VISIBLE);
        mFooterProgress.setVisibility(View.GONE);
        mTextContent.setText(text);
        if (callBack != null) {
            mTextContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.callBack();
                }
            });
        } else {
            setForeground(new ColorDrawable(getResources().getColor(R.color.transparent)));
            mTextContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public void showText(int text, final CallBack callBack) {
        mTextContent.setVisibility(View.VISIBLE);
        mFooterProgress.setVisibility(View.GONE);
        mTextContent.setText(text);
        if (callBack != null) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.callBack();
                }
            });
        } else {
            setForeground(new ColorDrawable(getResources().getColor(R.color.transparent)));
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    public interface CallBack {
        void callBack();
    }
}
