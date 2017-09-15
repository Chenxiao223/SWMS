package com.zjfd.chenxiao.DHL.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.Operation;
import com.dao.ShowAdapter2;
import com.hiklife.rfidapi.InventoryEvent;
import com.hiklife.rfidapi.OnInventoryEventListener;
import com.hiklife.rfidapi.radioBusyException;
import com.loopj.android.http.RequestParams;
import com.zjfd.chenxiao.DHL.Entity.RfidInfo;
import com.zjfd.chenxiao.DHL.R;
import com.zjfd.chenxiao.DHL.http.BaseHttpResponseHandler;
import com.zjfd.chenxiao.DHL.http.HttpNetworkRequest;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/17 0017.
 */
public class PandianFragment extends Fragment {
    protected static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_TOAST = 4;
    protected static final int MSG_TOAST_ERROR = 5;
    protected static final int MSG_TOAST_SUCCESS = 6;

    private Handler hMsg = new StartHander();
    private RfidInfo info = new RfidInfo();
    private static List<String> tagInfoList = new ArrayList<String>();
    private static List<HashMap<String, String>> showInfoList = new ArrayList<HashMap<String, String>>();
    private static int tagCount = 0;
    private static int uploadCount = 0;
    public static TextView tv_readCount;
    public static TextView tv_uploadCount;
    private static ShowAdapter2 showadapter;
    public static ListView list_view;
    public static HashMap<String, String> hashMap;
    private Set<String> list_barcode = new HashSet<>();
    private Set<String> list_rfid = new HashSet<>();
    private Set<String> result_y = new HashSet<>();
    private Set<String> result_k = new HashSet<>();
    private Set<String> result_p = new HashSet<>();
    private HashMap<String, HashMap<String,String>> hm_rfid;
    private ArrayList<HashMap> list_mmp = new ArrayList<>();
    private Button btn_scan;
    private boolean flag = true;
    private HashMap<String,String> hm_info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pandian, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public void initView() {
        btn_scan = (Button) getView().findViewById(R.id.btn_scan);
        tv_readCount = (TextView) getView().findViewById(R.id.tv_readCount);
        tv_uploadCount = (TextView) getView().findViewById(R.id.tv_uploadCount);
        list_view = (ListView) getView().findViewById(R.id.lv_EnterWH);
        showadapter = new ShowAdapter2(getActivity(), showInfoList);
        list_view.setAdapter(showadapter);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == true) {
                    btn_scan.setText("开 始");
                    stopScanRfid();
                    compare();
                    flag = false;
                } else {
                    btn_scan.setText("停 止");
                    startScanRfid();
                    flag = true;
                }
            }
        });
    }

    public void compare() {
        hashMap.clear();
        showInfoList.clear();

        result_y.clear();
        result_y.addAll(list_rfid);
        result_y.removeAll(list_barcode);
        Log.i("P盘盈", "" + result_y);
        result_k.clear();
        result_k.addAll(list_barcode);
        result_k.removeAll(list_rfid);
        Log.i("P盘亏", "" + result_k);
        result_p.clear();
        result_p.addAll(list_rfid);
        result_p.retainAll(list_barcode);
        Log.i("P盘平", "" + result_p);
        Iterator it_y = result_y.iterator();
        while (it_y.hasNext()) {
            hashMap = new HashMap<>();
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                String time = df.format(new Date());
                String rfid=it_y.next().toString();
                hashMap.put("content7", "1");
                hashMap.put("content1", rfid);
                hashMap.put("content2", time);
                hashMap.put("content3", "");
                hashMap.put("content4", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            showInfoList.add(hashMap);
        }
        Iterator it_k = result_k.iterator();
        while (it_k.hasNext()) {
            hashMap = new HashMap<>();
            try {
                HashMap<String,String> mmp=hm_rfid.get(it_k.next().toString());
                hashMap.put("content1", mmp.get("importRfid"));
                hashMap.put("content2", mmp.get("importTime"));
                hashMap.put("content3", mmp.get("barcode"));
                hashMap.put("content4", mmp.get("barcodeTime"));
                hashMap.put("content7", "2");
            } catch (Exception e) {
                e.printStackTrace();
            }
            showInfoList.add(hashMap);
        }
        Iterator it_p = result_p.iterator();
        while (it_p.hasNext()) {
            hashMap = new HashMap<>();
            try {
                HashMap<String,String> mmp=hm_rfid.get(it_p.next().toString());
                hashMap.put("content1", mmp.get("importRfid"));
                hashMap.put("content2", mmp.get("importTime"));
                hashMap.put("content3", mmp.get("barcode"));
                hashMap.put("content4", mmp.get("barcodeTime"));
                hashMap.put("content7", "3");
            } catch (Exception e) {
                e.printStackTrace();
            }
            showInfoList.add(hashMap);
        }
        showadapter.notifyDataSetChanged();
    }

    //切换碎片是执行此方法
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            hm_rfid = new HashMap<>();
            getData();//获取数据
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
            list_rfid.add(epc);
        }
    }

    //如果是字母则返回true
    public static boolean isLetterDigitOrChinese(String str) {
        return str.matches("[a-zA-Z]+");
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
                    } catch (Exception e) {
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

    public void getBarCode(final String times, final String rfid) {
        try {
            RequestParams params = new RequestParams();
            params.put("index", "2");
            params.put("tablename", "warehouse");
            params.put("parameter", "importRfid");
            params.put("parameter1", rfid);
            Log.i("getBarCode", rfid);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("pandian", e.getMessage());
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

    public void getData() {
        RequestParams params = new RequestParams();
        params.put("index", "2");
        params.put("tablename", "warehouse");
        params.put("parameter", "isExport");
        params.put("parameter1", "1");
        HttpNetworkRequest.get("query", params, new BaseHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                try {
                    JSONArray jsonArray = new JSONArray(rawResponse);
                    int size = jsonArray.length();
                    for (int i = 0; i < size; i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            list_barcode.add((String) jsonObject.get("importRfid"));//将每一条包裹条形码存入集合
                            hm_info=new HashMap<String, String>();
                            hm_info.put("importRfid", String.valueOf(jsonObject.get("importRfid")));
                            hm_info.put("importTime", String.valueOf(jsonObject.get("importTime")));
                            hm_info.put("barcode", String.valueOf(jsonObject.get("barcode")));
                            hm_info.put("barcodeTime", String.valueOf(jsonObject.get("barcodeTime")));
                            hm_rfid.put((String) jsonObject.get("importRfid"), hm_info);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, Object errorResponse) {
                showMasage("网络异常");
            }
        });
    }

    public void showMasage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
