package com.randian.win.support.network;

import android.os.SystemClock;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class BaseArrayRequest extends ApiRequest<JSONArray> {

    private long mRequestBeginTime = 0;

    public BaseArrayRequest(int method, String url, Response.Listener<JSONArray> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    //解析返回的数据
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            byte[] data = response.data;
            String json = new String(data, HttpHeaderParser.parseCharset(response.headers));
            if (VolleyLog.DEBUG) {
                VolleyLog.d("response:%s", json);
            }
            return Response.success(new JSONArray(json), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public void addMarker(String tag) {
        super.addMarker(tag);
        if (mRequestBeginTime == 0) {
            mRequestBeginTime = SystemClock.elapsedRealtime();
        }
    }

    @Override
    protected void deliverResponse(JSONArray jsonArray) {
        super.deliverResponse(jsonArray);
        //请求用掉的总时间
        long requestTime = SystemClock.elapsedRealtime() - mRequestBeginTime;
    }

    @Override
    //可以自己在这里完成错误的自定义处理
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        //错误发生时候response的数据 byte[]
        //error.networkResponse.data;
    }
}
