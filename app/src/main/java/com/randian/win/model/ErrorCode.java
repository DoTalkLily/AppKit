package com.randian.win.model;

/**
 * Created by lily on 15-8-6.
 */
public class ErrorCode {
    private int  error_code;
    private String  error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }
}
