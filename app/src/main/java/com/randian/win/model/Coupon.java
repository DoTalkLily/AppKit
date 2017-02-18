package com.randian.win.model;

import java.io.Serializable;

/**
 * Created by lily on 15-8-8.
 */
public class Coupon implements Serializable {
    private long id;
    private long receive_mobile;
    private float coupon_fee;
    private String created_at;
    private String coupon_expire_at;
    private String coupon_state_text;
    private String coupon_description;

    public String getCoupon_description() {
        return coupon_description;
    }

    public void setCoupon_description(String coupon_description) {
        this.coupon_description = coupon_description;
    }

    public String getCoupon_expire_at() {
        return coupon_expire_at;
    }

    public void setCoupon_expire_at(String coupon_expire_at) {
        this.coupon_expire_at = coupon_expire_at;
    }

    public float getCoupon_fee() {
        return coupon_fee;
    }

    public void setCoupon_fee(float coupon_fee) {
        this.coupon_fee = coupon_fee;
    }

    public String getCoupon_state_text() {
        return coupon_state_text;
    }

    public void setCoupon_state_text(String coupon_state_text) {
        this.coupon_state_text = coupon_state_text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReceive_mobile() {
        return receive_mobile;
    }

    public void setReceive_mobile(long receive_mobile) {
        this.receive_mobile = receive_mobile;
    }
}
