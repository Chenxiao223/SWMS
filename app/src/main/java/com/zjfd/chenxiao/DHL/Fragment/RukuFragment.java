package com.zjfd.chenxiao.DHL.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.dao.Setting;
import com.dao.ShowAdapter;
import com.dao.ShowInfo;
import com.google.gson.Gson;
import com.hiklife.rfidapi.InventoryEvent;
import com.hiklife.rfidapi.OnInventoryEventListener;
import com.hiklife.rfidapi.radioBusyException;
import com.loopj.android.http.RequestParams;
import com.zjfd.chenxiao.DHL.Entity.UpDataState;
import com.zjfd.chenxiao.DHL.R;
import com.zjfd.chenxiao.DHL.UI.HomeActivity;
import com.zjfd.chenxiao.DHL.UI.ScanActivity;
import com.zjfd.chenxiao.DHL.http.BaseHttpResponseHandler;
import com.zjfd.chenxiao.DHL.http.HttpNetworkRequest;
import com.zjfd.chenxiao.DHL.rfid.Result;
import com.zjfd.chenxiao.DHL.rfid.RfidOperation;

import org.apache.http.Header;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/7/17 0017.
 */
public class RukuFragment extends Fragment {
    public static RukuFragment rukuFragment;
    protected static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_TOAST = 4;
    protected static final int MSG_TOAST_ERROR = 5;
    protected static final int MSG_TOAST_SUCCESS = 6;

    private Handler hMsg = new StartHander();
    private static List<String> tagInfoList = new ArrayList<String>();
    private static List<ShowInfo> showInfoList = new ArrayList<ShowInfo>();
    private static int tagCount = 0;
    private static int uploadCount = 0;
    public static TextView tv_readCount;
    public static TextView tv_uploadCount;
    private static ShowAdapter showadapter;
    public static ListView list_view;
    private ImageView iv_scan;
    public static ShowInfo showinfo;
    private int num=1;//记录上传成功的次数

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ruku, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rukuFragment=this;

        initView();

    }

    public void initView() {
        iv_scan= (ImageView) getView().findViewById(R.id.iv_scan);
        tv_readCount = (TextView) getView().findViewById(R.id.tv_readCount);
        tv_uploadCount = (TextView) getView().findViewById(R.id.tv_uploadCount);
        list_view = (ListView) getView().findViewById(R.id.lv_EnterWH);
        showadapter = new ShowAdapter(getActivity(), showInfoList);
        list_view.setAdapter(showadapter);
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanActivity.class);
//                intent.putExtra("Scan", (Serializable) setting);
                startActivity(intent);
            }
        });
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
            //开始扫描
//            startScanRfid();
        } else {
            //停止扫描
            stopScanRfid();
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

    public void ShowEPC(Activity activity, String flagID) {

        String epc = com.dao.BaseDao.exChange(flagID);

        if (!tagInfoList.contains(epc)) {
            tagCount++;
            tagInfoList.add(epc);
            addShowInfoToList(epc);
            showadapter.notifyDataSetChanged();
            stopScanRfid();
            try {
                tv_readCount.setText(String.format("%d", tagCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void addShowInfoToList(String epc) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String time = df.format(new Date());

        if (TextUtils.isEmpty(epc)){//为空
            addShowInfoToList(RukuFragment.rukuFragment.getRfid());
        }else {
            showinfo.setEpc2(epc);
            showinfo.setTime2(time);
            showinfo.setUploadFlag(false);
            upData(showinfo);
            showInfoList.add(showinfo);
            showadapter.notifyDataSetChanged();
            tv_readCount.setText(""+showInfoList.size());
        }
    }

    public void AllClear() {
        tv_uploadCount.setText(String.format("%d", uploadCount));
        tv_readCount.setText(String.format("%d", tagCount));
        showadapter.notifyDataSetChanged();
    }

    //扫描获取rfid
    public String getRfid() {
        com.dao.Result result = Operation.readUnGivenTid((short) 3, (short) 3);
        return result.getReadInfo().toString();
    }

    //数据上传成功
    public void upData(final ShowInfo showinfo){
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String time = df.format(new Date());
            RequestParams params=new RequestParams();
            params.put("index","1");
            params.put("tablename","warehouse");
            params.put("parameter",showinfo.getEpc());//包裹条码
            params.put("parameter1",time);//入库时间
            params.put("parameter2",showinfo.getEpc2());//包裹rfid
            params.put("parameter3", showinfo.getTime());//条码时间
            HttpNetworkRequest.get("add", params, new BaseHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawResponse, Object response) {
                    if (rawResponse.equals("ok")) {
                        showadapter=new ShowAdapter(getActivity(), showInfoList);
                        list_view.setAdapter(showadapter);
                        showadapter.changeColor(showInfoList.size() - 1);//传1改变字体颜色
                        showadapter.notifyDataSetChanged();
                        tv_uploadCount.setText(""+num++);
                        Toast.makeText(getActivity(), "数据上传成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "数据上传失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, String rawData, Object errorResponse) {
                    Toast.makeText(getActivity(), "数据上传失败", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
        }
    }

}
