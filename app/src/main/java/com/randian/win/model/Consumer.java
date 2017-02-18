package com.randian.win.model;

/**
 * Created by lily on 15-8-3.
 */
public class Consumer {
    String consignee_address;
    String consignee_street;
    String consignee_name;
    long consignee_mobile;
    long id;

    public String getConsignee_address() {
        return consignee_address;
    }

    public void setConsignee_address(String consignee_address) {
        this.consignee_address = consignee_address;
    }

    public long getConsignee_mobile() {
        return consignee_mobile;
    }

    public void setConsignee_mobile(long consignee_mobile) {
        this.consignee_mobile = consignee_mobile;
    }

    public String getConsignee_name() {
        return consignee_name;
    }

    public void setConsignee_name(String consignee_name) {
        this.consignee_name = consignee_name;
    }

    public String getConsignee_street() {
        return consignee_street;
    }

    public void setConsignee_street(String consignee_street) {
        this.consignee_street = consignee_street;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
