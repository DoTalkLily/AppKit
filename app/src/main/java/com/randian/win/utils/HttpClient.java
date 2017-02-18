package com.randian.win.utils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.randian.win.model.Consumer;
import com.randian.win.model.Order;
import com.randian.win.support.network.BaseRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Lily on 2015/6/12.
 */
public class HttpClient {
    public static final String API_HOST = "http://service.staging.randian.net";

    public static BaseRequest sendVerifyCode(Response.Listener listener,Response.ErrorListener errorListener, long mobile,boolean isVoice) throws IOException{
        String params = "?mobile=" + mobile + "&sign=" + Utils.getMD5String(mobile + "&randian") + (isVoice ? "&code_type=voice" : "");
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/users/smscode"+params, listener, errorListener);
        return request;
    }

    public static BaseRequest getSportList(Response.Listener listener, Response.ErrorListener errorListener, int cursor,int categoryId) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/sports?cursor=" + cursor + "&count=" + Consts.PAGE_SIZE+"&category_id="+categoryId, listener, errorListener);
        return request;
    }

    public static BaseRequest getSportDetail(Response.Listener listener, Response.ErrorListener errorListener, long id) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/sports/" + id, listener, errorListener);
        return request;
    }

    public static BaseRequest getCoachList(Response.Listener listener, Response.ErrorListener errorListener, int cursor,int categoryId) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/coaches?cursor=" + cursor + "&count=" + Consts.PAGE_SIZE+"&category_id="+categoryId, listener, errorListener);
        LogUtils.e("lili",API_HOST + "/coaches?cursor=" + cursor + "&count=" + Consts.PAGE_SIZE+"&category_id="+categoryId);
        return request;
    }

    public static BaseRequest getOrderList(Response.Listener listener, Response.ErrorListener errorListener, int cursor) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/orders?cursor=" + cursor + "&count=" + Consts.PAGE_SIZE, listener, errorListener);
        return request;
    }

    public static BaseRequest getCoachDetail(Response.Listener listener, Response.ErrorListener errorListener, long id) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/coaches/" + id, listener, errorListener);
        return request;
    }

    public static BaseRequest login(Response.Listener listener, Response.ErrorListener errorListener, long mobile, int code) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.POST, API_HOST + "/baidu/signup", listener, errorListener);
        request.part("sms_code", code);
        request.part("mobile", mobile);
        return request;
    }

    public static BaseRequest getConsumerInfo(Response.Listener listener, Response.ErrorListener errorListener) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/useraddrs/", listener, errorListener);
        return request;
    }

    public static BaseRequest postConsumerInfo(Response.Listener listener, Response.ErrorListener errorListener,Consumer consumer) throws IOException {
        String url = consumer.getId() == 0 ? "/useraddrs/":"/useraddrs/"+consumer.getId();
        BaseRequest request;

        if(consumer.getId() == 0){
            request = new BaseRequest(Request.Method.POST, API_HOST + url, listener, errorListener);
        }else{
            request = new BaseRequest(Request.Method.PUT, API_HOST + url, listener, errorListener);
        }

        request.part("consignee_name",consumer.getConsignee_name());
        request.part("consignee_mobile",consumer.getConsignee_mobile());
        request.part("consignee_street",consumer.getConsignee_street());
        request.part("consignee_address",consumer.getConsignee_address());

        return request;
    }

    public static BaseRequest getTimeListOfSport(Response.Listener listener, Response.ErrorListener errorListener, long sportId, String date, int duration) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/sports/" + sportId + "/schedule?date=" + date + "&duration=" + duration, listener, errorListener);
        return request;
    }

    public static BaseRequest getTimeListOfCoach(Response.Listener listener, Response.ErrorListener errorListener, long coachId, String date, int duration) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/coaches/" + coachId + "/schedule?date=" + date + "&duration=" + duration, listener, errorListener);
        return request;
    }

    public static BaseRequest getCoachListForOrder(Response.Listener listener, Response.ErrorListener errorListener, long sportId, String date, String startTime, String endTime) throws IOException {
            BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/sports/" + sportId + "/coaches?date=" + date + "&start_time=" + startTime + "&end_time=" + endTime, listener, errorListener);
        return request;
    }

    public static BaseRequest generateOrder(Response.Listener listener, Response.ErrorListener errorListener,Order order) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.POST, API_HOST + "/orders", listener, errorListener);
        request.part("sport_id",order.getSport_id());
        request.part("coach_id", order.getCoach_id());
        request.part("useraddr_id",order.getConsignee_address_id());
        request.part("sport_date",order.getSport_start_date());
        request.part("sport_order_num",order.getSport_order_num());
        request.part("sport_start_time",order.getSport_start_time());
        request.part("mark",order.getMark() == null?"":order.getMark());
        return request;
    }

    //platform : 5 支付宝手机端  6 微信支付
    public static BaseRequest createOrderPay(Response.Listener listener, Response.ErrorListener errorListener,String orderNo, int platform) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.POST, API_HOST + "/orders/create_pay", listener, errorListener);
        request.part("order_no",orderNo);
        request.part("pay_platform_id",platform);
        return request;
    }

    public static BaseRequest getCouponList(Response.Listener listener, Response.ErrorListener errorListener) throws IOException {
        String url = API_HOST+"/coupons?order_by="+URLEncoder.encode("coupon_fee desc,coupon_expire_at","UTF-8")+"&valid=true";
        BaseRequest request = new BaseRequest(Request.Method.GET, url , listener, errorListener);
        return request;
    }

    public static BaseRequest getMyCouponList(Response.Listener listener, Response.ErrorListener errorListener) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/coupons?coupon_state,coupon_expire_at", listener, errorListener);
        return request;
    }


    public static BaseRequest sendFeedback(Response.Listener listener, Response.ErrorListener errorListener,String content) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.POST, API_HOST + "/feedbacks", listener, errorListener);
        request.part("body", content);
        return request;
    }

    public static BaseRequest getCommentList(Response.Listener listener, Response.ErrorListener errorListener,long coachId,int cursor,int pageSize) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/coaches/"+coachId+"/comment?cursor="+cursor+"&count="+pageSize, listener, errorListener);
        return request;
    }

    public static BaseRequest getOrderDetail(Response.Listener listener, Response.ErrorListener errorListener,String orderNo)throws IOException{
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/orders/"+orderNo, listener, errorListener);
        return request;
    }

    public static BaseRequest sendComment(Response.Listener listener, Response.ErrorListener errorListener,String orderNo, int starCount,String content) throws IOException {
        BaseRequest request = new BaseRequest(Request.Method.POST, API_HOST + "/orders/"+orderNo+"/comment", listener, errorListener);
        request.part("stars", starCount);
        request.part("content",content);
        return request;
    }

    public static BaseRequest reOrder(Response.Listener listener, Response.ErrorListener errorListener,String orderNo) throws IOException{
        BaseRequest request = new BaseRequest(Request.Method.GET, API_HOST + "/orders/reorder_params?order_no="+orderNo, listener, errorListener);
        return request;
    }
}
