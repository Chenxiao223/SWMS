package com.zjfd.chenxiao.DHL.http;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zjfd.chenxiao.DHL.UI.HomeActivity;


/**
 * Created by Administrator on 2017/2/16 0016.
 */
public class HttpNetworkRequest {
    //    public static final String BASE_URL = ActivityLogin.activityLogin.edit_server.getText().toString();
    public static final String BASE_URL = "http://"+ HomeActivity.setting.getServer()+"/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(null, getAbsoluteUrl(url), null, params, responseHandler);
    }

//    public static void delete(Context context, String url, Header[] headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.delete(context, getAbsoluteUrl(url), headers, params, responseHandler);
//    }

    public static void post(String url, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url,RequestParams params,AsyncHttpResponseHandler responseHandler){
        client.put(getAbsoluteUrl(url),params,responseHandler);
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        String uuu = BASE_URL + relativeUrl;
        return BASE_URL + relativeUrl;
    }

}
