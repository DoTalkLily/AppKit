package com.randian.win.utils;

import com.randian.win.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by li.lli on 2015/5/31.
 */
public class Consts {
    //wx
    public static String APP_ID = "wx2aa81a7ccfee7f9e";
    //error define
    public static String SERVER_ERROR = "啊欧，服务器开小差了，请稍后再试";
    //URL
    public static final String PROTOCAL_URL = "protocal/agreement.html";
    //评论字数
    public static final int COMMENT_WORD_COUNT = 120;
    //normal consts
    public static final int PAGE_SIZE = 10;
    public static final int FROM_SPORT = 1;
    public static final int FROM_COACH = 2;
    public static final int DAY_COUNT = 5;//下单第二部可以选择多少天的时间
    public static final int NO_COUPON = 404;
    public static final String ERROR_CODE = "error";
    public static final String EXTRA_PARAM_0 = "extra_param_0";
    public static final String EXTRA_PARAM_1 = "extra_param_1";
    public static final String EXTRA_PARAM_2 = "extra_param_2";
    public static final String EXTRA_PARAM_3 = "extra_param_3";
    //broadcast action name
    public static final String WX_PAY_ACTION = "wx_pay_action";
    public static final String ALIPAY_ACTION = "alipay_action";
    //支付方式
    public static final int ALIPAY_PLATFORM = 5;//支付宝客户端支付
    public static final int WX_PAY_PLATFORM = 6;//微信客户端支付

    public static final int GYM_CATEGORY = 1;//健身类目id
    public static final int YOGA_CATEGORY = 2;//瑜伽类目id
    public static final int JUNIOR_COACH = 1;//中级教练

    public static final Map<Integer, String> COACH_LEVEL = new HashMap<Integer, String>() {
        {
            put(1, "中级");
            put(2, "高级");
            put(3, "特级");
            put(4, "首席");
        }
    };

    public static final Map<Integer, Integer> COACH_LEVEL_IMG_WRAPPER = new HashMap<Integer, Integer>() {
        {
            put(1, R.drawable.bubble_grey);
            put(2, R.drawable.bubble_grey_high);
            put(3, R.drawable.bubble_grey_high);//图片代替换
            put(4, R.drawable.bubble_grey_high);
        }
    };

}
