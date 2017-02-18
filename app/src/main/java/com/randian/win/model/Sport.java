package com.randian.win.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by li.lli on 2015/6/8.
 */
public class Sport implements Serializable {
    private long id;
    private String name;
    private float price;
    private float coach_price;
    private int duration;
    private int max_user_num;
    private int min_user_num;
    private float original_price;
    private String suggest;
    private String created_at;
    private String updated_at;
    private String head_image_url;
    private String description;
    private List<String> detail_image_urls;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMax_user_num() {
        return max_user_num;
    }

    public void setMax_user_num(int max_user_num) {
        this.max_user_num = max_user_num;
    }

    public int getMin_user_num() {
        return min_user_num;
    }

    public void setMin_user_num(int min_user_num) {
        this.min_user_num = min_user_num;
    }

    public float getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(float original_price) {
        this.original_price = original_price;
    }

    public String getHead_image_url() {
        return head_image_url;
    }

    public void setHead_image_url(String head_image_url) {
        this.head_image_url = head_image_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public List<String> getDetail_image_urls() {
        return detail_image_urls;
    }

    public void setDetail_image_urls(List<String> detail_image_urls) {
        this.detail_image_urls = detail_image_urls;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public float getCoach_price() {
        return coach_price;
    }

    public void setCoach_price(float coach_price) {
        this.coach_price = coach_price;
    }
}
