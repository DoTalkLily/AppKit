package com.randian.win.ui.welcome.indicator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.randian.win.R;
import com.randian.win.ui.base.BaseFragment;

/**
 * User: 42
 */
public class IntroImagePageFragment extends BaseFragment {

    private static final String IMAGE_ID = "imageid";
    private int mImageId;

    public static IntroImagePageFragment newInstance(int imageId) {
        IntroImagePageFragment fragment = new IntroImagePageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IMAGE_ID, imageId);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageId = getArguments().getInt(IMAGE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_image, container, false);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageResource(mImageId);
        return view;
    }

}
