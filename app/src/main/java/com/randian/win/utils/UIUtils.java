package com.randian.win.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.randian.win.model.Coach;
import com.randian.win.model.Order;
import com.randian.win.model.Sport;
import com.randian.win.ui.HomeActivity;
import com.randian.win.ui.coach.CoachDetailActivity;
import com.randian.win.ui.coach.CommentListActivity;
import com.randian.win.ui.coupon.CouponListActivity;
import com.randian.win.ui.order.OrderConfirmActivity;
import com.randian.win.ui.order.OrderCustomerInfoActivity;
import com.randian.win.ui.order.OrderChooseTimeActivity;
import com.randian.win.ui.order.OrderChooseCoachActivity;
import com.randian.win.ui.order.OrderDetailActivity;
import com.randian.win.ui.order.OrderPayActivity;
import com.randian.win.ui.personal.AboutUsActivity;
import com.randian.win.ui.personal.FeedbackActivity;
import com.randian.win.ui.personal.MyAgreementActivity;
import com.randian.win.ui.personal.MyCouponActivity;
import com.randian.win.ui.sport.SportDetailActivity;
import com.randian.win.ui.welcome.LoginActivity;
import com.randian.win.ui.welcome.SplashActivity;
import com.randian.win.ui.welcome.indicator.IndicatorActivity;

/**
 * Created by lily on 15-7-5.
 */
public class UIUtils {

    public static void startHomeActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void startHomeActivityToFragment(Activity activity,int showIndex) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.putExtra("tabIndex",showIndex);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void startSportDetailActivity(Context context, Sport sport) {
        Intent intent = new Intent(context, SportDetailActivity.class);
        intent.putExtra(Consts.EXTRA_PARAM_0, sport);
        context.startActivity(intent);
    }

    public static void startCoachDetailActivity(Context context, Coach coach) {
        Intent intent = new Intent(context, CoachDetailActivity.class);
        intent.putExtra(Consts.EXTRA_PARAM_0, coach);
        context.startActivity(intent);
    }

    public static void startOrderDetailActivity(Context context,String orderNo) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(Consts.EXTRA_PARAM_0,orderNo);
        context.startActivity(intent);
    }

    public static void startSplashActivity(Context context){
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    public static void startIndicatorActivity(Context context) {
        Intent intent = new Intent(context, IndicatorActivity.class);
        context.startActivity(intent);
    }

    public static void startLoginActivity(Context context){
        Intent intent  = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startOrder1Activity(Context context,Order order,int from){
        Intent intent  = new Intent(context, OrderCustomerInfoActivity.class);
        intent.putExtra(Consts.EXTRA_PARAM_0, order);
        intent.putExtra(Consts.EXTRA_PARAM_1,from);
        context.startActivity(intent);
    }

    public static void startOrder2Activity(Context context,Order order,int from){
        Intent intent  = new Intent(context, OrderChooseTimeActivity.class);
        intent.putExtra(Consts.EXTRA_PARAM_0,order);
        intent.putExtra(Consts.EXTRA_PARAM_1,from);
        context.startActivity(intent);
    }

    public static void startChooseCoachOfOrderActivity(Context context,Order order,String chosenDate,int from,String time){
        Intent intent  = new Intent(context, OrderChooseCoachActivity.class);
        intent.putExtra(Consts.EXTRA_PARAM_0,order);
        intent.putExtra(Consts.EXTRA_PARAM_1,chosenDate);
        intent.putExtra(Consts.EXTRA_PARAM_2,from);
        intent.putExtra(Consts.EXTRA_PARAM_3,time);
        context.startActivity(intent);
    }

    public static void startOrder3Activity(Context context,Order order){
        Intent intent  = new Intent(context, OrderConfirmActivity.class);
        intent.putExtra(Consts.EXTRA_PARAM_0,order);
        context.startActivity(intent);
    }

    public static void startChooseCouponActivity(Activity activity,Context context){
        Intent intent  = new Intent(context, CouponListActivity.class);
        activity.startActivityForResult(intent, 0);
    }

    public static void startOrderPayActivity(Context context,String orderNo){
        Intent intent  = new Intent(context, OrderPayActivity.class);
        intent.putExtra(Consts.EXTRA_PARAM_0, orderNo);
        context.startActivity(intent);
    }

    public static void startMyCouponActivity(Context context){
        Intent intent  = new Intent(context, MyCouponActivity.class);
        context.startActivity(intent);
    }

    public static void startAgreementActivity(Context context){
        Intent intent  = new Intent(context, MyAgreementActivity.class);
        context.startActivity(intent);
    }

    public static void startAboutUsActivity(Context context){
        Intent intent  = new Intent(context, AboutUsActivity.class);
        context.startActivity(intent);
    }

    public static void startFeedbackActivity(Context context){
        Intent intent  = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    public static void startCommentListActivity(Context context,long coachId){
        Intent intent  = new Intent(context, CommentListActivity.class);
        intent.putExtra(Consts.EXTRA_PARAM_0, coachId);
        context.startActivity(intent);
    }
}
