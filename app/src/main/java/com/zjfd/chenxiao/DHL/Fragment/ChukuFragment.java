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
import com.zjfd.chenxiao.DHL.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
    private static List<HashMap<String,String>> showInfoList = new ArrayList<HashMap<String,String>>();
    private static int tagCount = 0;
    private static int uploadCount = 0;
    public static TextView tv_readCount;
    public static TextView tv_uploadCount;
    private static ShowAdapter2 showadapter;
    public static ListView list_view;
    private static int flag=0;
    public static HashMap<String,String> hashMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chuku,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    public void initView(){
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
            startScanRfid();
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

    public static void ShowEPC(Activity activity, String flagID) {

        String epc = com.dao.BaseDao.exChange(flagID);

        if (!tagInfoList.contains(epc)) {
            tagCount++;
            tagInfoList.add(epc);
            addShowInfoToList(epc);
            showadapter.notifyDataSetChanged();
            try {
                tv_readCount.setText(String.format("%d", tagCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void addShowInfoToList(String epc) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String time = df.format(new Date());
        if (flag==0) {
            hashMap=new HashMap<>();
            hashMap.put("content1",epc);
            hashMap.put("content2",time);
            flag+=1;
        }else if (flag==1){
            hashMap.put("content3", epc);
            hashMap.put("content4",time);
            flag=0;
            showInfoList.add(hashMap);
        }
//        showinfo.setUploadFlag(false);
    }

    public void AllClear() {
        tv_uploadCount.setText(String.format("%d", uploadCount));
        tv_readCount.setText(String.format("%d", tagCount));
        showadapter.notifyDataSetChanged();
    }

}
