package com.randian.win.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lily on 15-6-24.
 */
public class Coach implements Serializable {
    private int id;
    private int level;//等级 1:中级 2:高级 3:特级 4:专家
    private int distance;
    private int order_num;
    private int comment_num;
    private int reputation_num;
    private float score;
    private float coach_price;
    private String sex;
    private String name;
    private String city;
    private String location;
    private String verified;
    private String created_at;
    private String updated_at;
    private String description;
    private String hours_class;
    private String available_areas;
    private String profile_image_url;
    private String positive_reputation;
    private String categoriesStr;
    private List<Sport> sports;//教练对应的课程列表
    private List<Category> categories; //category的结构 ［{"name":"瑜伽"},{"name":"健身"}］


    public class Category implements Serializable{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public List<Category> getCategoriesList() {
        return categories;
    }

    public void setCategoriesList(List<Category> categories) {
        this.categories = categories;
    }

    public String getCategories() {
        return categoriesStr;
    }

    public void setCategories(String categoriesStr) {
        this.categoriesStr = categoriesStr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_num() {
        return order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getReputation_num() {
        return reputation_num;
    }

    public void setReputation_num(int reputation_num) {
        this.reputation_num = reputation_num;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPositive_reputation() {
        return positive_reputation;
    }

    public void setPositive_reputation(String positive_reputation) {
        this.positive_reputation = positive_reputation;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHours_class() {
        return hours_class;
    }

    public void setHours_class(String hours_class) {
        this.hours_class = hours_class;
    }

    public String getAvailable_areas() {
        return available_areas;
    }

    public void setAvailable_areas(String available_areas) {
        this.available_areas = available_areas;
    }

    public List<Sport> getSports() {
        return sports;
    }

    public void setSports(List<Sport> sports) {
        this.sports = sports;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getCoach_price() {
        return coach_price;
    }

    public void setCoach_price(float coach_price) {
        this.coach_price = coach_price;
    }
}
