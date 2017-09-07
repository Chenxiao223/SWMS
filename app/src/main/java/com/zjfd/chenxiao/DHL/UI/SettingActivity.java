package com.zjfd.chenxiao.DHL.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dao.AgentOperation;
import com.dao.DBOperation;
import com.dao.Setting;
import com.zjfd.chenxiao.DHL.R;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/9 0009.
 */
public class SettingActivity extends Activity {
    ImageView imageView;
    Setting setting;

    private int resultCode;
    private Spinner spin_userManage;
    private TextView tv_server_info;
    private TextView tv_server_click;
    private TextView tv_dataBase_info;
    private TextView tv_dataBase_click;
    private TextView tv_power_info;
    private TextView tv_power_click;
    private TextView tv_CCTime_info;
    private TextView tv_CCTime_click;
    private TextView tv_AgainDeadline_info;
    private TextView tv_AgainDeadline_click;
    private TextView tv_ScanInterval_info;
    private TextView tv_ScanInterval_click;
    private ArrayAdapter arrayAdapter;
    private List<String> list=new ArrayList<>();

    private List<String> list_ProductName = new ArrayList<String>();

    private String[] items1;

    private class setHandler extends Handler {

        WeakReference<Activity> mActivityRef;

        setHandler(Activity activity) {

            mActivityRef = new WeakReference<Activity>(activity);
        }

        public void handleMessage(android.os.Message msg) {

            Activity activity = mActivityRef.get();
            if (activity == null) {
                return;
            }

            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Toast.makeText(SettingActivity.this, val, Toast.LENGTH_SHORT).show();
        }

    }


    private setHandler handler = new setHandler(SettingActivity.this);

    Runnable networkTask1 = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message msg = new Message();
            Bundle data = new Bundle();
            if (!AgentOperation.NetworkIsConnected(getApplicationContext())) {

                data.putString("value", "网络无法连接,无法获取产品库");
                msg.setData(data);
                handler.sendMessage(msg);

            } else {
//                AgentOperation.setConfig(setting);

                list_ProductName.clear();
                try {
//                    list_ProductName = AgentOperation.getProductName();
                    if (list_ProductName.size() > 0) {
                        items1 = new String[list_ProductName.size()];

                        list_ProductName.toArray(items1);

                        data.putString("value", "已连接产品库，请选择产品");

                        msg.setData(data);

                        handler.sendMessage(msg);


                    } else {

                        data.putString("value", "已连接产品库，无其他产品");
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    data.putString("value", "连接产品库异常");
                    msg.setData(data);
                    handler.sendMessage(msg);
                }


            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set);

        setting = (Setting) this.getIntent().getSerializableExtra("Setting");
        initView();

        setSettingToMenu(setting);

        tv_server_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("服务器", tv_server_info.getText().toString(), tv_server_info);
                //setting.setCompany(tv_Company_info.getText().toString());
            }
        });

        tv_dataBase_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showEditDialog("数据库", tv_dataBase_info.getText().toString(), tv_dataBase_info);
                //setting.setWorkName(tv_WorkName_info.getText().toString());
            }
        });

        tv_power_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                showEditDialog("节点", tv_Node_info.getText().toString(), tv_Node_info);
                //setting.setNode(tv_Node_info.getText().toString());
            }
        });

        tv_CCTime_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                showEditDialog("网址", tv_Url_info.getText().toString(), tv_Url_info);
                //setting.setDBIP(tv_ip_address_info.getText().toString());
            }
        });

        tv_AgainDeadline_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                showEditDialog("电话", tv_Telephone_info.getText().toString(), tv_Telephone_info);
                //setting.setPort(tv_port_address_info.getText().toString());
            }
        });

        tv_ScanInterval_click.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//                showEditDialog("经纬度", tv_Lat_Lon_info.getText().toString(), tv_Lat_Lon_info);
            }
        });

    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // TODO Auto-generated method stub
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            setMenuToSetting();
//            insertDataBase();
//            resultCode = 9;
//
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra("Setting", (Serializable) setting);
//            SettingActivity.this.setResult(resultCode, resultIntent);
//            SettingActivity.this.finish();
//
//            return super.onKeyDown(keyCode, event);
//
//        } else {
//
//            return false;
//        }
//
//    }


    public void initView() {
        list.add("默认用户");
        list.add("强制用户");
        imageView = (ImageView) findViewById(R.id.imageView);
        spin_userManage = (Spinner) findViewById(R.id.spin_userManage);
        tv_server_info = (TextView) findViewById(R.id.tv_server_info);
        tv_server_click = (TextView) findViewById(R.id.tv_server_click);
        tv_dataBase_info = (TextView) findViewById(R.id.tv_dataBase_info);
        tv_dataBase_click = (TextView) findViewById(R.id.tv_dataBase_click);
        tv_power_info = (TextView) findViewById(R.id.tv_power_info);
        tv_power_click = (TextView) findViewById(R.id.tv_power_click);
        tv_CCTime_info = (TextView) findViewById(R.id.tv_CCTime_info);
        tv_CCTime_click = (TextView) findViewById(R.id.tv_CCTime_click);
        tv_AgainDeadline_info = (TextView) findViewById(R.id.tv_AgainDeadline_info);
        tv_AgainDeadline_click = (TextView) findViewById(R.id.tv_AgainDeadline_click);
        tv_ScanInterval_info= (TextView) findViewById(R.id.tv_ScanInterval_info);
        tv_ScanInterval_click= (TextView) findViewById(R.id.tv_ScanInterval_click);
        arrayAdapter=new ArrayAdapter(this,R.layout.spinner_item,R.id.tv_item,list);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spin_userManage.setAdapter(arrayAdapter);
        //返回主页面
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setMenuToSetting();
                insertDataBase();
                resultCode = 9;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("Setting", (Serializable) setting);
                SettingActivity.this.setResult(resultCode, resultIntent);
                SettingActivity.this.finish();
            }
        });
    }


    public void setSettingToMenu(Setting newset) {
        if (newset.getUserManage().equals("默认用户")) {
            spin_userManage.setSelection(0);
        }else{
            spin_userManage.setSelection(1);
        }
        tv_server_info.setText(newset.getServer());
        tv_dataBase_info.setText(newset.getDataBase());
        tv_power_info.setText(newset.getPower());
        tv_CCTime_info.setText(newset.getCCTime());
        tv_AgainDeadline_info.setText(newset.getAgainDeadline());
        tv_ScanInterval_info.setText(newset.getScanInterval());
    }

    public void showSeekbarDialog(String title, String edit, final View v) {

        final SeekBar sb = new SeekBar(SettingActivity.this);
        int i = 0;
        try {

            i = Integer.parseInt(edit);

        } catch (Exception e) {
            // TODO: handle exception
            i = 30;
        }

        sb.setProgress(i);
        sb.setMax(30);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub

            }
        });

        new AlertDialog.Builder(this).
                // 设置标题
                        setTitle(title)
                        // 添加输入的文本框
                .setView(sb)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        //	((TextView) v).setText(Integer.toString(sb.getProgress()));

                    }
                })
                .setNegativeButton("取消", null)
                .create().show();

    }


    public void showEditDialog(String title, String edit, final View v) {
        final EditText et = new EditText(this);

        et.setText(edit);

        new AlertDialog.Builder(this).
                // 设置标题
                        setTitle(title).
                // 添加输入的文本框
                        setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        ((TextView) v).setText(et.getText());

                    }
                })
                .setNegativeButton("取消", null)
                .create().show();

    }

    public void showTxtDialog(String title, String edit) {

        new AlertDialog.Builder(this).
                // 设置标题
                        setTitle(title).setMessage(edit)
                // 添加输入的文本
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        arg0.dismiss();
                    }
                })
                .create().show();

    }


    public void setMenuToSetting() {
        setting.setUserManage(spin_userManage.getSelectedItem().toString());
        setting.setServer(tv_server_info.getText().toString());
        setting.setDataBase(tv_dataBase_info.getText().toString());
        setting.setPower(tv_power_info.getText().toString());
        setting.setCCTime(tv_CCTime_info.getText().toString());
        setting.setAgainDeadline(tv_AgainDeadline_info.getText().toString());
        setting.setScanInterval(tv_ScanInterval_info.getText().toString());

//        System.out.println(setting.getCompany()+","+setting.getNode()+","+setting.getWorkName()+","+setting.getUrl()+","+setting.getTelephone()
//        +","+setting.getLat_Lon()+","+setting.getNewData()+","+setting.getPower()+","+setting.getLogDeadline()+","+setting.getCCTime()+","+setting.getScanStrategy()
//        +","+setting.getAgainDeadline()+","+setting.getScanInterval());
    }

    public void insertDataBase() {
        DBOperation dboperation = new DBOperation(this);
        dboperation.deleteSet();
//        this.deleteDatabase("goss.db");
        dboperation.insertSet(setting);
    }

}
