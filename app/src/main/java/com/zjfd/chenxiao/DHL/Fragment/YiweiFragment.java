package com.zjfd.chenxiao.DHL.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.Operation;
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
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/17 0017.
 */
public class YiweiFragment extends Fragment implements View.OnClickListener {
    private TextView tv_showfullcode, tv_showscantime, tv_showfullcode2, tv_showscantime2,tv_shelf,tv_plies;
    protected static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_TOAST = 4;
    protected static final int MSG_TOAST_ERROR = 5;
    protected static final int MSG_TOAST_SUCCESS = 6;

    private Handler hMsg = new StartHander();
    private static List<String> tagInfoList = new ArrayList<String>();
    private static int tagCount = 0;
    private static int uploadCount = 0;
    private static int flag = 0;
    private LinearLayout button;
    private Button btn_notarize, btn_cancle;
    MediaUtil mediaUtil = new MediaUtil(HomeActivity.homeActivity);
    private ImageView iv_scan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yiwei, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public void initView() {
        iv_scan= (ImageView) getView().findViewById(R.id.iv_scan);
        tv_shelf= (TextView) getView().findViewById(R.id.tv_shelf);
        tv_plies= (TextView) getView().findViewById(R.id.tv_plies);
        button = (LinearLayout) getView().findViewById(R.id.button);
        tv_showfullcode = (TextView) getView().findViewById(R.id.tv_showfullcode);
        tv_showscantime = (TextView) getView().findViewById(R.id.tv_showscantime);
        tv_showfullcode2 = (TextView) getView().findViewById(R.id.tv_showfullcode2);
        tv_showscantime2 = (TextView) getView().findViewById(R.id.tv_showscantime2);
        btn_notarize = (Button) getView().findViewById(R.id.btn_notarize);
        btn_cancle = (Button) getView().findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(this);
        btn_notarize.setOnClickListener(this);
        iv_scan.setOnClickListener(this);
    }

    //切换碎片是执行此方法
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            //监听盘点
//            Operation.myRadio.setInventoryEventListener(new OnInventoryEventListener() {
//                @Override
//                public void RadioInventory(InventoryEvent event) {
//                    Message msg = new Message();
//                    msg.obj = event;
//                    msg.what = MSG_SHOW_EPC_INFO;
//                    hMsg.sendMessage(msg);
//                }
//            });
//            timeDelay();
        } else {
            //停止扫描
//            stopScanRfid();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_notarize:
                yiwei();
                break;
            case R.id.btn_cancle:
                tv_showfullcode.setText("");
                tv_showscantime.setText("");
                tv_showfullcode2.setText("");
                tv_showscantime2.setText("");
                startScanRfid();
                break;
            case R.id.iv_scan:
                String rfid=getRfid();
                if (!TextUtils.isEmpty(rfid)) {
                    addShowInfoToList(rfid);
                }else {
                    showMasage("无结果，请重新扫描");
                }
                break;
        }
    }

    public void yiwei(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String time = df.format(new Date());
        RequestParams params=new RequestParams();
        params.put("index","2");
        params.put("tablename","warehouse");
        params.put("parameter","importRfid");
        params.put("parameter1",tv_showfullcode2.getText().toString());//包裹rfid
        params.put("parameter2","dutyRfid");
        params.put("parameter3",tv_showfullcode.getText().toString());//货架rfid
        params.put("parameter4","dutyTime");
        params.put("parameter5",time);//放入货架时间
        try {
            HttpNetworkRequest.get("revise", params, new BaseHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                    if (rawResponse.equals("ok")){
                        showMasege("移位成功");
                        mediaUtil.music(R.raw.success);//语音提示
                    }else{
                        showMasege("移位失败");
                        mediaUtil.music(R.raw.failure);//语音提示
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, Object errorResponse) {
                    showMasege("网络异常");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMasege(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
//                            tv_uploadCount.setText(String.format("%d", uploadCount));
//                            tv_readCount.setText(String.format("%d", tagCount));
//                            showadapter.notifyDataSetChanged();

//                            btn_scan.setText("停止扫描");
//                            btn_scan.setBackgroundColor(android.graphics.Color.parseColor("#66CDAA"));
                            break;
                        case 9:
                            Toast.makeText(getActivity(), R.string.back_error_info_forNotStopInv, Toast.LENGTH_SHORT).show();
                            break;
                        case 10:
//                            AllClear();
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
//                    tv_uploadCount.setText(String.format("%d", returnValue3));
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
//            tagInfoList.clear();
//            showInfoList.clear();
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
        }
    }

    public void addShowInfoToList(String epc) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String time = df.format(new Date());
            if (flag == 0 && isLetterDigitOrChinese(epc.substring(0,4))) {
                tv_showfullcode.setText(epc);
                tv_showscantime.setText(time);
                yiweiUpData(epc);
                flag += 1;
            } else if (flag == 1) {
                tv_showfullcode2.setText(epc);
                tv_showscantime2.setText(time);
                button.setVisibility(View.VISIBLE);
                flag = 0;
                //停止扫描
                stopScanRfid();
            }else{
                showMasage("请先扫描货架");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMasage(e.getMessage());
        }
    }

    private void showMasage(String smg) {
        Toast.makeText(getActivity(), smg, Toast.LENGTH_SHORT).show();
    }

    //如果是字母则返回true
    public static boolean isLetterDigitOrChinese(String str) {
        return str.matches("[a-zA-Z]+");
    }

    public void yiweiUpData(String rfid){
        try {
            RequestParams params=new RequestParams();
            params.put("index","2");
            params.put("tablename","duty");
            params.put("parameter","dutyRfid");
            params.put("parameter1", rfid);
            HttpNetworkRequest.get("query", params, new BaseHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                    try {
                        JSONArray jsonArray=new JSONArray(rawResponse);
                        JSONObject jsonObject=jsonArray.getJSONObject(0);
                        tv_shelf.setText((String) jsonObject.get("duty"));
                        tv_plies.setText((String) jsonObject.get("cell"));
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

    //扫描获取rfid
    public String getRfid() {
        com.dao.Result result = Operation.readUnGivenEpc((short) 2, (short) 6);
        return result.getReadInfo().toString();
    }

}
