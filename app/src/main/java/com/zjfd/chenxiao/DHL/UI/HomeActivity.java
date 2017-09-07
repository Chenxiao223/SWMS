package com.zjfd.chenxiao.DHL.UI;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.DBOperation;
import com.dao.Operation;
import com.dao.Setting;
import com.zjfd.chenxiao.DHL.Adapter.HomePageAdapter;
import com.zjfd.chenxiao.DHL.R;
import com.zjfd.chenxiao.DHL.rfid.RfidOperation;

import java.io.Serializable;
import java.lang.ref.WeakReference;


/**
 * Created by Administrator on 2017/8/9 0009.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    protected static final int MSG_DISCONNECT = 3;
    protected static final int MSG_CONNECT = 2;
    protected static final int MSG_OPERATION_SUCCESS = 1;
//    protected static final int MSG_DISCONNECT=4;
    private TextView tv_ruku, tv_shangjia, tv_yiwei, tv_chuku, tv_pandian;
    private HomePageAdapter homePageAdapter = null;
    public ViewPager pager = null;
    private ImageView iv_setting, iv_search;
    public static Setting setting;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //
        connectRadio();
        setting = getSettingData();
        Oldsetting = setting;
        initView();

    }

    public void initView() {
        tv_ruku = (TextView) findViewById(R.id.tv_ruku);
        tv_shangjia = (TextView) findViewById(R.id.tv_shangjia);
        tv_yiwei = (TextView) findViewById(R.id.tv_yiwei);
        tv_chuku = (TextView) findViewById(R.id.tv_chuku);
        tv_pandian = (TextView) findViewById(R.id.tv_pandian);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_search = (ImageView) findViewById(R.id.iv_search);

        tv_ruku.setOnClickListener(this);
        tv_shangjia.setOnClickListener(this);
        tv_yiwei.setOnClickListener(this);
        tv_chuku.setOnClickListener(this);
        tv_pandian.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        iv_search.setOnClickListener(this);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(4);//碎片左右各四个保持当前状态
        homePageAdapter = new HomePageAdapter(getSupportFragmentManager());
        pager.setAdapter(homePageAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ruku:
                pager.setCurrentItem(1);
                changeColor(true, false, false, false, false);
                break;
            case R.id.tv_shangjia:
                pager.setCurrentItem(2);
                changeColor(false, true, false, false, false);
                break;
            case R.id.tv_yiwei:
                pager.setCurrentItem(3);
                changeColor(false, false, true, false, false);
                break;
            case R.id.tv_chuku:
                pager.setCurrentItem(4);
                changeColor(false, false, false, true, false);
                break;
            case R.id.tv_pandian:
                pager.setCurrentItem(5);
                changeColor(false, false, false, false, true);
                break;
            case R.id.iv_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("Setting", (Serializable) setting);
                requestCode = 0;
                startActivityForResult(intent, requestCode);
                break;
            case R.id.iv_search:
                startActivity(new Intent(this,SearchActivity.class));
                break;
        }
    }

    public void changeColor(boolean ruku, boolean shangjia, boolean yiwei, boolean chuku, boolean pandian) {
        if (ruku) {
            //字体加粗
            TextPaint tp = tv_ruku.getPaint();
            tp.setFakeBoldText(true);
            tv_ruku.setTextColor(this.getResources().getColor(R.color.black));
        } else {
            //字体不加粗
            TextPaint tp = tv_ruku.getPaint();
            tp.setFakeBoldText(false);
            tv_ruku.setTextColor(this.getResources().getColor(R.color.gray));
        }

        if (shangjia) {
            TextPaint tp = tv_shangjia.getPaint();
            tp.setFakeBoldText(true);
            tv_shangjia.setTextColor(this.getResources().getColor(R.color.black));
        } else {
            TextPaint tp = tv_shangjia.getPaint();
            tp.setFakeBoldText(false);
            tv_shangjia.setTextColor(this.getResources().getColor(R.color.gray));
        }

        if (yiwei) {
            TextPaint tp = tv_yiwei.getPaint();
            tp.setFakeBoldText(true);
            tv_yiwei.setTextColor(this.getResources().getColor(R.color.black));
        } else {
            TextPaint tp = tv_yiwei.getPaint();
            tp.setFakeBoldText(false);
            tv_yiwei.setTextColor(this.getResources().getColor(R.color.gray));
        }

        if (chuku) {
            TextPaint tp = tv_chuku.getPaint();
            tp.setFakeBoldText(true);
            tv_chuku.setTextColor(this.getResources().getColor(R.color.black));
        } else {
            TextPaint tp = tv_chuku.getPaint();
            tp.setFakeBoldText(false);
            tv_chuku.setTextColor(this.getResources().getColor(R.color.gray));
        }

        if (pandian) {
            TextPaint tp = tv_pandian.getPaint();
            tp.setFakeBoldText(true);
            tv_pandian.setTextColor(this.getResources().getColor(R.color.black));
        } else {
            TextPaint tp = tv_pandian.getPaint();
            tp.setFakeBoldText(false);
            tv_pandian.setTextColor(this.getResources().getColor(R.color.gray));
        }
    }

    public Setting getSettingData() {

        DBOperation dboperation = new DBOperation(getApplicationContext());
        Setting setting = dboperation.querySet();

        if (setting.getID() == null || setting.getUserManage() == null || setting.getServer() == null
                || setting.getDataBase() == null || setting.getPower() == null || setting.getCCTime() == null || setting.getAgainDeadline() == null
                || setting.getScanInterval() == null) {
            Setting newset = new Setting();
            newset.setUserManage("默认用户");
            newset.setServer("162.168.2.3:3040");
            newset.setDataBase("HDL");
            newset.setPower("30");
            newset.setCCTime("2");
            newset.setAgainDeadline("2");
            newset.setScanInterval("3");
            dboperation.insertSet(newset);
            setting = dboperation.querySet();
        }

        return setting;
    }

    public static Setting Oldsetting = new Setting();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                Serializable setdata = data.getSerializableExtra("Setting");
                setting = (Setting) setdata;
                Log.i("info",setting.getPower());
                if (Oldsetting.getPower().equals(setting.getPower())) {
//                    disconnect();
                    Toast.makeText(getApplicationContext(), "重新设置功率中。。。", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//                    connectRadio();
                    Oldsetting = setting;
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //连接RFID模块，返回结果
    public void connectRadio() {
        new Thread() {
            public void run() {
                Message closemsg = new Message();
                try {
                    Operation.setAntennaPower(15);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                closemsg.obj = Operation.connectRadio();
                closemsg.what = MSG_CONNECT;
                hMsg2.sendMessage(closemsg);
            }
        }.start();
    }

    private Handler hMsg2 = new StartHander2(this);
    private class StartHander2 extends Handler {
        WeakReference<Activity> mActivityRef;

        StartHander2(Activity activity) {
            mActivityRef = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivityRef.get();
            if (activity == null) {
                return;
            }

            switch (msg.what) {

                case MSG_DISCONNECT:
                    int returnValue = (Integer) msg.obj;
                    switch (returnValue) {
                        case 0:
                            Toast.makeText(HomeActivity.this, "RFID断开失败", Toast.LENGTH_SHORT).show();
                            break;

                        case 1:
                            Toast.makeText(HomeActivity.this, "RFID断开成功", Toast.LENGTH_SHORT).show();
                            break;

                        case -1:
                            Toast.makeText(HomeActivity.this, "RFID断开失败：rfid正在运行", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;


                case MSG_CONNECT:
                    int returnValue1 = (Integer) msg.obj;
                    switch (returnValue1) {
                        case 0:
                            Toast.makeText(HomeActivity.this, "RFID连接成功", Toast.LENGTH_SHORT).show();
                            break;

                        case -1:
                            Toast.makeText(HomeActivity.this, "失败", Toast.LENGTH_SHORT).show();
                            break;

                        case -2:
                            Toast.makeText(HomeActivity.this, "失败：忙中", Toast.LENGTH_SHORT).show();

                            break;

                        case 2:
                            Toast.makeText(HomeActivity.this, "失败：设置天线", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;

//                case MSG_DISCONNECT:
//
//                    int returnValue3 = (Integer) msg.obj;
//
//                    switch (returnValue3) {
//                        case 0:
//                            Toast.makeText(HomeActivity.this, "断开失败", Toast.LENGTH_SHORT).show();
//                            break;
//
//                        case -1:
//                            Toast.makeText(HomeActivity.this, "断开失败：RFID正在运行中", Toast.LENGTH_SHORT).show();
//                            break;
//
//                        case 1:
//                            Toast.makeText(HomeActivity.this, "断开成功", Toast.LENGTH_SHORT).show();
//
//                            break;
//                    }
//
//                    break;

                case MSG_OPERATION_SUCCESS:
                    String returnValue2 = (String) msg.obj;
                    break;

                default:

                    break;

            }
        }
    }

    @Override
    public void onBackPressed() {
        //点击弹出对话框
        new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("是否要退出仓库管理系统应用？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //断开RFID模块
                        disconnectRadio();
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


    //连接RFID模块，返回结果
    public void disconnectRadio() {
        new Thread() {
            public void run() {
                Message closemsg = new Message();
                closemsg.obj = RfidOperation.DisconnectRadio();
                closemsg.what = MSG_DISCONNECT;
                hMsg2.sendMessage(closemsg);
            }
        }.start();
    }
}
