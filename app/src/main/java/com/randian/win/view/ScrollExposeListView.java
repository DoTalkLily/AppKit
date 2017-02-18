package com.randian.win.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by lily on 15-9-4.
 */
public class ScrollExposeListView extends PullToRefreshListView {


    public ScrollExposeListView(Context context) {
        super(context);
    }

    public ScrollExposeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
