package com.randian.win.model;

import java.io.Serializable;

/**
 * Created by lily on 15-8-22.
 */
public class CommentItem implements Serializable{
    private int stars;
    private long id;
    private long user_id;
    private long coach_id;
    private String mobile;
    private String created_at;
    private String content;
    private String order_no;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public long getCoach_id() {
        return coach_id;
    }

    public void setCoach_id(long coach_id) {
        this.coach_id = coach_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
