package com.zjfd.chenxiao.DHL.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dao.Operation;
import com.dao.ShowAdapter3;
import com.hiklife.rfidapi.InventoryEvent;
import com.hiklife.rfidapi.OnInventoryEventListener;
import com.hiklife.rfidapi.radioBusyException;
import com.zjfd.chenxiao.DHL.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/8/31 0031.
 */
public class SearchActivity extends Activity implements View.OnClickListener {
    public static SearchActivity searchActivity;
    private ImageView iv_back;

    protected static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_TOAST = 4;
    protected static final int MSG_TOAST_ERROR = 5;
    protected static final int MSG_TOAST_SUCCESS = 6;

    private Handler hMsg = new StartHander();
    private static List<String> tagInfoList = new ArrayList<String>();
    private static List<HashMap<String, String>> showInfoList = new ArrayList<HashMap<String, String>>();
    private static int tagCount = 0;
    private static int uploadCount = 0;
    private static ShowAdapter3 showadapter;
    public static ListView list_view;
    public static HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchActivity=this;

        //监听盘点
//        Operation.myRadio.setInventoryEventListener(new OnInventoryEventListener() {
//            @Override
//            public void RadioInventory(InventoryEvent event) {
//                Message msg = new Message();
//                msg.obj = event;
//                msg.what = MSG_SHOW_EPC_INFO;
//                hMsg.sendMessage(msg);
//            }
//        });
        initView();

        Intent it = new Intent(this, ScanActivity.class);
        it.putExtra("search", 1);
        startActivity(it);
    }

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        list_view = (ListView) findViewById(R.id.lv_EnterWH);
        showadapter = new ShowAdapter3(this, showInfoList);
        list_view.setAdapter(showadapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //用于集中处理显示等事件信息的静态类
    private class StartHander extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MSG_SHOW_EPC_INFO:
                    InventoryEvent info = (InventoryEvent) msg.obj;
                    ShowEPC(SearchActivity.this, info.GetFlagID());

                    break;


                case MSG_TOAST:
                    int returnValue1 = (Integer) msg.obj;
                    switch (returnValue1) {
                        case 1:
                            Toast.makeText(SearchActivity.this, "网络无法连接", Toast.LENGTH_SHORT).show();
                            break;
                        case 7:
//                            btn_scan.setText("开始扫描");
//                            btn_scan.setBackgroundColor(android.graphics.Color.parseColor("#EE799F"));
                            break;
                        case 8:
                            showadapter.notifyDataSetChanged();
//                            btn_scan.setText("停止扫描");
//                            btn_scan.setBackgroundColor(android.graphics.Color.parseColor("#66CDAA"));
                            break;
                        case 9:
                            Toast.makeText(SearchActivity.this, R.string.back_error_info_forNotStopInv, Toast.LENGTH_SHORT).show();
                            break;
                        case 10:
                            AllClear();
//                            MainEnterWHActivity.this.finish();
                            break;
                    }
                    break;
                case MSG_TOAST_ERROR:
                    String returnValue2 = (String) msg.obj;
                    Toast.makeText(SearchActivity.this.getApplicationContext(), returnValue2, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_TOAST_SUCCESS:
                    int returnValue3 = (Integer) msg.obj;
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
//                tv_readCount.setText(String.format("%d", tagCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void addShowInfoToList(String epc) {
        if (TextUtils.isEmpty(epc)){//为空
            addShowInfoToList(SearchActivity.searchActivity.getRfid());
        }else {
            hashMap.put("content2", epc);
            showInfoList.add(hashMap);
            showadapter.notifyDataSetChanged();
        }
    }

    public void AllClear() {
//        tv_uploadCount.setText(String.format("%d", uploadCount));
//        tv_readCount.setText(String.format("%d", tagCount));
        showadapter.notifyDataSetChanged();
    }

    //扫描获取rfid
    public String getRfid() {
        com.dao.Result result = Operation.readUnGivenTid((short) 3, (short) 3);
        return result.getReadInfo().toString();
    }
}