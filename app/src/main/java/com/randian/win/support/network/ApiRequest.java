package com.randian.win.support.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;


/**
 * Created by Lily on 15-06-12
 */
public abstract class ApiRequest<T> extends OkRequest<T> {

    /**
     * 构造函数
     *
     * @param method        请求方法 Method.GET 等
     * @param url           请求的URL
     * @param errorListener 出错回调
     */
    public ApiRequest(int method, String url, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
    }

    protected abstract Response<T> parseNetworkResponse(NetworkResponse response);

    /**
     * 构造函数
     *
     * @param method        请求方法 Method.GET 等
     * @param url           请求的URL
     * @param listener      成功回调
     * @param errorListener 出错回调
     */
    public ApiRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(method, url, errorListener);
        setReseponseListener(listener);
    }
}
