package com.zjfd.chenxiao.DHL.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.Operation;
import com.dao.ShowAdapter2;
import com.hiklife.rfidapi.InventoryEvent;
import com.hiklife.rfidapi.OnInventoryEventListener;
import com.hiklife.rfidapi.radioBusyException;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/17 0017.
 */
public class ChukuFragment extends Fragment {
    protected static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_TOAST = 4;
    protected static final int MSG_TOAST_ERROR = 5;
    protected static final int MSG_TOAST_SUCCESS = 6;

    private Handler hMsg = new StartHander();
    private static List<String> tagInfoList = new ArrayList<String>();
    private static List<HashMap<String, String>> showInfoList = new ArrayList<HashMap<String, String>>();
    private static int tagCount = 0;
    private static int uploadCount = 0;
    public static TextView tv_readCount;
    public static TextView tv_uploadCount;
    private static ShowAdapter2 showadapter;
    public static ListView list_view;
    public static HashMap<String, String> hashMap;
    private int num=1;//记录上传成功的次数
    MediaUtil mediaUtil = new MediaUtil(HomeActivity.homeActivity);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chuku, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public void initView() {
        tv_readCount = (TextView) getView().findViewById(R.id.tv_readCount);
        tv_uploadCount = (TextView) getView().findViewById(R.id.tv_uploadCount);
        list_view = (ListView) getView().findViewById(R.id.lv_EnterWH);
        showadapter = new ShowAdapter2(getActivity(), showInfoList);
        list_view.setAdapter(showadapter);
    }

    //切换碎片是执行此方法
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            //监听盘点
            Operation.myRadio.setInventoryEventListener(new OnInventoryEventListener() {
                @Override
                public void RadioInventory(InventoryEvent event) {
                    Message msg = new Message();
                    msg.obj = event;
                    msg.what = MSG_SHOW_EPC_INFO;
                    hMsg.sendMessage(msg);
                }
            });
            //开始扫描
            timeDelay();
        } else {
            //停止扫描
            stopScanRfid();
        }
    }



    //用于集中处理显示等事件信息的静态类
    private class StartHander extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MSG_SHOW_EPC_INFO:
                    InventoryEvent info = (InventoryEvent) msg.obj;
                    ShowEPC(getActivity(), info.GetFlagID());

                    break;


                case MSG_TOAST:
                    int returnValue1 = (Integer) msg.obj;
                    switch (returnValue1) {

                        case 1:
                            Toast.makeText(getActivity(), "网络无法连接", Toast.LENGTH_SHORT).show();
                            break;

                        case 7:
//                            btn_scan.setText("开始扫描");
//                            btn_scan.setBackgroundColor(android.graphics.Color.parseColor("#EE799F"));
                            break;

                        case 8:
                            tv_uploadCount.setText(String.format("%d", uploadCount));
                            tv_readCount.setText(String.format("%d", tagCount));
                            showadapter.notifyDataSetChanged();

//                            btn_scan.setText("停止扫描");
//                            btn_scan.setBackgroundColor(android.graphics.Color.parseColor("#66CDAA"));
                            break;

                        case 9:
                            Toast.makeText(getActivity(), R.string.back_error_info_forNotStopInv, Toast.LENGTH_SHORT).show();

                            break;

                        case 10:
                            AllClear();
//                            MainEnterWHActivity.this.finish();
                            break;
                    }

                    break;

                case MSG_TOAST_ERROR:

                    String returnValue2 = (String) msg.obj;

                    Toast.makeText(getActivity().getApplicationContext(), returnValue2, Toast.LENGTH_SHORT).show();

                    break;


                case MSG_TOAST_SUCCESS:

                    int returnValue3 = (Integer) msg.obj;

                    tv_uploadCount.setText(String.format("%d", returnValue3));

                    break;
            }
        }
    }

    public void startScanRfid() {
        int i = -1;
        try {
            i = Operation.StartInventory();
        } catch (radioBusyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (i == 1) {
            tagInfoList.clear();
            showInfoList.clear();
            tagCount = 0;
            uploadCount = 0;

            Message closemsg = new Message();
            closemsg.obj = 8;
            closemsg.what = MSG_TOAST;
            hMsg.sendMessage(closemsg);
        } else {
            return;
        }
    }

    public void stopScanRfid() {
        int i = Operation.StopInventory();
        if (i == 1) {
            Message closemsg = new Message();
            closemsg.obj = 7;
            closemsg.what = MSG_TOAST;
            hMsg.sendMessage(closemsg);

        } else {
            return;
        }
    }

    public void ShowEPC(Activity activity, String flagID) {
        String epc = com.dao.BaseDao.exChange(flagID);
        if (!tagInfoList.contains(epc)) {
            tagCount++;
            tagInfoList.add(epc);
            addShowInfoToList(epc);
            try {
                tv_readCount.setText(String.format("%d", tagCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addShowInfoToList(String epc) {
        if (!isLetterDigitOrChinese(epc.substring(0,12))) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String time = df.format(new Date());
            hashMap = new HashMap<>();
            hashMap.put("content3", epc);
            hashMap.put("content4", time);
            hashMap.put("content7", "0");
            queryShelf(epc);
            getBarCode(time, epc);
        }
    }

    //如果是字母则返回true
    public static boolean isLetterDigitOrChinese(String str) {
        return str.matches("[a-zA-Z]+");
    }

    public void getBarCode(final String times, final String rfid) {
        RequestParams params = new RequestParams();
        params.put("index", "2");
        params.put("tablename", "warehouse");
        params.put("parameter", "importRfid");
        params.put("parameter1", rfid);
        try {
            HttpNetworkRequest.get("query", params, new BaseHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                    try {
                        JSONArray jsonArray = new JSONArray(rawResponse);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        hashMap.put("content1", (String) jsonObject.get("barcode"));
                        hashMap.put("content2", times);
                        showInfoList.add(hashMap);
                        showadapter.notifyDataSetChanged();
                        upData(rfid, times);
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public void AllClear() {
        tv_uploadCount.setText(String.format("%d", uploadCount));
        tv_readCount.setText(String.format("%d", tagCount));
        showadapter.notifyDataSetChanged();
    }

    //获取货位和层数
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, Object errorResponse) {
                    showMasage("数据请求失败");
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

    public void upData(String rfid,String times){
        RequestParams params=new RequestParams();
        params.put("index","3");
        params.put("tablename","warehouse");
        params.put("parameter","importRfid");
        params.put("parameter1",rfid);//包裹rfid
        params.put("parameter2","exportTime");
        params.put("parameter3",times);//出库时间
        params.put("parameter4","isExport");
        params.put("parameter5","2");
        try {
            HttpNetworkRequest.get("revise", params, new BaseHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                    if (rawResponse.equals("ok")) {
                        hashMap.put("content7", "4");
                        showadapter = new ShowAdapter2(getActivity(), showInfoList);
                        list_view.setAdapter(showadapter);
                        showadapter.notifyDataSetChanged();
                        showMasage("上传成功");
                        mediaUtil.music(R.raw.success);//语音提示
                        tv_uploadCount.setText("" + num++);
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

    public void timeDelay(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startScanRfid();
            }
        };
        timer.schedule(timerTask, 1000);
    }

}
