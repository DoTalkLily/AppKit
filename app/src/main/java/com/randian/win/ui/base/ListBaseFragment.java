package com.randian.win.ui.base;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.randian.win.R;
import com.randian.win.ui.HomeActivity;
import com.randian.win.utils.Consts;
import com.randian.win.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by lily on 15-7-5.
 */
public class ListBaseFragment extends BaseFragment implements AbsListView.OnScrollListener{

    //上方两个tab
    protected View mGymTabBtn;
    protected View mYogaTabBtn;
    protected TextView mGymTab;
    protected TextView mYogaTab;
    protected TextView mGymUnderline;
    protected TextView mYogaUnderline;

    protected boolean hasMore;
    protected int totalLoadedCount;
    protected View mMenuView;
    protected View mHeaderView;
    protected ListView mListView;
    protected FooterView mFooterView;
    private SliderLayout mSlider;

    protected void initHeader() {
        mHeaderView = getActivity().getLayoutInflater().inflate(R.layout.view_image_header_slider, null);
        mSlider = (SliderLayout)mHeaderView.findViewById(R.id.slider);
        initTab();
        initImageSlider();
        mListView.addHeaderView(mHeaderView);
        mListView.setOnTouchListener(onTouchListener);
    }

    protected void initHeaderWidthPic(int imgId,int colorId){
        mHeaderView = getActivity().getLayoutInflater().inflate(R.layout.view_image_header_pic, null);
        mHeaderView.findViewById(R.id.container).setBackgroundResource(colorId);
        ((ImageView)mHeaderView.findViewById(R.id.image)).setImageResource(imgId);
        mListView.addHeaderView(mHeaderView);
        mListView.setOnTouchListener(onTouchListener);
    }

    protected void initFooter() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_empty_footer, null);
        mFooterView = (FooterView) view.findViewById(R.id.footer);
        mListView.addFooterView(view);
        mMenuView = ((HomeActivity)getActivity()).getMenuContainer();
    }

    protected void initMenu() {
        mMenuView = ((HomeActivity)getActivity()).getMenuContainer();
        animateBack();
    }


    private void initImageSlider(){
        //TODO 图片每次请求
//        HashMap<String,String> url_maps = new HashMap<>();
//        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
//        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
//        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        HashMap<String,Integer> url_maps = new HashMap<>();
        url_maps.put("banner1",R.drawable.banner1);
        url_maps.put("banner2",R.drawable.banner2);
        url_maps.put("banner3",R.drawable.banner3);
        url_maps.put("banner4",R.drawable.banner4);

        for(String name : url_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            mSlider.addSlider(textSliderView);
        }
        mSlider.setCustomAnimation(new SliderDescriptionAnimation());
//        mSlider.setCustomIndicator((PagerIndicator) mHeaderView.findViewById(R.id.custom_indicator));
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean load = totalItemCount != totalLoadedCount && firstVisibleItem + visibleItemCount >= totalItemCount;
        if (load && hasMore) {
            totalLoadedCount = totalItemCount;
            mFooterView.showProgress();
            startGetRemoteListTask(true);
        }
    }

    protected void startGetRemoteListTask(boolean hasMore){
    }

    @Override
    public void onStop() {
        if (mSlider != null) {
            mSlider.stopAutoCycle();
        }
        super.onStop();
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        float lastY = 0f;
        float currentY = 0f;
        //下面两个表示滑动的方向，大于0表示向下滑动，小于0表示向上滑动，等于0表示未滑动
        int lastDirection = 0;
        int currentDirection = 0;
        int touchSlop = 10;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = event.getY();
                    currentY = event.getY();
                    currentDirection = 0;
                    lastDirection = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mListView.getFirstVisiblePosition() > 0) {
                        //只有在listView.getFirstVisiblePosition()>0的时候才判断是否进行显隐动画。因为listView.getFirstVisiblePosition()==0时，
                        //ToolBar——也就是头部元素必须是可见的，如果这时候隐藏了起来，那么占位置用了headerview就被用户发现了
                        //但是当用户将列表向下拉露出列表的headerview的时候，应该要让头尾元素再次出现才对——这个判断写在了后面onScrollListener里面……
                        float tmpCurrentY = event.getY();
                        if (Math.abs(tmpCurrentY - lastY) > touchSlop) {//滑动距离大于touchslop时才进行判断
                            currentY = tmpCurrentY;
                            currentDirection = (int) (currentY - lastY);
                            if (lastDirection != currentDirection) {
                                //如果与上次方向不同，则执行显/隐动画
                                if (currentDirection < 0) {
                                    animateHide();
                                } else {
                                    animateBack();
                                }
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    //手指抬起的时候要把currentDirection设置为0，这样下次不管向哪拉，都与当前的不同（其实在ACTION_DOWN里写了之后这里就用不着了……）
                    currentDirection = 0;
                    lastDirection = 0;
                    break;
            }
            return false;
        }
    };

    AnimatorSet backAnimatorSet;//这是显示头尾元素使用的动画

    public void animateBack() {
        //先清除其他动画
        if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
            hideAnimatorSet.cancel();
        }
        if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
            //如果这个动画已经在运行了，就不管它
        } else {
            backAnimatorSet = new AnimatorSet();
            //下面两句是将头尾元素放回初始位置。
            if(mMenuView!=null){
                ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(mMenuView, "translationY", mMenuView.getTranslationY(), 0f);
                backAnimatorSet.setDuration(300);
                backAnimatorSet.play(footerAnimator);
                backAnimatorSet.start();
            }
        }
    }

    AnimatorSet hideAnimatorSet;//这是隐藏头尾元素使用的动画

    private void animateHide() {
        //先清除其他动画
        if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
            backAnimatorSet.cancel();
        }
        if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
            //如果这个动画已经在运行了，就不管它
        } else {
            hideAnimatorSet = new AnimatorSet();
            ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(mMenuView, "translationY", mMenuView.getTranslationY(), mMenuView.getHeight());//将Button隐藏到下面
            hideAnimatorSet.setDuration(200);
            hideAnimatorSet.playTogether(footerAnimator);
            hideAnimatorSet.start();
        }
    }

    protected void updateFootView(boolean hasMore) {
        if (!hasMore) {
            mFooterView.showText(R.string.no_more);
            return;
        }
        mFooterView.showText(getString(R.string.get_more), new FooterView.CallBack() {
            @Override
            public void callBack() {
                mFooterView.showProgress();
                startGetRemoteListTask(true);
            }
        });
    }

    private void initTab(){
        mGymTab = (TextView) mHeaderView.findViewById(R.id.gym_tab);
        mYogaTab = (TextView) mHeaderView.findViewById(R.id.yoga_tab);
        mGymTabBtn = mHeaderView.findViewById(R.id.category_js);
        mYogaTabBtn = mHeaderView.findViewById(R.id.category_yoga);
        mGymUnderline = (TextView) mHeaderView.findViewById(R.id.gym_underline);
        mYogaUnderline = (TextView)mHeaderView.findViewById(R.id.yoga_underline);
    }

    protected void switchTab(int type){
        if(type == Consts.GYM_CATEGORY){
            mYogaTab.setTextColor(getResources().getColor(R.color.c_ccc));
            mYogaUnderline.setBackgroundColor(getResources().getColor(R.color.c_ccc));
            mGymTab.setTextColor(getResources().getColor(R.color.orange));
            mGymUnderline.setBackgroundColor(getResources().getColor(R.color.orange));
        }else{
            mYogaTab.setTextColor(getResources().getColor(R.color.orange));
            mYogaUnderline.setBackgroundColor(getResources().getColor(R.color.orange));
            mGymTab.setTextColor(getResources().getColor(R.color.c_ccc));
            mGymUnderline.setBackgroundColor(getResources().getColor(R.color.c_ccc));
        }
    }
}
