package com.randian.win.model;

import java.io.Serializable;

/**
 * Created by lily on 15-7-4.
 */
public class Order implements Serializable{
    private String state;
    private String order_no;
    private String mark;
    private long user_id;
    private long coach_id;
    private long sport_id;
    private long comment_id;
    private long consignee_address_id;
    private String coach_name;
    private String coach_gender;
    private String coach_description;
    private String coach_available_areas;
    private String sport_name;
    private String sport_img_url;
    private String created_at;
    private String updated_at;
    private float order_cash_fee;//实际支付的价格
    private float order_off_fee;
    private float order_pay_fee;
    private float order_coupon_fee;
    private int sport_duration;
    private int sport_order_num;
    private int coach_level;
    private String suggest;
    private String coach_img_url;
    private String category_name;
    private String sport_start_date;
    private String sport_start_time;
    private String sport_detail_time;
    private String consignee_address;
    private String consignee_street;
    private String consignee_name;
    private long consignee_mobile;
    private long coupon_id;
    private float origin_price;
    private CommentItem comment_info;

    public String getConsignee_address() {
        return consignee_address;
    }

    public void setConsignee_address(String consignee_address) {
        this.consignee_address = consignee_address;
    }

    public String getConsignee_street() {
        return consignee_street;
    }

    public void setConsignee_street(String consignee_street) {
        this.consignee_street = consignee_street;
    }

    public String getConsignee_name() {
        return consignee_name;
    }

    public void setConsignee_name(String consignee_name) {
        this.consignee_name = consignee_name;
    }

    public long getConsignee_mobile() {
        return consignee_mobile;
    }

    public void setConsignee_mobile(long consignee_mobile) {
        this.consignee_mobile = consignee_mobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCoach_name() {
        return coach_name;
    }

    public void setCoach_name(String coach_name) {
        this.coach_name = coach_name;
    }

    public String getSport_name() {
        return sport_name;
    }

    public void setSport_name(String sport_name) {
        this.sport_name = sport_name;
    }

    public String getCoach_img_url() {
        return coach_img_url;
    }

    public void setCoach_img_url(String coach_img_url) {
        this.coach_img_url = coach_img_url;
    }

    public float getOrder_pay_fee() {
        return order_pay_fee;
    }

    public void setOrder_pay_fee(float order_pay_fee) {
        this.order_pay_fee = order_pay_fee;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSport_start_time() {
        return sport_start_time;
    }

    public void setSport_start_time(String sport_start_time) {
        this.sport_start_time = sport_start_time;
    }

    public int getSport_order_num() {
        return sport_order_num;
    }

    public void setSport_order_num(int sport_order_num) {
        this.sport_order_num = sport_order_num;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public float getOrder_cash_fee() {
        return order_cash_fee;
    }

    public float getOrder_off_fee() {
        return order_off_fee;
    }

    public void setOrder_off_fee(int order_off_fee) {
        this.order_off_fee = order_off_fee;
    }

    public int getSport_duration() {
        return sport_duration;
    }

    public void setSport_duration(int sport_duration) {
        this.sport_duration = sport_duration;
    }

    public float getOrigin_price() {
        return origin_price;
    }

    public void setOrigin_price(float origin_price) {
        this.origin_price = origin_price;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public long getCoach_id() {
        return coach_id;
    }

    public void setCoach_id(long coach_id) {
        this.coach_id = coach_id;
    }

    public long getSport_id() {
        return sport_id;
    }

    public void setSport_id(long sport_id) {
        this.sport_id = sport_id;
    }

    public String getCoach_available_areas() {
        return coach_available_areas;
    }

    public void setCoach_available_areas(String coach_available_areas) {
        this.coach_available_areas = coach_available_areas;
    }

    public String getCoach_description() {
        return coach_description;
    }

    public void setCoach_description(String coach_description) {
        this.coach_description = coach_description;
    }

    public String getCoach_gender() {
        return coach_gender;
    }

    public void setCoach_gender(String coach_gender) {
        this.coach_gender = coach_gender;
    }

    public String getSport_detail_time() {
        return sport_detail_time;
    }

    public void setSport_detail_time(String sport_detail_time) {
        this.sport_detail_time = sport_detail_time;
    }

    public String getSport_img_url() {
        return sport_img_url;
    }

    public void setSport_img_url(String sport_img_url) {
        this.sport_img_url = sport_img_url;
    }

    public void setOrder_cash_fee(float order_cash_fee) {
        this.order_cash_fee = order_cash_fee;
    }

    public float getOrder_coupon_fee() {
        return order_coupon_fee;
    }

    public void setOrder_coupon_fee(float order_coupon_fee) {
        this.order_coupon_fee = order_coupon_fee;
    }

    public void setOrder_off_fee(float order_off_fee) {
        this.order_off_fee = order_off_fee;
    }

    public long getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(long coupon_id) {
        this.coupon_id = coupon_id;
    }

    public CommentItem getComment_info() {
        return comment_info;
    }

    public void setComment_info(CommentItem comment_info) {
        this.comment_info = comment_info;
    }

    public long getComment_id() {
        return comment_id;
    }

    public void setComment_id(long comment_id) {
        this.comment_id = comment_id;
    }


    public long getConsignee_address_id() {
        return consignee_address_id;
    }

    public void setConsignee_address_id(long consignee_address_id) {
        this.consignee_address_id = consignee_address_id;
    }

    public String getSport_start_date() {
        return sport_start_date;
    }

    public void setSport_start_date(String sport_start_date) {
        this.sport_start_date = sport_start_date;
    }

    public int getCoach_level() {
        return coach_level;
    }

    public void setCoach_level(int coach_level) {
        this.coach_level = coach_level;
    }
}
