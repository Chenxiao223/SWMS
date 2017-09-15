package com.zjfd.chenxiao.DHL.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.Operation;
import com.dao.ShowAdapter2;
import com.loopj.android.http.RequestParams;
import com.zjfd.chenxiao.DHL.R;
import com.zjfd.chenxiao.DHL.UI.HomeActivity;
import com.zjfd.chenxiao.DHL.http.BaseHttpResponseHandler;
import com.zjfd.chenxiao.DHL.http.HttpNetworkRequest;
import com.zjfd.chenxiao.DHL.util.MediaUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/7/17 0017.
 */
public class ShangjiaFragment extends Fragment {
    protected static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_TOAST = 4;
    protected static final int MSG_TOAST_ERROR = 5;
    protected static final int MSG_TOAST_SUCCESS = 6;

    private static List<HashMap<String, String>> showInfoList = new ArrayList<HashMap<String, String>>();
    public static TextView tv_readCount;
    public static TextView tv_uploadCount;
    private static ShowAdapter2 showadapter;
    public static ListView list_view;
    private static int flag = 0;
    public static HashMap<String, String> hashMap;
    private int num=1;//记录上传成功的次数
    private int readCount=1;//记录扫到的数量
    MediaUtil mediaUtil = new MediaUtil(HomeActivity.homeActivity);
    private ImageView iv_scan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shangjia, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    public void initView() {
        iv_scan= (ImageView) getView().findViewById(R.id.iv_scan);
        tv_readCount = (TextView) getView().findViewById(R.id.tv_readCount);
        tv_uploadCount = (TextView) getView().findViewById(R.id.tv_uploadCount);
        list_view = (ListView) getView().findViewById(R.id.lv_EnterWH);
        showadapter = new ShowAdapter2(getActivity(), showInfoList);
        list_view.setAdapter(showadapter);

        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rfid = getRfid();
                if (!TextUtils.isEmpty(rfid)) {
                    addShowInfoToList(rfid);
                    showadapter.notifyDataSetChanged();
                }else{
                    showMasage("无结果，请重新扫描");
                }
            }
        });
    }

    //清空页面的数据
    public void clearPage(){
        tv_readCount.setText("0");
        tv_uploadCount.setText("0");
        flag=0;
        num=1;
        readCount=0;
        showInfoList.clear();
        showadapter.notifyDataSetChanged();
    }

    public void addShowInfoToList(String epc) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String time = df.format(new Date());
        if (flag == 0 && isLetterDigitOrChinese(epc.substring(0,12))) {
            hashMap = new HashMap<>();
            hashMap.put("content1", epc);
            hashMap.put("content2", time);
            hashMap.put("content7", "0");
            flag += 1;
            showInfoList.add(hashMap);
        } else if (flag == 1) {
            queryShelf(epc);
            hashMap.put("content3", epc);
            hashMap.put("content4", time);
            flag = 0;
            tv_readCount.setText("" + readCount++);
            upData(hashMap.get("content3"), hashMap.get("content1"));
        }else{
            showMasage("请先扫描货架");
        }
        showadapter.notifyDataSetChanged();
    }

    //如果是字母则返回true
    public boolean isLetterDigitOrChinese(String str) {
        return str.matches("[a-zA-Z]+");
    }

    public void queryShelf(final String rfid) {
        try {
            RequestParams params = new RequestParams();
            params.put("index", "2");
            params.put("tablename", "duty");
            params.put("parameter", "dutyRfid");
            params.put("parameter1", rfid);
            HttpNetworkRequest.get("query", params, new BaseHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                    try {
                        JSONArray jsonArray = new JSONArray(rawResponse);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String duty = (String) jsonObject.get("duty");
                        String cell = (String) jsonObject.get("cell");
                        hashMap.put("content5", duty);
                        hashMap.put("content6", cell);
                        showadapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, Object errorResponse) {
                    showMasage("请求失败");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMasage("网络异常");
        }

    }

    public void showMasage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void upData(String rfid1,String rfid2){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String time = df.format(new Date());
        RequestParams params=new RequestParams();
        params.put("index","2");
        params.put("tablename","warehouse");
        params.put("parameter","importRfid");
        params.put("parameter1",rfid1);
        params.put("parameter2","dutyRfid");
        params.put("parameter3",rfid2);
        params.put("parameter4","dutyTime");
        params.put("parameter5", time);
        try {
            HttpNetworkRequest.get("revise", params, new BaseHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                    if (rawResponse.equals("ok")) {
                        hashMap.put("content7", "4");
                        showadapter = new ShowAdapter2(getActivity(), showInfoList);
                        list_view.setAdapter(showadapter);
                        showadapter.notifyDataSetChanged();
                        tv_uploadCount.setText("" + num++);
                        showMasage("上传成功");
                        mediaUtil.music(R.raw.success);//语音提示
                    } else {
                        showMasage("上传失败");
                        mediaUtil.music(R.raw.failure);//语音提示
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, Object errorResponse) {
                    showMasage("网络异常");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //扫描获取rfid
    public String getRfid() {
        com.dao.Result result = Operation.readUnGivenEpc((short) 2, (short) 6);
        return result.getReadInfo().toString();
    }
}
