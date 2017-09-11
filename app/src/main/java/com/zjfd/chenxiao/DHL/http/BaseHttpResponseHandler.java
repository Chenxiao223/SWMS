package com.zjfd.chenxiao.DHL.http;


import com.loopj.android.http.BaseJsonHttpResponseHandler;

/**
 * Created by gang.shi on 2014/8/28 0:03.
 */
public abstract class BaseHttpResponseHandler extends BaseJsonHttpResponseHandler {
    protected Object parseResponse(String rawJsonData) throws Throwable {
        System.out.println("http result:" + rawJsonData);
        return null;
    }
}
